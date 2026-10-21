/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.manolo8.darkbot.Main
 *  com.github.manolo8.darkbot.extensions.features.FeatureDefinition
 *  eu.darkbot.api.PluginAPI
 *  eu.darkbot.api.managers.AuthAPI
 *  eu.darkbot.leanon00.e.p
 */
package eu.darkbot.leanon00.e;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.extensions.features.FeatureDefinition;
import eu.darkbot.api.PluginAPI;
import eu.darkbot.api.managers.AuthAPI;
import eu.darkbot.leanon00.e.o;
import eu.darkbot.leanon00.e.p;
import java.util.Objects;

public class v {
    public static boolean addicted(PluginAPI pluginAPI, Object object) {
        Main main = (Main)pluginAPI.requireInstance(Main.class);
        String string = ((AuthAPI)pluginAPI.requireAPI(AuthAPI.class)).getAuthId();
        v.checkAddicted(main, string, object);
        return true;
    }

    public static void donor(PluginAPI pluginAPI, Object object) {
        Main main = (Main)pluginAPI.requireInstance(Main.class);
        AuthAPI authAPI = (AuthAPI)pluginAPI.requireAPI(AuthAPI.class);
        if (!authAPI.isAuthenticated()) {
            authAPI.setupAuth();
        }
        v.checkDonor(main, authAPI.getAuthId(), object);
    }

    private static void checkDonor(Main main, String string, Object object) {
        p p2 = o.Get(string, true);
        FeatureDefinition featureDefinition = Objects.requireNonNull(main.featureRegistry.getFeatureDefinition(object));
        if (p2.don) {
            featureDefinition.addInfo("Verified Donor", "Can use the feature!");
        } else {
            featureDefinition.getIssues().addFailure("Super Leaner role required...", "Contact !leanon \nDonate \nRefresh plugins. \nmember: " + p2.mem + "\n" + p2.info);
            featureDefinition.setEnabled(false);
        }
    }

    private static void checkAddicted(Main main, String string, Object object) {
        p p2 = o.Get(string, true);
        FeatureDefinition featureDefinition = Objects.requireNonNull(main.featureRegistry.getFeatureDefinition(object));
        if (p2.addicted) {
            featureDefinition.addInfo("Verified Addicted", "Can use the feature!");
        } else {
            featureDefinition.getIssues().addFailure("Addicted role required...", "Contact !leanon \nDonate \nRefresh plugins. \nmember: " + p2.mem + "\n" + p2.info);
            featureDefinition.setEnabled(false);
        }
    }
}
