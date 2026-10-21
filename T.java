import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TCP Proxy — captures DarkOrbit game protocol traffic.
 *
 * Usage:
 *   1. Run:  java T.java [localPort] [remoteHost] [remotePort]
 *      Defaults: localPort=6124, remoteHost=gameserver-577-04-public.prod.darkorbit.com, remotePort=6124
 *
 *   2. Point KekkaPlayer/game to connect to localhost:6124 instead of the real server
 *      (e.g. edit hosts file: 127.0.0.1 gameserver-577-04-public.prod.darkorbit.com)
 *
 *   3. Traffic is logged to console + d:\DO\logs\proxy_capture.log
 */
public class T {
    static String REMOTE_HOST = "gameserver-577-04-public.prod.darkorbit.com";
    static int REMOTE_PORT = 6124;
    static int LOCAL_PORT = 6124;

    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    static final AtomicInteger SESSION_COUNT = new AtomicInteger(0);
    static PrintWriter logWriter;

    public static void main(String[] args) throws Exception {
        if (args.length >= 1) LOCAL_PORT = Integer.parseInt(args[0]);
        if (args.length >= 2) REMOTE_HOST = args[1];
        if (args.length >= 3) REMOTE_PORT = Integer.parseInt(args[2]);

        // Open log file
        var logFile = new File("d:\\DO\\logs\\proxy_capture.log");
        logFile.getParentFile().mkdirs();
        logWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)), true);

        log("=============================================================");
        log("TCP PROXY started — listening on localhost:" + LOCAL_PORT);
        log("Forwarding to " + REMOTE_HOST + ":" + REMOTE_PORT);
        log("=============================================================");

        try (var server = new ServerSocket(LOCAL_PORT)) {
            server.setReuseAddress(true);
            System.out.println("Proxy listening on localhost:" + LOCAL_PORT + " -> " + REMOTE_HOST + ":" + REMOTE_PORT);
            System.out.println("Log: " + logFile.getAbsolutePath());
            System.out.println("Waiting for connections...\n");

            while (true) {
                Socket client = server.accept();
                int sessionId = SESSION_COUNT.incrementAndGet();
                log("[S" + sessionId + "] Client connected from " + client.getRemoteSocketAddress());
                new Thread(() -> handleSession(sessionId, client), "proxy-session-" + sessionId).start();
            }
        }
    }

    static void handleSession(int id, Socket client) {
        String tag = "S" + id;
        try {
            client.setTcpNoDelay(true);
            Socket remote = new Socket();
            remote.setTcpNoDelay(true);
            remote.connect(new InetSocketAddress(REMOTE_HOST, REMOTE_PORT), 10000);
            log("[" + tag + "] Connected to remote " + REMOTE_HOST + ":" + REMOTE_PORT);

            AtomicLong clientBytes = new AtomicLong(0);
            AtomicLong serverBytes = new AtomicLong(0);

            // Client -> Server
            Thread c2s = new Thread(() -> {
                try {
                    pipe(tag, "C->S", client.getInputStream(), remote.getOutputStream(), clientBytes);
                } catch (IOException e) {
                    log("[" + tag + "] C->S closed: " + e.getMessage());
                } finally {
                    close(remote);
                }
            }, tag + "-c2s");

            // Server -> Client
            Thread s2c = new Thread(() -> {
                try {
                    pipe(tag, "S->C", remote.getInputStream(), client.getOutputStream(), serverBytes);
                } catch (IOException e) {
                    log("[" + tag + "] S->C closed: " + e.getMessage());
                } finally {
                    close(client);
                }
            }, tag + "-s2c");

            c2s.setDaemon(true);
            s2c.setDaemon(true);
            c2s.start();
            s2c.start();

            c2s.join();
            s2c.join();

            log("[" + tag + "] Session ended — client sent " + clientBytes.get() + " bytes, server sent " + serverBytes.get() + " bytes");
        } catch (Exception e) {
            log("[" + tag + "] ERROR: " + e);
        } finally {
            close(client);
        }
    }

    static void pipe(String tag, String dir, InputStream in, OutputStream out, AtomicLong counter) throws IOException {
        byte[] buf = new byte[65536];
        int pktNum = 0;
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
            out.flush();
            counter.addAndGet(n);
            pktNum++;
            logPacket(tag, dir, pktNum, buf, n);
        }
    }

    static void logPacket(String tag, String dir, int pktNum, byte[] data, int len) {
        var sb = new StringBuilder();
        String ts = LocalDateTime.now().format(FMT);
        sb.append(String.format("[%s] [%s] %s pkt#%d len=%d", ts, tag, dir, pktNum, len));

        // Check if it's the Flash policy request/response (text)
        boolean isText = true;
        for (int i = 0; i < Math.min(len, 20); i++) {
            byte b = data[i];
            if (b != 0 && (b < 0x20 || b > 0x7E) && b != '\n' && b != '\r' && b != '\t') {
                isText = false;
                break;
            }
        }

        if (isText && len < 1024) {
            sb.append("\n  TXT: ").append(new String(data, 0, len, StandardCharsets.UTF_8).replace("\0", "\\0").replace("\n", "\\n"));
        }

        // Always show hex dump (first 256 bytes)
        int hexLen = Math.min(len, 256);
        sb.append("\n  HEX: ");
        for (int i = 0; i < hexLen; i++) {
            if (i > 0 && i % 32 == 0) sb.append("\n       ");
            sb.append(String.format("%02X ", data[i]));
        }
        if (len > 256) sb.append("... (").append(len - 256).append(" more)");

        // Try to identify packet structure (first 2/4 bytes = possible length/command ID)
        if (len >= 4 && !isText) {
            int short0 = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
            int int0 = ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
            sb.append(String.format("\n  BE_SHORT[0]=%d BE_INT[0]=%d", short0, int0));

            // Little-endian too
            int leShort0 = (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
            int leInt0 = (data[0] & 0xFF) | ((data[1] & 0xFF) << 8) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 24);
            sb.append(String.format(" LE_SHORT[0]=%d LE_INT[0]=%d", leShort0, leInt0));
        }

        log(sb.toString());
    }

    static synchronized void log(String msg) {
        System.out.println(msg);
        if (logWriter != null) {
            logWriter.println(LocalDateTime.now().format(FMT) + " " + msg);
            logWriter.flush();
        }
    }

    static void close(Socket s) {
        try { if (!s.isClosed()) s.close(); } catch (Exception ignored) {}
    }
}
