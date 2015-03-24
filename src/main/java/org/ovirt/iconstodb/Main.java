package org.ovirt.iconstodb;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class Main {

    private static final String HOME_DIR = System.getProperty("user.home");
    private static final Path LARGE_ICONS_INPUT_DIR = Paths.get(HOME_DIR + "/src/ovirt-engine/frontend/webadmin/modules/userportal-gwtp/src/main/resources/org/ovirt/engine/ui/userportal/images/os/large");
    private static final Path SMALL_ICONS_INPUT_DIR = Paths.get(HOME_DIR + "/src/ovirt-engine/frontend/webadmin/modules/userportal-gwtp/src/main/resources/org/ovirt/engine/ui/userportal/images/os");

    private static Map<String, Integer> osNameToIdMap = new HashMap<>();

    private static PrintStream printStream;

    public static void main(String[] args) throws IOException {
        printStream = new PrintStream(new File("output.sql"));
        initOsMap();
        printDirectory(LARGE_ICONS_INPUT_DIR, false);
        printDirectory(SMALL_ICONS_INPUT_DIR, true);
        printStream.close();

    }

    private static void printDirectory(Path directory, boolean isSmall) throws IOException {
        final DirectoryStream<Path> paths = Files.newDirectoryStream(directory,
                path -> path.toString().matches(".+\\.(png|jpg|jpeg|gif)$"));
        StreamSupport.stream(paths.spliterator(), false)
                .map(path -> {
                    final String dataUri = pathToDataUri(path);
                    final String insertLineStatement = getInsertLine(
                            toUuid(dataUri),
                            dataUri,
                            toNameWithoutExtension(path),
                            isSmall);
                    return insertLineStatement;
                })
                .forEach(item -> printStream.println(item));
        printStream.println();
    }

    private static void initOsMap() throws IOException {
        final Path configFile = Paths.get(HOME_DIR + "/src/ovirt-engine/packaging/conf/osinfo-defaults.properties");
        Files.lines(configFile)
                .filter(line -> line.matches("os\\.[^.]+\\.id\\.value.*"))
                .forEach(line -> {
                    final Pattern pattern = Pattern.compile("^os\\.([^.]+)\\..*=[ ]?([0-9]+)$");
                    final Matcher matcher = pattern.matcher(line);
                    matcher.matches();
                    osNameToIdMap.put(
                            matcher.group(1),
                            Integer.valueOf(matcher.group(2)));
                });
    }

    private static String getInsertLine(String uuid, String dataUri, String osName, boolean isSmall) {
        final Integer osId = osNameToIdMap.get(osName);
        return "-- " + osName
                + " osId=" + osId
                + " " + (isSmall ? "small" : "large")
                + System.lineSeparator()
                + "INSERT INTO vm_icons(id, data_url, default_for_os, is_small) "
                + "VALUES ('" + uuid + "', '" + dataUri + "', " + osId + ", " + String.valueOf(isSmall) + " );";
    }

    private static String toUuid(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes(Charset.forName("UTF-8"))).toString();
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
}
