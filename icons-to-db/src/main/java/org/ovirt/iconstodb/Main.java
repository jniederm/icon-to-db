package org.ovirt.iconstodb;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Path ENGINE_SRC_DIR = Paths.get(System.getProperty("user.home") + "/src/ovirt-engine");
    private static final Path LARGE_ICONS_INPUT_DIR = Paths.get(ENGINE_SRC_DIR + "/frontend/webadmin/modules/userportal-gwtp/src/main/resources/org/ovirt/engine/ui/userportal/images/os/large");
    private static final Path SMALL_ICONS_INPUT_DIR = Paths.get(ENGINE_SRC_DIR + "/frontend/webadmin/modules/userportal-gwtp/src/main/resources/org/ovirt/engine/ui/userportal/images/os");
    private static final Path CONFIGURATION_FILE = Paths.get(ENGINE_SRC_DIR + "/packaging/conf/osinfo-defaults.properties");

    public static void main(String[] args) throws IOException {

        try (PrintStream stream = new PrintStream(new File("output.sql"))) {
            StatementGenerator.generate(stream,
                    CONFIGURATION_FILE,
                    SMALL_ICONS_INPUT_DIR,
                    LARGE_ICONS_INPUT_DIR);
        }
    }
}