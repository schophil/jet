package be.chipit.jet.adapters.command;

import be.chipit.jet.common.JetException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Slf4j
class InstructionFiles {

    private InstructionFiles() {
        // utility class
    }

    public static void writeCommandFile(String command) {
        // write command to temporary file, it will be executed by the jet script
        String instructionFile = "jet.command";
        writeInstructionFile(command, instructionFile);
    }

    public static File writeCopyFile(String command) {
        String instructionFile = "jet.copy";
        return writeInstructionFile(command, instructionFile);
    }

    public static void executeCopy(String command) {
        File file = writeCopyFile(command);
        try {
            Runtime.getRuntime().exec(new String[]{
                System.getProperty("app.bin") + "/jet-command.sh",
            });
        } catch (Exception e) {
            throw new JetException("Error executing copy command", e);
        }
    }

    private static File writeInstructionFile(String command, String instructionFile) {
        try {
            File tempFile = Path.of(System.getProperty("java.io.tmpdir"), instructionFile).toFile();
            log.debug("Writing command to {}", tempFile.getAbsolutePath());
            try (var fout = new FileOutputStream(tempFile)) {
                IOUtils.write(command, fout, StandardCharsets.UTF_8);
                return tempFile;
            }
        } catch (Exception e) {
            throw new JetException("Error creating command file", e);
        }
    }
}
