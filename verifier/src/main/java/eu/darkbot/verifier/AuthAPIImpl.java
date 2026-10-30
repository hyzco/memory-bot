package eu.darkbot.verifier;

import com.github.manolo8.darkbot.utils.AuthAPI;

import java.io.IOException;
import java.util.jar.JarFile;

public class AuthAPIImpl implements AuthAPI {

    @Override
    public void setupAuth() {
        // No-op or simple console output
        System.out.println("Auth setup stub");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean isDonor() {
        return true;
    }

    @Override
    public boolean requireDonor() {
        return true;
    }

    @Override
    public String getAuthId() {
        return "fake-auth-id";
    }

    @Override
    public Boolean checkPluginJarSignature(JarFile jarFile) {
        return true;
    }

}
