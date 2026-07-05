# Solace Client

An open source version of you know what client, but with everything bad stripped from it. Solace includes a plugin API and SDK.

<img width="1290" height="612" alt="image" src="https://github.com/user-attachments/assets/37f734a9-b647-4bda-8377-f33eb746ea57" />


**Not affiliated with Jagex or RuneLite.** Use at your own risk per the [Jagex Terms of Service](https://www.jagex.com/terms).

## Requirements

- JDK 11+ (JDK 17 for collision map generation)
- Pinned RuneLite version in `mappings/version-package.json`

## Build & run

**Production** — fat jar, normal RuneLite (no debug/dev flags):

```powershell
.\gradlew buildClient
.\Solace.bat
```

Or: `java -jar client/loader/build/libs/solace-0.0.5.jar`

Use `Solace.bat` instead of double-clicking the JAR directly — Windows often launches JARs with `javaw`, which hides errors.

**Development only** — RuneLite `--debug --developer-mode`:

```powershell
.\gradlew :loader:runDev
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
