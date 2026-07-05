# Mappings

Obfuscation mappings for the pinned RuneLite revision. The loader ships `version-package.json`.

## Version bump

1. Download old and new `injected-client-*.jar` into `mappings/jars/` (gitignored).

```powershell
$version = "1.12.31.1"
Invoke-WebRequest `
  -Uri "https://repo.runelite.net/net/runelite/injected-client/$version/injected-client-$version.jar" `
  -OutFile "mappings/jars/injected-client-$version.jar"
```

2. Migrate canonical mappings:

```powershell
.\gradlew migrateMappings --args="mappings/jars/injected-client-OLD.jar mappings/canonical/mappings-OLD.json mappings/jars/injected-client-NEW.jar mappings/canonical/mappings-NEW.json mappings/canonical/mapper-NEW.log"
```

3. Build `version-package.json`:

```powershell
.\gradlew generateVersionPackage --args="--canonical mappings/canonical/mappings-NEW.json --inject-client mappings/jars/injected-client-NEW.jar --version NEW --commit COMMIT --output mappings/version-package.json"
```

Use `--merge mappings/version-package.json` to carry forward hand-maintained hooks.

4. Update the RuneLite pin in `gradle/libs.versions.toml`, rebuild, and fix breakages in `client/bindings/`.

Override at runtime with `SOLACE_VP_PATH`.
