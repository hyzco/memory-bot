package com.github.manolo8.darkbot.extensions.util;

/**
 * Compat shim for plugins (e.g. PenguinPlugin) whose VerifierChecker.checkAuthenticity
 * returns the AuthAPI instance instead of void.  The class remapper in PluginClassLoader
 * redirects those plugin-internal references here so the method descriptor matches.
 */
public class VerifierCheckerCompat {

    public static eu.darkbot.api.managers.AuthAPI checkAuthenticity(eu.darkbot.api.managers.AuthAPI api) {
        return api;
    }

    public static void verifyAuthApi(eu.darkbot.api.managers.AuthAPI instance) {
        // no-op
    }

    public static void requireAuthenticity(eu.darkbot.api.managers.AuthAPI api) {
        // no-op
    }
}
