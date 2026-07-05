# Interaction system

Game interactions (NPCs, objects, widgets, ground items) go through interactable types in `client/bindings`, menu builders, and a queue in `client/loader`.

## Main code

| Area | Path |
|------|------|
| Interactables | `client/bindings/src/main/java/net/solace/impl/domain/` |
| Menu builders | `client/bindings/src/main/java/net/solace/impl/interact/builder/` |
| Queue & execution | `client/loader/src/main/java/net/solace/loader/interact/` |

## Usage

```java
npc.interact("Attack");
gameObject.interact("Mine");
```

Menus are built from entity actions, queued as `AutomatedMenu` instances, and executed on client ticks via `InteractManagerImpl`.

Plugins can observe or cancel interactions with `AutomatedInteraction` / `PostAutomatedInteraction` events.
