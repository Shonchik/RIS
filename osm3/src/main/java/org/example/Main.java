package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final String STATISTIC_FORMAT = "%s : %d%n";
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Wrong argument count");
        }
        LOG.info("File decompressing start");
        try (InputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(args[0]))) {
            LOG.info("File decompressing finish");
            OsmResultContainer resultContainer = OsmProcessor.process(inputStream);
            System.out.println("Changes statistic:");
            printStatistic(resultContainer.getUsers());
            System.out.println("Tags statistic:");
            printStatistic(resultContainer.getTags());

        } catch (FileNotFoundException e) {
            LOG.error("File not found", e);
        } catch (XMLStreamException e) {
            LOG.error("XML reading error", e);
        } catch (IOException e) {
            LOG.error("File read error", e);
        }
    }

    private static void printStatistic(Map<String, Integer> statistic) {
        statistic.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEach(e -> System.out.printf(STATISTIC_FORMAT, e.getKey(), e.getValue()));
    }
}