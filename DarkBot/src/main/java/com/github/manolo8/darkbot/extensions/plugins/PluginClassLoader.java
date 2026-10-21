package com.github.manolo8.darkbot.extensions.plugins;

import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.commons.ClassRemapper;
import net.bytebuddy.jar.asm.commons.SimpleRemapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginClassLoader extends URLClassLoader {

    private static final List<Predicate<String>> PROTECTED = Stream.of(
            "java.lang.reflect.*",
            "java.lang.Thread",
            "java.awt.TrayIcon",
            "java.awt.SystemTray",
            "java.lang.Runtime",
            "java.lang.ProcessBuilder",
            "*.ReflectionUtils")
            .map(PluginClassLoader::toMatcher)
            .collect(Collectors.toList());

    private static Predicate<String> toMatcher(String pattern) {
        if (pattern.endsWith("*")) {
            String start = pattern.substring(0, pattern.length() - 1);
            return s -> s.startsWith(start);
        } else if (pattern.startsWith("*")) {
            String end = pattern.substring(1);
            return s -> s.endsWith(end);
        } else {
            return s -> s.equals(pattern);
        }
    }

    PluginClassLoader(URL[] urls) {
        super(urls, PluginClassLoader.class.getClassLoader());
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (PROTECTED.stream().anyMatch(p -> p.test(name)))
            throw new ClassNotFoundException(name + " is a protected class");

        if (isPluginClass(name)) {
            Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                if (resolve) resolveClass(loaded);
                return loaded;
            }

            try {
                String path = name.replace('.', '/') + ".class";
                try (InputStream classStream = getResourceAsStream(path)) {
                    if (classStream == null) return super.loadClass(name, resolve);

                    ClassReader reader = new ClassReader(classStream);
                    ClassWriter writer = new ClassWriter(reader, 0);

                        SimpleRemapper combinedRemapper = new SimpleRemapper(Map.ofEntries(
                            Map.entry("eu/darkbot/popcorn/def/VerifierChecker", "com/github/manolo8/darkbot/extensions/util/VerifierChecker"),
                            Map.entry("eu/darkbot/popcorn/don/VerifierChecker", "com/github/manolo8/darkbot/extensions/util/VerifierChecker"),
                            Map.entry("eu/darkbot/leanon00/e/m", "com/github/manolo8/darkbot/extensions/util/VerifierChecker"),
                            Map.entry("eu/darkbot/leanon00/e/u", "com/github/manolo8/darkbot/extensions/util/VerifierChecker"),
                            Map.entry("com/punikekk/d", "com/github/manolo8/darkbot/extensions/util/VerifierChecker"),
                            Map.entry("eu/darkbot/leanon00/e/g", "com/github/manolo8/darkbot/extensions/util/g"),
                            Map.entry("eu/darkbot/leanon00/e/o", "com/github/manolo8/darkbot/extensions/util/g"),
                            Map.entry("eu/darkbot/leanon00/e/h", "com/github/manolo8/darkbot/extensions/util/h"),
                            Map.entry("eu/darkbot/leanon00/e/p", "com/github/manolo8/darkbot/extensions/util/h"),
                            Map.entry("com/deeme/types/VerifierChecker", "com/github/manolo8/darkbot/extensions/util/VerifierChecker"),
                            Map.entry("com/deemeplus/utils/Backpage", "com/github/manolo8/darkbot/extensions/util/DeemePlusBackpage")
                        ));

                    ClassRemapper remapper = new ClassRemapper(writer, combinedRemapper);
                    reader.accept(remapper, 0);

                    byte[] newBytecode = writer.toByteArray();
                    Class<?> clazz = defineClass(name, newBytecode, 0, newBytecode.length);
                    if (resolve) resolveClass(clazz);
                    return clazz;
                }
            } catch (IOException e) {
                throw new ClassNotFoundException("Failed to remap class", e);
            }
        }

        return super.loadClass(name, resolve);
    }

    private boolean isPluginClass(String name) {
        return name.startsWith("eu.darkbot.plugins")
                || name.contains("popcorn")
                || name.contains("punikekk")
                || name.contains("leanon00")
                || name.contains("deeme");
    }

    @Override
    protected String findLibrary(String libname) {
        throw new SecurityException("Library loading is not allowed from plugins");
    }

}
