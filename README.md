# Solace Client

An open source version of you know what client, but with everything bad stripped from it. Solace includes a plugin API and SDK.

**Not affiliated with Jagex or RuneLite.** Use at your own risk per the [Jagex Terms of Service](https://www.jagex.com/terms).

## Requirements

- JDK 11+ (JDK 17 for collision map generation)
- Pinned RuneLite version in `mappings/version-package.json`

## Build & run

```powershell
.\gradlew build
.\gradlew :loader-dev:runDev
```

## External plugins

Drop PF4J plugin JARs in `~/.solace/plugins/`.

## Mappings

When RuneLite updates, follow [`mappings/README.md`](mappings/README.md).

## Docs

- [Interaction system](docs/INTERACTION_SYSTEM.md)
- [Mouse movement](docs/MOUSE_MOVEMENT_CUSTOMIZATION.md)

P.S. Fuck Burak, we're glad your shitty client got taken down. P.P.S fuck Jim, you're a freak. P.P.P.S, fuck Allure for stealing people's credentials. 
Shoutout to notBarr for releasing this initialy. 

https://storm-client.com

## License

BSD 2-Clause — [LICENSE](LICENSE), [NOTICE](NOTICE)
