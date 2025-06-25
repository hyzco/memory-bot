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

    public PluginClassLoader(URL[] urls) {
        super(urls, PluginClassLoader.class.getClassLoader()); // important: set parent!
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (PROTECTED.stream().anyMatch(p -> p.test(name)))
            throw new ClassNotFoundException(name + " is a protected class");

        // Intercept plugin classes only
        if (isPluginClass(name)) {
            try {
                String path = name.replace('.', '/') + ".class";
                InputStream classStream = getResourceAsStream(path);
                if (classStream == null) return super.loadClass(name, resolve);

                // Read original class bytecode
                ClassReader reader = new ClassReader(classStream);
                ClassWriter writer = new ClassWriter(reader, 0);

                // Remap VerifierChecker reference
                ClassRemapper remapper = new ClassRemapper(writer, new SimpleRemapper(
                        "eu/darkbot/popcorn/def/VerifierChecker",
                        "com/github/manolo8/darkbot/extensions/util/VerifierChecker"
                ));

                reader.accept(remapper, 0);
                byte[] newBytecode = writer.toByteArray();

                // Define the modified class
                Class<?> clazz = defineClass(name, newBytecode, 0, newBytecode.length);
                if (resolve) resolveClass(clazz);
                return clazz;
            } catch (IOException e) {
                throw new ClassNotFoundException("Failed to remap class", e);
            }
        }

        // Default loader
        return super.loadClass(name, resolve);
    }

    private boolean isPluginClass(String name) {
        return name.startsWith("eu.darkbot.plugins") || name.contains("popcorn"); // adapt as needed
    }


    @Override
    protected String findLibrary(String libname) {
        return libname;
    }
}
