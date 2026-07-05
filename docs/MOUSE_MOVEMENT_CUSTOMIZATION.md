# Mouse movement

Interactions can use different mouse path strategies instead of jumping straight to the target.

## Built-in strategies

| Class | Behavior |
|-------|----------|
| `DirectMouseMovement` | Default — instant move |
| `LinearPathMouseMovement` | Straight path with intermediate points |
| `BezierCurveMouseMovement` | Curved path |

Code lives in `client/loader/src/main/java/net/solace/loader/interact/mouse/`.

## Configure

```java
InteractManagerImpl manager = (InteractManagerImpl) client.getInteractManager();
manager.setMouseMovementStrategy(new BezierCurveMouseMovement());
```

Paths are generated when an interaction is queued and stepped through on client ticks (non-blocking).
