package net.solace.mappings.tool;

import net.solace.sdn.plugins.version.mappings.ClassMapping;
import net.solace.sdn.plugins.version.mappings.FieldMapping;
import net.solace.sdn.plugins.version.mappings.MethodMapping;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class NamedMappingExtractor {
    private static final String NAMED_DESC = "Ljavax/inject/Named;";

    private NamedMappingExtractor() {
    }

    public static VersionPackageMappings extractFromJar(Path jarPath) throws IOException {
        var classMappings = new ArrayList<ClassMapping>();

        try (var jar = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }

                try (InputStream input = jar.getInputStream(entry)) {
                    var reader = new ClassReader(input.readAllBytes());
                    var node = new ClassNode();
                    reader.accept(node, 0);
                    var mapping = extractClass(node);
                    if (mapping != null) {
                        classMappings.add(mapping);
                    }
                }
            }
        }

        return new VersionPackageMappings(classMappings, List.of(), List.of(), java.util.Map.of());
    }

    private static ClassMapping extractClass(ClassNode node) {
        var obfName = node.name.replace('/', '.');
        var fields = new ArrayList<FieldMapping>();
        for (var field : node.fields) {
            var mapped = namedValue(field.visibleAnnotations);
            if (mapped == null) {
                mapped = namedValue(field.invisibleAnnotations);
            }
            if (mapped == null) {
                continue;
            }
            fields.add(new FieldMapping(
                    mapped,
                    field.name,
                    field.desc,
                    (field.access & Opcodes.ACC_STATIC) != 0
            ));
        }

        var methods = new ArrayList<MethodMapping>();
        for (var method : node.methods) {
            var mapped = namedValue(method.visibleAnnotations);
            if (mapped == null) {
                mapped = namedValue(method.invisibleAnnotations);
            }
            if (mapped == null) {
                continue;
            }
            methods.add(new MethodMapping(
                    mapped,
                    method.name,
                    method.desc,
                    (method.access & Opcodes.ACC_STATIC) != 0
            ));
        }

        if (fields.isEmpty() && methods.isEmpty()) {
            return null;
        }

        var className = namedValue(node.visibleAnnotations);
        if (className == null) {
            className = namedValue(node.invisibleAnnotations);
        }
        if (className == null) {
            className = inferClassName(obfName);
        }

        return new ClassMapping(className, obfName, fields, methods);
    }

    private static String inferClassName(String obfName) {
        var simple = obfName.substring(obfName.lastIndexOf('.') + 1);
        if (simple.isEmpty()) {
            return obfName;
        }
        return Character.toUpperCase(simple.charAt(0)) + simple.substring(1);
    }

    @SuppressWarnings("rawtypes")
    private static String namedValue(List<AnnotationNode> annotations) {
        if (annotations == null) {
            return null;
        }

        for (var annotation : annotations) {
            if (!NAMED_DESC.equals(annotation.desc)) {
                continue;
            }
            var values = annotation.values;
            if (values == null) {
                continue;
            }
            for (int i = 0; i < values.size() - 1; i += 2) {
                if ("value".equals(values.get(i)) && values.get(i + 1) instanceof String) {
                    return (String) values.get(i + 1);
                }
            }
        }

        return null;
    }
}
