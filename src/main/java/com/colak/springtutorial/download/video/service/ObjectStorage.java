package com.colak.springtutorial.download.video.service;

import com.colak.springtutorial.download.video.dto.StreamContentDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ObjectStorage {

    public StreamContentDto getStreamContent(String fileName, String range) {
        log.info("Streaming range content for file name: {}, range: {}", fileName, range);
        try {
            Path filePath = getObjectPath(fileName);
            long fileSize = Files.size(filePath);
            Pair<Long, Long> ranges = getRange(range, fileSize);

            boolean partial = range != null;
            return new StreamContentDto(
                    partial,
                    "video/mp4",
                    getContentLength(ranges),
                    getContentRange(ranges, fileSize),
                    getStreamingResponseBody(filePath, ranges));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while streaming content", e);
        }
    }

    private static StreamingResponseBody getStreamingResponseBody(Path filePath, Pair<Long, Long> ranges) {
        byte[] buffer = new byte[1024];
        return outputStream -> {
            try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                long pos = ranges.getLeft();
                file.seek(pos);

                while (pos < ranges.getRight()) {
                    file.read(buffer);
                    outputStream.write(buffer);
                    pos += buffer.length;
                }

                outputStream.flush();
            } catch (Exception exception) {
                log.error("Error occurred while streaming content {}", exception.getMessage());
            }
        };
    }

    private static String getContentLength(Pair<Long, Long> ranges) {
        long result = (ranges.getRight() - ranges.getLeft()) + 1;
        return Long.toString(result);
    }

    private static String getContentRange(Pair<Long, Long> ranges, long fileSize) {
        return new StringBuilder()
                .append("bytes ")
                .append(ranges.getLeft())
                .append("-")
                .append(ranges.getRight())
                .append("/")
                .append(fileSize)
                .toString();
    }

    private Path getObjectPath(String fileName) throws FileNotFoundException {
        URL mediaResource = ObjectStorage.class.getClassLoader().getResource(fileName);

        if (mediaResource != null) {
            try {
                return Paths.get(mediaResource.toURI());
            } catch (URISyntaxException e) {
                throw new FileNotFoundException();
            }
        }

        throw new FileNotFoundException();
    }

    private Pair<Long, Long> getRange(String range, long fileSize) {
        if (range == null) {
            return Pair.of(0L, fileSize - 1);
        }
        String[] ranges = range.split("-");
        Long rangeStart = Long.parseLong(ranges[0].substring(6));
        long rangeEnd;
        if (ranges.length > 1) {
            rangeEnd = Long.parseLong(ranges[1]);
        } else {
            rangeEnd = fileSize - 1;
        }
        if (fileSize < rangeEnd) {
            rangeEnd = fileSize - 1;
        }
        return Pair.of(rangeStart, rangeEnd);
    }

}
