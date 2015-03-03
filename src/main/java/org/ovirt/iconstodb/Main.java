package org.ovirt.iconstodb;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static final String HOME = System.getProperty("user.home");
    private static final Path INPUT_DIR = Paths.get(HOME + "/src/ovirt-engine/frontend/webadmin/modules/userportal-gwtp/src/main/resources/org/ovirt/engine/ui/userportal/images/os/large");

    private static Map<String, Integer> osNameToIdMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        initOsMap();
        final DirectoryStream<Path> paths = Files.newDirectoryStream(INPUT_DIR,
                path -> path.toString().matches(".+\\.(png|jpg|jpeg|gif)$"));
        StreamSupport.stream(paths.spliterator(), false)
                .map(path -> getInsertLine(
                        toUuid(toNameWithoutExtension(path)),
                        pathToDataUri(path),
                        toNameWithoutExtension(path)
                ))
                .forEach(item -> {
                    System.out.println(item);
                });

    }

    private static void initOsMap() throws IOException {
        final Path configFile = Paths.get(HOME + "/src/ovirt-engine/packaging/conf/osinfo-defaults.properties");
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

    private static String getInsertLine(String uuid, String dataUri, String osName) {
        return "-- " + osName + " " + osNameToIdMap.get(osName) + System.lineSeparator()
                + "INSERT INTO vm_icons VALUES ('" + uuid + "', '" + dataUri + "', " + osNameToIdMap.get(osName) + ");";
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
