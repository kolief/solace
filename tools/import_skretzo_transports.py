#!/usr/bin/env python3
"""
Import transports from Skretzo's shortest-path RuneLite plugin into Solace's transports.json.

Downloads transports.tsv and agility_shortcuts.tsv from Skretzo's GitHub repo,
converts entries to Solace's JSON format, deduplicates against existing entries,
and merges into the existing transports.json.

Usage:
    python3 tools/import_skretzo_transports.py \
        --existing api/src/main/resources/transports.json \
        --output api/src/main/resources/transports.json

    python3 tools/import_skretzo_transports.py --dry-run \
        --existing api/src/main/resources/transports.json
"""

import argparse
import json
import re
import sys
import urllib.request
from collections import Counter

SKRETZO_BASE = "https://raw.githubusercontent.com/Skretzo/shortest-path/master/src/main/resources/transports"

EXCLUDED_ACTIONS = frozenset({
    "Travel", "Board", "Transport", "Sail", "Ride", "Follow",
    "Pay-toll(2-Ecto)", "Quick-pass", "Operate", "Teleport",
    "Chop-down", "Chop", "Slash", "Mine", "Mines",
})

QUEST_NAME_MAP = {
    "Another Slice of H.A.M.": "ANOTHER_SLICE_OF_HAM",
    "Between a Rock...": "BETWEEN_A_ROCK",
    "Children of the Sun": "CHILDREN_OF_THE_SUN",
    "Creature of Fenkenstrain": "CREATURE_OF_FENKENSTRAIN",
    "Death to the Dorgeshuun": "DEATH_TO_THE_DORGESHUUN",
    "Desert Treasure I": "DESERT_TREASURE_I",
    "Dragon Slayer II": "DRAGON_SLAYER_II",
    "Enter the Abyss": None,  # miniquest, not in Quest enum
    "Garden of Tranquillity": "GARDEN_OF_TRANQUILLITY",
    "Legends' Quest": "LEGENDS_QUEST",
    "Lost City": "LOST_CITY",
    "Mourning's End Part II": "MOURNINGS_END_PART_II",
    "Observatory Quest": "OBSERVATORY_QUEST",
    "Perilous Moons": "PERILOUS_MOONS",
    "Regicide": "REGICIDE",
    "Shadows of the Storm": "SHADOWS_OF_THE_STORM",
    "Sins of the Father": "SINS_OF_THE_FATHER",
    "Song of the Elves": "SONG_OF_THE_ELVES",
    "Swan Song": "SWAN_SONG",
    "The Lost Tribe": "THE_LOST_TRIBE",
    "Troll Stronghold": "TROLL_STRONGHOLD",
}


def parse_coordinate(text):
    parts = text.strip().split()
    if len(parts) != 3:
        return None
    try:
        return {"x": int(parts[0]), "y": int(parts[1]), "plane": int(parts[2])}
    except ValueError:
        return None


def parse_menu(text):
    text = text.strip()
    if not text:
        return None, None
    tokens = text.split()
    if len(tokens) < 2:
        return text, None
    action = tokens[0]
    if action and action[0].islower():
        action = action[0].upper() + action[1:]
    try:
        object_id = int(tokens[-1])
        return action, object_id
    except ValueError:
        return action, None


def parse_skills(text):
    text = text.strip()
    if not text:
        return []
    reqs = []
    for part in text.split(";"):
        part = part.strip()
        if not part:
            continue
        match = re.match(r"(\d+)\s+(\w+)", part)
        if match:
            reqs.append({
                "skill": match.group(2).upper(),
                "level": int(match.group(1)),
            })
    return reqs


def parse_quests(text):
    text = text.strip()
    if not text:
        return []
    reqs = []
    for name in text.split(";"):
        name = name.strip()
        if not name:
            continue
        enum_name = QUEST_NAME_MAP.get(name)
        if enum_name is None:
            if name in QUEST_NAME_MAP:
                continue  # explicitly mapped to None (miniquest)
            print(f"  WARNING: Unknown quest '{name}', skipping entry", file=sys.stderr)
            return None
        reqs.append({
            "quest": enum_name,
            "states": ["FINISHED"],
        })
    return reqs


def parse_var_expression(text, var_type):
    text = text.strip()
    if not text:
        return []
    reqs = []
    for part in text.split(";"):
        part = part.strip()
        if not part:
            continue
        if "@" in part:
            continue  # countdown timer, can't represent

        for op, comparison in [
            (">=", "GREATER_THAN_EQUAL"),
            ("<=", "LESS_THAN_EQUAL"),
            ("!=", "NOT_EQUAL"),
            (">", "GREATER_THAN"),
            ("<", "LESS_THAN"),
            ("=", "EQUAL"),
        ]:
            if op in part:
                var_str, val_str = part.split(op, 1)
                try:
                    reqs.append({
                        "comparison": comparison,
                        "type": var_type,
                        "var": int(var_str.strip()),
                        "value": int(val_str.strip()),
                    })
                except ValueError:
                    print(f"  WARNING: Bad var expression '{part}'", file=sys.stderr)
                break
    return reqs


def has_item_reqs(text):
    return bool(text and text.strip())


def build_requirements(skills, quests, varbits, varplayers):
    reqs = {}
    if skills:
        reqs["skillRequirements"] = skills
    if quests:
        reqs["questRequirements"] = quests

    var_reqs = []
    if varbits:
        var_reqs.extend(varbits)
    if varplayers:
        var_reqs.extend(varplayers)
    if var_reqs:
        reqs["varRequirements"] = var_reqs

    return reqs


def fetch_tsv(url):
    with urllib.request.urlopen(url) as resp:
        return resp.read().decode("utf-8")


def parse_transports_tsv(content):
    # Columns: Origin, Destination, menuOption menuTarget objectID, Skills, Items, Quests, Varbits, VarPlayers, Duration, Display info
    entries = []
    for line in content.split("\n"):
        if not line.strip() or line.startswith("#"):
            continue
        parts = line.split("\t")
        if len(parts) < 3:
            continue

        source = parse_coordinate(parts[0])
        dest = parse_coordinate(parts[1])
        if not source or not dest:
            continue

        action, object_id = parse_menu(parts[2] if len(parts) > 2 else "")
        skills_raw = parts[3].strip() if len(parts) > 3 else ""
        items_raw = parts[4].strip() if len(parts) > 4 else ""
        quests_raw = parts[5].strip() if len(parts) > 5 else ""
        varbits_raw = parts[6].strip() if len(parts) > 6 else ""
        varplayers_raw = parts[7].strip() if len(parts) > 7 else ""

        entries.append({
            "source": source,
            "destination": dest,
            "action": action,
            "object_id": object_id,
            "skills_raw": skills_raw,
            "items_raw": items_raw,
            "quests_raw": quests_raw,
            "varbits_raw": varbits_raw,
            "varplayers_raw": varplayers_raw,
            "file": "transports.tsv",
        })
    return entries


def parse_agility_tsv(content):
    # Columns: Origin, Destination, menuOption menuTarget objectID, Skills, Items, Varbits, VarPlayers, Duration, Quests
    entries = []
    for line in content.split("\n"):
        if not line.strip() or line.startswith("#"):
            continue
        parts = line.split("\t")
        if len(parts) < 3:
            continue

        source = parse_coordinate(parts[0])
        dest = parse_coordinate(parts[1])
        if not source or not dest:
            continue

        action, object_id = parse_menu(parts[2] if len(parts) > 2 else "")
        skills_raw = parts[3].strip() if len(parts) > 3 else ""
        items_raw = parts[4].strip() if len(parts) > 4 else ""
        varbits_raw = parts[5].strip() if len(parts) > 5 else ""
        varplayers_raw = parts[6].strip() if len(parts) > 6 else ""
        quests_raw = parts[8].strip() if len(parts) > 8 else ""

        entries.append({
            "source": source,
            "destination": dest,
            "action": action,
            "object_id": object_id,
            "skills_raw": skills_raw,
            "items_raw": items_raw,
            "quests_raw": quests_raw,
            "varbits_raw": varbits_raw,
            "varplayers_raw": varplayers_raw,
            "file": "agility_shortcuts.tsv",
        })
    return entries


def coord_key(source, dest):
    return (source["x"], source["y"], source["plane"],
            dest["x"], dest["y"], dest["plane"])


def convert_entry(raw):
    skills = parse_skills(raw["skills_raw"])
    quests = parse_quests(raw["quests_raw"])
    if quests is None:
        return None, "unknown_quest"

    varbits = parse_var_expression(raw["varbits_raw"], "VARBIT")
    varplayers = parse_var_expression(raw["varplayers_raw"], "VARP")
    requirements = build_requirements(skills, quests, varbits, varplayers)

    entry = {
        "source": raw["source"],
        "destination": raw["destination"],
        "action": raw["action"],
        "objectId": raw["object_id"],
        "requirements": requirements,
    }
    return entry, None


def main():
    parser = argparse.ArgumentParser(description="Import Skretzo transports into Solace")
    parser.add_argument("--existing", required=True, help="Path to existing transports.json")
    parser.add_argument("--output", help="Path to write merged transports.json (omit for dry-run)")
    parser.add_argument("--dry-run", action="store_true", help="Print summary without writing")
    args = parser.parse_args()

    print("Loading existing transports...")
    with open(args.existing) as f:
        existing = json.load(f)
    print(f"  {len(existing)} existing entries")

    existing_keys = set()
    for e in existing:
        key = coord_key(e["source"], e["destination"])
        existing_keys.add(key)

    print("Fetching Skretzo transports.tsv...")
    transports_content = fetch_tsv(f"{SKRETZO_BASE}/transports.tsv")
    transports_raw = parse_transports_tsv(transports_content)
    print(f"  {len(transports_raw)} rows parsed")

    print("Fetching Skretzo agility_shortcuts.tsv...")
    agility_content = fetch_tsv(f"{SKRETZO_BASE}/agility_shortcuts.tsv")
    agility_raw = parse_agility_tsv(agility_content)
    print(f"  {len(agility_raw)} rows parsed")

    all_raw = transports_raw + agility_raw

    stats = Counter()
    new_entries = []

    for raw in all_raw:
        key = coord_key(raw["source"], raw["destination"])

        if key in existing_keys:
            stats["already_exists"] += 1
            continue

        if raw["action"] in EXCLUDED_ACTIONS:
            stats[f"excluded_action:{raw['action']}"] += 1
            continue

        if has_item_reqs(raw["items_raw"]):
            stats["skipped_item_reqs"] += 1
            continue

        if raw["object_id"] is None:
            stats["no_object_id"] += 1
            continue

        entry, err = convert_entry(raw)
        if err:
            stats[f"convert_error:{err}"] += 1
            continue

        existing_keys.add(key)
        new_entries.append(entry)

        if raw["skills_raw"] and raw["quests_raw"]:
            stats["added_skill_quest"] += 1
        elif raw["skills_raw"]:
            stats["added_skill_only"] += 1
        elif raw["quests_raw"]:
            stats["added_quest_only"] += 1
        elif raw["varbits_raw"] or raw["varplayers_raw"]:
            stats["added_var_only"] += 1
        else:
            stats["added_simple"] += 1

    print(f"\n=== Summary ===")
    print(f"New entries to add: {len(new_entries)}")
    for k in sorted(stats.keys()):
        print(f"  {k}: {stats[k]}")

    if args.dry_run or not args.output:
        print("\nDry run — not writing output.")
        return

    merged = existing + new_entries
    print(f"\nWriting {len(merged)} entries to {args.output}...")
    with open(args.output, "w") as f:
        json.dump(merged, f, indent=2)
        f.write("\n")
    print("Done.")


if __name__ == "__main__":
    main()
