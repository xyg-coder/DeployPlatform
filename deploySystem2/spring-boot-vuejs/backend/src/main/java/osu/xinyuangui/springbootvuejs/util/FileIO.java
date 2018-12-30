package osu.xinyuangui.springbootvuejs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class mainly handles the fileIO
 */
public class FileIO {
    private static Logger logger = LoggerFactory.getLogger(FileIO.class);
    private static final Map<String, String> fileNameMap;
    static {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("java", "Main.java");
        tempMap.put("python", "main.py");
        tempMap.put("cpp", "main.cpp");
        fileNameMap = Collections.unmodifiableMap(tempMap);
    }

    /**
     * write this code to file
     * @param code
     * @param userCodePath
     */
    public static void writeCodeToFile(SingleFileCode code, String userCodePath) throws IOException {
        int id = code.getId();
        Path path = Paths.get(userCodePath, Integer.toString(id));
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        String fileName = fileNameMap.get(code.getType().name().toLowerCase());
        String dst = String.format("%s/%s", path.toString(), fileName);
        try (FileWriter fileWriter = new FileWriter(dst)) {
            fileWriter.write(code.getCode());
        }
    }

    /**
     * read the file dynimically
     * create the process and return it
     * @param destination
     * @return
     * @throws IOException
     */
    public static Process readFileProcess(String destination) throws IOException {
        File file = new File(destination);
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("No such file to read");
        }
        String command =
                "./backend/src/main/java/osu/xinyuangui/springbootvuejs/util/read_file_dynamically.sh "
                + destination;
        return Runtime.getRuntime().exec(command);
    }

    public static String[] killFileReadingProcessCommand(String destination) {
        return new String[]{"./backend/src/main/java/osu/xinyuangui/springbootvuejs/util/read_file_dynamically.sh ",
        "tail", "-f", "-n", destination};
    }
}
