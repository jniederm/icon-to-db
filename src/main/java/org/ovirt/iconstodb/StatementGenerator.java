package org.ovirt.iconstodb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StatementGenerator {

    private PrintStream outputStream;

    private Map<String, OsInfo> osNameToInfoMap = new HashMap<>();

    public StatementGenerator(PrintStream outputStream,
                              Path configurationFile,
                              Path smallIconsDirectory,
                              Path largeIconsDirectory) throws IOException {
        this.outputStream = outputStream;

        initOsMap(configurationFile);
        printIcons(largeIconsDirectory, false);
        printIcons(smallIconsDirectory, true);
        printIconsDefaults();
    }

    public static void generate(PrintStream outputStream,
                                Path configurationFile,
                                Path smallIconsDirectory,
                                Path largeIconsDirectory) throws IOException {
        new StatementGenerator(outputStream, configurationFile, smallIconsDirectory, largeIconsDirectory);
    }

    private void printIconsDefaults() {
        osNameToInfoMap.entrySet().forEach((entry) -> {
            if (entry.getValue().getLargeIconId() != null
                    && entry.getValue().getSmallIconId() != null) {
                outputStream.println(getInsertIconDefaultsLine(
                        entry.getKey(),
                        toUuid(entry.getKey()).toString(),
                        entry.getValue().getId(),
                        entry.getValue().getSmallIconId().toString(),
                        entry.getValue().getLargeIconId().toString()));
            } else {
                System.err.println("Not all icons found for os " + entry.getKey() + " " + entry.getValue());
            }
        });
        outputStream.println();
    }

    private String getInsertIconDefaultsLine(String osName,
                                             String rowId,
                                             int osId,
                                             String smallIconId,
                                             String largeIconId) {
        return "-- " + osName
                + " " + osId
                + System.lineSeparator()
                + "INSERT INTO vm_icon_defaults (id, os_id, small_icon_id, large_icon_id) VALUES ( "
                + "'" + rowId
                + "', " + osId
                + ", '" + smallIconId
                + "', '" + largeIconId + "' );";
    }

    private void initOsMap(Path configurationFile) throws IOException {
        Files.lines(configurationFile)
                .filter(line -> line.matches("os\\.[^.]+\\.id\\.value.*"))
                .forEach(line -> {
                    final Pattern pattern = Pattern.compile("^os\\.([^.]+)\\..*=[ ]?([0-9]+)$");
                    final Matcher matcher = pattern.matcher(line);
                    matcher.matches();
                    final String osName = matcher.group(1);
                    final Integer osId = Integer.valueOf(matcher.group(2));
                    osNameToInfoMap.put(osName, new OsInfo(osId));
                });
    }

    private void printIcons(Path directory, boolean isSmall) throws IOException {
        final DirectoryStream<Path> paths = Files.newDirectoryStream(directory,
                path -> path.toString().matches(".+\\.(png|jpg|jpeg|gif)$"));
        StreamSupport.stream(paths.spliterator(), false)
                .flatMap(this::filterIconsOfUnkonwsOs)
                .map(path -> new PathAndData(path, pathToDataUri(path)))
                .map(pathAndData -> iconMet(pathAndData, isSmall))
                .collect(Collectors.groupingBy(PathAndData::getDataUrl))
                .entrySet()
                .stream()
                .map(entry -> getInsertIconLine(entry, isSmall))
                .forEach(item -> outputStream.println(item));
        outputStream.println();
    }

    private Stream<Path> filterIconsOfUnkonwsOs(Path iconPath) {
        final String simpleName = toNameWithoutExtension(iconPath);
        final OsInfo osInfo = osNameToInfoMap.get(simpleName);
        if (osInfo == null) {
            System.err.println("Icon if os that is not in configuration file: " + iconPath);
            return Stream.empty();
        }
        return Stream.of(iconPath);
    }

    private static Stream<String> iconForUnknowsOs(Path path) {
        System.err.println("Icon if os that is not in configuration file: " + path);
        return Stream.empty();
    }

    private PathAndData iconMet(PathAndData pathAndData, boolean isSmall) {
        final UUID iconId = toUuid(pathAndData.getDataUrl());
        final OsInfo osInfo = osNameToInfoMap.get(toNameWithoutExtension(pathAndData.getPath()));
        if (isSmall) {
            osInfo.setSmallIconId(iconId);
        } else {
            osInfo.setLargeIconId(iconId);
        }
        return pathAndData;
    }

    /**
     * @param entry dataUrl -> List
     */
    private String getInsertIconLine(Map.Entry<String, List<PathAndData>> entry, boolean isSmall) {
        final UUID uuid = toUuid(entry.getKey());
        final String dataUrl = entry.getKey();
        final String operatingSystems = entry.getValue()
                .stream().map(PathAndData::getPath)
                .map(StatementGenerator::toNameWithoutExtension)
                .map(osName -> osName + " " + osNameToInfoMap.get(osName).getId())
                .collect(Collectors.joining(", "));
        return "-- " + (isSmall ? "small" : "large")
                + " " + operatingSystems
                + System.lineSeparator()
                + "INSERT INTO vm_icons(id, data_url) "
                + "VALUES ( '" + uuid + "', '" + dataUrl + "' );";
    }

    private static UUID toUuid(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes(Charset.forName("UTF-8")));
    }

    private static String toNameWithoutExtension(Path path) {
        return path.getFileName().toString().replaceFirst("[.][^.]+$", "");
    }

    private static String pathToDataUri(Path path) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        Runtime.getRuntime().exec(new String[]{"datauri", path.toString()}).getInputStream()))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class PathAndData {

        private final Path path;
        private final String dataUrl;

        public PathAndData(Path path, String dataUrl) {
            this.path = path;
            this.dataUrl = dataUrl;
        }

        public Path getPath() {
            return path;
        }

        public String getDataUrl() {
            return dataUrl;
        }
    }

    private static class OsInfo {

        private final int id;
        private UUID smallIconId;
        private UUID largeIconId;

        public OsInfo(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public UUID getSmallIconId() {
            return smallIconId;
        }

        public void setSmallIconId(UUID smallIconId) {
            this.smallIconId = smallIconId;
        }

        public UUID getLargeIconId() {
            return largeIconId;
        }

        public void setLargeIconId(UUID largeIconId) {
            this.largeIconId = largeIconId;
        }

        @Override
        public String toString() {
            return "OsInfo{" +
                    "id=" + id +
                    ", smallIconId=" + smallIconId +
                    ", largeIconId=" + largeIconId +
                    '}';
        }
    }
}
