/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.manolo8.darkbot.utils.Base64Utils
 *  eu.darkbot.leanon00.e.o$1
 *  eu.darkbot.leanon00.e.p
 */
package eu.darkbot.leanon00.e;

import com.github.manolo8.darkbot.utils.Base64Utils;
import eu.darkbot.leanon00.e.o;
import eu.darkbot.leanon00.e.p;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

public class o {
    private static String dI = "1007884280854220871";
    private static String addI = "1133882834692620349";
    private static p cR = new 1();

    private static p GetD(String string) {
        String string2 = string.split("-")[1];
        p p2 = new p();
        try {
            String string3;
            URL uRL = new URL(Base64Utils.decode((String)"aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvdjEwL2d1aWxkcy84MjE3MzAwNzMxNTY2NDg5NzEvbWVtYmVycy8=") + string2);
            HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
            httpURLConnection.setRequestProperty("Cookie", "__dcfduid=30f430561d4d11ed8e9b4eb210449b73; __sdcfduid=30f430561d4d11ed8e9b4eb210449b739b4046392439322b7a2f7eb171ea1dc838afabf8eec75a97ab292ecb9fbc78bb");
            httpURLConnection.setRequestProperty("Host", "<calculated when request is sent>");
            httpURLConnection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
            httpURLConnection.setRequestProperty("Accept", "*/*");
            httpURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");
            httpURLConnection.setRequestProperty("Connection", "keep-alive");
            httpURLConnection.setRequestProperty(Base64Utils.decode((String)"QXV0aG9yaXphdGlvbg=="), Base64Utils.decode((String)"Qm90") + " " + Base64Utils.decode((String)"TVRBd09UQXpOemd4TWpVM05UYzJORFV3TWcuRzF5cG4tLmoyVjgxUGxSbDhoMF9ERnlfR3UyTjRidGhjSGtndXZxdjAtSnJ3"));
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while ((string3 = bufferedReader.readLine()) != null) {
                stringBuilder.append(string3);
            }
            if (!stringBuilder.toString().contains("{\n    \"message\": \"Unknown User\",")) {
                p2.mem = true;
            }
            if (stringBuilder.toString().contains(dI)) {
                p2.don = true;
            }
            if (stringBuilder.toString().contains(addI)) {
                p2.addicted = true;
            }
            p2.info = stringBuilder.toString();
            bufferedReader.close();
            httpURLConnection.disconnect();
        }
        catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        o.save(p2.mem, p2.don, p2.addicted);
        return p2;
    }

    public static p Get(String string, boolean bl) {
        if (string == null) {
            System.out.println("Discord ID Null!!!");
            return cR;
        }
        Preferences preferences = Preferences.userNodeForPackage(o.class);
        return o.GetD(string);
    }

    private static void save(boolean bl, boolean bl2, boolean bl3) {
        Preferences preferences = Preferences.userNodeForPackage(o.class);
        preferences.putBoolean("dMem", bl);
        preferences.putBoolean("dDon", bl2);
        preferences.putBoolean("dAdd", bl3);
        preferences.putLong("dLast", System.currentTimeMillis());
    }
}
