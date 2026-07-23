package net.solace.mappings.tool;

import net.solace.mappings.tool.canonical.CanonicalMappingConverter;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts Solace MappingGenerator canonical JSON into {@code version-package.json}.
 *
 * Usage:
 *   --canonical mappings/canonical/mappings-1.12.33.json
 *   [--inject-client path/to/injected-client.jar]
 *   [--merge mappings/version-package.json]
 *   --version 1.12.33
 *   --commit 12e5bee
 *   --output mappings/version-package.json
 */
public final class GenerateVersionPackage {
    public static void main(String[] args) throws Exception {
        Path canonical = null;
        Path injectClient = null;
        Path merge = null;
        Path output = Path.of("mappings/version-package.json");
        String version = null;
        String commit = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--canonical".equals(arg)) {
                canonical = Path.of(requireArg(args, ++i, "--canonical"));
            } else if ("--inject-client".equals(arg)) {
                injectClient = Path.of(requireArg(args, ++i, "--inject-client"));
            } else if ("--merge".equals(arg)) {
                merge = Path.of(requireArg(args, ++i, "--merge"));
            } else if ("--output".equals(arg)) {
                output = Path.of(requireArg(args, ++i, "--output"));
            } else if ("--version".equals(arg)) {
                version = requireArg(args, ++i, "--version");
            } else if ("--commit".equals(arg)) {
                commit = requireArg(args, ++i, "--commit");
            } else if ("--help".equals(arg)) {
                printUsage();
                return;
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        if (canonical == null) {
            throw new IllegalArgumentException("--canonical is required");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("--version is required");
        }
        if (commit == null || commit.isBlank()) {
            throw new IllegalArgumentException("--commit is required");
        }

        var mappings = CanonicalMappingConverter.fromJsonFile(canonical);

        if (injectClient != null) {
            mappings = VersionPackageMappings.merge(mappings, NamedMappingExtractor.extractFromJar(injectClient));
        }
        if (merge != null) {
            mappings = VersionPackageMappings.merge(mappings, MappingJsonCodec.fromJsonFile(merge));
        }

        var document = VersionPackageDocument.fromMappings(mappings, version, commit);
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.writeString(output, MappingJsonCodec.toJson(document));

        System.out.printf(
                "Wrote %s (%d classes, %d static fields, %d static methods, %d garbage entries)%n",
                output,
                mappings.getClassMappings().size(),
                mappings.getStaticFields().size(),
                mappings.getStaticMethods().size(),
                mappings.getGarbage().size()
        );
    }

    private static String requireArg(String[] args, int index, String flag) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for " + flag);
        }
        return args[index];
    }

    private static void printUsage() {
        System.out.println("Usage: GenerateVersionPackage --canonical <canonical.json> --version <ver> --commit <hash> [options]");
        System.out.println();
        System.out.println("  --canonical <path>      Solace MappingGenerator canonical JSON (required)");
        System.out.println("  --inject-client <jar>   Merge @Named mappings from injected-client JAR");
        System.out.println("  --merge <path>          Merge supplemental version-package JSON");
        System.out.println("  --output <path>         Output path (default: mappings/version-package.json)");
        System.out.println("  --version <ver>         RuneLite version string (required)");
        System.out.println("  --commit <hash>         RuneLite commit hash (required)");
    }
}
