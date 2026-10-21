/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  eu.darkbot.api.managers.AuthAPI
 */
package eu.darkbot.leanon00.e;

import eu.darkbot.api.managers.AuthAPI;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class u {
    private static final String META_INF = "META-INF/";
    private static final String SIG_PREFIX = "META-INF/SIG-";
    private static final byte[] POPCORN_PUB = Base64.getDecoder().decode("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzqOpdk4bdoMlk3IkDaHFSOpwyYmpfACHCuhNDiml13Wf9J4D9g4kszOV3Qz+FT1jdYO36pWCxI01Mr03dPLky9COwD//dQM/KRFBe7Z0wRsC91n5fprgWIkwdKs79en6vmynyyPi5hAgwpifKm4o9DP5xR0YP/KRoPH8ZekS+STBxPsLdy/BeBiFFFgNQ0usRNIkLBKYWFJ3A3br4QkVicOLvycHKrfsN9K2Ly25VXyYo/GJdeEY30ixKhsCdo9xc50ERVuEVkzqlqLUSFDgHyFAO1o91QIhG+G0GURlI8iSt/b5cn39DM0OtkL+1TqqwT4NJqBH8nHSok8lReu1o/iMu9VbrFyJTUK0qUjVhnySJQV3i5oV0oxwqPodDihvmNUhMUel5gM/yRnloKKEYk+74MLdClgcFWmbEYFUQF32vxdkKpGYYRmzH0Y8+pGKE8nBbe1/eKg2HVu42vStb/yKp7DpxQ05UovJ5nrXA7lUfwCwBOwzOmCjn3AKNhH+Hbg/tutwZn5KNU4zJCRUEM4FLkCCJMEDJTGnpjxNO/vUMEm+Co6RgrD1vBIgRzNxaYh1BInbDdlKncXhysHNR5b6Et2POyCrlrM4flvFvTg42/zbI1ElKgEFNbhujdP5fBtxeD1hkc5UUa8JtYHsHa0LBrTUfnr3F29rRwHFpFUCAwEAAQ==");

    public static void checkAuthenticity(AuthAPI authAPI) {
        u.verifyAuthApi(authAPI);
        if (!authAPI.isAuthenticated()) {
            authAPI.setupAuth();
        }
        authAPI.getAuthId();
    }

    public static void verifyAuthApi(AuthAPI authAPI) {
        try (JarFile jarFile = new JarFile(u.findPathJar(authAPI.getClass()), true);){
            Object object;
            Cloneable cloneable;
            Vector<Manifest> vector = new Vector<Manifest>();
            byte[] byArray = new byte[8192];
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                cloneable = enumeration.nextElement();
                vector.addElement((Manifest)cloneable);
                object = jarFile.getInputStream((ZipEntry)cloneable);
                try {
                    while (((InputStream)object).read(byArray, 0, byArray.length) != -1) {
                    }
                }
                finally {
                    if (object == null) continue;
                    ((InputStream)object).close();
                }
            }
            cloneable = jarFile.getManifest();
            if (cloneable == null) {
                throw new SecurityException("Verifier not signed");
            }
            object = vector.elements();
            HashSet<Certificate> hashSet = new HashSet<Certificate>();
            while (object.hasMoreElements()) {
                Boolean bl;
                JarEntry jarEntry = (JarEntry)object.nextElement();
                String string = jarEntry.getName();
                if (jarEntry.isDirectory() || u.signatureRelated(string) || (bl = u.checkCertificates(jarEntry.getCertificates(), hashSet)) != null && bl.booleanValue()) continue;
                throw new SecurityException("Verifier not properly signed");
            }
        }
        catch (Exception exception) {
            throw new SecurityException("Failed to check verifier signature", exception);
        }
    }

    private static Boolean checkCertificates(Certificate[] certificateArray, Set<Certificate> set) {
        if (certificateArray == null || certificateArray.length == 0) {
            return null;
        }
        for (Certificate certificate : certificateArray) {
            if (set.contains(certificate)) {
                return true;
            }
            if (!Arrays.equals(POPCORN_PUB, certificate.getPublicKey().getEncoded())) continue;
            set.add(certificate);
            return true;
        }
        return false;
    }

    private static boolean signatureRelated(String string) {
        String string2 = string.toUpperCase(Locale.ENGLISH);
        if (string2.equals("META-INF/MANIFEST.MF") || string2.equals(META_INF) || string2.startsWith(SIG_PREFIX) && string2.indexOf("/") == string2.lastIndexOf("/")) {
            return true;
        }
        return string2.startsWith(META_INF) && (string2.endsWith(".SF") || string2.endsWith(".DSA") || string2.endsWith(".RSA") || string2.endsWith(".EC")) && string2.indexOf("/") == string2.lastIndexOf("/");
    }

    private static String findPathJar(Class<?> clazz) {
        String string = clazz.getName();
        int n = string.lastIndexOf(46);
        String string2 = (n == -1 ? string : string.substring(n + 1)) + ".class";
        String string3 = clazz.getResource(string2).toString();
        if (string3.startsWith("file:")) {
            throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
        }
        if (!string3.startsWith("jar:file:")) {
            int n2 = string3.indexOf(58);
            String string4 = n2 == -1 ? "(unknown)" : string3.substring(0, n2);
            throw new IllegalStateException("This class has been loaded remotely via the " + string4 + " protocol. Only loading from a jar on the local file system is supported.");
        }
        int n3 = string3.indexOf(33);
        if (n3 == -1) {
            throw new IllegalStateException("You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");
        }
        try {
            String string5 = URLDecoder.decode(string3.substring("jar:file:".length(), n3), Charset.defaultCharset().name());
            return new File(string5).getAbsolutePath();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("default charset doesn't exist. Your VM is borked.");
        }
    }
}
