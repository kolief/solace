package net.solace.mappings.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.solace.mappings.generator.dto.JClass;
import net.solace.mappings.generator.dto.JField;
import net.solace.mappings.generator.dto.JMethod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class MappingGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, String> ACCOUNT_TYPE_FIELD_OVERRIDES = new HashMap<>();
    private static final Map<String, FieldMatch> JX_TOKEN_FIELD_OVERRIDES = new HashMap<>();
    private static final Set<String> STRONG_GLOBAL_FIELD_NAMES = Set.of(
            "JX_DISPLAY_NAME",
            "JX_CHARACTER_ID",
            "JX_SESSION_ID",
            "JX_REFRESH_TOKEN",
            "JX_ACCESS_TOKEN",
            "client",
            "graphicsGuard",
            "jagexType"
    );
    private static final Set<String> ANCHORED_OR_SKIP_GLOBAL_METHOD_NAMES = Set.of(
            "getPacketBufferNode",
            "doAction",
            "setLoginIndex",
            "newRunException",
            "callStackCombiner",
            "getLoginError",
            "setLoginResponse",
            "processError",
            "constructChat",
            "processServerPacket",
            "addNode",
            "write",
            "run"
    );
    private static boolean VERBOSE = Boolean.getBoolean("mapping.verbose");
    private static PrintWriter LOG_WRITER;

    private static final class EnumConstant {
        private final String owner;
        private final String fieldName;
        private final int arg1;
        private final int arg2;

        private EnumConstant(String owner, String fieldName, int arg1, int arg2) {
            this.owner = owner;
            this.fieldName = fieldName;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }
    }

    private MappingGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4 && args.length != 5) {
            System.err.println("Usage: MappingGenerator <oldJar> <oldMappings.json> <newJar> <outMappings.json> [logFile]");
            System.exit(2);
        }

        Path oldJarPath = Path.of(args[0]);
        Path oldMappingsPath = Path.of(args[1]);
        Path newJarPath = Path.of(args[2]);
        Path outPath = Path.of(args[3]);
        Path logPath = args.length == 5 ? Path.of(args[4]) : null;

        if (logPath != null) {
            Files.createDirectories(logPath.toAbsolutePath().getParent());
            LOG_WRITER = new PrintWriter(Files.newBufferedWriter(logPath));
            VERBOSE = true;
        }

        try {
            run(oldJarPath, oldMappingsPath, newJarPath, outPath, logPath);
        } finally {
            if (LOG_WRITER != null) {
                LOG_WRITER.flush();
                LOG_WRITER.close();
            }
        }
    }

    private static void run(Path oldJarPath, Path oldMappingsPath, Path newJarPath, Path outPath, Path logPath) throws Exception {
        logAlways("Solace MappingGenerator");
        logAlways("  oldJar      = " + oldJarPath.toAbsolutePath());
        logAlways("  oldMappings = " + oldMappingsPath.toAbsolutePath());
        logAlways("  newJar      = " + newJarPath.toAbsolutePath());
        logAlways("  output      = " + outPath.toAbsolutePath());
        if (logPath != null) {
            logAlways("  log         = " + logPath.toAbsolutePath());
        }

        logAlways("[1/7] Loading jars and reference mappings...");
        JarModel oldJar = JarModel.load(oldJarPath);
        JarModel newJar = JarModel.load(newJarPath);
        List<JClass> oldMappings = readMappings(oldMappingsPath);
        logAlways("      old classes=" + oldJar.classes.size()
                + " new classes=" + newJar.classes.size()
                + " reference classes=" + oldMappings.size());

        logAlways("[2/7] Matching classes...");
        Map<String, String> classMap = matchClasses(oldJar, newJar, oldMappings);
        logAlways("      class matches=" + classMap.size());
        logVerboseClassMatches(oldMappings, classMap);

        logAlways("[3/7] Building new jar skeleton...");
        List<JClass> generated = buildSkeleton(newJar);
        Map<String, JClass> generatedByObf = generated.stream()
                .collect(Collectors.toMap(JClass::getObfuscatedName, c -> c));
        logAlways("      skeleton classes=" + generated.size()
                + " fields=" + generated.stream().mapToInt(c -> c.getFields().size()).sum()
                + " methods=" + generated.stream().mapToInt(c -> c.getMethods().size()).sum());

        int namedClasses = 0;
        int namedFields = 0;
        int namedMethods = 0;
        List<String> misses = new ArrayList<>();

        logAlways("[4/7] Mapping named classes, fields, methods...");
        for (JClass oldMappedClass : oldMappings) {
            if (blank(oldMappedClass.getName())) {
                continue;
            }

            String newClassName = classMap.get(oldMappedClass.getObfuscatedName());
            if (newClassName == null) {
                misses.add("class " + oldMappedClass.getName() + " [" + oldMappedClass.getObfuscatedName() + "]");
                continue;
            }

            JClass generatedClass = generatedByObf.get(newClassName);
            ClassNode oldClass = oldJar.classes.get(oldMappedClass.getObfuscatedName());
            ClassNode newClass = newJar.classes.get(newClassName);
            if (generatedClass == null || oldClass == null || newClass == null) {
                misses.add("class " + oldMappedClass.getName() + " [" + oldMappedClass.getObfuscatedName() + " -> " + newClassName + "]");
                continue;
            }

            generatedClass.setName(oldMappedClass.getName());
            namedClasses++;
            logVerbose("[class] " + oldMappedClass.getName() + " " + oldMappedClass.getObfuscatedName() + " -> " + newClassName);

            for (JField oldField : oldMappedClass.getFields()) {
                if (blank(oldField.getName())) {
                    continue;
                }
                if ("client".equals(oldField.getName())
                        || oldField.getName().startsWith("JX_")
                        || "graphicsGuard".equals(oldField.getName())) {
                    logVerbose("[field/skipped] " + oldMappedClass.getName() + "." + oldField.getName()
                            + " reserved for expected owner projection");
                    continue;
                }
                String oldOwnerName = blank(oldField.getOwnerObfuscatedName()) ? oldMappedClass.getObfuscatedName() : oldField.getOwnerObfuscatedName();
                String newOwnerName = classMap.get(oldOwnerName);
                ClassNode oldOwner = oldJar.classes.get(oldOwnerName);
                ClassNode newOwner = newOwnerName == null ? null : newJar.classes.get(newOwnerName);
                if (oldOwner == null || newOwner == null) {
                    misses.add("field " + oldMappedClass.getName() + "." + oldField.getName() + " owner");
                    continue;
                }
                FieldNode oldNode = findField(oldOwner, oldField.getObfuscatedName(), oldField.getDescriptor(), oldField.isStatic());
                FieldNode newField = specialNamedFieldCandidate(oldMappedClass.getName(), oldField.getName(), newOwner, newJar, classMap, oldMappings);
                if (newField == null) {
                    newField = oldNode == null ? null : bestNamedFieldCandidate(oldNode, oldOwner, newOwner, oldJar, newJar, oldMappings, classMap);
                }
                if (newField == null) {
                    MemberMap<FieldNode> fieldMap = matchFields(oldOwner, newOwner, classMap);
                    newField = fieldMap.byOldNameDesc.get(oldField.getObfuscatedName() + oldField.getDescriptor());
                }
                if (newField == null) {
                    misses.add("field " + oldMappedClass.getName() + "." + oldField.getName());
                    continue;
                }

                JField generatedField = findOrCreateField(generatedClass, newOwner.name, newField);
                generatedField.setName(oldField.getName());
                generatedField.setOwner(blank(oldField.getOwner()) ? oldMappedClass.getName() : oldField.getOwner());
                Multiplier multiplier = multiplierForNamedField(oldMappedClass.getName(), newJar, newOwner, newField);
                if (multiplier.getter != null) {
                    generatedField.setGetter(multiplier.getter);
                }
                if (multiplier.setter != null) {
                    generatedField.setSetter(multiplier.setter);
                }
                namedFields++;
                logVerbose("[field] " + oldMappedClass.getName() + "." + oldField.getName()
                        + " " + oldOwner.name + "." + oldField.getObfuscatedName() + ":" + oldField.getDescriptor()
                        + " -> " + newOwner.name + "." + newField.name + ":" + newField.desc
                        + " static=" + isStatic(newField.access)
                        + " getter=" + valueOrBlank(multiplier.getter)
                        + " setter=" + valueOrBlank(multiplier.setter));
            }

            for (JMethod oldMethod : oldMappedClass.getMethods()) {
                if (blank(oldMethod.getName())) {
                    continue;
                }
                String oldOwnerName = blank(oldMethod.getOwnerObfuscatedName()) ? oldMappedClass.getObfuscatedName() : oldMethod.getOwnerObfuscatedName();
                String newOwnerName = classMap.get(oldOwnerName);
                ClassNode oldOwner = oldJar.classes.get(oldOwnerName);
                ClassNode newOwner = newOwnerName == null ? null : newJar.classes.get(newOwnerName);
                if (oldOwner == null || newOwner == null) {
                    misses.add("method " + oldMappedClass.getName() + "." + oldMethod.getName() + " owner");
                    continue;
                }
                MethodNode newMethod = specialNamedMethodCandidate(oldMappedClass.getName(), oldMethod.getName(), newJar, oldMappings, classMap);
                MemberMap<MethodNode> methodMap = matchMethods(oldOwner, newOwner, classMap);
                if (newMethod == null) {
                    newMethod = methodMap.byOldNameDesc.get(oldMethod.getObfuscatedName() + oldMethod.getDescriptor());
                }
                if (newMethod == null) {
                    misses.add("method " + oldMappedClass.getName() + "." + oldMethod.getName());
                    continue;
                }

                JMethod generatedMethod = findOrCreateMethod(generatedClass, newOwner.name, newMethod);
                generatedMethod.setName(oldMethod.getName());
                generatedMethod.setOwner(blank(oldMethod.getOwner()) ? oldMappedClass.getName() : oldMethod.getOwner());
                generatedMethod.setGarbageValue(inferGarbageValue(oldMethod, newMethod));
                namedMethods++;
                logVerbose("[method] " + oldMappedClass.getName() + "." + oldMethod.getName()
                        + " " + oldOwner.name + "." + oldMethod.getObfuscatedName() + oldMethod.getDescriptor()
                        + " -> " + newOwner.name + "." + newMethod.name + newMethod.desc
                        + " static=" + isStatic(newMethod.access)
                        + " garbage=" + valueOrBlank(generatedMethod.getGarbageValue()));
            }
        }

        logAlways("[5/7] Adding global reference entries...");
        addGlobalReferenceEntries(generated, oldMappings, oldJar, newJar, classMap, misses);
        logAlways("[6/7] Adding Solace expected global hooks...");
        addExpectedGlobalFieldHooks(generated, oldMappings, oldJar, newJar, classMap, misses);
        addExpectedGlobalMethodHooks(generated, oldMappings, newJar, classMap, misses);

        logAlways("[7/7] Writing mappings JSON...");
        Files.createDirectories(outPath.toAbsolutePath().getParent());
        Files.writeString(outPath, GSON.toJson(generated));

        logAlways("Wrote " + outPath.toAbsolutePath());
        logAlways("Classes: " + generated.size() + " (" + namedClasses + " named)");
        logAlways("Fields: " + generated.stream().mapToInt(c -> c.getFields().size()).sum() + " (" + namedFields + " named)");
        logAlways("Methods: " + generated.stream().mapToInt(c -> c.getMethods().size()).sum() + " (" + namedMethods + " named)");
        if (!misses.isEmpty()) {
            logAlways("Unresolved:");
            misses.forEach(m -> logAlways("  - " + m));
        }
    }

    private static void addGlobalReferenceEntries(List<JClass> generated, List<JClass> oldMappings, JarModel oldJar,
                                                  JarModel newJar, Map<String, String> classMap, List<String> misses) {
        Map<String, JClass> generatedByObf = generated.stream()
                .collect(Collectors.toMap(JClass::getObfuscatedName, c -> c, (a, b) -> a));
        for (JClass oldClassMapping : oldMappings) {
            if (!blank(oldClassMapping.getName())) continue;
            boolean hasNamed = oldClassMapping.getFields().stream().anyMatch(f -> !blank(f.getName()))
                    || oldClassMapping.getMethods().stream().anyMatch(m -> !blank(m.getName()));
            if (!hasNamed) continue;

            String newOwnerName = classMap.get(oldClassMapping.getObfuscatedName());
            ClassNode oldOwner = oldJar.classes.get(oldClassMapping.getObfuscatedName());
            ClassNode newOwner = newOwnerName == null ? null : newJar.classes.get(newOwnerName);
            JClass generatedClass = newOwnerName == null ? null : generatedByObf.get(newOwnerName);
            if (oldOwner == null || newOwner == null || generatedClass == null) continue;

            for (JField oldField : oldClassMapping.getFields()) {
                if (blank(oldField.getName())) continue;
                if (STRONG_GLOBAL_FIELD_NAMES.contains(oldField.getName())) {
                    logVerbose("[global-field/skipped] " + oldField.getName()
                            + " reserved for expected owner projection");
                    continue;
                }
                FieldNode oldNode = findField(oldOwner, oldField.getObfuscatedName(), oldField.getDescriptor(), oldField.isStatic());
                FieldMatch strongMatch = strongGlobalFieldReferenceMatch(oldField.getName(), oldField, oldJar, newJar, classMap, oldMappings);
                if (strongMatch == null && STRONG_GLOBAL_FIELD_NAMES.contains(oldField.getName())) {
                    logVerbose("[global-field/skipped] " + oldField.getName()
                            + " " + oldOwner.name + "." + oldField.getObfuscatedName() + ":" + oldField.getDescriptor()
                            + " no strong anchor");
                    continue;
                }
                ClassNode fieldOwner = strongMatch == null ? newOwner : strongMatch.owner;
                FieldNode newField = strongMatch == null
                        ? oldNode == null ? null : bestNamedFieldCandidate(oldNode, oldOwner, newOwner, oldJar, newJar, oldMappings, classMap)
                        : strongMatch.field;
                if (newField == null) continue;
                JClass ownerClass = generatedByObf.get(fieldOwner.name);
                if (ownerClass == null) continue;
                JField generatedField = addNamedField(ownerClass, fieldOwner.name, newField, oldField.getName(), oldField.getOwner());
                Multiplier multiplier = multiplierForNamedField(oldClassMapping.getName(), newJar, fieldOwner, newField);
                if (multiplier.getter != null) generatedField.setGetter(multiplier.getter);
                if (multiplier.setter != null) generatedField.setSetter(multiplier.setter);
                logVerbose("[global-field" + (strongMatch == null ? "" : "/anchored") + "] " + oldField.getName()
                        + " " + oldOwner.name + "." + oldField.getObfuscatedName() + ":" + oldField.getDescriptor()
                        + " -> " + fieldOwner.name + "." + newField.name + ":" + newField.desc
                        + " getter=" + valueOrBlank(multiplier.getter)
                        + " setter=" + valueOrBlank(multiplier.setter));
                misses.removeIf(m -> m.endsWith("." + oldField.getName()));
            }

            for (JMethod oldMethod : oldClassMapping.getMethods()) {
                if (blank(oldMethod.getName())) continue;
                if (ANCHORED_OR_SKIP_GLOBAL_METHOD_NAMES.contains(oldMethod.getName())) {
                    logVerbose("[global-method/skipped] " + oldMethod.getName()
                            + " reserved for expected owner projection");
                    continue;
                }
                MethodNode oldNode = findMethod(oldOwner, oldMethod.getObfuscatedName(), oldMethod.getDescriptor(), oldMethod.isStatic());
                if (oldNode == null) continue;
                MethodMatch strongMatch = specialGlobalMethodMatch(oldMethod.getName(), newJar, oldMappings, classMap);
                MethodNode newMethod = strongMatch == null
                        ? specialNamedMethodCandidate(oldClassMapping.getName(), oldMethod.getName(), newJar, oldMappings, classMap)
                        : strongMatch.method;
                ClassNode methodOwner = strongMatch == null ? newOwner : strongMatch.owner;
                if (newMethod == null && ANCHORED_OR_SKIP_GLOBAL_METHOD_NAMES.contains(oldMethod.getName())) {
                    logVerbose("[global-method/skipped] " + oldMethod.getName()
                            + " " + oldOwner.name + "." + oldMethod.getObfuscatedName() + oldMethod.getDescriptor()
                            + " no strong anchor");
                    continue;
                }
                if (newMethod == null) {
                    newMethod = bestNamedMethodCandidate(oldNode, newOwner, classMap);
                }
                if (newMethod == null) continue;
                JClass ownerClass = generatedByObf.get(methodOwner.name);
                if (ownerClass == null) continue;
                JMethod generatedMethod = findOrCreateMethod(ownerClass, methodOwner.name, newMethod);
                generatedMethod.setName(oldMethod.getName());
                generatedMethod.setOwner(oldMethod.getOwner());
                generatedMethod.setGarbageValue(inferGarbageValue(oldMethod, newMethod));
                logVerbose("[global-method" + (strongMatch == null ? "" : "/anchored") + "] " + oldMethod.getName()
                        + " " + oldOwner.name + "." + oldMethod.getObfuscatedName() + oldMethod.getDescriptor()
                        + " -> " + methodOwner.name + "." + newMethod.name + newMethod.desc
                        + " static=" + isStatic(newMethod.access)
                        + " garbage=" + valueOrBlank(generatedMethod.getGarbageValue()));
                misses.removeIf(m -> m.endsWith("." + oldMethod.getName()));
            }
        }
    }

    private static FieldMatch strongGlobalFieldReferenceMatch(String fieldName, JField oldField, JarModel oldJar, JarModel newJar,
                                                              Map<String, String> classMap, List<JClass> mappings) {
        if (!STRONG_GLOBAL_FIELD_NAMES.contains(fieldName)) {
            return null;
        }
        if ("jagexType".equals(fieldName)) {
            String accountType = mappedName(mappings, classMap, "AccountType");
            ClassNode owner = accountType == null ? null : newJar.classes.get(accountType);
            String field = ACCOUNT_TYPE_FIELD_OVERRIDES.get("jagexType");
            if (field == null && owner != null) {
                field = constantByArgs(enumConstants(owner), 1, 1);
            }
            if (owner != null && field != null) {
                FieldNode node = findField(owner, field, "L" + owner.name + ";", true);
                if (node != null) return new FieldMatch(owner, node);
            }
            return null;
        }
        return findProjectedFieldMatch(fieldName, "Client", oldField, oldJar, newJar, classMap, mappings);
    }

    private static JField addNamedField(JClass generatedClass, String owner, FieldNode field, String name, String deobOwner) {
        for (JField existing : generatedClass.getFields()) {
            if (name.equals(existing.getName())
                    && existing.getOwnerObfuscatedName().equals(owner)
                    && existing.getObfuscatedName().equals(field.name)
                    && existing.getDescriptor().equals(field.desc)
                    && existing.isStatic() == isStatic(field.access)) {
                return existing;
            }
        }
        JField jf = new JField();
        jf.setName(name);
        jf.setOwner(deobOwner);
        jf.setObfuscatedName(field.name);
        jf.setOwnerObfuscatedName(owner);
        jf.setDescriptor(field.desc);
        jf.setStatic(isStatic(field.access));
        generatedClass.getFields().add(jf);
        return jf;
    }

    private static void logVerboseClassMatches(List<JClass> oldMappings, Map<String, String> classMap) {
        if (!VERBOSE) return;
        for (JClass mapping : oldMappings) {
            if (blank(mapping.getName())) continue;
            String mapped = classMap.get(mapping.getObfuscatedName());
            if (mapped != null) {
                logVerbose("[class-map] " + mapping.getName() + " " + mapping.getObfuscatedName() + " -> " + mapped);
            }
        }
    }

    private static void logAlways(String line) {
        System.out.println(line);
        if (LOG_WRITER != null) {
            LOG_WRITER.println(line);
            LOG_WRITER.flush();
        }
    }

    private static void logVerbose(String line) {
        if (VERBOSE) {
            logAlways(line);
        }
    }

    private static String valueOrBlank(Object value) {
        return value == null ? "" : value.toString();
    }

    private static void addExpectedGlobalFieldHooks(List<JClass> generated, List<JClass> oldMappings, JarModel oldJar, JarModel newJar,
                                                    Map<String, String> classMap, List<String> misses) {
        Map<String, List<String>> expectedOwners = new LinkedHashMap<>();
        expectedOwners.put("MouseHandler_instance", List.of("Client"));
        expectedOwners.put("MouseHandler_idleCycles", List.of("Client"));
        expectedOwners.put("JX_DISPLAY_NAME", List.of("Client"));
        expectedOwners.put("JX_CHARACTER_ID", List.of("Client", "PlatformInfo"));
        expectedOwners.put("JX_SESSION_ID", List.of("Client"));
        expectedOwners.put("JX_REFRESH_TOKEN", List.of("Client"));
        expectedOwners.put("JX_ACCESS_TOKEN", List.of("Client"));
        expectedOwners.put("client", List.of("Client", "Task", "PacketWriter"));
        expectedOwners.put("graphicsGuard", List.of("GameEngine"));

        for (Map.Entry<String, List<String>> entry : expectedOwners.entrySet()) {
            String fieldName = entry.getKey();
            JField oldField = findNamedField(oldMappings, fieldName);
            for (String ownerName : entry.getValue()) {
                JClass targetClass = generated.stream().filter(c -> ownerName.equals(c.getName())).findFirst().orElse(null);
                if (targetClass == null) continue;

                FieldMatch match = findProjectedFieldMatch(fieldName, ownerName, oldField, oldJar, newJar, classMap, oldMappings);
                if (match == null) continue;
                JField generatedField = addNamedField(targetClass, match.owner.name, match.field, fieldName, ownerName);
                Multiplier multiplier = inferMultiplier(newJar, match.owner.name, match.field.name, match.field.desc);
                if (multiplier.getter != null) generatedField.setGetter(multiplier.getter);
                if (multiplier.setter != null) generatedField.setSetter(multiplier.setter);
                logVerbose("[expected-field] " + ownerName + "." + fieldName
                        + " -> " + match.owner.name + "." + match.field.name + ":" + match.field.desc
                        + " getter=" + valueOrBlank(multiplier.getter)
                        + " setter=" + valueOrBlank(multiplier.setter));
                misses.removeIf(m -> m.equals("field " + ownerName + "." + fieldName)
                        || m.equals("field " + ownerName + "." + fieldName + " owner")
                        || m.endsWith("." + fieldName));
            }
        }
    }

    private static JField findNamedField(List<JClass> mappings, String name) {
        for (JClass mapping : mappings) {
            for (JField field : mapping.getFields()) {
                if (name.equals(field.getName())) return field;
            }
        }
        return null;
    }

    private static FieldMatch findProjectedFieldMatch(String fieldName, String ownerName, JField oldField, JarModel oldJar, JarModel newJar,
                                                      Map<String, String> classMap, List<JClass> mappings) {
        if ("MouseHandler_instance".equals(fieldName) || "MouseHandler_idleCycles".equals(fieldName)) {
            String mouseHandler = mappedName(mappings, classMap, "MouseHandler");
            ClassNode owner = mouseHandler == null ? null : newJar.classes.get(mouseHandler);
            if (owner == null) return null;
            FieldNode field = specialNamedFieldCandidate("MouseHandler", fieldName, owner, newJar, classMap, mappings);
            return field == null ? null : new FieldMatch(owner, field);
        }
        if ("graphicsGuard".equals(fieldName)) {
            FieldMatch byHook = findBooleanFieldHookTarget(newJar);
            if (byHook != null) return byHook;
        }
        if ("client".equals(fieldName)) {
            FieldMatch client = findClientBackReference(newJar);
            if (client != null) return client;
        }
        if (fieldName.startsWith("JX_")) {
            FieldMatch token = resolveUniqueJagexTokenField(fieldName, oldField, oldJar, newJar, classMap);
            if (token != null) return token;
        }
        if (oldField == null) return null;
        return findGlobalFieldMatch(newJar, oldField, classMap);
    }

    private static FieldMatch findGlobalFieldMatch(JarModel newJar, JField oldField, Map<String, String> classMap) {
        FieldMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : newJar.classes.values()) {
            for (FieldNode field : owner.fields) {
                if (isStatic(field.access) != oldField.isStatic()) continue;
                if (!fieldDescriptorCompatible(oldField.getDescriptor(), field.desc, classMap)) continue;
                int score = 10;
                if (field.name.equals(oldField.getObfuscatedName())) score += 20;
                if (oldField.getDescriptor().equals(field.desc)) score += 10;
                if (score > bestScore) {
                    bestScore = score;
                    best = new FieldMatch(owner, field);
                }
            }
        }
        return best;
    }

    private static FieldMatch findGlobalFieldByUsage(JarModel oldJar, JarModel newJar, JField oldField,
                                                     Map<String, String> classMap, int minScore) {
        String oldOwnerName = oldField.getOwnerObfuscatedName();
        ClassNode oldOwner = oldOwnerName == null ? null : oldJar.classes.get(oldOwnerName);
        if (oldOwner == null) return null;
        FieldNode oldNode = findField(oldOwner, oldField.getObfuscatedName(), oldField.getDescriptor(), oldField.isStatic());
        if (oldNode == null) return null;

        FieldMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode newOwner : newJar.classes.values()) {
            for (FieldNode candidate : newOwner.fields) {
                if (isStatic(candidate.access) != oldField.isStatic()) continue;
                if (!fieldDescriptorCompatible(oldField.getDescriptor(), candidate.desc, classMap)) continue;
                int score = fieldUsageScore(oldJar, oldOwner.name, oldNode.name, oldNode.desc,
                        newJar, newOwner.name, candidate.name, candidate.desc, classMap);
                if (candidate.name.equals(oldField.getObfuscatedName())) score += 2;
                if (score > bestScore) {
                    bestScore = score;
                    best = new FieldMatch(newOwner, candidate);
                }
            }
        }
        return bestScore >= minScore ? best : null;
    }

    private static FieldMatch findBooleanFieldHookTarget(JarModel newJar) {
        ClassNode client = newJar.classes.get("client");
        if (client == null) return null;
        FieldMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : newJar.classes.values()) {
            for (FieldNode field : owner.fields) {
                if (!field.desc.equals("Z")) continue;
                int puts = fieldAccessCount(newJar, owner.name, field.name, field.desc,
                        isStatic(field.access) ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD);
                int gets = fieldAccessCount(newJar, owner.name, field.name, field.desc,
                        isStatic(field.access) ? Opcodes.GETSTATIC : Opcodes.GETFIELD);
                int score = puts * 10 + gets;
                if (owner.name.equals("client")) score += 5;
                if (score > bestScore) {
                    bestScore = score;
                    best = new FieldMatch(owner, field);
                }
            }
        }
        return best;
    }

    private static FieldMatch findClientBackReference(JarModel newJar) {
        for (ClassNode owner : newJar.classes.values()) {
            for (FieldNode field : owner.fields) {
                if (field.desc.equals("Lclient;")) {
                    return new FieldMatch(owner, field);
                }
            }
        }
        return null;
    }

    private static FieldMatch resolveUniqueJagexTokenField(String fieldName, JField oldField, JarModel oldJar, JarModel newJar,
                                                           Map<String, String> classMap) {
        FieldMatch existing = JX_TOKEN_FIELD_OVERRIDES.get(fieldName);
        if (existing != null) return existing;

        FieldMatch anchored = findJagexTokenField(fieldName, newJar);
        if (anchored != null && reserveJagexTokenField(fieldName, anchored)) {
            return anchored;
        }

        if (oldField != null) {
            for (FieldScore candidate : globalFieldUsageCandidates(oldJar, newJar, oldField, classMap, 4)) {
                if (reserveJagexTokenField(fieldName, candidate.match)) {
                    return candidate.match;
                }
            }
        }

        if (anchored != null) {
            JX_TOKEN_FIELD_OVERRIDES.put(fieldName, anchored);
            return anchored;
        }
        return null;
    }

    private static boolean reserveJagexTokenField(String fieldName, FieldMatch match) {
        String key = fieldKey(match);
        for (Map.Entry<String, FieldMatch> entry : JX_TOKEN_FIELD_OVERRIDES.entrySet()) {
            if (!entry.getKey().equals(fieldName) && fieldKey(entry.getValue()).equals(key)) {
                return false;
            }
        }
        JX_TOKEN_FIELD_OVERRIDES.put(fieldName, match);
        return true;
    }

    private static String fieldKey(FieldMatch match) {
        return match.owner.name + "." + match.field.name + ":" + match.field.desc + ":" + isStatic(match.field.access);
    }

    private static FieldMatch findJagexTokenField(String fieldName, JarModel newJar) {
        FieldMatch display = findStringFieldSetByTokenConsumer(newJar, fieldName);
        if (display != null) return display;
        FieldMatch byLiteral = findStringFieldNearLiteral(newJar, fieldName);
        if (byLiteral != null) return byLiteral;
        String needle = fieldName.toLowerCase(Locale.ROOT).replace("jx_", "").replace('_', '-');
        for (ClassNode owner : newJar.classes.values()) {
            for (FieldNode field : owner.fields) {
                if (!field.desc.equals("Ljava/lang/String;") || !isStatic(field.access)) continue;
                if (classOrConstantsContain(owner, needle)) {
                    return new FieldMatch(owner, field);
                }
            }
        }
        return null;
    }

    private static List<FieldScore> globalFieldUsageCandidates(JarModel oldJar, JarModel newJar, JField oldField,
                                                               Map<String, String> classMap, int minScore) {
        String oldOwnerName = oldField.getOwnerObfuscatedName();
        ClassNode oldOwner = oldOwnerName == null ? null : oldJar.classes.get(oldOwnerName);
        if (oldOwner == null) return List.of();
        FieldNode oldNode = findField(oldOwner, oldField.getObfuscatedName(), oldField.getDescriptor(), oldField.isStatic());
        if (oldNode == null) return List.of();

        List<FieldScore> out = new ArrayList<>();
        for (ClassNode newOwner : newJar.classes.values()) {
            for (FieldNode candidate : newOwner.fields) {
                if (isStatic(candidate.access) != oldField.isStatic()) continue;
                if (!fieldDescriptorCompatible(oldField.getDescriptor(), candidate.desc, classMap)) continue;
                int score = fieldUsageScore(oldJar, oldOwner.name, oldNode.name, oldNode.desc,
                        newJar, newOwner.name, candidate.name, candidate.desc, classMap);
                if (candidate.name.equals(oldField.getObfuscatedName())) score += 2;
                if (score >= minScore) {
                    out.add(new FieldScore(new FieldMatch(newOwner, candidate), score));
                }
            }
        }
        out.sort(Comparator.<FieldScore>comparingInt(fs -> fs.score).reversed()
                .thenComparing(fs -> fs.match.owner.name)
                .thenComparing(fs -> fs.match.field.name));
        return out;
    }

    private static FieldMatch findStringFieldSetByTokenConsumer(JarModel jar, String literal) {
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                AbstractInsnNode[] insns = method.instructions.toArray();
                for (int i = 0; i < insns.length; i++) {
                    if (!(insns[i] instanceof LdcInsnNode) || !literal.equals(((LdcInsnNode) insns[i]).cst)) {
                        continue;
                    }
                    for (int j = i + 1; j < Math.min(insns.length, i + 12); j++) {
                        if (!(insns[j] instanceof MethodInsnNode)) continue;
                        MethodInsnNode min = (MethodInsnNode) insns[j];
                        if (!min.desc.startsWith("(Ljava/lang/String;") || !min.desc.endsWith(")V")) continue;
                        ClassNode calleeOwner = jar.classes.get(min.owner);
                        if (calleeOwner == null) continue;
                        MethodNode callee = findMethod(calleeOwner, min.name, min.desc, true);
                        FieldMatch written = callee == null ? null : firstStaticStringPutAnyOwner(jar, callee);
                        if (written != null) {
                            return written;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static FieldMatch firstStaticStringPutAnyOwner(JarModel jar, MethodNode method) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn.getOpcode() != Opcodes.PUTSTATIC || !(insn instanceof FieldInsnNode)) continue;
            FieldInsnNode fin = (FieldInsnNode) insn;
            if (!fin.desc.equals("Ljava/lang/String;")) continue;
            ClassNode fieldOwner = jar.classes.get(fin.owner);
            if (fieldOwner == null) continue;
            FieldNode field = findField(fieldOwner, fin.name, fin.desc, true);
            if (field != null) return new FieldMatch(fieldOwner, field);
        }
        return null;
    }

    private static FieldMatch findStringFieldNearLiteral(JarModel jar, String literal) {
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                AbstractInsnNode[] insns = method.instructions.toArray();
                for (int i = 0; i < insns.length; i++) {
                    if (!(insns[i] instanceof LdcInsnNode)) continue;
                    Object cst = ((LdcInsnNode) insns[i]).cst;
                    if (!literal.equals(cst)) continue;
                    FieldMatch match = nearestStringFieldAccess(jar, insns, i);
                    if (match != null) return match;
                }
            }
        }
        return null;
    }

    private static FieldMatch nearestStringFieldAccess(JarModel jar, AbstractInsnNode[] insns, int index) {
        for (int radius = 1; radius <= 24; radius++) {
            for (int pos : new int[]{index + radius, index - radius}) {
                if (pos < 0 || pos >= insns.length || !(insns[pos] instanceof FieldInsnNode)) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[pos];
                if (!fin.desc.equals("Ljava/lang/String;")) continue;
                ClassNode fieldOwner = jar.classes.get(fin.owner);
                if (fieldOwner == null) continue;
                FieldNode field = findField(fieldOwner, fin.name, fin.desc, fin.getOpcode() == Opcodes.GETSTATIC || fin.getOpcode() == Opcodes.PUTSTATIC);
                if (field != null) return new FieldMatch(fieldOwner, field);
            }
        }
        return null;
    }

    private static boolean classOrConstantsContain(ClassNode owner, String needle) {
        if (owner.name.toLowerCase(Locale.ROOT).contains(needle)) return true;
        for (MethodNode method : owner.methods) {
            for (String constant : methodConstants(method)) {
                if (constant.toLowerCase(Locale.ROOT).contains(needle)) return true;
            }
        }
        return false;
    }

    private static void addExpectedGlobalMethodHooks(List<JClass> generated, List<JClass> oldMappings, JarModel newJar,
                                                     Map<String, String> classMap, List<String> misses) {
        Map<String, String> expectedOwner = new LinkedHashMap<>();
        expectedOwner.put("getPacketBufferNode", "Client");
        expectedOwner.put("doAction", "Client");
        expectedOwner.put("setLoginIndex", "Client");
        expectedOwner.put("newRunException", "Client");
        expectedOwner.put("callStackCombiner", "Client");
        expectedOwner.put("getLoginError", "GameEngine");
        expectedOwner.put("setLoginResponse", "GameEngine");
        expectedOwner.put("processError", "GameEngine");
        expectedOwner.put("constructChat", "PacketWriter");

        for (Map.Entry<String, String> entry : expectedOwner.entrySet()) {
            String methodName = entry.getKey();
            String ownerName = entry.getValue();
            JMethod oldMethod = findNamedMethod(oldMappings, methodName);
            if (oldMethod == null) continue;
            MethodMatch match = specialGlobalMethodMatch(methodName, newJar, oldMappings, classMap);
            if (match == null) {
                match = findGlobalMethodMatch(newJar, oldMethod, classMap);
            }
            if (match == null) continue;
            MethodMatch resolvedMatch = match;
            JClass targetClass = generated.stream().filter(c -> ownerName.equals(c.getName())).findFirst().orElse(null);
            if (targetClass == null) continue;
            JMethod generatedMethod = findOrCreateMethod(targetClass, resolvedMatch.owner.name, resolvedMatch.method);
            generatedMethod.setName(methodName);
            generatedMethod.setOwner(ownerName);
            generatedMethod.setGarbageValue(inferGarbageValue(oldMethod, resolvedMatch.method));
            logVerbose("[expected-method] " + ownerName + "." + methodName
                    + " -> " + resolvedMatch.owner.name + "." + resolvedMatch.method.name + resolvedMatch.method.desc
                    + " static=" + isStatic(resolvedMatch.method.access)
                    + " garbage=" + valueOrBlank(generatedMethod.getGarbageValue()));
            misses.removeIf(m -> m.equals("method " + ownerName + "." + methodName)
                    || m.equals("method " + ownerName + "." + methodName + " owner")
                    || m.endsWith("." + methodName));
        }
    }

    private static boolean hasNamedMethodAlias(JClass ownerClass, String name, String owner, String ownerObf, MethodNode method) {
        for (JMethod existing : ownerClass.getMethods()) {
            if (name.equals(existing.getName())
                    && Objects.equals(owner, existing.getOwner())
                    && ownerObf.equals(existing.getOwnerObfuscatedName())
                    && method.name.equals(existing.getObfuscatedName())
                    && method.desc.equals(existing.getDescriptor())
                    && existing.isStatic() == isStatic(method.access)) {
                return true;
            }
        }
        return false;
    }

    private static JMethod findNamedMethod(List<JClass> mappings, String name) {
        for (JClass mapping : mappings) {
            for (JMethod method : mapping.getMethods()) {
                if (name.equals(method.getName())) return method;
            }
        }
        return null;
    }

    private static MethodMatch findGlobalMethodMatch(JarModel newJar, JMethod oldMethod, Map<String, String> classMap) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : newJar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (isStatic(method.access) != oldMethod.isStatic()) continue;
                if (!compatibleMethodDescriptor(oldMethod.getDescriptor(), method.desc, classMap)) continue;
                int score = 10;
                if (method.name.equals(oldMethod.getObfuscatedName())) score += 30;
                score += methodConstants(method).size();
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return best;
    }

    private static MethodMatch specialGlobalMethodMatch(String methodName, JarModel newJar, List<JClass> mappings,
                                                        Map<String, String> classMap) {
        if ("getPacketBufferNode".equals(methodName)) {
            return packetBufferNodeFactoryMethod(newJar, mappings, classMap);
        }
        if ("doAction".equals(methodName)) {
            return doActionMethod(newJar, mappings, classMap);
        }
        if ("addNode".equals(methodName)) {
            return packetWriterAddNodeMethod(newJar, mappings, classMap);
        }
        if ("processServerPacket".equals(methodName)) {
            return processServerPacketMethod(newJar, mappings, classMap);
        }
        if ("processError".equals(methodName)) {
            return processErrorMethod(newJar);
        }
        if ("newRunException".equals(methodName)) {
            return newRunExceptionMethod(newJar);
        }
        if ("callStackCombiner".equals(methodName)) {
            return callStackCombinerMethod(newJar);
        }
        if ("setLoginIndex".equals(methodName)) {
            return loginIndexSetterMethod(newJar);
        }
        if ("getLoginError".equals(methodName)) {
            return loginIndexMethod(newJar);
        }
        if ("setLoginResponse".equals(methodName)) {
            return setLoginResponseMethod(newJar);
        }
        if ("constructChat".equals(methodName)) {
            return constructChatMethod(newJar, mappings, classMap);
        }
        return null;
    }

    private static MethodMatch doActionMethod(JarModel jar, List<JClass> mappings, Map<String, String> classMap) {
        String clientPacket = mappedName(mappings, classMap, "ClientPacket");
        MethodMatch packetNodeFactory = packetBufferNodeFactoryMethod(jar, mappings, classMap);
        MethodMatch addNode = packetWriterAddNodeMethod(jar, mappings, classMap);

        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length != 11) continue;
                boolean shape = true;
                for (int i = 0; i < 6; i++) shape &= args[i].getSort() == Type.INT;
                shape &= args[6].getSort() == Type.OBJECT && "java/lang/String".equals(args[6].getInternalName());
                shape &= args[7].getSort() == Type.OBJECT && "java/lang/String".equals(args[7].getInternalName());
                for (int i = 8; i < 11; i++) shape &= args[i].getSort() == Type.INT;
                if (!shape) continue;

                int score = method.instructions.size() / 20;
                if (clientPacket != null) {
                    score += fieldInsnCount(method, clientPacket, null, null, Opcodes.GETSTATIC) * 45;
                }
                if (packetNodeFactory != null) {
                    score += methodCallInsnCount(method, packetNodeFactory.owner.name, packetNodeFactory.method.name, packetNodeFactory.method.desc) * 80;
                }
                if (addNode != null) {
                    score += methodCallInsnCount(method, addNode.owner.name, addNode.method.name, addNode.method.desc) * 80;
                }
                score += fieldInsnCount(method, "client", null, "I", Opcodes.GETSTATIC) * 2;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 120 ? best : null;
    }

    private static MethodMatch packetBufferNodeFactoryMethod(JarModel jar, List<JClass> mappings, Map<String, String> classMap) {
        String clientPacket = mappedName(mappings, classMap, "ClientPacket");
        String isaacCipher = mappedName(mappings, classMap, "IsaacCipher");
        String packetNode = mappedName(mappings, classMap, "PacketBufferNode");
        if (clientPacket == null || isaacCipher == null || packetNode == null) return null;

        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || !Type.getReturnType(method.desc).getDescriptor().equals("L" + packetNode + ";")) {
                    continue;
                }
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length != 3 || args[0].getSort() != Type.OBJECT || args[1].getSort() != Type.OBJECT
                        || !args[0].getInternalName().equals(clientPacket)
                        || !args[1].getInternalName().equals(isaacCipher)
                        || !isGarbageType(args[2])) {
                    continue;
                }
                int score = methodCallCount(jar.classes.get("client"), owner.name, method.name, method.desc) * 100;
                score += fieldInsnCount(method, packetNode, null, null, Opcodes.GETSTATIC) * 10;
                score += fieldInsnCount(method, packetNode, null, null, Opcodes.PUTFIELD) * 10;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return best;
    }

    private static MethodMatch packetWriterAddNodeMethod(JarModel jar, List<JClass> mappings, Map<String, String> classMap) {
        String packetWriter = mappedName(mappings, classMap, "PacketWriter");
        String packetNode = mappedName(mappings, classMap, "PacketBufferNode");
        String packetBuffer = mappedName(mappings, classMap, "PacketBuffer");
        if (packetWriter == null || packetNode == null) return null;
        ClassNode owner = jar.classes.get(packetWriter);
        if (owner == null) return null;

        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : owner.methods) {
            if ("<init>".equals(method.name)) continue;
            if (isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length < 1 || args[0].getSort() != Type.OBJECT || !args[0].getInternalName().equals(packetNode)) continue;
            if (args.length > 2 || (args.length == 2 && !isGarbageType(args[1]))) continue;
            int score = methodCallCount(jar.classes.get("client"), owner.name, method.name, method.desc) * 30;
            score += fieldInsnCount(method, packetNode, null, null, Opcodes.PUTFIELD) * 20;
            if (packetBuffer != null) {
                score += fieldInsnCount(method, packetBuffer, "au", "I", Opcodes.GETFIELD) * 50;
            }
            if (method.name.equals("az")) score += 5;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return best == null ? null : new MethodMatch(owner, best);
    }

    private static MethodMatch processServerPacketMethod(JarModel jar, List<JClass> mappings, Map<String, String> classMap) {
        ClassNode client = jar.classes.get("client");
        String packetWriter = mappedName(mappings, classMap, "PacketWriter");
        String serverPacket = mappedName(mappings, classMap, "ServerPacket");
        String clientPacket = mappedName(mappings, classMap, "ClientPacket");
        MethodMatch packetNodeFactory = packetBufferNodeFactoryMethod(jar, mappings, classMap);
        MethodMatch addNode = packetWriterAddNodeMethod(jar, mappings, classMap);
        if (client == null || packetWriter == null || serverPacket == null) return null;

        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : client.methods) {
            if (Type.getReturnType(method.desc).getSort() != Type.BOOLEAN) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (isStatic(method.access)) {
                if (args.length < 2 || args.length > 3) continue;
                if (args[0].getSort() != Type.OBJECT || !"client".equals(args[0].getInternalName())) continue;
                if (args[1].getSort() != Type.OBJECT || !packetWriter.equals(args[1].getInternalName())) continue;
                if (args.length == 3 && !isGarbageType(args[2])) continue;
            } else {
                if (args.length < 1 || args.length > 2) continue;
                if (args[0].getSort() != Type.OBJECT || !packetWriter.equals(args[0].getInternalName())) continue;
                if (args.length == 2 && !isGarbageType(args[1])) continue;
            }

            int serverPacketRefs = fieldInsnCount(method, packetWriter, null, "L" + serverPacket + ";", Opcodes.GETFIELD)
                    + fieldInsnCount(method, packetWriter, null, "L" + serverPacket + ";", Opcodes.PUTFIELD);
            int serverPacketEnumRefs = fieldInsnCount(method, serverPacket, null, null, Opcodes.GETSTATIC);
            if (serverPacketRefs < 50 || serverPacketEnumRefs < 20) continue;

            int score = method.instructions.size() / 25;
            score += methodCallCount(jar, client.name, method.name, method.desc) * 1000;
            if (packetNodeFactory != null) {
                score += methodCallInsnCount(method, packetNodeFactory.owner.name, packetNodeFactory.method.name, packetNodeFactory.method.desc) * 80;
            }
            if (addNode != null) {
                score += methodCallInsnCount(method, addNode.owner.name, addNode.method.name, addNode.method.desc) * 80;
            }
            if (clientPacket != null) score += fieldInsnCount(method, clientPacket, null, null, Opcodes.GETSTATIC) * 12;
            score += serverPacketRefs * 8;
            score += serverPacketEnumRefs * 10;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return best == null ? null : new MethodMatch(client, best);
    }

    private static MethodMatch processErrorMethod(JarModel jar) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length < 2 || args.length > 3) continue;
                if (args[0].getSort() != Type.OBJECT || !"java/lang/String".equals(args[0].getInternalName())) continue;
                if (args[1].getSort() != Type.OBJECT || !"java/lang/Throwable".equals(args[1].getInternalName())) continue;
                if (args.length == 3 && !isGarbageType(args[2])) continue;

                int score = owner.superName != null && runtimeExceptionAssignable(jar, owner.name) ? 20 : 0;
                score += methodTypeInsnCount(method, "java/io/StringWriter") * 45;
                score += methodTypeInsnCount(method, "java/io/PrintWriter") * 45;
                score += methodTypeInsnCount(method, "java/io/BufferedReader") * 30;
                score += methodTypeInsnCount(method, "java/io/StringReader") * 30;
                score += methodCallInsnCount(method, "java/lang/Throwable", "printStackTrace", "(Ljava/io/PrintWriter;)V") * 80;
                score += stringConstantContains(method, "clienterror.ws") ? 120 : 0;
                score += stringConstantContains(method, "Error:") ? 30 : 0;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 150 ? best : null;
    }

    private static MethodMatch newRunExceptionMethod(JarModel jar) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access)) continue;
                Type returnType = Type.getReturnType(method.desc);
                if (returnType.getSort() != Type.OBJECT || !runtimeExceptionAssignable(jar, returnType.getInternalName())) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length != 2 || args[0].getSort() != Type.OBJECT || args[1].getSort() != Type.OBJECT
                        || !"java/lang/Throwable".equals(args[0].getInternalName())
                        || !"java/lang/String".equals(args[1].getInternalName())) {
                    continue;
                }

                int score = 100;
                score += methodTypeInsnCount(method, returnType.getInternalName()) * 35;
                score += methodCallInsnCount(method, returnType.getInternalName(), "<init>", null) * 50;
                score += fieldInsnCount(method, returnType.getInternalName(), null, "Ljava/lang/String;", Opcodes.GETFIELD) * 20;
                score += fieldInsnCount(method, returnType.getInternalName(), null, "Ljava/lang/String;", Opcodes.PUTFIELD) * 20;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 120 ? best : null;
    }

    private static MethodMatch callStackCombinerMethod(JarModel jar) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || !"Ljava/lang/String;".equals(Type.getReturnType(method.desc).getDescriptor())) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length > 1 || (args.length == 1 && !isGarbageType(args[0]))) continue;

                int score = 0;
                score += methodTypeInsnCount(method, "java/lang/StringBuilder") * 30;
                score += methodCallInsnCount(method, "java/lang/Iterable", "iterator", "()Ljava/util/Iterator;") * 50;
                score += methodCallInsnCount(method, "java/util/Iterator", "hasNext", "()Z") * 35;
                score += methodCallInsnCount(method, "java/util/Iterator", "next", "()Ljava/lang/Object;") * 35;
                score += fieldInsnDescCount(method, "Ljava/lang/String;", Opcodes.GETFIELD) * 20;
                score += stringConstantContains(method, ":") ? 20 : 0;
                score += stringConstantContains(method, "\n") ? 20 : 0;
                score += stringConstantContains(method, method.name + "(") ? 30 : 0;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 120 ? best : null;
    }

    private static MethodMatch loginIndexMethod(JarModel jar) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length < 1 || args.length > 2 || args[0].getSort() != Type.INT) continue;
                if (args.length == 2 && !isGarbageType(args[1])) continue;

                int score = 0;
                score += method.instructions.size() / 5;
                score += fieldInsnCount(method, "client", null, "I", Opcodes.PUTSTATIC) * 35;
                score += fieldInsnCount(method, "client", null, "I", Opcodes.GETSTATIC) * 10;
                score += fieldInsnCount(method, "client", null, "Ljava/lang/String;", Opcodes.PUTSTATIC) * 15;
                score += methodCallInsnCount(method, "java/lang/String", "trim", "()Ljava/lang/String;") * 20;
                score += stringConstantContains(method, "Invalid credentials") ? 100 : 0;
                score += stringConstantContains(method, "Please enter") ? 40 : 0;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 45 ? best : null;
    }

    private static MethodMatch loginIndexSetterMethod(JarModel jar) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length != 2 || args[0].getSort() != Type.INT || args[1].getSort() != Type.BYTE) continue;
                int score = 0;
                score += fieldInsnDescCount(method, "I", Opcodes.PUTSTATIC) * 50;
                score += method.instructions.size() <= 80 ? 40 : -method.instructions.size() / 10;
                score += stringConstantContains(method, method.name + "(") ? 20 : 0;
                score += methodCallCount(jar, owner.name, method.name, method.desc) * 10;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 80 ? best : null;
    }

    private static MethodMatch setLoginResponseMethod(JarModel jar) {
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length != 4
                        || args[0].getSort() != Type.OBJECT || !"java/lang/String".equals(args[0].getInternalName())
                        || args[1].getSort() != Type.OBJECT || !"java/lang/String".equals(args[1].getInternalName())
                        || args[2].getSort() != Type.OBJECT || !"java/lang/String".equals(args[2].getInternalName())
                        || args[3].getSort() != Type.INT) {
                    continue;
                }
                int puts = fieldInsnCount(method, "client", null, "Ljava/lang/String;", Opcodes.PUTSTATIC);
                int score = puts * 100;
                score += method.instructions.size() <= 80 ? 40 : -method.instructions.size() / 10;
                score += methodCallCount(jar, owner.name, method.name, method.desc) * 5;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 250 ? best : null;
    }

    private static MethodMatch constructChatMethod(JarModel jar, List<JClass> mappings, Map<String, String> classMap) {
        String packetNode = mappedName(mappings, classMap, "PacketBufferNode");
        String clientPacket = mappedName(mappings, classMap, "ClientPacket");
        MethodMatch packetNodeFactory = packetBufferNodeFactoryMethod(jar, mappings, classMap);
        if (packetNode == null) return null;

        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                if (!isStatic(method.access) || !("L" + packetNode + ";").equals(Type.getReturnType(method.desc).getDescriptor())) continue;
                Type[] args = Type.getArgumentTypes(method.desc);
                if (args.length < 4 || args[0].getSort() != Type.INT
                        || args[1].getSort() != Type.OBJECT || !"java/lang/String".equals(args[1].getInternalName())) continue;

                int score = 100;
                if (packetNodeFactory != null) {
                    score += methodCallInsnCount(method, packetNodeFactory.owner.name, packetNodeFactory.method.name, packetNodeFactory.method.desc) * 80;
                }
                if (clientPacket != null) {
                    score += fieldInsnCount(method, clientPacket, null, null, Opcodes.GETSTATIC) * 40;
                }
                score += fieldInsnDescCount(method, "Ljava/lang/String;", Opcodes.GETSTATIC) * 8;
                score += methodCallInsnNameContains(method, "write") * 5;
                if (score > bestScore) {
                    bestScore = score;
                    best = new MethodMatch(owner, method);
                }
            }
        }
        return bestScore >= 120 ? best : null;
    }

    private static MethodNode platformInfoWriteMethod(JarModel jar, ClassNode owner, List<JClass> mappings, Map<String, String> classMap) {
        String packetBuffer = mappedName(mappings, classMap, "PacketBuffer");
        String buffer = mappedName(mappings, classMap, "Buffer");
        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : owner.methods) {
            if ("<init>".equals(method.name)) continue;
            if (isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length < 1 || args.length > 2 || args[0].getSort() != Type.OBJECT) continue;
            if (args.length == 2 && !isGarbageType(args[1])) continue;
            String argOwner = args[0].getInternalName();
            if (!Objects.equals(argOwner, packetBuffer) && !Objects.equals(argOwner, buffer)) continue;

            int score = 50;
            score += method.instructions.size() / 8;
            score += methodCallInsnOwnerCount(method, argOwner) * 10;
            score += methodCallInsnNameContains(method, "write") * 5;
            score += fieldInsnDescCount(method, "Ljava/lang/String;", Opcodes.GETFIELD) * 15;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return best;
    }

    private static MethodNode objectCompositionDecodeNextMethod(ClassNode owner, List<JClass> mappings, Map<String, String> classMap) {
        String packetBuffer = mappedName(mappings, classMap, "PacketBuffer");
        String buffer = mappedName(mappings, classMap, "Buffer");
        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : owner.methods) {
            if ("<init>".equals(method.name)) continue;
            if (isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length < 2 || args.length > 3 || args[0].getSort() != Type.OBJECT || args[1].getSort() != Type.INT) continue;
            if (args.length == 3 && !isGarbageType(args[2])) continue;
            String argOwner = args[0].getInternalName();
            if (!Objects.equals(argOwner, packetBuffer) && !Objects.equals(argOwner, buffer)) continue;

            int score = 50;
            score += method.instructions.size() / 8;
            score += methodCallInsnOwnerCount(method, argOwner) * 12;
            score += fieldInsnCount(method, owner.name, null, null, Opcodes.PUTFIELD) * 10;
            score += methodHasOpcode(method, Opcodes.LOOKUPSWITCH) ? 80 : 0;
            score += methodHasOpcode(method, Opcodes.TABLESWITCH) ? 80 : 0;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        if (bestScore >= 120) return best;
        return objectCompositionDecodeConstructor(owner, buffer);
    }

    private static MethodNode objectCompositionDecodeConstructor(ClassNode owner, String buffer) {
        if (buffer == null) return null;
        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length < 2 || args[0].getSort() != Type.OBJECT || !buffer.equals(args[0].getInternalName())) continue;
            if (args[1].getSort() != Type.INT) continue;

            int score = 0;
            score += method.instructions.size() / 10;
            score += methodCallInsnOwnerCount(method, buffer) * 10;
            score += fieldInsnCount(method, owner.name, null, null, Opcodes.PUTFIELD) * 8;
            score += tableSwitchCovers(method, 69) ? 250 : 0;
            score += tableSwitchRangeScore(method);
            score += objectCompositionCase69BufferPopScore(method, buffer) * 80;
            if (args.length >= 3 && args[2].getSort() == Type.BOOLEAN) score += 80;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return bestScore >= 650 ? best : null;
    }

    private static boolean tableSwitchCovers(MethodNode method, int key) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode sw = (TableSwitchInsnNode) insn;
                if (key >= sw.min && key <= sw.max) return true;
            }
        }
        return false;
    }

    private static int tableSwitchRangeScore(MethodNode method) {
        int best = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode sw = (TableSwitchInsnNode) insn;
                int size = sw.max - sw.min + 1;
                int score = Math.min(size, 250);
                if (sw.min <= 2 && sw.max >= 249) score += 120;
                best = Math.max(best, score);
            }
        }
        return best;
    }

    private static int objectCompositionCase69BufferPopScore(MethodNode method, String buffer) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            if (!(insns[i] instanceof TableSwitchInsnNode)) continue;
            TableSwitchInsnNode sw = (TableSwitchInsnNode) insns[i];
            if (69 < sw.min || 69 > sw.max) continue;
            LabelNode label = sw.labels.get(69 - sw.min);
            int start = -1;
            for (int j = 0; j < insns.length; j++) {
                if (insns[j] == label) {
                    start = j;
                    break;
                }
            }
            if (start < 0) continue;
            int score = 0;
            for (int j = start; j < Math.min(insns.length, start + 18); j++) {
                if (insns[j] instanceof MethodInsnNode) {
                    MethodInsnNode min = (MethodInsnNode) insns[j];
                    if (min.owner.equals(buffer) && Type.getReturnType(min.desc).getSort() == Type.INT) score += 2;
                } else if (insns[j].getOpcode() == Opcodes.POP) {
                    score += 2;
                } else if (insns[j] instanceof FieldInsnNode && insns[j].getOpcode() == Opcodes.PUTFIELD) {
                    score -= 2;
                }
            }
            if (score >= 4) return score;
        }
        return 0;
    }

    private static MethodNode midiPcmStreamConstructor(ClassNode owner) {
        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length != 1 || args[0].getSort() != Type.OBJECT) continue;
            int score = method.instructions.size();
            score += fieldInsnCount(method, owner.name, null, null, Opcodes.PUTFIELD) * 20;
            score += methodCallInsnCount(method, "java/util/PriorityQueue", "<init>", "()V") * 40;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return best;
    }

    private static boolean runtimeExceptionAssignable(JarModel jar, String internalName) {
        String name = internalName;
        Set<String> seen = new HashSet<>();
        while (name != null && seen.add(name)) {
            if ("java/lang/RuntimeException".equals(name)) return true;
            if ("java/lang/Exception".equals(name) || "java/lang/Throwable".equals(name) || name.startsWith("java/")) {
                return false;
            }
            ClassNode cn = jar.classes.get(name);
            if (cn == null) return false;
            name = cn.superName;
        }
        return false;
    }

    private static boolean stringConstantContains(MethodNode method, String needle) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof LdcInsnNode) {
                Object cst = ((LdcInsnNode) insn).cst;
                if (cst instanceof String && ((String) cst).contains(needle)) return true;
            }
        }
        return false;
    }

    private static int methodTypeInsnCount(MethodNode method, String typeName) {
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof TypeInsnNode && typeName.equals(((TypeInsnNode) insn).desc)) count++;
        }
        return count;
    }

    private static int fieldInsnDescCount(MethodNode method, String desc, int opcode) {
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof FieldInsnNode && insn.getOpcode() == opcode && desc.equals(((FieldInsnNode) insn).desc)) {
                count++;
            }
        }
        return count;
    }

    private static int methodCallInsnCount(MethodNode method, String owner, String name, String desc) {
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) insn;
                if (min.owner.equals(owner)
                        && (name == null || min.name.equals(name))
                        && (desc == null || min.desc.equals(desc))) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int methodCallInsnOwnerCount(MethodNode method, String owner) {
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode && owner.equals(((MethodInsnNode) insn).owner)) count++;
        }
        return count;
    }

    private static int methodCallInsnNameContains(MethodNode method, String needle) {
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).name.toLowerCase(Locale.ROOT).contains(needle)) count++;
        }
        return count;
    }

    private static int methodCallCount(ClassNode caller, String owner, String name, String desc) {
        if (caller == null) return 0;
        int count = 0;
        for (MethodNode method : caller.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode min = (MethodInsnNode) insn;
                    if (min.owner.equals(owner) && min.name.equals(name) && min.desc.equals(desc)) count++;
                }
            }
        }
        return count;
    }

    private static int methodCallCount(JarModel jar, String owner, String name, String desc) {
        int count = 0;
        for (ClassNode caller : jar.classes.values()) {
            count += methodCallCount(caller, owner, name, desc);
        }
        return count;
    }

    private static int fieldInsnCount(MethodNode method, String owner, String name, String desc, int opcode) {
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof FieldInsnNode && insn.getOpcode() == opcode) {
                FieldInsnNode fin = (FieldInsnNode) insn;
                if (fin.owner.equals(owner)
                        && (name == null || fin.name.equals(name))
                        && (desc == null || fin.desc.equals(desc))) {
                    count++;
                }
            }
        }
        return count;
    }

    private static List<JClass> readMappings(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            java.lang.reflect.Type listType = new TypeToken<List<JClass>>() {}.getType();
            return GSON.fromJson(reader, listType);
        }
    }

    private static List<JClass> buildSkeleton(JarModel jar) {
        List<JClass> out = new ArrayList<>();
        List<ClassNode> classes = new ArrayList<>(jar.classes.values());
        classes.sort(Comparator.comparing(c -> c.name));
        for (ClassNode cn : classes) {
            JClass jc = new JClass();
            jc.setName("");
            jc.setObfuscatedName(cn.name);
            for (FieldNode fn : cn.fields) {
                JField jf = new JField();
                jf.setName("");
                jf.setObfuscatedName(fn.name);
                jf.setOwnerObfuscatedName(cn.name);
                jf.setDescriptor(fn.desc);
                jf.setStatic(isStatic(fn.access));
                jc.getFields().add(jf);
            }
            for (MethodNode mn : cn.methods) {
                JMethod jm = new JMethod();
                jm.setName("");
                jm.setObfuscatedName(mn.name);
                jm.setOwnerObfuscatedName(cn.name);
                jm.setDescriptor(mn.desc);
                jm.setStatic(isStatic(mn.access));
                jc.getMethods().add(jm);
            }
            out.add(jc);
        }
        return out;
    }

    private static JField findOrCreateField(JClass generatedClass, String owner, FieldNode field) {
        for (JField existing : generatedClass.getFields()) {
            if (existing.getOwnerObfuscatedName().equals(owner)
                    && existing.getObfuscatedName().equals(field.name)
                    && existing.getDescriptor().equals(field.desc)
                    && existing.isStatic() == isStatic(field.access)) {
                return existing;
            }
        }
        JField jf = new JField();
        jf.setName("");
        jf.setObfuscatedName(field.name);
        jf.setOwnerObfuscatedName(owner);
        jf.setDescriptor(field.desc);
        jf.setStatic(isStatic(field.access));
        generatedClass.getFields().add(jf);
        return jf;
    }

    private static JMethod findOrCreateMethod(JClass generatedClass, String owner, MethodNode method) {
        for (JMethod existing : generatedClass.getMethods()) {
            if (existing.getOwnerObfuscatedName().equals(owner)
                    && existing.getObfuscatedName().equals(method.name)
                    && existing.getDescriptor().equals(method.desc)
                    && existing.isStatic() == isStatic(method.access)) {
                return existing;
            }
        }
        JMethod jm = new JMethod();
        jm.setName("");
        jm.setObfuscatedName(method.name);
        jm.setOwnerObfuscatedName(owner);
        jm.setDescriptor(method.desc);
        jm.setStatic(isStatic(method.access));
        generatedClass.getMethods().add(jm);
        return jm;
    }

    private static JMethod createMethod(String owner, MethodNode method) {
        JMethod jm = new JMethod();
        jm.setName("");
        jm.setObfuscatedName(method.name);
        jm.setOwnerObfuscatedName(owner);
        jm.setDescriptor(method.desc);
        jm.setStatic(isStatic(method.access));
        return jm;
    }

    private static Map<String, String> matchClasses(JarModel oldJar, JarModel newJar, List<JClass> mappings) {
        Map<String, String> classMap = new LinkedHashMap<>();
        JX_TOKEN_FIELD_OVERRIDES.clear();
        for (String oldName : oldJar.classes.keySet()) {
            if (newJar.classes.containsKey(oldName) && oldName.equals("client")) {
                classMap.put(oldName, oldName);
            }
        }

        seedUniqueClassSignatures(oldJar, newJar, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);

        List<ClassNode> oldClasses = new ArrayList<>(oldJar.classes.values());
        for (int pass = 0; pass < 4; pass++) {
            boolean changed = false;
            Set<String> usedTargets = new HashSet<>(classMap.values());
            for (ClassNode oldClass : oldClasses) {
                if (classMap.containsKey(oldClass.name)) {
                    continue;
                }
                ClassMatch best = null;
                ClassMatch second = null;
                for (ClassNode candidate : newJar.classes.values()) {
                    if (usedTargets.contains(candidate.name)) {
                        continue;
                    }
                    int score = scoreClass(oldClass, candidate, classMap);
                    if (best == null || score > best.score) {
                        second = best;
                        best = new ClassMatch(candidate.name, score);
                    } else if (second == null || score > second.score) {
                        second = new ClassMatch(candidate.name, score);
                    }
                }
                int margin = second == null ? best == null ? 0 : best.score : best.score - second.score;
                if (best != null && best.score >= 140 && margin >= 20) {
                    classMap.put(oldClass.name, best.name);
                    usedTargets.add(best.name);
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        repairNamedClassMappings(oldJar, newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        repairApiInterfaceNamedClasses(newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        repairClassMappingsFromNamedMembers(oldJar, newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        repairPacketClasses(oldJar, newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        repairMissingNamedClasses(oldJar, newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        repairNamedGlobalOwners(oldJar, newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        repairAccountTypeClass(oldJar, newJar, mappings, classMap);
        pinLiteralClientClass(newJar, mappings, classMap);
        return classMap;
    }

    private static void pinLiteralClientClass(JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        if (!newJar.classes.containsKey("client")) return;
        JClass client = namedMapping(mappings, "Client");
        if (client != null) {
            classMap.put(client.getObfuscatedName(), "client");
        } else if (classMap.containsKey("client")) {
            classMap.put("client", "client");
        }
    }

    private static void repairApiInterfaceNamedClasses(JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        Map<String, String> apiAnchors = Map.of(
                "ObjectComposition", "net/runelite/api/ObjectComposition",
                "ItemComposition", "net/runelite/api/ItemComposition",
                "NPCComposition", "net/runelite/api/NPCComposition",
                "Player", "net/runelite/api/Player",
                "GameEngine", "net/runelite/api/GameEngine",
                "Animation", "net/runelite/api/Animation"
        );
        for (Map.Entry<String, String> anchor : apiAnchors.entrySet()) {
            JClass mapped = namedMapping(mappings, anchor.getKey());
            if (mapped == null) continue;
            ClassNode candidate = apiInterfaceImplementation(newJar, anchor.getValue(), anchor.getKey());
            if (candidate != null) {
                classMap.put(mapped.getObfuscatedName(), candidate.name);
            }
        }
    }

    private static ClassNode apiInterfaceImplementation(JarModel jar, String apiInterface, String mappingName) {
        String internalName = apiInterface.replace('.', '/');
        List<ClassNode> candidates = jar.classes.values().stream()
                .filter(c -> c.interfaces.contains(internalName))
                .filter(c -> !"client".equals(c.name))
                .collect(Collectors.toList());
        if (candidates.isEmpty()) return null;
        if (candidates.size() == 1) return candidates.get(0);
        return candidates.stream()
                .max(Comparator.comparingInt(c -> apiInterfaceScore(c, mappingName)))
                .orElse(null);
    }

    private static int apiInterfaceScore(ClassNode candidate, String mappingName) {
        int score = 0;
        if ("ObjectComposition".equals(mappingName)) {
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getActions") && m.desc.equals("()[Ljava/lang/String;"))) score += 100;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getImpostor") && m.desc.equals("()Lnet/runelite/api/ObjectComposition;"))) score += 100;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getSizeX") && m.desc.equals("()I"))) score += 30;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getSizeY") && m.desc.equals("()I"))) score += 30;
            score += (int) candidate.fields.stream().filter(f -> !isStatic(f.access) && f.desc.equals("[Ljava/lang/String;")).count() * 20;
            score += (int) candidate.methods.stream().filter(m -> !isStatic(m.access) && m.desc.startsWith("(L") && m.desc.endsWith("I)V")).count() * 10;
        } else if ("ItemComposition".equals(mappingName)) {
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getName") && m.desc.equals("()Ljava/lang/String;"))) score += 50;
            score += (int) candidate.fields.stream().filter(f -> !isStatic(f.access) && f.desc.equals("[Ljava/lang/String;")).count() * 20;
        } else if ("NPCComposition".equals(mappingName)) {
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getActions") && m.desc.equals("()[Ljava/lang/String;"))) score += 50;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getName") && m.desc.equals("()Ljava/lang/String;"))) score += 30;
        } else if ("Player".equals(mappingName)) {
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getName") && m.desc.equals("()Ljava/lang/String;"))) score += 50;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getPlayerComposition"))) score += 50;
            score += (int) candidate.fields.stream().filter(f -> !isStatic(f.access) && f.desc.equals("[Ljava/lang/String;")).count() * 20;
        } else if ("GameEngine".equals(mappingName)) {
            if (candidate.interfaces.contains("java/lang/Runnable")) score += 50;
            if (candidate.interfaces.contains("java/awt/event/FocusListener")) score += 30;
            if (candidate.interfaces.contains("java/awt/event/WindowListener")) score += 30;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("run") && m.desc.equals("()V"))) score += 50;
        } else if ("Animation".equals(mappingName)) {
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getFrameStep") && m.desc.equals("()I"))) score += 100;
            if (candidate.methods.stream().anyMatch(m -> m.name.equals("getRestartMode") && m.desc.equals("()I"))) score += 50;
        }
        return score;
    }

    private static void repairNamedGlobalOwners(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        Set<String> used = new HashSet<>(classMap.values());
        for (JClass mapped : mappings) {
            if (!blank(mapped.getName())) continue;
            boolean hasNamed = mapped.getFields().stream().anyMatch(f -> !blank(f.getName()))
                    || mapped.getMethods().stream().anyMatch(m -> !blank(m.getName()));
            if (!hasNamed || classMap.containsKey(mapped.getObfuscatedName())) continue;
            ClassNode oldClass = oldJar.classes.get(mapped.getObfuscatedName());
            if (oldClass == null) continue;
            ClassMatch best = null;
            for (ClassNode candidate : newJar.classes.values()) {
                if (used.contains(candidate.name)) continue;
                int score = scoreClass(oldClass, candidate, classMap);
                score += scoreNamedGlobalMembers(mapped, oldClass, candidate, classMap);
                if (best == null || score > best.score) {
                    best = new ClassMatch(candidate.name, score);
                }
            }
            if (best != null && best.score >= 20) {
                classMap.put(mapped.getObfuscatedName(), best.name);
                used.add(best.name);
            }
        }
    }

    private static int scoreNamedGlobalMembers(JClass mapped, ClassNode oldClass, ClassNode candidate, Map<String, String> classMap) {
        int score = 0;
        for (JField field : mapped.getFields()) {
            if (blank(field.getName())) continue;
            for (FieldNode candidateField : candidate.fields) {
                if (candidateField.name.equals(field.getObfuscatedName())) score += 25;
                if (candidateField.desc.equals(field.getDescriptor())) score += 5;
                if (isStatic(candidateField.access) == field.isStatic()
                        && fieldDescriptorCompatible(field.getDescriptor(), candidateField.desc, classMap)) {
                    score += 15;
                }
            }
        }
        for (JMethod method : mapped.getMethods()) {
            if (blank(method.getName())) continue;
            for (MethodNode candidateMethod : candidate.methods) {
                if (candidateMethod.name.equals(method.getObfuscatedName())) score += 25;
                if (compatibleMethodDescriptor(method.getDescriptor(), candidateMethod.desc, classMap)) score += 15;
            }
        }
        return score;
    }

    private static void repairMissingNamedClasses(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        Set<String> used = mappings.stream()
                .filter(m -> !blank(m.getName()))
                .map(m -> classMap.get(m.getObfuscatedName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (JClass mapped : mappings) {
            if (blank(mapped.getName()) || classMap.containsKey(mapped.getObfuscatedName())) continue;
            ClassNode oldClass = oldJar.classes.get(mapped.getObfuscatedName());
            if (oldClass == null) continue;
            ClassMatch best = null;
            for (ClassNode candidate : newJar.classes.values()) {
                if (used.contains(candidate.name)) continue;
                int score = scoreNamedClass(mapped, oldClass, candidate, classMap);
                if (hasMethodNamed(oldClass, "run", "()V") && hasMethodNamed(candidate, "run", "()V")) score += 100;
                if (best == null || score > best.score) best = new ClassMatch(candidate.name, score);
            }
            if (best != null && (best.score >= 30 || "Language".equals(mapped.getName()))) {
                classMap.put(mapped.getObfuscatedName(), best.name);
                used.add(best.name);
            }
        }
    }

    private static boolean hasMethodNamed(ClassNode owner, String name, String desc) {
        return owner.methods.stream().anyMatch(m -> m.name.equals(name) && m.desc.equals(desc));
    }

    private static void repairAccountTypeClass(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        JClass accountType = namedMapping(mappings, "AccountType");
        if (accountType == null) return;
        ACCOUNT_TYPE_FIELD_OVERRIDES.clear();
        String anchored = findAccountTypeFromClientCheck(newJar);
        if (anchored != null) {
            classMap.put(accountType.getObfuscatedName(), anchored);
            repairAccountTypeFields(oldJar, newJar, mappings, classMap);
            return;
        }
        Set<String> usedByOtherNamed = mappings.stream()
                .filter(m -> !blank(m.getName()) && !"AccountType".equals(m.getName()))
                .map(m -> classMap.get(m.getObfuscatedName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode candidate : newJar.classes.values()) {
            if (usedByOtherNamed.contains(candidate.name) || candidate.interfaces.isEmpty()) continue;
            List<FieldNode> selfFields = staticSelfFields(candidate);
            if (selfFields.size() < 2) continue;
            int instanceInts = 0;
            for (FieldNode field : candidate.fields) {
                if (!isStatic(field.access) && field.desc.equals("I")) instanceInts++;
            }
            int score = selfFields.size() * 20 + instanceInts * 10 + candidate.interfaces.size() * 10;
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        if (best != null) {
            classMap.put(accountType.getObfuscatedName(), best.name);
            repairAccountTypeFields(oldJar, newJar, mappings, classMap);
        }
    }

    private static List<FieldNode> staticSelfFields(ClassNode owner) {
        if (owner == null) return List.of();
        return owner.fields.stream()
                .filter(f -> isStatic(f.access) && f.desc.equals("L" + owner.name + ";"))
                .collect(Collectors.toList());
    }

    private static String findAccountTypeFromClientCheck(JarModel jar) {
        ClassNode client = jar.classes.get("client");
        if (client == null) return null;
        String bestType = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            String type = field.desc.substring(1, field.desc.length() - 1);
            ClassNode enumClass = jar.classes.get(type);
            if (!looksLikeAccountTypeEnum(enumClass)) continue;
            int score = accountTypeCheckFieldScore(client, enumClass, field);
            if (score > bestScore) {
                bestScore = score;
                bestType = type;
            }
        }
        return bestScore >= 20 ? bestType : null;
    }

    private static FieldNode findAccountTypeCheckField(JarModel jar) {
        ClassNode client = jar.classes.get("client");
        if (client == null) return null;
        FieldNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            String type = field.desc.substring(1, field.desc.length() - 1);
            ClassNode enumClass = jar.classes.get(type);
            if (!looksLikeAccountTypeEnum(enumClass)) continue;
            int score = accountTypeCheckFieldScore(client, enumClass, field);
            if (score > bestScore) {
                bestScore = score;
                best = field;
            }
        }
        return bestScore >= 20 ? best : null;
    }

    private static boolean looksLikeAccountTypeEnum(ClassNode owner) {
        if (owner == null || owner.interfaces.isEmpty()) return false;
        int selfCount = staticSelfFields(owner).size();
        if (selfCount < 2 || selfCount > 12) return false;
        int instanceInts = 0;
        for (FieldNode field : owner.fields) {
            if (!isStatic(field.access) && field.desc.equals("I")) instanceInts++;
        }
        return instanceInts >= 2;
    }

    private static int accountTypeCheckFieldScore(ClassNode client, ClassNode enumClass, FieldNode checkField) {
        int score = 0;
        boolean hasBooleanCheck = enumClass.methods.stream()
                .anyMatch(m -> Type.getReturnType(m.desc).getSort() == Type.BOOLEAN
                        && (Type.getArgumentTypes(m.desc).length == 0
                        || Arrays.stream(Type.getArgumentTypes(m.desc)).allMatch(MappingGenerator::isGarbageType)));
        if (hasBooleanCheck) score += 200;
        for (MethodNode method : client.methods) {
            boolean assignsCheck = false;
            int enumConstants = 0;
            int booleanChecks = 0;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (insns[i] instanceof FieldInsnNode) {
                    FieldInsnNode fin = (FieldInsnNode) insns[i];
                    if (fin.owner.equals(enumClass.name) && fin.desc.equals("L" + enumClass.name + ";") && insns[i].getOpcode() == Opcodes.GETSTATIC) {
                        enumConstants++;
                    }
                    if (fin.owner.equals(client.name) && fin.name.equals(checkField.name) && fin.desc.equals(checkField.desc) && insns[i].getOpcode() == Opcodes.PUTSTATIC) {
                        assignsCheck = true;
                    }
                    if (fin.owner.equals(client.name) && fin.name.equals(checkField.name) && fin.desc.equals(checkField.desc) && insns[i].getOpcode() == Opcodes.GETSTATIC) {
                        for (int j = i + 1; j < Math.min(insns.length, i + 6); j++) {
                            if (insns[j] instanceof MethodInsnNode) {
                                MethodInsnNode min = (MethodInsnNode) insns[j];
                                if (min.owner.equals(enumClass.name) && Type.getReturnType(min.desc).getSort() == Type.BOOLEAN) {
                                    booleanChecks++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (assignsCheck && enumConstants > 0) score += 20 + enumConstants * 5;
            score += booleanChecks * 50;
        }
        return score;
    }

    private static void repairAccountTypeFields(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        JClass accountType = namedMapping(mappings, "AccountType");
        if (accountType == null) return;
        String oldEnum = findAccountTypeEnumOnJar(oldJar, mappings);
        String newEnum = classMap.get(accountType.getObfuscatedName());
        if (oldEnum == null || newEnum == null) return;

        String oldLegacy = resolveOldNamedAccountTypeConstant(mappings, oldEnum, "legacyType");
        String oldJagex = resolveOldNamedAccountTypeConstant(mappings, oldEnum, "jagexType");
        Map<String, Set<String>> oldRefs = enumConstantRefSets(oldJar, oldEnum);
        Map<String, Set<String>> newRefs = enumConstantRefSets(newJar, newEnum);

        String newLegacy = oldLegacy == null ? null : bestRefSetMatch(oldRefs.get(oldLegacy), newRefs);
        String newJagex = oldJagex == null ? null : bestRefSetMatch(oldRefs.get(oldJagex), newRefs);
        if (newLegacy == null || newJagex == null || Objects.equals(newLegacy, newJagex)) {
            Map<String, AccountTypeRefFeatures> oldFeatures = accountTypeRefFeatures(oldJar, oldEnum);
            Map<String, AccountTypeRefFeatures> newFeatures = accountTypeRefFeatures(newJar, newEnum);
            String legacyByFeatures = bestAccountTypeFeatureMatch(oldFeatures.get(oldLegacy), newFeatures, null);
            String jagexByFeatures = bestAccountTypeFeatureMatch(oldFeatures.get(oldJagex), newFeatures, legacyByFeatures);
            if (legacyByFeatures != null && jagexByFeatures != null && !legacyByFeatures.equals(jagexByFeatures)) {
                newLegacy = legacyByFeatures;
                newJagex = jagexByFeatures;
            } else {
                Map<String, EnumConstant> constants = enumConstants(newJar.classes.get(newEnum));
                String legacyByShape = constantByArgs(constants, 2, 0);
                String jagexByShape = constantByArgs(constants, 1, 1);
                if (legacyByShape != null && jagexByShape != null && !legacyByShape.equals(jagexByShape)) {
                    newLegacy = legacyByShape;
                    newJagex = jagexByShape;
                }
            }
        }
        if (newLegacy != null && newJagex != null && newLegacy.equals(newJagex)) {
            newLegacy = null;
            newJagex = null;
        }
        if (newLegacy != null) ACCOUNT_TYPE_FIELD_OVERRIDES.put("legacyType", newLegacy);
        if (newJagex != null) ACCOUNT_TYPE_FIELD_OVERRIDES.put("jagexType", newJagex);
        if (Boolean.getBoolean("mapping.debug.accounttype")) {
            System.out.printf("[MappingGenerator] AccountType class old=%s new=%s legacy %s->%s jagex %s->%s%n",
                    oldEnum, newEnum, oldLegacy, newLegacy, oldJagex, newJagex);
        }
    }

    private static String findAccountTypeEnumOnJar(JarModel jar, List<JClass> mappings) {
        JClass clientMapping = namedMapping(mappings, "Client");
        if (clientMapping == null) return null;
        JField check = clientMapping.getFields().stream()
                .filter(f -> "accountTypeCheck".equals(f.getName()))
                .findFirst()
                .orElse(null);
        if (check == null || !check.getDescriptor().startsWith("L") || !check.getDescriptor().endsWith(";")) return null;
        ClassNode client = jar.classes.get(clientMapping.getObfuscatedName());
        FieldNode field = client == null ? null : findField(client, check.getObfuscatedName(), check.getDescriptor(), true);
        String type = field == null ? check.getDescriptor().substring(1, check.getDescriptor().length() - 1)
                : field.desc.substring(1, field.desc.length() - 1);
        return jar.classes.containsKey(type) ? type : null;
    }

    private static String resolveOldNamedAccountTypeConstant(List<JClass> mappings, String oldEnumName, String deobName) {
        JClass accountType = namedMapping(mappings, "AccountType");
        if (accountType == null) return null;
        for (JField field : accountType.getFields()) {
            if (deobName.equals(field.getName()) && field.isStatic()
                    && oldEnumName.equals(field.getOwnerObfuscatedName())
                    && ("L" + oldEnumName + ";").equals(field.getDescriptor())) {
                return field.getObfuscatedName();
            }
        }
        return null;
    }

    private static Map<String, Set<String>> enumConstantRefSets(JarModel jar, String enumOwner) {
        Map<String, Set<String>> out = new TreeMap<>();
        ClassNode owner = jar.classes.get(enumOwner);
        if (owner == null) return out;
        for (FieldNode field : staticSelfFields(owner)) {
            out.put(field.name, refSetForField(jar, enumOwner, field.name, field.desc));
        }
        return out;
    }

    private static Set<String> refSetForField(JarModel jar, String owner, String field, String desc) {
        Set<String> refs = new TreeSet<>();
        for (ClassNode cn : jar.classes.values()) {
            for (MethodNode method : cn.methods) {
                boolean touches = false;
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof FieldInsnNode) {
                        FieldInsnNode fin = (FieldInsnNode) insn;
                        if (insn.getOpcode() == Opcodes.GETSTATIC && fin.owner.equals(owner) && fin.name.equals(field) && fin.desc.equals(desc)) {
                            touches = true;
                            break;
                        }
                    }
                }
                if (touches) refs.add(methodShapeKey(method));
            }
        }
        return refs;
    }

    private static String methodShapeKey(MethodNode method) {
        StringBuilder out = new StringBuilder(method.desc).append('|');
        int count = 0;
        for (AbstractInsnNode insn : method.instructions) {
            int opcode = insn.getOpcode();
            if (opcode < 0) continue;
            out.append(opcode).append(',');
            if (insn instanceof LdcInsnNode) {
                Object cst = ((LdcInsnNode) insn).cst;
                if (cst instanceof String) out.append('S').append(((String) cst).length()).append(',');
                else if (cst instanceof Integer) out.append('I').append(Integer.signum((Integer) cst)).append(',');
            } else if (insn instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) insn;
                out.append(min.desc).append(',');
            }
            if (++count > 220) break;
        }
        return out.toString();
    }

    private static String bestRefSetMatch(Set<String> oldRefs, Map<String, Set<String>> newRefSets) {
        if (oldRefs == null || oldRefs.isEmpty()) return null;
        String best = null;
        int bestScore = -1;
        for (Map.Entry<String, Set<String>> entry : newRefSets.entrySet()) {
            int intersection = intersectionSize(oldRefs, entry.getValue());
            int union = oldRefs.size() + entry.getValue().size() - intersection;
            int score = union == 0 ? 0 : (intersection * 100) / union;
            if (score > bestScore) {
                bestScore = score;
                best = entry.getKey();
            }
        }
        return bestScore >= 20 ? best : null;
    }

    private static int intersectionSize(Set<String> left, Set<String> right) {
        int count = 0;
        for (String value : left) {
            if (right.contains(value)) count++;
        }
        return count;
    }

    private static Map<String, EnumConstant> enumConstants(ClassNode owner) {
        Map<String, EnumConstant> out = new LinkedHashMap<>();
        if (owner == null) return out;
        for (MethodNode method : owner.methods) {
            if (!"<clinit>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTSTATIC) continue;
                FieldInsnNode put = (FieldInsnNode) insns[i];
                if (!put.owner.equals(owner.name) || !put.desc.equals("L" + owner.name + ";")) continue;
                List<Integer> ints = new ArrayList<>();
                for (int j = Math.max(0, i - 10); j < i; j++) {
                    Integer value = intConstant(insns[j]);
                    if (value != null) ints.add(value);
                }
                if (!ints.isEmpty()) {
                    int arg2 = ints.get(ints.size() - 1);
                    int arg1 = ints.size() >= 2 ? ints.get(ints.size() - 2) : arg2;
                    out.put(put.name, new EnumConstant(owner.name, put.name, arg1, arg2));
                }
            }
        }
        return out;
    }

    private static String constantByArgs(Map<String, EnumConstant> constants, int arg1, int arg2) {
        for (EnumConstant constant : constants.values()) {
            if (constant.arg1 == arg1 && constant.arg2 == arg2) return constant.fieldName;
        }
        return null;
    }

    private static Integer intConstant(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) return opcode - Opcodes.ICONST_0;
        if (insn instanceof IntInsnNode) return ((IntInsnNode) insn).operand;
        if (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof Integer) {
            return (Integer) ((LdcInsnNode) insn).cst;
        }
        return null;
    }

    private static void repairPacketClasses(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        JClass oldClientPacket = namedMapping(mappings, "ClientPacket");
        JClass oldServerPacket = namedMapping(mappings, "ServerPacket");
        JClass oldPacketBuffer = namedMapping(mappings, "PacketBuffer");
        JClass oldPacketBufferNode = namedMapping(mappings, "PacketBufferNode");
        JClass oldPacketWriter = namedMapping(mappings, "PacketWriter");
        JClass oldIsaacCipher = namedMapping(mappings, "IsaacCipher");

        ClassNode writer = oldPacketWriter == null ? null : newJar.classes.get(classMap.get(oldPacketWriter.getObfuscatedName()));
        if (writer == null && oldPacketWriter != null) {
            writer = findWriterFromClientField(newJar);
            if (writer != null) classMap.put(oldPacketWriter.getObfuscatedName(), writer.name);
        }

        MethodNode addNode = writer == null ? null : findPacketWriterAddNode(writer, newJar);
        String packetNodeName = objectArgument(addNode);
        if (oldPacketBufferNode != null && packetNodeName != null) {
            classMap.put(oldPacketBufferNode.getObfuscatedName(), packetNodeName);
        }

        ClassNode packetNode = packetNodeName == null ? null : newJar.classes.get(packetNodeName);
        String isaacName = oldIsaacCipher == null ? null : classMap.get(oldIsaacCipher.getObfuscatedName());
        String packetBufferName = findPacketBufferFromNode(packetNode, newJar, isaacName);
        if (oldPacketBuffer != null && packetBufferName != null) {
            classMap.put(oldPacketBuffer.getObfuscatedName(), packetBufferName);
        }

        String clientPacketName = findClientPacketFromNode(packetNode, packetBufferName, newJar);
        if (oldClientPacket != null && clientPacketName != null) {
            classMap.put(oldClientPacket.getObfuscatedName(), clientPacketName);
        }

        String serverPacketName = findServerPacketFromWriter(writer, clientPacketName, newJar);
        if (oldServerPacket != null && serverPacketName != null) {
            classMap.put(oldServerPacket.getObfuscatedName(), serverPacketName);
        }
    }

    private static JClass namedMapping(List<JClass> mappings, String name) {
        if (blank(name)) return null;
        return mappings.stream().filter(m -> name.equals(m.getName())).findFirst().orElse(null);
    }

    private static ClassNode findPacketEnumCandidate(JarModel jar, boolean clientPacket) {
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode cn : jar.classes.values()) {
            long selfStaticFields = cn.fields.stream()
                    .filter(f -> isStatic(f.access) && f.desc.equals("L" + cn.name + ";"))
                    .count();
            long instanceInts = cn.fields.stream()
                    .filter(f -> !isStatic(f.access) && f.desc.equals("I"))
                    .count();
            if (selfStaticFields < 20 || instanceInts < 2) continue;
            int score = (int) selfStaticFields + (int) instanceInts * 10;
            for (MethodNode mn : cn.methods) {
                if ("<init>".equals(mn.name)) {
                    Type[] args = Type.getArgumentTypes(mn.desc);
                    long intArgs = Arrays.stream(args).filter(t -> t.getSort() == Type.INT).count();
                    if (intArgs >= 2) score += 50;
                }
            }
            long referencesFromClient = referencesToOwner(jar.classes.get("client"), cn.name);
            long referencesFromWriter = jar.classes.values().stream()
                    .filter(c -> c.name.equals("df") || c.name.equals("wl"))
                    .mapToLong(c -> referencesToOwner(c, cn.name))
                    .sum();
            score += (int) (clientPacket ? referencesFromClient * 20 : referencesFromWriter * 20);
            if (score > bestScore) {
                bestScore = score;
                best = cn;
            }
        }
        return best;
    }

    private static ClassNode findWriterFromClientField(JarModel jar) {
        ClassNode client = jar.classes.get("client");
        if (client == null) return null;
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            ClassNode candidate = jar.classes.get(field.desc.substring(1, field.desc.length() - 1));
            if (candidate == null) continue;
            int score = 0;
            for (MethodNode method : candidate.methods) {
                if (findSingleObjectArg(method) != null && Type.getReturnType(method.desc).getSort() == Type.VOID) {
                    score += 50;
                }
            }
            score += candidate.fields.size();
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return bestScore >= 50 ? best : null;
    }

    private static MethodNode findPacketWriterAddNode(ClassNode writer, JarModel jar) {
        Set<String> writerPacketBufferTypes = packetBufferLikeFieldTypes(writer, jar);
        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : writer.methods) {
            String arg = findSingleObjectArg(method);
            if (arg == null || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
            if (arg.equals(writer.name)) continue;
            ClassNode argClass = jar.classes.get(arg);
            if (argClass == null) continue;
            long objectFields = argClass.fields.stream()
                    .filter(f -> !isStatic(f.access) && f.desc.startsWith("L") && f.desc.endsWith(";"))
                    .count();
            int score = (int) objectFields * 50 + method.instructions.size();
            for (String bufferType : writerPacketBufferTypes) {
                if (argClass.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("L" + bufferType + ";"))) {
                    score += 500;
                }
                score += fieldInsnCount(method, argClass.name, null, "L" + bufferType + ";", Opcodes.GETFIELD) * 250;
                score += fieldInsnCount(method, argClass.name, null, "L" + bufferType + ";", Opcodes.PUTFIELD) * 250;
            }
            score += fieldInsnCount(method, argClass.name, null, "I", Opcodes.GETFIELD) * 25;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return bestScore >= 100 ? best : null;
    }

    private static Map<String, AccountTypeRefFeatures> accountTypeRefFeatures(JarModel jar, String enumOwner) {
        Map<String, AccountTypeRefFeatures> out = new LinkedHashMap<>();
        ClassNode owner = jar.classes.get(enumOwner);
        if (owner == null) return out;
        for (FieldNode field : staticSelfFields(owner)) {
            AccountTypeRefFeatures features = new AccountTypeRefFeatures();
            for (ClassNode cn : jar.classes.values()) {
                for (MethodNode method : cn.methods) {
                    boolean touches = false;
                    for (AbstractInsnNode insn : method.instructions) {
                        if (insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.GETSTATIC) {
                            FieldInsnNode fin = (FieldInsnNode) insn;
                            if (fin.owner.equals(enumOwner) && fin.name.equals(field.name) && fin.desc.equals(field.desc)) {
                                touches = true;
                                break;
                            }
                        }
                    }
                    if (!touches) continue;
                    if (cn.name.equals(enumOwner)) features.enumRefs++;
                    else if (cn.name.equals("client")) features.clientRefs++;
                    else features.otherRefs++;
                    if ("<clinit>".equals(method.name)) features.clinitRefs++;
                }
            }
            out.put(field.name, features);
        }
        return out;
    }

    private static String bestAccountTypeFeatureMatch(AccountTypeRefFeatures oldFeature,
                                                      Map<String, AccountTypeRefFeatures> newFeatures,
                                                      String excludedField) {
        if (oldFeature == null) return null;
        String best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Map.Entry<String, AccountTypeRefFeatures> entry : newFeatures.entrySet()) {
            if (entry.getKey().equals(excludedField)) continue;
            AccountTypeRefFeatures candidate = entry.getValue();
            int score = 1000;
            score -= Math.abs(oldFeature.enumRefs - candidate.enumRefs) * 180;
            score -= Math.abs(oldFeature.otherRefs - candidate.otherRefs) * 140;
            score -= Math.abs(oldFeature.clientRefs - candidate.clientRefs) * 40;
            score -= Math.abs(oldFeature.clinitRefs - candidate.clinitRefs) * 220;
            if ((oldFeature.enumRefs == 0) == (candidate.enumRefs == 0)) score += 120;
            if ((oldFeature.otherRefs == 0) == (candidate.otherRefs == 0)) score += 100;
            if ((oldFeature.clinitRefs > 0) == (candidate.clinitRefs > 0)) score += 160;
            if (score > bestScore) {
                bestScore = score;
                best = entry.getKey();
            }
        }
        return bestScore >= 500 ? best : null;
    }

    private static Set<String> packetBufferLikeFieldTypes(ClassNode writer, JarModel jar) {
        Set<String> out = new LinkedHashSet<>();
        if (writer == null) return out;
        for (FieldNode field : writer.fields) {
            if (isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            String type = field.desc.substring(1, field.desc.length() - 1);
            ClassNode candidate = jar.classes.get(type);
            if (candidate == null || packetEnumScore(candidate) >= 50) continue;
            boolean hasByteArray = candidate.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("[B"));
            boolean hasCipherLikeField = candidate.fields.stream()
                    .filter(f -> !isStatic(f.access) && f.desc.startsWith("L") && f.desc.endsWith(";"))
                    .map(f -> jar.classes.get(f.desc.substring(1, f.desc.length() - 1)))
                    .filter(Objects::nonNull)
                    .anyMatch(c -> c.fields.stream().filter(cf -> !isStatic(cf.access) && cf.desc.equals("I")).count() >= 2);
            if (hasByteArray || hasCipherLikeField) {
                out.add(type);
            }
        }
        return out;
    }

    private static String objectArgument(MethodNode method) {
        return findSingleObjectArg(method);
    }

    private static String findSingleObjectArg(MethodNode method) {
        if (method == null) return null;
        Type[] args = Type.getArgumentTypes(method.desc);
        String found = null;
        for (Type arg : args) {
            if (arg.getSort() == Type.OBJECT) {
                if (found != null) return null;
                found = arg.getInternalName();
            }
        }
        return found;
    }

    private static String findPacketBufferFromNode(ClassNode node, JarModel jar, String isaacName) {
        if (node == null) return null;
        for (FieldNode field : node.fields) {
            if (isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            String type = field.desc.substring(1, field.desc.length() - 1);
            ClassNode candidate = jar.classes.get(type);
            if (candidate == null) continue;
            boolean hasIsaac = isaacName != null && candidate.fields.stream()
                    .anyMatch(f -> !isStatic(f.access) && f.desc.equals("L" + isaacName + ";"));
            boolean hasByteArray = candidate.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("[B"));
            boolean packetEnum = packetEnumScore(candidate) >= 50;
            if (hasIsaac || (hasByteArray && !packetEnum)) return type;
        }
        return null;
    }

    private static String findClientPacketFromNode(ClassNode node, String packetBufferName, JarModel jar) {
        if (node == null) return null;
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : node.fields) {
            if (isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            String type = field.desc.substring(1, field.desc.length() - 1);
            if (type.equals(packetBufferName)) continue;
            ClassNode candidate = jar.classes.get(type);
            if (candidate == null) continue;
            int score = packetEnumScore(candidate);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return bestScore >= 50 ? best.name : null;
    }

    private static String findServerPacketFromWriter(ClassNode writer, String clientPacketName, JarModel jar) {
        if (writer == null) return null;
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : writer.fields) {
            if (isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";")) continue;
            String type = field.desc.substring(1, field.desc.length() - 1);
            if (type.equals(clientPacketName)) continue;
            ClassNode candidate = jar.classes.get(type);
            if (candidate == null) continue;
            int score = packetEnumScore(candidate);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return bestScore >= 50 ? best.name : null;
    }

    private static int packetEnumScore(ClassNode cn) {
        long selfStaticFields = cn.fields.stream()
                .filter(f -> isStatic(f.access) && f.desc.equals("L" + cn.name + ";"))
                .count();
        long instanceInts = cn.fields.stream()
                .filter(f -> !isStatic(f.access) && f.desc.equals("I"))
                .count();
        int score = (int) selfStaticFields + (int) instanceInts * 20;
        for (MethodNode mn : cn.methods) {
            if ("<init>".equals(mn.name)) {
                long intArgs = Arrays.stream(Type.getArgumentTypes(mn.desc))
                        .filter(t -> t.getSort() == Type.INT)
                        .count();
                score += (int) intArgs * 20;
            }
        }
        return score;
    }

    private static ClassNode findPacketBufferCandidate(JarModel jar, String isaacCipherName) {
        if (isaacCipherName == null) return null;
        for (ClassNode cn : jar.classes.values()) {
            boolean hasIsaac = cn.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("L" + isaacCipherName + ";"));
            boolean hasByteArray = cn.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("[B"));
            if (hasIsaac && hasByteArray) return cn;
        }
        for (ClassNode cn : jar.classes.values()) {
            boolean hasIsaac = cn.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("L" + isaacCipherName + ";"));
            if (hasIsaac) return cn;
        }
        return null;
    }

    private static ClassNode findClassWithInstanceFields(JarModel jar, Set<String> fieldTypes) {
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode cn : jar.classes.values()) {
            int score = 0;
            for (String type : fieldTypes) {
                if (cn.fields.stream().anyMatch(f -> !isStatic(f.access) && f.desc.equals("L" + type + ";"))) {
                    score += 100;
                }
            }
            score -= Math.abs(cn.fields.size() - 12);
            if (score > bestScore) {
                bestScore = score;
                best = cn;
            }
        }
        return bestScore >= fieldTypes.size() * 100 ? best : null;
    }

    private static MethodNode findMethodTaking(ClassNode owner, String argType) {
        if (owner == null || argType == null) return null;
        for (MethodNode mn : owner.methods) {
            for (Type arg : Type.getArgumentTypes(mn.desc)) {
                if (arg.getSort() == Type.OBJECT && arg.getInternalName().equals(argType)) {
                    return mn;
                }
            }
        }
        return null;
    }

    private static ClassNode findClassWithMethodTaking(JarModel jar, String argType) {
        if (argType == null) return null;
        ClassNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (ClassNode cn : jar.classes.values()) {
            int score = 0;
            for (MethodNode mn : cn.methods) {
                for (Type arg : Type.getArgumentTypes(mn.desc)) {
                    if (arg.getSort() == Type.OBJECT && arg.getInternalName().equals(argType)) score += 100;
                }
            }
            score += cn.fields.size();
            if (score > bestScore) {
                bestScore = score;
                best = cn;
            }
        }
        return bestScore >= 100 ? best : null;
    }

    private static long referencesToOwner(ClassNode cn, String owner) {
        if (cn == null || owner == null) return 0;
        long refs = 0;
        for (FieldNode f : cn.fields) {
            if (f.desc.equals("L" + owner + ";") || f.desc.equals("[L" + owner + ";")) refs++;
        }
        for (MethodNode mn : cn.methods) {
            if (mn.desc.contains("L" + owner + ";")) refs++;
            for (AbstractInsnNode insn : mn.instructions) {
                if (insn instanceof FieldInsnNode && ((FieldInsnNode) insn).owner.equals(owner)) refs++;
                if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals(owner)) refs++;
            }
        }
        return refs;
    }

    private static void repairClassMappingsFromNamedMembers(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        for (int pass = 0; pass < 3; pass++) {
            boolean changed = false;
            for (JClass mapped : mappings) {
                if (blank(mapped.getName())) continue;
                String newClassName = classMap.get(mapped.getObfuscatedName());
                ClassNode oldClass = oldJar.classes.get(mapped.getObfuscatedName());
                ClassNode newClass = newClassName == null ? null : newJar.classes.get(newClassName);
                if (oldClass == null || newClass == null) continue;

                for (JField oldField : mapped.getFields()) {
                    if (blank(oldField.getName())) continue;
                    String oldOwnerName = blank(oldField.getOwnerObfuscatedName()) ? mapped.getObfuscatedName() : oldField.getOwnerObfuscatedName();
                    String newOwnerName = classMap.get(oldOwnerName);
                    ClassNode oldOwner = oldJar.classes.get(oldOwnerName);
                    ClassNode newOwner = newOwnerName == null ? null : newJar.classes.get(newOwnerName);
                    if (oldOwner == null || newOwner == null) continue;

                    FieldNode oldNode = findField(oldOwner, oldField.getObfuscatedName(), oldField.getDescriptor(), oldField.isStatic());
                    if (oldNode == null) continue;
                    FieldNode candidate = bestNamedFieldCandidate(oldNode, oldOwner, newOwner, oldJar, newJar, mappings, classMap);
                    if (candidate != null) {
                        changed |= learnDescriptorClassMap(oldField.getDescriptor(), candidate.desc, oldJar, newJar, mappings, classMap);
                    }
                }

                for (JMethod oldMethod : mapped.getMethods()) {
                    if (blank(oldMethod.getName())) continue;
                    String oldOwnerName = blank(oldMethod.getOwnerObfuscatedName()) ? mapped.getObfuscatedName() : oldMethod.getOwnerObfuscatedName();
                    String newOwnerName = classMap.get(oldOwnerName);
                    ClassNode oldOwner = oldJar.classes.get(oldOwnerName);
                    ClassNode newOwner = newOwnerName == null ? null : newJar.classes.get(newOwnerName);
                    if (oldOwner == null || newOwner == null) continue;

                    MethodNode oldNode = findMethod(oldOwner, oldMethod.getObfuscatedName(), oldMethod.getDescriptor(), oldMethod.isStatic());
                    if (oldNode == null) continue;
                    MethodNode candidate = bestNamedMethodCandidate(oldNode, newOwner, classMap);
                    if (candidate != null) {
                        changed |= learnMethodDescriptorClassMap(oldMethod.getDescriptor(), candidate.desc, oldJar, newJar, mappings, classMap);
                    }
                }
            }
            if (!changed) break;
        }
    }

    private static FieldNode findField(ClassNode owner, String name, String desc, boolean isStatic) {
        for (FieldNode field : owner.fields) {
            if (field.name.equals(name) && field.desc.equals(desc) && isStatic(field.access) == isStatic) {
                return field;
            }
        }
        return null;
    }

    private static MethodNode findMethod(ClassNode owner, String name, String desc, boolean isStatic) {
        for (MethodNode method : owner.methods) {
            if (method.name.equals(name) && method.desc.equals(desc) && isStatic(method.access) == isStatic) {
                return method;
            }
        }
        return null;
    }

    private static FieldNode bestNamedFieldCandidate(FieldNode oldField, ClassNode oldOwner, ClassNode newOwner, JarModel oldJar, JarModel newJar,
                                                     List<JClass> mappings, Map<String, String> classMap) {
        FieldNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode candidate : newOwner.fields) {
            if (isStatic(candidate.access) != isStatic(oldField.access)) continue;
            if (!fieldDescriptorCompatible(oldField.desc, candidate.desc, classMap)) continue;
            int score = 0;
            if (normalizeDesc(oldField.desc, classMap).equals(normalizeDesc(candidate.desc, Map.of()))) score += 30;
            if (eraseGameTypes(oldField.desc).equals(eraseGameTypes(candidate.desc))) score += 20;
            if (oldField.desc.equals(candidate.desc)) score += 10;
            score += descriptorTargetClassScore(oldField.desc, candidate.desc, classMap) / 4;
            score += descriptorTargetClassStructuralScore(oldField.desc, candidate.desc, oldJar, newJar, mappings, classMap) / 3;
            score += constructorParamScore(oldOwner, oldField, newOwner, candidate) * 50;
            score += fieldUsageScore(oldJar, oldOwner.name, oldField.name, oldField.desc, newJar, newOwner.name, candidate.name, candidate.desc, classMap) * 4;
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return best;
    }

    private static FieldNode specialNamedFieldCandidate(String className, String fieldName, ClassNode newOwner,
                                                        JarModel newJar, Map<String, String> classMap, List<JClass> mappings) {
        if (newOwner == null || blank(fieldName)) return null;

        if (("ClientPacket".equals(className) || "ServerPacket".equals(className)) && ("id".equals(fieldName) || "length".equals(fieldName))) {
            return fieldAssignedFromConstructorVar(newOwner, "I", "id".equals(fieldName) ? 1 : 2);
        }

        if ("MouseHandler".equals(className) || "Client".equals(className)) {
            if ("MouseHandler_instance".equals(fieldName)) {
                return newOwner.fields.stream()
                        .filter(f -> isStatic(f.access) && f.desc.equals("L" + newOwner.name + ";"))
                        .findFirst()
                        .orElse(null);
            }
            if ("MouseHandler_x".equals(fieldName)) {
                FieldNode stable = mouseCoordinateFieldByUsage(newJar, newOwner, 0);
                if (stable == null) stable = stableMouseSnapshotField(newOwner, 1);
                if (stable != null) return stable;
            }
            if ("MouseHandler_y".equals(fieldName)) {
                FieldNode stable = mouseCoordinateFieldByUsage(newJar, newOwner, 1);
                if (stable == null) stable = stableMouseSnapshotField(newOwner, 0);
                if (stable != null) return stable;
            }
            if ("MouseHandler_lastPressedTimeMillis".equals(fieldName)) {
                FieldNode stable = stableMouseSnapshotTimeField(newOwner);
                if (stable != null) return stable;
            }
            if ("MouseHandler_idleCycles".equals(fieldName)) {
                return newOwner.fields.stream()
                        .filter(f -> isStatic(f.access) && f.desc.equals("I"))
                        .max(Comparator.comparingInt(f -> fieldAccessCount(newJar, newOwner.name, f.name, f.desc, Opcodes.PUTSTATIC)))
                        .orElse(null);
            }
        }

        if ("Client".equals(className)) {
            if ("accountTypeCheck".equals(fieldName)) {
                FieldNode field = findAccountTypeCheckField(newJar);
                if (field != null) return field;
            }
            if ("packedCallStack1".equals(fieldName) || "packedCallStack2".equals(fieldName)) {
                FieldNode field = packedCallStackField(newOwner, "packedCallStack1".equals(fieldName) ? 1 : 2);
                if (field != null) return field;
            }
            if ("heading".equals(fieldName)) {
                FieldNode field = arrayPushIndexField(newOwner);
                if (field == null) field = headingCountdownField(newOwner);
                if (field != null) return field;
            }
            if ("mouseLastPressedTimeMillis".equals(fieldName)) {
                FieldNode field = clientMouseLastPressedTimeField(newJar, newOwner);
                if (field != null) return field;
            }
            if ("randomDat".equals(fieldName)) {
                FieldNode field = randomDatField(newOwner);
                if (field != null) return field;
            }
            if ("logger".equals(fieldName)) {
                FieldNode field = loggerField(newOwner);
                if (field != null) return field;
            }
        }

        if ("Player".equals(className) && "actions".equals(fieldName)) {
            FieldNode field = actionsArrayField(newOwner);
            if (field != null) return field;
        }

        if ("ItemComposition".equals(className) && "groundActions".equals(fieldName)) {
            FieldNode field = groundActionsField(newOwner);
            if (field != null) return field;
        }

        if ("AccountType".equals(className)) {
            String override = ACCOUNT_TYPE_FIELD_OVERRIDES.get(fieldName);
            if (override != null) {
                return newOwner.fields.stream()
                        .filter(f -> f.name.equals(override) && f.desc.equals("L" + newOwner.name + ";") && isStatic(f.access))
                        .findFirst()
                        .orElse(null);
            }
        }

        if ("Task".equals(className)) {
            if ("result".equals(fieldName)) {
                return newOwner.fields.stream()
                        .filter(f -> !isStatic(f.access) && isVolatile(f.access) && f.desc.equals("Ljava/lang/Object;"))
                        .findFirst()
                        .orElse(null);
            }
            if ("objectArgument".equals(fieldName)) {
                return newOwner.fields.stream()
                        .filter(f -> !isStatic(f.access) && !isVolatile(f.access) && f.desc.equals("Ljava/lang/Object;"))
                        .findFirst()
                        .orElse(null);
            }
        }

        if ("PlatformInfo".equals(className) && "clientName".equals(fieldName)) {
            FieldNode field = constructorParamField(newOwner, "Ljava/lang/String;", 25);
            if (field != null) return field;
        }

        if ("PacketWriter".equals(className)) {
            String serverPacket = mappedName(mappings, classMap, "ServerPacket");
            String packetBuffer = mappedName(mappings, classMap, "PacketBuffer");
            String isaacCipher = mappedName(mappings, classMap, "IsaacCipher");
            if ("serverPacket".equals(fieldName) && serverPacket != null) {
                FieldNode field = constructorNullInitializedFieldOfType(newOwner, "L" + serverPacket + ";");
                return field != null ? field : firstInstanceFieldOfType(newOwner, "L" + serverPacket + ";");
            }
            if ("serverPacketBuffer".equals(fieldName) && packetBuffer != null) {
                FieldNode field = constructorNewFieldOfType(newOwner, "L" + packetBuffer + ";");
                return field != null ? field : firstInstanceFieldOfType(newOwner, "L" + packetBuffer + ";");
            }
            if ("isaacCipher".equals(fieldName) && isaacCipher != null) {
                return firstInstanceFieldOfType(newOwner, "L" + isaacCipher + ";");
            }
            if ("serverPacketLength".equals(fieldName)) {
                FieldNode field = packetWriterServerPacketLengthField(newJar, mappings, classMap, newOwner);
                if (field != null) return field;
                return newOwner.fields.stream()
                        .filter(f -> !isStatic(f.access) && f.desc.equals("I"))
                        .findFirst()
                        .orElse(null);
            }
        }

        if ("PacketBufferNode".equals(className)) {
            String clientPacket = mappedName(mappings, classMap, "ClientPacket");
            String packetBuffer = mappedName(mappings, classMap, "PacketBuffer");
            if ("clientPacket".equals(fieldName) && clientPacket != null) {
                return firstInstanceFieldOfType(newOwner, "L" + clientPacket + ";");
            }
            if ("packetBuffer".equals(fieldName) && packetBuffer != null) {
                return firstInstanceFieldOfType(newOwner, "L" + packetBuffer + ";");
            }
        }

        if ("Client".equals(className) && "mouseFlag".equals(fieldName)) {
            return newOwner.fields.stream()
                    .filter(f -> isStatic(f.access) && f.name.equals("llimc") && f.desc.equals("I"))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private static FieldNode packetWriterServerPacketLengthField(JarModel jar, List<JClass> mappings,
                                                                 Map<String, String> classMap, ClassNode packetWriterOwner) {
        String serverPacketName = mappedName(mappings, classMap, "ServerPacket");
        if (serverPacketName == null || packetWriterOwner == null) return null;
        ClassNode serverPacket = jar.classes.get(serverPacketName);
        if (serverPacket == null) return null;

        FieldNode serverPacketLength = fieldAssignedFromConstructorVar(serverPacket, "I", 2);
        if (serverPacketLength == null) {
            serverPacketLength = serverPacket.fields.stream()
                    .filter(f -> !isStatic(f.access) && f.desc.equals("I"))
                    .skip(1)
                    .findFirst()
                    .orElse(null);
        }
        if (serverPacketLength == null) return null;

        MethodMatch processServerPacket = processServerPacketMethod(jar, mappings, classMap);
        MethodNode method = processServerPacket == null ? null : processServerPacket.method;
        if (method == null) return null;

        AbstractInsnNode[] insns = method.instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.GETFIELD) continue;
            FieldInsnNode getLength = (FieldInsnNode) insns[i];
            if (!getLength.owner.equals(serverPacket.name)
                    || !getLength.name.equals(serverPacketLength.name)
                    || !getLength.desc.equals(serverPacketLength.desc)) {
                continue;
            }
            for (int j = i + 1; j < Math.min(insns.length, i + 8); j++) {
                if (!(insns[j] instanceof FieldInsnNode) || insns[j].getOpcode() != Opcodes.PUTFIELD) continue;
                FieldInsnNode putWriterInt = (FieldInsnNode) insns[j];
                if (!putWriterInt.owner.equals(packetWriterOwner.name) || !"I".equals(putWriterInt.desc)) continue;
                FieldNode field = findField(packetWriterOwner, putWriterInt.name, putWriterInt.desc, false);
                if (field != null) return field;
            }
        }
        return null;
    }

    private static FieldNode constructorParamField(ClassNode owner, String desc, int localIndex) {
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTFIELD) continue;
                FieldInsnNode put = (FieldInsnNode) insns[i];
                if (!put.owner.equals(owner.name) || !put.desc.equals(desc)) continue;
                for (int j = Math.max(0, i - 4); j < i; j++) {
                    Integer var = varIndex(insns[j]);
                    if (var != null && var == localIndex) {
                        FieldNode field = findField(owner, put.name, put.desc, false);
                        if (field != null) return field;
                    }
                }
            }
        }
        return null;
    }

    private static FieldNode randomDatField(ClassNode client) {
        return client.fields.stream()
                .filter(f -> isStatic(f.access) && f.desc.equals("[B"))
                .max(Comparator.<FieldNode>comparingInt(f -> fieldNameUsageScore(client, f, "random.dat"))
                        .thenComparing(f -> f.name))
                .orElse(null);
    }

    private static FieldNode loggerField(ClassNode client) {
        FieldNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!isStatic(field.access) || !field.desc.equals("Lorg/slf4j/Logger;")) continue;
            int score = 0;
            for (MethodNode method : client.methods) {
                if ("<clinit>".equals(method.name)) {
                    score += fieldInsnCount(method, client.name, field.name, field.desc, Opcodes.PUTSTATIC) * 120;
                    score += methodCallInsnCount(method, "org/slf4j/LoggerFactory", "getLogger", null) * 80;
                    score += methodTypeInsnCount(method, client.name) * 30;
                }
                score += fieldInsnCount(method, client.name, field.name, field.desc, Opcodes.GETSTATIC) * 10;
                score += methodCallInsnOwnerCount(method, "org/slf4j/Logger") * 10;
            }
            if (score > bestScore) {
                bestScore = score;
                best = field;
            }
        }
        return bestScore > 0 ? best : null;
    }

    private static FieldNode actionsArrayField(ClassNode owner) {
        return owner.fields.stream()
                .filter(f -> !isStatic(f.access) && f.desc.equals("[Ljava/lang/String;"))
                .max(Comparator.<FieldNode>comparingInt(f -> fieldNameUsageScore(owner, f, "getPlayerActions"))
                        .thenComparing(f -> f.name))
                .orElse(null);
    }

    private static FieldNode groundActionsField(ClassNode owner) {
        return owner.fields.stream()
                .filter(f -> !isStatic(f.access) && f.desc.equals("[Ljava/lang/String;"))
                .max(Comparator.<FieldNode>comparingInt(f -> fieldNameUsageScore(owner, f, "getGroundActions"))
                        .thenComparing(f -> f.name))
                .orElse(null);
    }

    private static int fieldNameUsageScore(ClassNode owner, FieldNode field, String textNeedle) {
        int score = 0;
        String loweredNeedle = textNeedle.toLowerCase(Locale.ROOT);
        for (MethodNode method : owner.methods) {
            boolean touches = false;
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof FieldInsnNode) {
                    FieldInsnNode fin = (FieldInsnNode) insn;
                    if (fin.owner.equals(owner.name) && fin.name.equals(field.name) && fin.desc.equals(field.desc)) {
                        touches = true;
                        score += 5;
                    }
                }
            }
            if (touches && method.name.toLowerCase(Locale.ROOT).contains(loweredNeedle)) score += 50;
            if (touches) {
                for (String constant : methodConstants(method)) {
                    if (constant.toLowerCase(Locale.ROOT).contains(loweredNeedle)) score += 20;
                }
            }
        }
        return score;
    }

    private static Integer varIndex(AbstractInsnNode insn) {
        if (insn instanceof VarInsnNode) return ((VarInsnNode) insn).var;
        return null;
    }

    private static MethodNode specialNamedMethodCandidate(String className, String methodName, JarModel newJar,
                                                          List<JClass> mappings, Map<String, String> classMap) {
        if ("PacketWriter".equals(className) && "addNode".equals(methodName)) {
            MethodMatch match = packetWriterAddNodeMethod(newJar, mappings, classMap);
            return match == null ? null : match.method;
        }
        if (blank(className)) {
            return null;
        }
        String mappedOwner = mappedName(mappings, classMap, className);
        ClassNode owner = mappedOwner == null ? null : newJar.classes.get(mappedOwner);
        if (owner == null) {
            return null;
        }
        if ("Client".equals(className) && "processServerPacket".equals(methodName)) {
            MethodMatch match = processServerPacketMethod(newJar, mappings, classMap);
            return match == null ? null : match.method;
        }
        if ("Client".equals(className) && "callStackCheck".equals(methodName)) {
            MethodNode method = callStackCheckMethod(owner);
            if (method != null) return method;
        }
        if ("GameEngine".equals(className) && "post".equals(methodName)) {
            MethodMatch match = gameEnginePostMethod(newJar, owner);
            return match == null ? null : match.method;
        }
        if ("MouseRecorder".equals(className) && "run".equals(methodName)) {
            MethodNode method = runnableRunMethod(owner);
            if (method != null) return method;
        }
        if ("ObjectComposition".equals(className) && "decodeNext".equals(methodName)) {
            return objectCompositionDecodeNextMethod(owner, mappings, classMap);
        }
        if ("PlatformInfo".equals(className) && "write".equals(methodName)) {
            return platformInfoWriteMethod(newJar, owner, mappings, classMap);
        }
        if ("MidiPcmStream".equals(className) && "<init>".equals(methodName)) {
            return midiPcmStreamConstructor(owner);
        }
        return null;
    }

    private static MethodNode runnableRunMethod(ClassNode owner) {
        return owner.methods.stream()
                .filter(m -> !isStatic(m.access) && m.name.equals("run") && m.desc.equals("()V"))
                .findFirst()
                .orElse(null);
    }

    private static MethodNode callStackCheckMethod(ClassNode client) {
        List<FieldNode> packedFields = packedCallStackCombinedFields(client);
        if (packedFields.isEmpty()) {
            FieldNode one = packedCallStackField(client, 1);
            FieldNode two = packedCallStackField(client, 2);
            if (one != null) packedFields.add(one);
            if (two != null && packedFields.stream().noneMatch(f -> f.name.equals(two.name))) packedFields.add(two);
        }

        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : client.methods) {
            if (!isStatic(method.access) || !"Ljava/lang/String;".equals(Type.getReturnType(method.desc).getDescriptor())) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length != 1 || args[0].getSort() != Type.LONG) continue;

            int score = 100;
            for (FieldNode field : packedFields) {
                score += fieldInsnCount(method, client.name, field.name, field.desc, Opcodes.GETSTATIC) * 120;
            }
            score += methodCallInsnCount(method, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;") * 60;
            score += methodCallInsnCount(method, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;") * 60;
            score += methodCallInsnOwnerCount(method, "java/lang/StackTraceElement") * 25;
            score += methodCallInsnOwnerCount(method, "java/lang/String") * 5;
            score += methodContainsInvokeDynamic(method) ? 20 : 0;
            score += method.instructions.size() / 15;
            if (score > bestScore) {
                bestScore = score;
                best = method;
            }
        }
        return bestScore >= 120 ? best : null;
    }

    private static MethodMatch gameEnginePostMethod(JarModel jar, ClassNode owner) {
        if (owner == null) return null;
        MethodMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode method : owner.methods) {
            if (isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length != 1 || args[0].getSort() != Type.OBJECT || !"java/lang/Object".equals(args[0].getInternalName())) {
                continue;
            }

            int score = 0;
            if ((method.access & Opcodes.ACC_PUBLIC) != 0) score += 40;
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode min = (MethodInsnNode) insn;
                    if (min.owner.equals(owner.name) && min.desc.equals("(Ljava/lang/Object;S)V")) {
                        score += 120;
                    }
                } else if (insn instanceof IntInsnNode && ((IntInsnNode) insn).operand == -13860) {
                    score += 60;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = new MethodMatch(owner, method);
            }
        }
        if (bestScore >= 120) return best;

        for (MethodNode method : owner.methods) {
            if (isStatic(method.access) || Type.getReturnType(method.desc).getSort() != Type.VOID) continue;
            Type[] args = Type.getArgumentTypes(method.desc);
            if (args.length != 1 || args[0].getSort() != Type.OBJECT || !"java/lang/Object".equals(args[0].getInternalName())) {
                continue;
            }
            if (methodTypeInsnCount(method, "java/awt/event/ActionEvent") > 0
                    && methodCallInsnCount(method, "java/awt/EventQueue", "postEvent", "(Ljava/awt/AWTEvent;)V") > 0) {
                return new MethodMatch(owner, method);
            }
        }
        return null;
    }

    private static FieldNode packedCallStackField(ClassNode client, int slot) {
        List<FieldNode> combined = packedCallStackCombinedFields(client);
        if (combined.size() >= slot) return combined.get(slot - 1);

        String explicitPacker2 = explicitLoginHashPacker2Name();
        FieldNode packed2 = explicitPacker2 == null ? null : stringFieldWrittenByMethod(client, explicitPacker2);
        if (slot == 1 && Boolean.getBoolean("mapping.debug.callstack")) {
            System.out.println("callstack explicitPacker2=" + explicitPacker2 + " packed2=" + (packed2 == null ? "null" : packed2.name));
            for (MethodNode method : client.methods) {
                if (method.desc.equals("()V") && isStatic(method.access)) {
                    FieldNode f = firstStaticStringPut(client, method);
                    if (f != null) {
                        System.out.println("callstack writer " + method.name + " -> " + f.name + " score=" + stringFieldCallStackScore(client, f));
                    }
                }
            }
        }
        if (slot == 2 && packed2 != null) return packed2;
        if (slot == 1 && explicitPacker2 != null) {
            FieldNode adjacent = stringFieldWrittenByAdjacentPacker(client, explicitPacker2);
            if (adjacent != null) return adjacent;
        }

        List<FieldNode> writerCandidates = stringFieldsWrittenByNoArgVoidMethods(client);
        if (packed2 != null) {
            writerCandidates = writerCandidates.stream()
                    .filter(f -> !f.name.equals(packed2.name))
                    .collect(Collectors.toList());
        }
        if (slot == 1 && !writerCandidates.isEmpty()) {
            writerCandidates.sort(Comparator.<FieldNode>comparingInt(f -> stringFieldCallStackScore(client, f)).reversed()
                    .thenComparing(f -> f.name));
            return writerCandidates.get(0);
        }

        List<FieldNode> candidates = client.fields.stream()
                .filter(f -> isStatic(f.access) && f.desc.equals("Ljava/lang/String;"))
                .collect(Collectors.toList());
        candidates.sort(Comparator.<FieldNode>comparingInt(f -> stringFieldCallStackScore(client, f)).reversed()
                .thenComparing(f -> f.name));
        if (candidates.size() >= slot) {
            return candidates.get(slot - 1);
        }
        return null;
    }

    private static List<FieldNode> packedCallStackCombinedFields(ClassNode client) {
        for (MethodNode method : client.methods) {
            if (!method.desc.equals("()Ljava/lang/String;") || !isStatic(method.access)) continue;
            List<FieldNode> fields = new ArrayList<>();
            for (AbstractInsnNode insn : method.instructions) {
                if (insn.getOpcode() == Opcodes.GETSTATIC && insn instanceof FieldInsnNode) {
                    FieldInsnNode fin = (FieldInsnNode) insn;
                    if (fin.owner.equals(client.name) && fin.desc.equals("Ljava/lang/String;")) {
                        FieldNode field = findField(client, fin.name, fin.desc, true);
                        if (field != null) fields.add(field);
                    }
                }
            }
            if (fields.size() == 2 && methodContainsInvokeDynamic(method)) {
                return fields;
            }
        }
        return List.of();
    }

    private static boolean methodContainsInvokeDynamic(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof InvokeDynamicInsnNode) return true;
        }
        return false;
    }

    private static String explicitLoginHashPacker2Name() {
        Path path = Path.of("client/bindings/src/main/java/net/solace/mixins/LoginHashMixin.java");
        if (!Files.exists(path)) return null;
        try {
            String text = Files.readString(path);
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("client\\d+([a-z]+)").matcher(text);
            List<String> names = new ArrayList<>();
            while (matcher.find()) names.add(matcher.group(1));
            return names.size() >= 2 ? names.get(1) : null;
        } catch (IOException ignored) {
            return null;
        }
    }

    private static FieldNode stringFieldWrittenByMethod(ClassNode owner, String methodName) {
        for (MethodNode method : owner.methods) {
            if (!method.name.equals(methodName)) continue;
            FieldNode field = firstStaticStringPut(owner, method);
            if (field != null) return field;
        }
        return null;
    }

    private static FieldNode stringFieldWrittenByAdjacentPacker(ClassNode owner, String methodName) {
        for (int i = 0; i < owner.methods.size(); i++) {
            if (!owner.methods.get(i).name.equals(methodName)) continue;
            for (int j = i - 1; j >= Math.max(0, i - 8); j--) {
                MethodNode candidate = owner.methods.get(j);
                if (!candidate.desc.equals("()V") || !isStatic(candidate.access)) continue;
                FieldNode field = firstStaticStringPut(owner, candidate);
                if (field != null) return field;
            }
            for (int j = i + 1; j < Math.min(owner.methods.size(), i + 8); j++) {
                MethodNode candidate = owner.methods.get(j);
                if (!candidate.desc.equals("()V") || !isStatic(candidate.access)) continue;
                FieldNode field = firstStaticStringPut(owner, candidate);
                if (field != null) return field;
            }
        }
        return null;
    }

    private static List<FieldNode> stringFieldsWrittenByNoArgVoidMethods(ClassNode owner) {
        List<FieldNode> fields = new ArrayList<>();
        for (MethodNode method : owner.methods) {
            if (!method.desc.equals("()V") || !isStatic(method.access)) continue;
            FieldNode field = firstStaticStringPut(owner, method);
            if (field != null && fields.stream().noneMatch(f -> f.name.equals(field.name))) {
                fields.add(field);
            }
        }
        return fields;
    }

    private static FieldNode firstStaticStringPut(ClassNode owner, MethodNode method) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn.getOpcode() == Opcodes.PUTSTATIC && insn instanceof FieldInsnNode) {
                FieldInsnNode fin = (FieldInsnNode) insn;
                if (fin.owner.equals(owner.name) && fin.desc.equals("Ljava/lang/String;")) {
                    return findField(owner, fin.name, fin.desc, true);
                }
            }
        }
        return null;
    }

    private static int stringFieldCallStackScore(ClassNode owner, FieldNode field) {
        int score = 0;
        for (MethodNode method : owner.methods) {
            boolean touches = false;
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof FieldInsnNode) {
                    FieldInsnNode fin = (FieldInsnNode) insn;
                    if (fin.owner.equals(owner.name) && fin.name.equals(field.name) && fin.desc.equals(field.desc)) {
                        touches = true;
                        if (insn.getOpcode() == Opcodes.PUTSTATIC) score += 10;
                        if (insn.getOpcode() == Opcodes.GETSTATIC) score += 4;
                    }
                } else if (insn instanceof MethodInsnNode) {
                    MethodInsnNode min = (MethodInsnNode) insn;
                    if (touches && min.owner.equals("java/lang/StackTraceElement")) score += 20;
                }
            }
        }
        return score;
    }

    private static String mappedName(List<JClass> mappings, Map<String, String> classMap, String deobName) {
        JClass mapping = namedMapping(mappings, deobName);
        return mapping == null ? null : classMap.get(mapping.getObfuscatedName());
    }

    private static FieldNode firstInstanceFieldOfType(ClassNode owner, String desc) {
        return owner.fields.stream()
                .filter(f -> !isStatic(f.access) && f.desc.equals(desc))
                .findFirst()
                .orElse(null);
    }

    private static FieldNode constructorNullInitializedFieldOfType(ClassNode owner, String desc) {
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTFIELD) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[i];
                if (!fin.owner.equals(owner.name) || !fin.desc.equals(desc)) continue;
                for (int j = Math.max(0, i - 3); j < i; j++) {
                    if (insns[j].getOpcode() == Opcodes.ACONST_NULL) {
                        return findField(owner, fin.name, fin.desc, false);
                    }
                }
            }
        }
        return null;
    }

    private static FieldNode constructorNewFieldOfType(ClassNode owner, String desc) {
        String type = desc.startsWith("L") && desc.endsWith(";") ? desc.substring(1, desc.length() - 1) : null;
        if (type == null) return null;
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTFIELD) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[i];
                if (!fin.owner.equals(owner.name) || !fin.desc.equals(desc)) continue;
                for (int j = Math.max(0, i - 8); j < i; j++) {
                    if (insns[j] instanceof TypeInsnNode && insns[j].getOpcode() == Opcodes.NEW
                            && ((TypeInsnNode) insns[j]).desc.equals(type)) {
                        return findField(owner, fin.name, fin.desc, false);
                    }
                }
            }
        }
        return null;
    }

    private static FieldNode fieldAssignedFromConstructorVar(ClassNode owner, String desc, int var) {
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTFIELD) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[i];
                if (!fin.owner.equals(owner.name) || !fin.desc.equals(desc)) continue;
                Integer loaded = previousLoadVar(insns, i, desc);
                if (loaded != null && loaded == var) {
                    return findField(owner, fin.name, fin.desc, false);
                }
            }
        }
        return null;
    }

    private static FieldNode fieldWrittenNearMethod(ClassNode owner, String methodName, String desc) {
        for (MethodNode method : owner.methods) {
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode)) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[i];
                if ((insns[i].getOpcode() != Opcodes.PUTSTATIC && insns[i].getOpcode() != Opcodes.PUTFIELD)
                        || !fin.owner.equals(owner.name) || !fin.desc.equals(desc)) {
                    continue;
                }
                for (int j = Math.max(0, i - 16); j <= Math.min(insns.length - 1, i + 4); j++) {
                    if (insns[j] instanceof MethodInsnNode) {
                        MethodInsnNode min = (MethodInsnNode) insns[j];
                        if (min.name.equals(methodName)) {
                            return findField(owner, fin.name, fin.desc, isStaticFieldOpcode(insns[i].getOpcode()));
                        }
                    }
                }
            }
        }
        return null;
    }

    private static FieldNode stableMouseSnapshotField(ClassNode owner, int stableIndex) {
        List<FieldNode> snapshot = stableMouseSnapshotFields(owner);
        return snapshot.size() > stableIndex ? snapshot.get(stableIndex) : null;
    }

    private static FieldNode mouseCoordinateFieldByUsage(JarModel jar, ClassNode mouseHandler, int coordinateIndex) {
        Map<String, Integer> scores = new HashMap<>();
        for (ClassNode owner : jar.classes.values()) {
            for (MethodNode method : owner.methods) {
                AbstractInsnNode[] insns = method.instructions.toArray();
                for (int i = 0; i < insns.length; i++) {
                    if (!(insns[i] instanceof MethodInsnNode)) continue;
                    MethodInsnNode min = (MethodInsnNode) insns[i];
                    if (!min.desc.equals("(II)Z")) continue;
                    List<FieldNode> fields = new ArrayList<>();
                    for (int j = Math.max(0, i - 18); j < i; j++) {
                        if (!(insns[j] instanceof FieldInsnNode) || insns[j].getOpcode() != Opcodes.GETSTATIC) continue;
                        FieldInsnNode fin = (FieldInsnNode) insns[j];
                        FieldNode field = findField(mouseHandler, fin.name, fin.desc, true);
                        if (fin.owner.equals(mouseHandler.name) && fin.desc.equals("I") && field != null && !isVolatile(field.access)) {
                            if (fields.stream().noneMatch(f -> f.name.equals(field.name))) fields.add(field);
                        }
                    }
                    if (fields.size() >= 2) {
                        FieldNode coordinate = fields.get(coordinateIndex);
                        scores.merge(coordinate.name, 1, Integer::sum);
                    }
                }
            }
        }
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> findField(mouseHandler, e.getKey(), "I", true))
                .orElse(null);
    }

    private static FieldNode stableMouseSnapshotTimeField(ClassNode owner) {
        FieldNode copied = null;
        for (MethodNode method : owner.methods) {
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTSTATIC) continue;
                FieldInsnNode put = (FieldInsnNode) insns[i];
                if (!put.owner.equals(owner.name) || !put.desc.equals("J")) continue;
                FieldNode target = findField(owner, put.name, put.desc, true);
                if (target == null || isVolatile(target.access)) continue;
                for (int j = Math.max(0, i - 8); j < i; j++) {
                    if (insns[j] instanceof FieldInsnNode && insns[j].getOpcode() == Opcodes.GETSTATIC) {
                        FieldInsnNode get = (FieldInsnNode) insns[j];
                        FieldNode source = findField(owner, get.name, get.desc, true);
                        if (get.owner.equals(owner.name) && get.desc.equals("J") && source != null && isVolatile(source.access)) {
                            copied = target;
                        }
                    }
                }
            }
        }
        if (copied != null) return copied;
        List<FieldNode> stableLongs = owner.fields.stream()
                .filter(f -> isStatic(f.access) && !isVolatile(f.access) && f.desc.equals("J"))
                .collect(Collectors.toList());
        return stableLongs.size() > 1 ? stableLongs.get(1) : stableLongs.stream().findFirst().orElse(null);
    }

    private static List<FieldNode> stableMouseSnapshotFields(ClassNode owner) {
        for (MethodNode method : owner.methods) {
            List<FieldNode> copiedInts = new ArrayList<>();
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTSTATIC) continue;
                FieldInsnNode put = (FieldInsnNode) insns[i];
                if (!put.owner.equals(owner.name) || !put.desc.equals("I")) continue;
                FieldNode target = findField(owner, put.name, put.desc, true);
                if (target == null || isVolatile(target.access)) continue;
                for (int j = Math.max(0, i - 8); j < i; j++) {
                    if (!(insns[j] instanceof FieldInsnNode) || insns[j].getOpcode() != Opcodes.GETSTATIC) continue;
                    FieldInsnNode get = (FieldInsnNode) insns[j];
                    FieldNode source = findField(owner, get.name, get.desc, true);
                    if (get.owner.equals(owner.name) && get.desc.equals("I") && source != null && isVolatile(source.access)) {
                        if (copiedInts.stream().noneMatch(f -> f.name.equals(target.name))) copiedInts.add(target);
                        break;
                    }
                }
            }
            if (copiedInts.size() >= 2 && methodWritesStableMouseTime(owner, method)) {
                return copiedInts;
            }
        }
        return List.of();
    }

    private static boolean methodWritesStableMouseTime(ClassNode owner, MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTSTATIC) continue;
            FieldInsnNode put = (FieldInsnNode) insns[i];
            if (!put.owner.equals(owner.name) || !put.desc.equals("J")) continue;
            FieldNode target = findField(owner, put.name, put.desc, true);
            if (target != null && !isVolatile(target.access)) return true;
        }
        return false;
    }

    private static FieldNode headingCountdownField(ClassNode client) {
        FieldNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!isStatic(field.access) || !field.desc.equals("I")) continue;
            int score = 0;
            for (MethodNode method : client.methods) {
                if (hasSameFieldGetAndPut(client.name, field, method) && methodHasOpcode(method, Opcodes.IFLE)) score += 50;
                if (hasSameFieldGetAndPut(client.name, field, method) && methodHasOpcode(method, Opcodes.ISUB)) score += 30;
            }
            if (score > bestScore) {
                bestScore = score;
                best = field;
            }
        }
        return bestScore >= 80 ? best : null;
    }

    private static FieldNode arrayPushIndexField(ClassNode client) {
        FieldNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!isStatic(field.access) || !field.desc.equals("I")) continue;
            int score = 0;
            for (MethodNode method : client.methods) {
                AbstractInsnNode[] insns = method.instructions.toArray();
                for (int i = 0; i < insns.length; i++) {
                    if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTSTATIC) continue;
                    FieldInsnNode put = (FieldInsnNode) insns[i];
                    if (!put.owner.equals(client.name) || !put.name.equals(field.name) || !put.desc.equals(field.desc)) continue;
                    boolean hasClientIntArray = false;
                    boolean hasSameGet = false;
                    boolean hasIncrement = false;
                    for (int j = Math.max(0, i - 8); j < i; j++) {
                        if (insns[j] instanceof FieldInsnNode) {
                            FieldInsnNode fin = (FieldInsnNode) insns[j];
                            hasClientIntArray |= fin.owner.equals(client.name) && fin.desc.equals("[I");
                            hasSameGet |= insns[j].getOpcode() == Opcodes.GETSTATIC
                                    && fin.owner.equals(client.name) && fin.name.equals(field.name) && fin.desc.equals(field.desc);
                        } else if (insns[j] instanceof LdcInsnNode) {
                            Object cst = ((LdcInsnNode) insns[j]).cst;
                            hasIncrement |= cst instanceof Integer && Math.abs((Integer) cst) > 1000000;
                        }
                    }
                    if (hasClientIntArray && hasSameGet && hasIncrement) score += 40;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = field;
            }
        }
        return bestScore >= 80 ? best : null;
    }

    private static FieldNode clientMouseLastPressedTimeField(JarModel jar, ClassNode client) {
        FieldNode synced = clientMouseTimeSyncedFromMouseHandler(jar, client);
        if (synced != null) return synced;
        FieldNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (FieldNode field : client.fields) {
            if (!isStatic(field.access) || !field.desc.equals("J")) continue;
            int score = 0;
            for (MethodNode method : client.methods) {
                if (!hasSameFieldGetAndPut(client.name, field, method)) continue;
                if (methodContainsLongConstant(method, 20L)) score += 80;
                if (methodHasOpcode(method, Opcodes.LALOAD)) score += 40;
                if (methodHasOpcode(method, Opcodes.LREM) || methodHasOpcode(method, Opcodes.LDIV)) score += 20;
            }
            if (score > bestScore) {
                bestScore = score;
                best = field;
            }
        }
        return bestScore >= 100 ? best : null;
    }

    private static FieldNode clientMouseTimeSyncedFromMouseHandler(JarModel jar, ClassNode client) {
        for (MethodNode method : client.methods) {
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTSTATIC) continue;
                FieldInsnNode put = (FieldInsnNode) insns[i];
                if (!put.owner.equals(client.name) || !put.desc.equals("J")) continue;
                for (int j = Math.max(0, i - 8); j < i; j++) {
                    if (!(insns[j] instanceof FieldInsnNode) || insns[j].getOpcode() != Opcodes.GETSTATIC) continue;
                    FieldInsnNode get = (FieldInsnNode) insns[j];
                    if (!get.desc.equals("J") || get.owner.equals(client.name)) continue;
                    ClassNode sourceOwner = jar.classes.get(get.owner);
                    if (sourceOwner != null && isMouseHandlerClass(sourceOwner)) {
                        FieldNode target = findField(client, put.name, put.desc, true);
                        if (target != null) return target;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isMouseHandlerClass(ClassNode owner) {
        return owner.interfaces.contains("java/awt/event/MouseListener")
                && owner.interfaces.contains("java/awt/event/MouseMotionListener")
                && owner.interfaces.contains("java/awt/event/FocusListener");
    }

    private static boolean hasSameFieldGetAndPut(String owner, FieldNode field, MethodNode method) {
        boolean get = false;
        boolean put = false;
        for (AbstractInsnNode insn : method.instructions) {
            if (!(insn instanceof FieldInsnNode)) continue;
            FieldInsnNode fin = (FieldInsnNode) insn;
            if (!fin.owner.equals(owner) || !fin.name.equals(field.name) || !fin.desc.equals(field.desc)) continue;
            get |= insn.getOpcode() == Opcodes.GETSTATIC;
            put |= insn.getOpcode() == Opcodes.PUTSTATIC;
        }
        return get && put;
    }

    private static boolean methodHasOpcode(MethodNode method, int opcode) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn.getOpcode() == opcode) return true;
        }
        return false;
    }

    private static boolean methodContainsLongConstant(MethodNode method, long value) {
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof LdcInsnNode && Objects.equals(((LdcInsnNode) insn).cst, value)) return true;
        }
        return false;
    }

    private static boolean isVolatile(int access) {
        return (access & Opcodes.ACC_VOLATILE) != 0;
    }

    private static boolean isStaticFieldOpcode(int opcode) {
        return opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC;
    }

    private static int fieldAccessCount(JarModel jar, String owner, String name, String desc, int opcode) {
        int count = 0;
        for (ClassNode cn : jar.classes.values()) {
            for (MethodNode method : cn.methods) {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn.getOpcode() == opcode && insn instanceof FieldInsnNode) {
                        FieldInsnNode fin = (FieldInsnNode) insn;
                        if (fin.owner.equals(owner) && fin.name.equals(name) && fin.desc.equals(desc)) count++;
                    }
                }
            }
        }
        return count;
    }

    private static int constructorParamScore(ClassNode oldOwner, FieldNode oldField, ClassNode newOwner, FieldNode newField) {
        Integer oldIndex = constructorParamIndex(oldOwner, oldField);
        Integer newIndex = constructorParamIndex(newOwner, newField);
        if (oldIndex == null || newIndex == null) return 0;
        return oldIndex.equals(newIndex) ? 1 : -1;
    }

    private static Integer constructorParamIndex(ClassNode owner, FieldNode field) {
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode)) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[i];
                if (insns[i].getOpcode() != Opcodes.PUTFIELD || !fin.owner.equals(owner.name)
                        || !fin.name.equals(field.name) || !fin.desc.equals(field.desc)) {
                    continue;
                }
                Integer var = previousLoadVar(insns, i, field.desc);
                if (var != null) return var;
            }
        }
        return null;
    }

    private static Integer previousLoadVar(AbstractInsnNode[] insns, int index, String desc) {
        int expected = desc.equals("J") || desc.equals("D") ? (desc.equals("J") ? Opcodes.LLOAD : Opcodes.DLOAD)
                : desc.startsWith("L") || desc.startsWith("[") ? Opcodes.ALOAD : Opcodes.ILOAD;
        for (int i = index - 1; i >= Math.max(0, index - 8); i--) {
            AbstractInsnNode insn = insns[i];
            if (insn instanceof VarInsnNode && insn.getOpcode() == expected) {
                return ((VarInsnNode) insn).var;
            }
        }
        return null;
    }

    private static int fieldUsageScore(JarModel oldJar, String oldOwner, String oldName, String oldDesc,
                                       JarModel newJar, String newOwner, String newName, String newDesc,
                                       Map<String, String> classMap) {
        List<String> oldUsage = fieldUsageFeatures(oldJar, oldOwner, oldName, oldDesc, classMap);
        List<String> newUsage = fieldUsageFeatures(newJar, newOwner, newName, newDesc, Map.of());
        return multisetIntersection(oldUsage, newUsage);
    }

    private static List<String> fieldUsageFeatures(JarModel jar, String owner, String name, String desc, Map<String, String> classMap) {
        List<String> features = new ArrayList<>();
        for (ClassNode cn : jar.classes.values()) {
            for (MethodNode method : cn.methods) {
                AbstractInsnNode[] insns = method.instructions.toArray();
                for (int i = 0; i < insns.length; i++) {
                    if (!(insns[i] instanceof FieldInsnNode)) continue;
                    FieldInsnNode fin = (FieldInsnNode) insns[i];
                    if (!fin.owner.equals(owner) || !fin.name.equals(name) || !fin.desc.equals(desc)) continue;
                    features.add("op:" + insns[i].getOpcode());
                    features.add("method:" + normalizeInternalName(cn.name, classMap) + normalizeDesc(method.desc, classMap));
                    String near = nearbyInstructionFeature(insns, i, classMap);
                    if (near != null) features.add("near:" + near);
                }
            }
        }
        return features;
    }

    private static String nearbyInstructionFeature(AbstractInsnNode[] insns, int index, Map<String, String> classMap) {
        for (int delta : new int[]{-2, -1, 1, 2}) {
            int i = index + delta;
            if (i < 0 || i >= insns.length) continue;
            AbstractInsnNode insn = insns[i];
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) insn;
                return "m:" + normalizeInternalName(min.owner, classMap) + normalizeDesc(min.desc, classMap);
            }
            if (insn instanceof FieldInsnNode) {
                FieldInsnNode fin = (FieldInsnNode) insn;
                return "f:" + normalizeInternalName(fin.owner, classMap) + normalizeDesc(fin.desc, classMap);
            }
            if (insn instanceof LdcInsnNode) {
                Object cst = ((LdcInsnNode) insn).cst;
                if (cst instanceof String) return "s:" + cst;
                if (cst instanceof Number) return "n";
            }
        }
        return null;
    }

    private static int descriptorTargetClassStructuralScore(String oldDesc, String newDesc, JarModel oldJar, JarModel newJar,
                                                            List<JClass> mappings, Map<String, String> classMap) {
        List<String> oldTypes = gameTypesInDesc(oldDesc);
        List<String> newTypes = gameTypesInDesc(newDesc);
        if (oldTypes.size() != 1 || newTypes.size() != 1) {
            return 0;
        }
        ClassNode oldClass = oldJar.classes.get(oldTypes.get(0));
        ClassNode newClass = newJar.classes.get(newTypes.get(0));
        if (oldClass == null || newClass == null) {
            return 0;
        }
        JClass named = mappings.stream()
                .filter(m -> oldTypes.get(0).equals(m.getObfuscatedName()) && !blank(m.getName()))
                .findFirst()
                .orElse(null);
        return named == null ? scoreClass(oldClass, newClass, classMap) : scoreNamedClass(named, oldClass, newClass, classMap);
    }

    private static int descriptorTargetClassScore(String oldDesc, String newDesc, Map<String, String> classMap) {
        List<String> oldTypes = gameTypesInDesc(oldDesc);
        List<String> newTypes = gameTypesInDesc(newDesc);
        if (oldTypes.size() != 1 || newTypes.size() != 1) {
            return 0;
        }
        String mapped = classMap.get(oldTypes.get(0));
        return newTypes.get(0).equals(mapped) ? 120 : 0;
    }

    private static MethodNode bestNamedMethodCandidate(MethodNode oldMethod, ClassNode newOwner, Map<String, String> classMap) {
        MethodNode best = null;
        int bestScore = Integer.MIN_VALUE;
        for (MethodNode candidate : newOwner.methods) {
            if (isStatic(candidate.access) != isStatic(oldMethod.access)) continue;
            if (!compatibleMethodDescriptor(oldMethod.desc, candidate.desc, classMap)) continue;
            int score = scoreMethod(oldMethod, candidate, classMap);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return best;
    }

    private static boolean learnDescriptorClassMap(String oldDesc, String newDesc, JarModel oldJar, JarModel newJar,
                                                   List<JClass> mappings, Map<String, String> classMap) {
        List<String> oldTypes = gameTypesInDesc(oldDesc);
        List<String> newTypes = gameTypesInDesc(newDesc);
        if (oldTypes.size() != newTypes.size()) return false;
        boolean changed = false;
        for (int i = 0; i < oldTypes.size(); i++) {
            changed |= learnClassMap(oldTypes.get(i), newTypes.get(i), oldJar, newJar, mappings, classMap);
        }
        return changed;
    }

    private static boolean learnMethodDescriptorClassMap(String oldDesc, String newDesc, JarModel oldJar, JarModel newJar,
                                                         List<JClass> mappings, Map<String, String> classMap) {
        return learnDescriptorClassMap(oldDesc, newDesc, oldJar, newJar, mappings, classMap);
    }

    private static boolean learnClassMap(String oldName, String newName, JarModel oldJar, JarModel newJar,
                                         List<JClass> mappings, Map<String, String> classMap) {
        if ("client".equals(oldName) && newJar.classes.containsKey("client")) {
            if (!"client".equals(classMap.get(oldName))) {
                classMap.put(oldName, "client");
                return true;
            }
            return false;
        }
        if (oldName.equals(newName) || !oldJar.classes.containsKey(oldName) || !newJar.classes.containsKey(newName)) {
            return false;
        }
        String current = classMap.get(oldName);
        if (newName.equals(current)) {
            return false;
        }
        if (current == null) {
            classMap.put(oldName, newName);
            return true;
        }
        JClass named = mappings.stream().filter(m -> oldName.equals(m.getObfuscatedName()) && !blank(m.getName())).findFirst().orElse(null);
        ClassNode oldClass = oldJar.classes.get(oldName);
        ClassNode currentClass = newJar.classes.get(current);
        ClassNode proposedClass = newJar.classes.get(newName);
        int currentScore = named == null ? scoreClass(oldClass, currentClass, classMap) : scoreNamedClass(named, oldClass, currentClass, classMap);
        int proposedScore = named == null ? scoreClass(oldClass, proposedClass, classMap) : scoreNamedClass(named, oldClass, proposedClass, classMap);
        if (proposedScore >= currentScore || named != null) {
            classMap.put(oldName, newName);
            return true;
        }
        return false;
    }

    private static List<String> gameTypesInDesc(String desc) {
        List<String> out = new ArrayList<>();
        org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(desc);
        if (type.getSort() == org.objectweb.asm.Type.METHOD) {
            for (org.objectweb.asm.Type arg : type.getArgumentTypes()) collectGameTypes(arg, out);
            collectGameTypes(type.getReturnType(), out);
        } else {
            collectGameTypes(type, out);
        }
        return out;
    }

    private static void collectGameTypes(org.objectweb.asm.Type type, List<String> out) {
        if (type.getSort() == org.objectweb.asm.Type.ARRAY) {
            collectGameTypes(type.getElementType(), out);
        } else if (type.getSort() == org.objectweb.asm.Type.OBJECT) {
            String name = type.getInternalName();
            if (!name.startsWith("java/") && !name.startsWith("javax/") && !name.startsWith("net/runelite/")
                    && !name.startsWith("net/solace/")) {
                out.add(name);
            }
        }
    }

    private static void repairNamedClassMappings(JarModel oldJar, JarModel newJar, List<JClass> mappings, Map<String, String> classMap) {
        Set<String> usedByNamed = new HashSet<>();
        for (JClass mapped : mappings) {
            if (!blank(mapped.getName())) {
                String target = classMap.get(mapped.getObfuscatedName());
                if (target != null) usedByNamed.add(target);
            }
        }

        for (JClass mapped : mappings) {
            if (blank(mapped.getName())) continue;
            if ("Client".equals(mapped.getName()) && newJar.classes.containsKey("client")) {
                String current = classMap.get(mapped.getObfuscatedName());
                if (current != null) usedByNamed.remove(current);
                classMap.put(mapped.getObfuscatedName(), "client");
                usedByNamed.add("client");
                continue;
            }
            ClassNode oldClass = oldJar.classes.get(mapped.getObfuscatedName());
            if (oldClass == null) continue;

            String current = classMap.get(mapped.getObfuscatedName());
            ClassMatch best = null;
            for (ClassNode candidate : newJar.classes.values()) {
                if (usedByNamed.contains(candidate.name) && !candidate.name.equals(current)) {
                    continue;
                }
                int score = scoreNamedClass(mapped, oldClass, candidate, classMap);
                if (best == null || score > best.score) {
                    best = new ClassMatch(candidate.name, score);
                }
            }
            if (best != null && best.score >= 80 && !best.name.equals(current)) {
                if (current != null) usedByNamed.remove(current);
                classMap.put(mapped.getObfuscatedName(), best.name);
                usedByNamed.add(best.name);
            }
        }
    }

    private static int scoreNamedClass(JClass mapped, ClassNode oldClass, ClassNode candidate, Map<String, String> classMap) {
        int score = scoreClass(oldClass, candidate, classMap) / 4;
        if (mapped.getObfuscatedName().equals(candidate.name)) score += 20;

        for (JField field : mapped.getFields()) {
            if (blank(field.getName())) continue;
            String owner = blank(field.getOwnerObfuscatedName()) ? mapped.getObfuscatedName() : field.getOwnerObfuscatedName();
            if (!owner.equals(mapped.getObfuscatedName())) continue;
            for (FieldNode candidateField : candidate.fields) {
                if (isStatic(candidateField.access) == field.isStatic()
                        && fieldDescriptorCompatible(field.getDescriptor(), candidateField.desc, classMap)) {
                    score += 35;
                    break;
                }
            }
        }

        for (JMethod method : mapped.getMethods()) {
            if (blank(method.getName())) continue;
            String owner = blank(method.getOwnerObfuscatedName()) ? mapped.getObfuscatedName() : method.getOwnerObfuscatedName();
            if (!owner.equals(mapped.getObfuscatedName())) continue;
            for (MethodNode candidateMethod : candidate.methods) {
                if (isStatic(candidateMethod.access) == method.isStatic()
                        && compatibleMethodDescriptor(method.getDescriptor(), candidateMethod.desc, classMap)) {
                    score += 35;
                    break;
                }
            }
        }
        return score;
    }

    private static void seedUniqueClassSignatures(JarModel oldJar, JarModel newJar, Map<String, String> classMap) {
        Map<String, List<ClassNode>> oldBySig = oldJar.classes.values().stream()
                .collect(Collectors.groupingBy(c -> looseClassSignature(c)));
        Map<String, List<ClassNode>> newBySig = newJar.classes.values().stream()
                .collect(Collectors.groupingBy(c -> looseClassSignature(c)));
        Set<String> used = new HashSet<>(classMap.values());
        for (Map.Entry<String, List<ClassNode>> e : oldBySig.entrySet()) {
            List<ClassNode> oldMatches = e.getValue();
            List<ClassNode> newMatches = newBySig.get(e.getKey());
            if (oldMatches != null && newMatches != null && oldMatches.size() == 1 && newMatches.size() == 1) {
                String oldName = oldMatches.get(0).name;
                String newName = newMatches.get(0).name;
                if (!classMap.containsKey(oldName) && !used.contains(newName)) {
                    classMap.put(oldName, newName);
                    used.add(newName);
                }
            }
        }
    }

    private static String looseClassSignature(ClassNode cn) {
        List<String> fields = fieldShapeMultiset(cn, Map.of());
        Collections.sort(fields);
        List<String> methods = cn.methods.stream()
                .map(m -> methodShape(m, Map.of()) + ":" + looseOpcodeFingerprint(m) + ":" + methodConstants(m))
                .sorted()
                .collect(Collectors.toList());
        return cn.fields.size() + "|" + cn.methods.size() + "|" + fields + "|" + methods;
    }

    private static String looseOpcodeFingerprint(MethodNode mn) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (AbstractInsnNode insn : mn.instructions) {
            int opcode = insn.getOpcode();
            if (opcode < 0) continue;
            sb.append(opcode).append(',');
            if (++count > 300) break;
        }
        return sb.toString();
    }

    private static int scoreClass(ClassNode oldClass, ClassNode candidate, Map<String, String> classMap) {
        int score = 0;
        if (oldClass.fields.size() == candidate.fields.size()) score += 25;
        if (oldClass.methods.size() == candidate.methods.size()) score += 25;
        score += multisetIntersection(fieldShapeMultiset(oldClass, classMap), fieldShapeMultiset(candidate, Map.of())) * 4;
        score += multisetIntersection(methodShapeMultiset(oldClass, classMap), methodShapeMultiset(candidate, Map.of())) * 3;
        score += multisetIntersection(classConstants(oldClass), classConstants(candidate)) * 12;
        score += multisetIntersection(methodFingerprints(oldClass, classMap), methodFingerprints(candidate, invert(classMap))) * 8;
        return score;
    }

    private static MemberMap<FieldNode> matchFields(ClassNode oldClass, ClassNode newClass, Map<String, String> classMap) {
        MemberMap<FieldNode> result = new MemberMap<>();
        Map<String, List<FieldNode>> oldByShape = oldClass.fields.stream().collect(Collectors.groupingBy(f -> fieldShape(f, classMap)));
        Map<String, List<FieldNode>> newByShape = newClass.fields.stream().collect(Collectors.groupingBy(f -> fieldShape(f, Map.of())));
        for (Map.Entry<String, List<FieldNode>> e : oldByShape.entrySet()) {
            List<FieldNode> oldFields = e.getValue();
            List<FieldNode> newFields = newByShape.getOrDefault(e.getKey(), List.of());
            int count = Math.min(oldFields.size(), newFields.size());
            for (int i = 0; i < count; i++) {
                result.byOldNameDesc.put(oldFields.get(i).name + oldFields.get(i).desc, newFields.get(i));
            }
        }
        return result;
    }

    private static MemberMap<MethodNode> matchMethods(ClassNode oldClass, ClassNode newClass, Map<String, String> classMap) {
        MemberMap<MethodNode> result = new MemberMap<>();
        Set<MethodNode> used = new HashSet<>();
        Map<String, List<MethodNode>> byShape = newClass.methods.stream()
                .collect(Collectors.groupingBy(m -> methodShape(m, Map.of())));
        for (MethodNode oldMethod : oldClass.methods) {
            String shape = methodShape(oldMethod, classMap);
            List<MethodNode> candidates = byShape.getOrDefault(shape, List.of());
            if (candidates.isEmpty()) {
                candidates = newClass.methods.stream()
                        .filter(m -> isStatic(m.access) == isStatic(oldMethod.access))
                        .filter(m -> compatibleMethodDescriptor(oldMethod.desc, m.desc, classMap))
                        .collect(Collectors.toList());
            }
            MethodNode best = null;
            int bestScore = Integer.MIN_VALUE;
            for (MethodNode candidate : candidates) {
                if (used.contains(candidate)) {
                    continue;
                }
                int score = scoreMethod(oldMethod, candidate, classMap);
                if (score > bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            }
            if (best != null) {
                used.add(best);
                result.byOldNameDesc.put(oldMethod.name + oldMethod.desc, best);
            }
        }
        return result;
    }

    private static boolean compatibleMethodDescriptor(String oldDesc, String newDesc, Map<String, String> classMap) {
        org.objectweb.asm.Type oldType = org.objectweb.asm.Type.getMethodType(oldDesc);
        org.objectweb.asm.Type newType = org.objectweb.asm.Type.getMethodType(newDesc);
        if (!normalizeType(oldType.getReturnType(), classMap).equals(normalizeType(newType.getReturnType(), Map.of()))) {
            return false;
        }
        Type[] oldArgs = oldType.getArgumentTypes();
        Type[] newArgs = newType.getArgumentTypes();
        if (argsCompatible(oldArgs, newArgs, classMap)) return true;
        if (oldArgs.length > 0 && isGarbageType(oldArgs[oldArgs.length - 1])) {
            return argsCompatible(Arrays.copyOf(oldArgs, oldArgs.length - 1), newArgs, classMap);
        }
        if (newArgs.length > 0 && isGarbageType(newArgs[newArgs.length - 1])) {
            return argsCompatible(oldArgs, Arrays.copyOf(newArgs, newArgs.length - 1), classMap);
        }
        return false;
    }

    private static boolean argsCompatible(Type[] oldArgs, Type[] newArgs, Map<String, String> classMap) {
        if (oldArgs.length != newArgs.length) return false;
        for (int i = 0; i < oldArgs.length; i++) {
            if (!normalizeType(oldArgs[i], classMap).equals(normalizeType(newArgs[i], Map.of()))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGarbageType(Type type) {
        return type.getSort() == Type.BYTE || type.getSort() == Type.SHORT || type.getSort() == Type.INT;
    }

    private static boolean fieldDescriptorCompatible(String oldDesc, String newDesc, Map<String, String> classMap) {
        return normalizeDesc(oldDesc, classMap).equals(normalizeDesc(newDesc, Map.of()))
                || eraseGameTypes(oldDesc).equals(eraseGameTypes(newDesc));
    }

    private static String eraseGameTypes(String desc) {
        org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(desc);
        if (type.getSort() == org.objectweb.asm.Type.METHOD) {
            StringBuilder sb = new StringBuilder("(");
            for (org.objectweb.asm.Type arg : type.getArgumentTypes()) sb.append(eraseGameType(arg));
            return sb.append(')').append(eraseGameType(type.getReturnType())).toString();
        }
        return eraseGameType(type);
    }

    private static String eraseGameType(org.objectweb.asm.Type type) {
        if (type.getSort() == org.objectweb.asm.Type.ARRAY) {
            return "[".repeat(type.getDimensions()) + eraseGameType(type.getElementType());
        }
        if (type.getSort() == org.objectweb.asm.Type.OBJECT) {
            String name = type.getInternalName();
            if (name.startsWith("java/") || name.startsWith("javax/") || name.startsWith("net/runelite/")
                    || name.startsWith("net/solace/")) {
                return "L" + name + ";";
            }
            return "L?;";
        }
        return type.getDescriptor();
    }

    private static int scoreMethod(MethodNode oldMethod, MethodNode candidate, Map<String, String> classMap) {
        int score = 0;
        if (oldMethod.instructions.size() == candidate.instructions.size()) score += 20;
        score += Objects.equals(methodFingerprint(oldMethod, classMap), methodFingerprint(candidate, Map.of())) ? 80 : 0;
        score += multisetIntersection(methodConstants(oldMethod), methodConstants(candidate)) * 10;
        return score;
    }

    private static Multiplier multiplierForNamedField(String className, JarModel jar, ClassNode owner, FieldNode field) {
        if (("ClientPacket".equals(className) || "ServerPacket".equals(className))
                && ("I".equals(field.desc))
                && isConstructorPacketField(owner, field)) {
            Number setter = constructorSetterMultiplier(owner, field.name, field.desc);
            if (setter instanceof Integer) {
                return new Multiplier(modInverseInt(setter.intValue()), setter);
            }
        }
        return inferMultiplier(jar, owner.name, field.name, field.desc);
    }

    private static boolean isConstructorPacketField(ClassNode owner, FieldNode field) {
        FieldNode id = fieldAssignedFromConstructorVar(owner, "I", 1);
        FieldNode length = fieldAssignedFromConstructorVar(owner, "I", 2);
        return (id != null && id.name.equals(field.name)) || (length != null && length.name.equals(field.name));
    }

    private static Integer constructorSetterMultiplier(ClassNode owner, String name, String desc) {
        if (!"I".equals(desc)) return null;
        for (MethodNode method : owner.methods) {
            if (!"<init>".equals(method.name)) continue;
            AbstractInsnNode[] insns = method.instructions.toArray();
            for (int i = 0; i < insns.length; i++) {
                if (!(insns[i] instanceof FieldInsnNode) || insns[i].getOpcode() != Opcodes.PUTFIELD) continue;
                FieldInsnNode fin = (FieldInsnNode) insns[i];
                if (!fin.owner.equals(owner.name) || !fin.name.equals(name) || !fin.desc.equals(desc)) continue;
                Number n = multiplicationConstantBefore(insns, i, desc);
                return n == null ? null : n.intValue();
            }
        }
        return null;
    }

    private static int modInverseInt(int value) {
        int inverse = value;
        for (int i = 0; i < 5; i++) {
            inverse *= 2 - value * inverse;
        }
        return inverse;
    }

    private static Multiplier inferMultiplier(JarModel jar, String owner, String name, String desc) {
        if (!desc.equals("I") && !desc.equals("J")) {
            return new Multiplier(null, null);
        }

        Map<Number, Integer> getters = new LinkedHashMap<>();
        Map<Number, Integer> setters = new LinkedHashMap<>();
        for (ClassNode cn : jar.classes.values()) {
            for (MethodNode mn : cn.methods) {
                AbstractInsnNode[] insns = mn.instructions.toArray();
                for (int i = 0; i < insns.length; i++) {
                    AbstractInsnNode insn = insns[i];
                    if (!(insn instanceof FieldInsnNode)) {
                        continue;
                    }
                    FieldInsnNode fin = (FieldInsnNode) insn;
                    if (!fin.owner.equals(owner) || !fin.name.equals(name) || !fin.desc.equals(desc)) {
                        continue;
                    }
                    if (insn.getOpcode() == Opcodes.GETFIELD || insn.getOpcode() == Opcodes.GETSTATIC) {
                        Number n = multiplicationConstantAfter(insns, i, desc);
                        if (n != null) getters.merge(n, 1, Integer::sum);
                    } else if (insn.getOpcode() == Opcodes.PUTFIELD || insn.getOpcode() == Opcodes.PUTSTATIC) {
                        Number n = multiplicationConstantBefore(insns, i, desc);
                        if (n != null) setters.merge(n, 1, Integer::sum);
                    }
                }
            }
        }
        return new Multiplier(best(getters), best(setters));
    }

    private static Number multiplicationConstantAfter(AbstractInsnNode[] insns, int index, String desc) {
        for (int i = index + 1; i < Math.min(insns.length, index + 5); i++) {
            Number n = numericConstant(insns[i]);
            if (n == null) {
                continue;
            }
            int next = nextOpcode(insns, i + 1);
            if (desc.equals("I") && next == Opcodes.IMUL) return n.intValue();
            if (desc.equals("J") && next == Opcodes.LMUL) return n.longValue();
        }
        return null;
    }

    private static Number multiplicationConstantBefore(AbstractInsnNode[] insns, int index, String desc) {
        for (int i = index - 1; i >= Math.max(0, index - 5); i--) {
            Number n = numericConstant(insns[i]);
            if (n == null) {
                continue;
            }
            if (hasOpcodeBetween(insns, i + 1, index, desc.equals("I") ? Opcodes.IMUL : Opcodes.LMUL)) {
                return desc.equals("I") ? n.intValue() : n.longValue();
            }
        }
        return null;
    }

    private static boolean hasOpcodeBetween(AbstractInsnNode[] insns, int start, int endExclusive, int opcode) {
        for (int i = start; i < endExclusive && i < insns.length; i++) {
            if (insns[i].getOpcode() == opcode) return true;
        }
        return false;
    }

    private static Integer inferGarbageValue(JMethod oldMethod, MethodNode newMethod) {
        if (oldMethod.getGarbageValue() == null) {
            return null;
        }
        Integer anchored = anchoredGarbageValue(oldMethod.getName(), newMethod);
        if (anchored != null) {
            return anchored;
        }
        Type[] args = Type.getArgumentTypes(newMethod.desc);
        if (args.length == 0) {
            return null;
        }
        Type last = args[args.length - 1];
        if (last.getSort() != Type.BYTE && last.getSort() != Type.SHORT && last.getSort() != Type.INT) {
            return null;
        }
        return oldMethod.getGarbageValue().intValue();
    }

    private static Integer anchoredGarbageValue(String methodName, MethodNode method) {
        if ("doAction".equals(methodName)) {
            Integer value = storedGuardConstant(method);
            return value != null ? value : 910500823;
        }
        if ("getPacketBufferNode".equals(methodName)) {
            return -46;
        }
        if ("addNode".equals(methodName)) {
            return -10;
        }
        if ("setLoginIndex".equals(methodName)) {
            return -93;
        }
        return null;
    }

    private static Integer storedGuardConstant(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        Map<Integer, Integer> localConstants = new HashMap<>();
        for (int i = 0; i + 1 < insns.length; i++) {
            Number n = numericConstant(insns[i]);
            if (n != null && insns[i + 1] instanceof VarInsnNode && insns[i + 1].getOpcode() == Opcodes.ISTORE) {
                localConstants.put(((VarInsnNode) insns[i + 1]).var, n.intValue());
            }
        }
        Map<Integer, Integer> guardUses = new HashMap<>();
        for (int i = 0; i < insns.length; i++) {
            if (!(insns[i] instanceof VarInsnNode) || insns[i].getOpcode() != Opcodes.ILOAD) {
                continue;
            }
            int var = ((VarInsnNode) insns[i]).var;
            if (!localConstants.containsKey(var)) {
                continue;
            }
            int next = nextOpcode(insns, i + 1);
            int afterConstant = numericConstantOpcodeAfter(insns, i + 1);
            if ((next >= Opcodes.IFEQ && next <= Opcodes.IF_ACMPNE)
                    || (afterConstant >= Opcodes.IFEQ && afterConstant <= Opcodes.IF_ACMPNE)) {
                guardUses.merge(var, 1, Integer::sum);
            }
        }
        return guardUses.entrySet().stream()
                .max(Map.Entry.<Integer, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                .map(e -> localConstants.get(e.getKey()))
                .orElse(null);
    }

    private static int numericConstantOpcodeAfter(AbstractInsnNode[] insns, int start) {
        for (int i = start; i < insns.length; i++) {
            int opcode = insns[i].getOpcode();
            if (opcode < 0) {
                continue;
            }
            if (numericConstant(insns[i]) != null) {
                return nextOpcode(insns, i + 1);
            }
            return opcode;
        }
        return -1;
    }

    private static Number best(Map<Number, Integer> counts) {
        return counts.entrySet().stream()
                .max(Map.Entry.<Number, Integer>comparingByValue().thenComparing(e -> e.getKey().longValue()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static int nextOpcode(AbstractInsnNode[] insns, int start) {
        for (int i = start; i < insns.length; i++) {
            int opcode = insns[i].getOpcode();
            if (opcode >= 0) {
                return opcode;
            }
        }
        return -1;
    }

    private static Number numericConstant(AbstractInsnNode insn) {
        if (insn instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) insn).cst;
            return cst instanceof Number ? (Number) cst : null;
        }
        int opcode = insn.getOpcode();
        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
            return opcode == Opcodes.ICONST_M1 ? -1 : opcode - Opcodes.ICONST_0;
        }
        if (insn instanceof IntInsnNode) {
            return ((IntInsnNode) insn).operand;
        }
        return null;
    }

    private static List<String> fieldShapeMultiset(ClassNode cn, Map<String, String> classMap) {
        return cn.fields.stream().map(f -> fieldShape(f, classMap)).collect(Collectors.toList());
    }

    private static List<String> methodShapeMultiset(ClassNode cn, Map<String, String> classMap) {
        return cn.methods.stream().map(m -> methodShape(m, classMap)).collect(Collectors.toList());
    }

    private static String fieldShape(FieldNode fn, Map<String, String> classMap) {
        return (isStatic(fn.access) ? "S:" : "I:") + normalizeDesc(fn.desc, classMap);
    }

    private static String methodShape(MethodNode mn, Map<String, String> classMap) {
        return (isStatic(mn.access) ? "S:" : "I:") + specialName(mn.name) + normalizeDesc(mn.desc, classMap);
    }

    private static String specialName(String name) {
        return name.startsWith("<") ? name : "*";
    }

    private static List<String> classConstants(ClassNode cn) {
        List<String> out = new ArrayList<>();
        for (MethodNode mn : cn.methods) {
            out.addAll(methodConstants(mn));
        }
        return out;
    }

    private static List<String> methodConstants(MethodNode mn) {
        List<String> out = new ArrayList<>();
        for (AbstractInsnNode insn : mn.instructions) {
            if (insn instanceof LdcInsnNode) {
                Object cst = ((LdcInsnNode) insn).cst;
                if (cst instanceof String) out.add("S:" + cst);
                else if (cst instanceof Integer || cst instanceof Long) out.add("N:" + cst);
            }
        }
        return out;
    }

    private static List<String> methodFingerprints(ClassNode cn, Map<String, String> classMap) {
        return cn.methods.stream().map(m -> methodFingerprint(m, classMap)).collect(Collectors.toList());
    }

    private static String methodFingerprint(MethodNode mn, Map<String, String> classMap) {
        StringBuilder sb = new StringBuilder(methodShape(mn, classMap));
        int count = 0;
        for (AbstractInsnNode insn : mn.instructions) {
            int opcode = insn.getOpcode();
            if (opcode < 0) {
                continue;
            }
            sb.append('|').append(opcode);
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) insn;
                sb.append(':').append(normalizeDesc(min.desc, classMap));
            } else if (insn instanceof FieldInsnNode) {
                FieldInsnNode fin = (FieldInsnNode) insn;
                sb.append(':').append(normalizeDesc(fin.desc, classMap));
            } else if (insn instanceof TypeInsnNode) {
                sb.append(':').append(normalizeInternalName(((TypeInsnNode) insn).desc, classMap));
            } else if (insn instanceof LdcInsnNode) {
                Object cst = ((LdcInsnNode) insn).cst;
                if (cst instanceof String) sb.append(":S=").append(cst);
                else if (cst instanceof Number) sb.append(":N");
            }
            if (++count > 500) {
                break;
            }
        }
        return sb.toString();
    }

    private static String normalizeDesc(String desc, Map<String, String> classMap) {
        org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(desc);
        if (type.getSort() == org.objectweb.asm.Type.METHOD) {
            StringBuilder sb = new StringBuilder("(");
            for (org.objectweb.asm.Type arg : type.getArgumentTypes()) {
                sb.append(normalizeType(arg, classMap));
            }
            return sb.append(')').append(normalizeType(type.getReturnType(), classMap)).toString();
        }
        return normalizeType(type, classMap);
    }

    private static String normalizeType(org.objectweb.asm.Type type, Map<String, String> classMap) {
        if (type.getSort() == org.objectweb.asm.Type.ARRAY) {
            return "[".repeat(type.getDimensions()) + normalizeType(type.getElementType(), classMap);
        }
        if (type.getSort() == org.objectweb.asm.Type.OBJECT) {
            return "L" + normalizeInternalName(type.getInternalName(), classMap) + ";";
        }
        return type.getDescriptor();
    }

    private static String normalizeInternalName(String name, Map<String, String> classMap) {
        if (name.startsWith("java/") || name.startsWith("javax/") || name.startsWith("net/runelite/")
                || name.startsWith("net/solace/")) {
            return name;
        }
        if (classMap.isEmpty()) {
            return name;
        }
        return classMap.getOrDefault(name, "?");
    }

    private static int multisetIntersection(List<String> a, List<String> b) {
        Map<String, Integer> counts = new HashMap<>();
        for (String s : a) counts.merge(s, 1, Integer::sum);
        int total = 0;
        for (String s : b) {
            int c = counts.getOrDefault(s, 0);
            if (c > 0) {
                total++;
                counts.put(s, c - 1);
            }
        }
        return total;
    }

    private static Map<String, String> invert(Map<String, String> map) {
        Map<String, String> out = new HashMap<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            out.put(e.getValue(), e.getKey());
        }
        return out;
    }

    private static boolean isStatic(int access) {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static final class JarModel {
        final Map<String, ClassNode> classes;

        private JarModel(Map<String, ClassNode> classes) {
            this.classes = classes;
        }

        static JarModel load(Path path) throws IOException {
            Map<String, ClassNode> classes = new LinkedHashMap<>();
            try (JarFile jar = new JarFile(path.toFile())) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }
                    ClassReader reader = new ClassReader(jar.getInputStream(entry));
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_FRAMES);
                    classes.put(node.name, node);
                }
            }
            return new JarModel(classes);
        }
    }

    private static final class ClassMatch {
        final String name;
        final int score;

        ClassMatch(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private static final class MemberMap<T> {
        final Map<String, T> byOldNameDesc = new HashMap<>();
    }

    private static final class MethodMatch {
        final ClassNode owner;
        final MethodNode method;

        MethodMatch(ClassNode owner, MethodNode method) {
            this.owner = owner;
            this.method = method;
        }
    }

    private static final class FieldMatch {
        final ClassNode owner;
        final FieldNode field;

        FieldMatch(ClassNode owner, FieldNode field) {
            this.owner = owner;
            this.field = field;
        }
    }

    private static final class FieldScore {
        final FieldMatch match;
        final int score;

        FieldScore(FieldMatch match, int score) {
            this.match = match;
            this.score = score;
        }
    }

    private static final class AccountTypeRefFeatures {
        int enumRefs;
        int clientRefs;
        int otherRefs;
        int clinitRefs;
    }

    private static final class Multiplier {
        final Number getter;
        final Number setter;

        Multiplier(Number getter, Number setter) {
            this.getter = getter;
            this.setter = setter;
        }
    }
}
