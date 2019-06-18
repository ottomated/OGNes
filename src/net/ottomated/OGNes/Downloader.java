package net.ottomated.OGNes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Downloader {
    private static String URL = "https://romsmania.cc/roms/nintendo/search?name={{SEARCH}}&genre=&region=&orderBy=downloads&orderAsc=0&page=1";

    static class NetRom {
        String name;
        String url;

        NetRom(String n, String u) {
            name = n;
            url = u;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static List<NetRom> search(String query) throws IOException {
        String url = URL.replace("{{SEARCH}}", query);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("tbody > tr > td > a");
        List<NetRom> ret = new ArrayList<NetRom>();
        for (Element link : links) {
            String name = link.text();
            ret.add(new NetRom(name, link.attr("href").replace("/roms/", "/download/roms/")));
        }
        return ret;
    }

    static File download(NetRom rom) throws IOException {
        Document doc = Jsoup.connect(rom.url).get();
        Element dl = doc.selectFirst(".wait__link");
        java.net.URL website = new URL(dl.attr("href"));
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        File zip = Paths.get(Main.settings.romPath, rom.name + ".zip").toFile();
        FileOutputStream fos = new FileOutputStream(zip);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return new File(extractFolder(zip.getPath(), Main.settings.romPath));
    }

    private static String extractFolder(String zipFile, String extractFolder) throws IOException {
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);

        new File(extractFolder).mkdir();
        // grab a zip file entry
        ZipEntry entry = zip.entries().nextElement();
        String currentEntry = entry.getName();

        File destFile = new File(extractFolder, currentEntry);

        if (!entry.isDirectory()) {
            BufferedInputStream is = new BufferedInputStream(zip
                    .getInputStream(entry));
            int currentByte;
            // establish buffer for writing file
            byte[] data = new byte[BUFFER];

            // write the current file to disk
            FileOutputStream fos = new FileOutputStream(destFile);
            BufferedOutputStream dest = new BufferedOutputStream(fos,
                    BUFFER);

            // read and write until last byte is encountered
            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, currentByte);
            }
            dest.flush();
            dest.close();
            is.close();
        }

        file.delete();
        return destFile.getAbsolutePath();
    }

}
