package com.github.manolo8.darkbot.extensions.util;

public final class DeemePlusBackpage {
    private DeemePlusBackpage() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendMessage(String title, String message) {
    }

    public static String getPlusMessageError(String userId, int ignored) {
        return "";
    }

    public static String getDiscordTagExternal(String userId) {
        return userId == null ? "id" : userId;
    }

    public static String getDiscordTag(String userId, int ignored) {
        return getDiscordTagExternal(userId);
    }

    public static synchronized boolean isDonorInternal(String userId, int ignored) {
        return true;
    }

    public static synchronized boolean isDonor(String userId, String token) {
        return true;
    }

    public static synchronized boolean isInDiscord(String userId) {
        return true;
    }

    public static void isDonorThrowError(String userId, int ignored) {
    }
}