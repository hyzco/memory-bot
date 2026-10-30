package com.github.manolo8.darkbot.tools;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import eu.darkbot.api.DarkMem;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone diagnostic tool (not part of the bot itself) for reverse-engineering memory offsets
 * against a specific Flash Player build. Reads via the same DarkMemAPI.dll the bot uses, but does
 * its OWN memory-region enumeration via raw Win32 VirtualQueryEx (through JNA) instead of relying
 * on DarkMemAPI.dll's built-in scan functions, which appear to be scoped to a specific module and
 * return nothing for this process. Run from the repo root (D:\DO) so lib/DarkMemAPI.dll resolves.
 *
 * Usage:
 *   java -cp DarkBot/build/libs/DarkBot-1.131.jar com.github.manolo8.darkbot.tools.MemScanner <pid> readraw <hexAddress>
 *   java -cp DarkBot/build/libs/DarkBot-1.131.jar com.github.manolo8.darkbot.tools.MemScanner <pid> ownscan <searchInt> [maxHits]
 *   java -cp DarkBot/build/libs/DarkBot-1.131.jar com.github.manolo8.darkbot.tools.MemScanner <pid> regions
 */
public class MemScanner {

    private static final int PAGE_READWRITE = 0x04;
    private static final int PAGE_EXECUTE_READWRITE = 0x40;
    private static final int PAGE_READONLY = 0x02;
    private static final int PAGE_EXECUTE_READ = 0x20;
    private static final int PAGE_GUARD = 0x100;
    private static final int MEM_COMMIT = 0x1000;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: MemScanner <pid> readraw <hexAddress>");
            System.err.println("       MemScanner <pid> ownscan <searchInt> [maxHits]");
            System.err.println("       MemScanner <pid> scanlog <hexMediatorAddr>");
            System.err.println("       MemScanner <pid> regions");
            System.exit(1);
        }

        long pid = Long.parseLong(args[0]);
        String mode = args[1];

        DarkMem mem = new DarkMem();
        System.out.println("DarkMemAPI version: " + mem.getVersion());
        mem.openProcess(pid);
        System.out.println("Opened process (DarkMemAPI) pid=" + pid);

        WinNT.HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(
                WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, (int) pid);
        boolean rawOk = hProcess != null;
        if (!rawOk) {
            System.err.println("OpenProcess (raw) failed, GetLastError=" + Native.getLastError() + " — modes requiring raw handle (regions/ownscan/patscan) will not work");
        } else {
            System.out.println("Opened process (raw Win32 handle) OK");
        }

        switch (mode) {
            case "readraw": {
                long addr = Long.parseUnsignedLong(args[2].replace("0x", ""), 16);
                readRaw(mem, addr);
                break;
            }
            case "regions": {
                if (!rawOk) { System.err.println("Raw handle required"); break; }
                for (Region r : enumerateRegions(hProcess)) {
                    System.out.println(r);
                }
                break;
            }
            case "patscan": {
                byte[] pat = com.github.manolo8.darkbot.core.BotInstaller.getMainApplicationPattern();
                int n = args.length > 2 ? Integer.parseInt(args[2]) : 10;
                int found = 0;
                for (Region r : enumerateRegions(hProcess)) {
                    if (!r.isReadableCommitted()) continue;
                    for (long cs = r.base; cs < r.base + r.size && found < n; cs += (1 << 20) - pat.length) {
                        int len = (int) Math.min(1 << 20, r.base + r.size - cs);
                        byte[] d;
                        try { d = mem.readBytes(cs, len); } catch (Throwable t) { continue; }
                        if (d == null) continue;
                        for (int i = 0; i <= d.length - pat.length && found < n; i++) {
                            boolean ok = true;
                            for (int j = 0; j < pat.length; j++) if (d[i + j] != pat[j]) { ok = false; break; }
                            if (ok) { System.out.println("PATTERN HIT at 0x" + Long.toHexString(cs + i)); found++; }
                        }
                    }
                }
                System.out.println("pattern hits: " + found);
                break;
            }
            case "fuzzscan": {
                byte[] pat = com.github.manolo8.darkbot.core.BotInstaller.getMainApplicationPattern();
                int minScore = args.length > 2 ? Integer.parseInt(args[2]) : 70;
                int shown = 0;
                for (Region r : enumerateRegions(hProcess)) {
                    if (!r.isReadableCommitted()) continue;
                    for (long cs = r.base; cs < r.base + r.size && shown < 30; cs += (1 << 20) - pat.length) {
                        int len = (int) Math.min(1 << 20, r.base + r.size - cs);
                        byte[] d;
                        try { d = mem.readBytes(cs, len); } catch (Throwable t) { continue; }
                        if (d == null) continue;
                        for (int i = 0; i <= d.length - pat.length && shown < 30; i += 4) {
                            if (d[i] != pat[0] || d[i + 12] != pat[12] || d[i + 20] != pat[20]) continue;
                            int score = 0;
                            for (int j = 0; j < pat.length; j++) if (d[i + j] == pat[j]) score++;
                            if (score >= minScore) {
                                StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < pat.length; j++) sb.append(String.format("%02x", d[i + j]));
                                System.out.println("0x" + Long.toHexString(cs + i) + " score=" + score + "/" + pat.length + " " + sb);
                                shown++;
                            }
                        }
                    }
                }
                System.out.println("fuzzy hits shown: " + shown);
                break;
            }
            case "ownscan": {
                if (!rawOk) { System.err.println("Raw handle required"); break; }
                int searchValue = Integer.parseInt(args[2]);
                int maxHits = args.length > 3 ? Integer.parseInt(args[3]) : 50;
                ownScan(mem, hProcess, searchValue, maxHits);
                break;
            }
            case "scanlog": {
                long addr = Long.parseUnsignedLong(args[2].replace("0x", ""), 16);
                scanLog(mem, addr);
                break;
            }
            default:
                System.err.println("Unknown mode: " + mode);
        }
    }

    /** Scan a Flash mediator/proxy object for embedded Flash Array/Vector children.
     *  Usage: MemScanner <pid> scanlog <hexMediatorAddr> */
    private static void scanLog(DarkMem mem, long mediatorAddr) {
        System.out.println("Scanning mediator at 0x" + Long.toHexString(mediatorAddr));
        // Read 0x200 bytes of the mediator
        byte[] mediatorBytes;
        try { mediatorBytes = mem.readBytes(mediatorAddr, 0x200); } catch (Throwable t) {
            System.out.println("Failed to read mediator: " + t); return;
        }

        long ATOM_MASK = ~7L;
        System.out.println("\n== Level 1: mediator fields ==");
        for (int off = 0x20; off <= 0x1F0; off += 8) {
            long raw = readLongLE(mediatorBytes, off);
            long child = raw & ATOM_MASK;
            if (child < 0x10000) continue;
            // Try to read child's +40 (arr size) and +56 (vec size)
            byte[] childBytes;
            try { childBytes = mem.readBytes(child, 0x80); } catch (Throwable t) { continue; }
            int arrSz  = readIntLE(childBytes, 40);
            int vecSz  = readIntLE(childBytes, 56);
            System.out.printf("  mediator+0x%02x  child=0x%x  arr@40=%d  vec@56=%d%n", off, child, arrSz, vecSz);
            // Flag promising candidates
            if ((arrSz > 0 && arrSz < 200) || (vecSz > 0 && vecSz < 200)) {
                System.out.printf("    *** CANDIDATE arr=%d vec=%d ***%n", arrSz, vecSz);
                // Dump first 128 bytes of child to help manual inspection
                StringBuilder sb = new StringBuilder("    bytes: ");
                for (int k = 0; k < Math.min(128, childBytes.length); k++) sb.append(String.format("%02x ", childBytes[k]));
                System.out.println(sb);
            }
            // Level 2: scan inside this child too
            for (int off2 = 0x20; off2 <= 0x70; off2 += 8) {
                if (off2 + 8 > childBytes.length) break;
                long raw2 = readLongLE(childBytes, off2);
                long gc2  = raw2 & ATOM_MASK;
                if (gc2 < 0x10000) continue;
                byte[] gc2Bytes;
                try { gc2Bytes = mem.readBytes(gc2, 0x80); } catch (Throwable t2) { continue; }
                int a2 = readIntLE(gc2Bytes, 40);
                int v2 = readIntLE(gc2Bytes, 56);
                if ((a2 > 0 && a2 < 200) || (v2 > 0 && v2 < 200)) {
                    System.out.printf("    child+0x%02x -> 0x%x  arr@40=%d vec@56=%d  *** DEEP CANDIDATE ***%n",
                            off2, gc2, a2, v2);
                    // Print first element pointer as string attempt
                    long tablePtr = readLongLE(gc2Bytes, 32) & ATOM_MASK;
                    System.out.printf("      arr table ptr (arr@32)=0x%x%n", tablePtr);
                    if (tablePtr > 0x10000) {
                        byte[] tableBytes;
                        try { tableBytes = mem.readBytes(tablePtr, 64); } catch (Throwable t3) { tableBytes = null; }
                        if (tableBytes != null) {
                            for (int ti = 16; ti <= 32; ti += 8) {
                                long elem = readLongLE(tableBytes, ti) & ATOM_MASK;
                                if (elem < 0x10000) continue;
                                // Try to read string at elem+0x28 (common Flash string field)
                                try {
                                    byte[] eb = mem.readBytes(elem + 0x28, 8);
                                    long strPtr = (readLongLE(eb, 0)) & ATOM_MASK;
                                    if (strPtr > 0x10000) {
                                        byte[] sb2 = mem.readBytes(strPtr, 64);
                                        System.out.printf("        elem[ti=%d]=0x%x  str@+28->0x%x: %s%n",
                                                ti, elem, strPtr, new String(sb2, "UTF-8").replaceAll("[^\\x20-\\x7E]", "?"));
                                    }
                                } catch (Throwable ignored) {}
                            }
                        }
                    }
                }
            }
        }
    }

    private static long readLongLE(byte[] b, int off) {
        if (off + 8 > b.length) return 0;
        long v = 0;
        for (int i = 7; i >= 0; i--) v = (v << 8) | (b[off + i] & 0xFFL);
        return v;
    }
    private static int readIntLE(byte[] b, int off) {
        if (off + 4 > b.length) return 0;
        return (b[off] & 0xff) | ((b[off+1] & 0xff) << 8) | ((b[off+2] & 0xff) << 16) | ((b[off+3] & 0xff) << 24);
    }

    private static void readRaw(DarkMem mem, long addr) {
        System.out.println("Reading raw bytes at 0x" + Long.toHexString(addr));
        try {
            byte[] bytes = mem.readBytes(addr, 64);
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x ", b));
            System.out.println("Bytes: " + sb);
        } catch (Throwable t) {
            System.out.println("Read failed: " + t);
        }
    }

    private static class Region {
        long base, size;
        int state, protect, type;

        @Override
        public String toString() {
            return String.format("base=0x%x size=0x%x state=0x%x protect=0x%x type=0x%x",
                    base, size, state, protect, type);
        }

        boolean isReadableCommitted() {
            if (state != MEM_COMMIT) return false;
            if ((protect & PAGE_GUARD) != 0) return false;
            return protect == PAGE_READWRITE || protect == PAGE_EXECUTE_READWRITE
                    || protect == PAGE_READONLY || protect == PAGE_EXECUTE_READ;
        }
    }

    private static List<Region> enumerateRegions(WinNT.HANDLE hProcess) {
        List<Region> regions = new ArrayList<>();
        long addr = 0x10000; // skip null page region
        long maxAddr = 0x7FFFFFFFFFFFL;

        WinNT.MEMORY_BASIC_INFORMATION mbi = new WinNT.MEMORY_BASIC_INFORMATION();
        while (addr < maxAddr) {
            BaseTSD.SIZE_T result = Kernel32.INSTANCE.VirtualQueryEx(
                    hProcess, new Pointer(addr), mbi, new BaseTSD.SIZE_T(mbi.size()));
            if (result.longValue() == 0) break;

            Region r = new Region();
            r.base = Pointer.nativeValue(mbi.baseAddress);
            r.size = mbi.regionSize.longValue();
            r.state = mbi.state.intValue();
            r.protect = mbi.protect.intValue();
            r.type = mbi.type.intValue();
            if (r.size <= 0) break; // avoid infinite loop
            regions.add(r);

            addr = r.base + r.size;
        }
        return regions;
    }

    private static void ownScan(DarkMem mem, WinNT.HANDLE hProcess, int searchValue, int maxHits) {
        List<Region> regions = enumerateRegions(hProcess);
        long totalBytes = 0;
        int readableCount = 0;
        for (Region r : regions) if (r.isReadableCommitted()) { readableCount++; totalBytes += r.size; }

        System.out.println("Enumerated " + regions.size() + " total regions, "
                + readableCount + " readable+committed, " + (totalBytes / 1024 / 1024) + " MB to scan");
        System.out.println("Searching for int value " + searchValue + " (little-endian bytes "
                + String.format("%02x %02x %02x %02x",
                        searchValue & 0xff, (searchValue >> 8) & 0xff,
                        (searchValue >> 16) & 0xff, (searchValue >> 24) & 0xff) + ") ...");

        byte[] needle = new byte[]{
                (byte) (searchValue & 0xff), (byte) ((searchValue >> 8) & 0xff),
                (byte) ((searchValue >> 16) & 0xff), (byte) ((searchValue >> 24) & 0xff)
        };

        int hits = 0;
        int chunkSize = 1 << 20; // 1MB per readBytes call
        long startTime = System.currentTimeMillis();
        int chunksAttempted = 0, chunksFailed = 0, chunksAllZero = 0, chunksWithData = 0;
        long bytesActuallyRead = 0;

        for (Region r : regions) {
            if (hits >= maxHits) break;
            if (!r.isReadableCommitted()) continue;

            long regionEnd = r.base + r.size;
            for (long chunkStart = r.base; chunkStart < regionEnd && hits < maxHits; chunkStart += chunkSize) {
                int len = (int) Math.min(chunkSize, regionEnd - chunkStart);
                byte[] data;
                chunksAttempted++;
                try {
                    data = mem.readBytes(chunkStart, len);
                } catch (Throwable t) {
                    chunksFailed++;
                    if (chunksFailed <= 3) System.out.println("  readBytes threw at 0x" + Long.toHexString(chunkStart) + ": " + t);
                    continue; // unreadable chunk, skip
                }
                if (data == null) {
                    chunksFailed++;
                    continue;
                }
                bytesActuallyRead += data.length;
                boolean allZero = true;
                for (byte b : data) if (b != 0) { allZero = false; break; }
                if (allZero) chunksAllZero++;
                else {
                    chunksWithData++;
                    if (chunksWithData == 1) {
                        System.out.println("  First non-zero chunk at 0x" + Long.toHexString(chunkStart)
                                + " len=" + data.length + " class=" + data.getClass());
                        StringBuilder sb = new StringBuilder("  First 64 bytes: ");
                        for (int k = 0; k < Math.min(64, data.length); k++) sb.append(String.format("%02x ", data[k]));
                        System.out.println(sb);
                        // manual scan of just this chunk, verbose
                        int manualHits = 0;
                        for (int i = 0; i <= data.length - 4; i++) {
                            if (data[i] == needle[0] && data[i + 1] == needle[1]
                                    && data[i + 2] == needle[2] && data[i + 3] == needle[3]) manualHits++;
                        }
                        System.out.println("  Manual verification scan of this chunk alone found " + manualHits + " matches for needle");
                    }
                }

                for (int i = 0; i <= data.length - 4 && hits < maxHits; i++) {
                    if (data[i] == needle[0] && data[i + 1] == needle[1]
                            && data[i + 2] == needle[2] && data[i + 3] == needle[3]) {
                        long hitAddr = chunkStart + i;
                        System.out.println("  HIT at 0x" + Long.toHexString(hitAddr)
                                + " (in region " + r + ")");
                        hits++;
                    }
                }
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Scan complete: " + hits + " hits, took " + elapsed + "ms");
        System.out.println("Diagnostics: chunksAttempted=" + chunksAttempted + " chunksFailed=" + chunksFailed
                + " chunksAllZero=" + chunksAllZero + " chunksWithData=" + chunksWithData
                + " bytesActuallyRead=" + bytesActuallyRead);
    }
}
