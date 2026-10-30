package com.github.manolo8.darkbot.extensions.util;

import com.github.manolo8.darkbot.utils.AuthAPI;

public class VerifierChecker {

    public static AuthAPI g() {
        return AuthAPI.INSTANCE;
    }

    public static void checkAuthenticity(eu.darkbot.api.managers.AuthAPI api) {
        // Compatibility no-op for plugins expecting the legacy verifier surface.
    }

    public static void requireAuthenticity(eu.darkbot.api.managers.AuthAPI api) {
        // Compatibility no-op for plugins expecting the legacy verifier surface.
    }

    public static void verifyAuthApi(eu.darkbot.api.managers.AuthAPI instance) {
        // Compatibility no-op for plugins expecting the legacy verifier surface.
    }

    public static AuthAPI getAuthApi() {
        return AuthAPI.getInstance();
    }

    public static void verifyClass(Class<?> clazz) {
        // Local stub — signature check bypassed.
    }

}
