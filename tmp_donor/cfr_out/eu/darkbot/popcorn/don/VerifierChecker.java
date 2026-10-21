/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  eu.darkbot.api.managers.AuthAPI
 */
package eu.darkbot.popcorn.don;

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

public class VerifierChecker {
    private static final String META_INF = "META-INF/";
    private static final String SIG_PREFIX = "META-INF/SIG-";
    private static final byte[] POPCORN_PUB = Base64.getDecoder().decode("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzqOpdk4bdoMlk3IkDaHFSOpwyYmpfACHCuhNDiml13Wf9J4D9g4kszOV3Qz+FT1jdYO36pWCxI01Mr03dPLky9COwD//dQM/KRFBe7Z0wRsC91n5fprgWIkwdKs79en6vmynyyPi5hAgwpifKm4o9DP5xR0YP/KRoPH8ZekS+STBxPsLdy/BeBiFFFgNQ0usRNIkLBKYWFJ3A3br4QkVicOLvycHKrfsN9K2Ly25VXyYo/GJdeEY30ixKhsCdo9xc50ERVuEVkzqlqLUSFDgHyFAO1o91QIhG+G0GURlI8iSt/b5cn39DM0OtkL+1TqqwT4NJqBH8nHSok8lReu1o/iMu9VbrFyJTUK0qUjVhnySJQV3i5oV0oxwqPodDihvmNUhMUel5gM/yRnloKKEYk+74MLdClgcFWmbEYFUQF32vxdkKpGYYRmzH0Y8+pGKE8nBbe1/eKg2HVu42vStb/yKp7DpxQ05UovJ5nrXA7lUfwCwBOwzOmCjn3AKNhH+Hbg/tutwZn5KNU4zJCRUEM4FLkCCJMEDJTGnpjxNO/vUMEm+Co6RgrD1vBIgRzNxaYh1BInbDdlKncXhysHNR5b6Et2POyCrlrM4flvFvTg42/zbI1ElKgEFNbhujdP5fBtxeD1hkc5UUa8JtYHsHa0LBrTUfnr3F29rRwHFpFUCAwEAAQ==");

    public static void verifyAuthApi(AuthAPI instance) {
        try (JarFile jf = new JarFile(VerifierChecker.findPathJar(instance.getClass()), true);){
            Vector<JarEntry> entriesVec = new Vector<JarEntry>();
            byte[] buffer = new byte[8192];
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                entriesVec.addElement(je);
                InputStream is = jf.getInputStream(je);
                try {
                    while (is.read(buffer, 0, buffer.length) != -1) {
                    }
                }
                finally {
                    if (is == null) continue;
                    is.close();
                }
            }
            Manifest man = jf.getManifest();
            if (man == null) {
                throw new SecurityException("Verifier not signed");
            }
            Enumeration e = entriesVec.elements();
            HashSet<Certificate> allowedCerts = new HashSet<Certificate>();
            while (e.hasMoreElements()) {
                Boolean signed;
                JarEntry je = (JarEntry)e.nextElement();
                String name = je.getName();
                if (je.isDirectory() || VerifierChecker.signatureRelated(name) || (signed = VerifierChecker.checkCertificates(je.getCertificates(), allowedCerts)) != null && signed.booleanValue()) continue;
                throw new SecurityException("Verifier not properly signed");
            }
        }
        catch (Exception e) {
            throw new SecurityException("Failed to check verifier signature", e);
        }
    }

    private static Boolean checkCertificates(Certificate[] certs, Set<Certificate> allowedCerts) {
        if (certs == null || certs.length == 0) {
            return null;
        }
        for (Certificate cert : certs) {
            if (allowedCerts.contains(cert)) {
                return true;
            }
            if (!Arrays.equals(POPCORN_PUB, cert.getPublicKey().getEncoded())) continue;
            allowedCerts.add(cert);
            return true;
        }
        return false;
    }

    private static boolean signatureRelated(String name) {
        String ucName = name.toUpperCase(Locale.ENGLISH);
        if (ucName.equals("META-INF/MANIFEST.MF") || ucName.equals(META_INF) || ucName.startsWith(SIG_PREFIX) && ucName.indexOf("/") == ucName.lastIndexOf("/")) {
            return true;
        }
        return ucName.startsWith(META_INF) && (ucName.endsWith(".SF") || ucName.endsWith(".DSA") || ucName.endsWith(".RSA") || ucName.endsWith(".EC")) && ucName.indexOf("/") == ucName.lastIndexOf("/");
    }

    private static String findPathJar(Class<?> context) throws IllegalStateException {
        String rawName = context.getName();
        int idx = rawName.lastIndexOf(46);
        String classFileName = (idx == -1 ? rawName : rawName.substring(idx + 1)) + ".class";
        String uri = context.getResource(classFileName).toString();
        if (uri.startsWith("file:")) {
            throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
        }
        if (!uri.startsWith("jar:file:")) {
            int idx2 = uri.indexOf(58);
            String protocol = idx2 == -1 ? "(unknown)" : uri.substring(0, idx2);
            throw new IllegalStateException("This class has been loaded remotely via the " + protocol + " protocol. Only loading from a jar on the local file system is supported.");
        }
        int idx3 = uri.indexOf(33);
        if (idx3 == -1) {
            throw new IllegalStateException("You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");
        }
        try {
            String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx3), Charset.defaultCharset().name());
            return new File(fileName).getAbsolutePath();
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalError("default charset doesn't exist. Your VM is borked.");
        }
    }
}
