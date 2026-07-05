package net.solace.sdn.update;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.update.FileDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

@Slf4j
public class SdnDownloader implements FileDownloader {
    @Override
    public Path downloadFile(URL fileUrl) throws IOException {
        var destination = Files.createTempDirectory("pf4j-update-downloader");
        destination.toFile().deleteOnExit();

        var path = fileUrl.getPath();
        var fileName = path.substring(path.lastIndexOf('/') + 1);
        var file = destination.resolve(fileName);

        try (InputStream in = fileUrl.openStream();
             OutputStream out = Files.newOutputStream(file)) {
            in.transferTo(out);
        }

        Files.setLastModifiedTime(file, FileTime.fromMillis(System.currentTimeMillis()));
        return file;
    }
}
