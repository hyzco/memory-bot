package com.github.manolo8.darkbot.extensions.plugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

final class LocalPluginTrust {
    static final Path TRUST_FILE = PluginHandler.PLUGIN_PATH.resolve("trusted-plugins.txt");

    private final Set<String> trustedHashes = new HashSet<>();

    void reload() {
        trustedHashes.clear();
        if (!Files.exists(TRUST_FILE)) return;

        try {
            List<String> lines = Files.readAllLines(TRUST_FILE);
            for (String rawLine : lines) {
                String line = rawLine.trim().toLowerCase(Locale.ROOT);
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+", 2);
                if (parts[0].matches("[0-9a-f]{64}")) trustedHashes.add(parts[0]);
            }
        } catch (IOException e) {
            System.err.println("Failed to read trusted plugin hashes from " + TRUST_FILE + ": " + e.getMessage());
        }
    }

    Optional<String> getTrustedHash(File pluginFile) throws IOException {
        String hash = sha256(pluginFile.toPath());
        return trustedHashes.contains(hash) ? Optional.of(hash) : Optional.empty();
    }

    private String sha256(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                if (read > 0) digest.update(buffer, 0, read);
            }
            return toHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(Character.forDigit((value >> 4) & 0xF, 16));
            builder.append(Character.forDigit(value & 0xF, 16));
        }
        return builder.toString();
    }
}