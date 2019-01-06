package osu.xinyuangui.springbootvuejs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import osu.xinyuangui.springbootvuejs.domain.SingleFileCode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class mainly handles the fileIO
 */
public class FileIO {
    private static Logger logger = LoggerFactory.getLogger(FileIO.class);
    private static final Map<String, String> fileNameMap;
    private static final String[] oldFiles;
    private static final Set<String> ignoredFiles;
    static {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("java", "Main.java");
        tempMap.put("python", "main.py");
        tempMap.put("cpp", "main.cpp");
        fileNameMap = Collections.unmodifiableMap(tempMap);
        oldFiles = new String[] {"Main.java", "Main.class", "main.py", "main.cpp", "single_file_out.out"};
        ignoredFiles = new HashSet<>(Arrays.asList(
                new String[] {"Main.java", "Main.class", "main.py", "main.cpp", "single_file_out.out", "result.log"}
        ));
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

        // delete old files
        for (String oldFile : oldFiles) {
            String dst = String.format("%s/%s", path.toString(), oldFile);
            Files.deleteIfExists(Paths.get(dst));
        }

        String fileName = fileNameMap.get(code.getType().name().toLowerCase());
        String dst = String.format("%s/%s", path.toString(), fileName);
        try (FileWriter fileWriter = new FileWriter(dst)) {
            fileWriter.write(code.getCode());
        }
    }

    /**
     * delete the code folder
     * @param code
     * @param userCodePath
     * @throws IOException
     */
    public static void deleteCodeFolder(SingleFileCode code, String userCodePath) throws IOException {
        int id = code.getId();
        Path path = Paths.get(userCodePath, Integer.toString(id));
        if (Files.notExists(path)) {
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
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
        return new String[]{"./backend/src/main/java/osu/xinyuangui/springbootvuejs/util/kill_with_command.sh",
        "tail", "-f", "-n", "500", destination};
    }

    /**
     * save file to the destination
     * @param destination
     * @param file
     * @param sizeThresholdInBytes
     * @throws StorageException
     */
    public static void storeFile(String destination, MultipartFile file, long sizeThresholdInBytes) throws StorageException {
        File directory = new File(destination);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new StorageException("This directory doesn't exist " + destination);
        }

        long originSize = directoyLengthInBytes(directory);
        long newSize = file.getSize() + originSize;
        if (newSize >= sizeThresholdInBytes) {
            throw new StorageException("The size exceeds the total limit");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path target = Paths.get(destination, fileName);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + fileName);
            }
            if (fileName.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + fileName);
            }
            try(InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + fileName, e);
        }
    }

    /**
     * return the size of the directory
     * @param directory
     * @return
     */
    public static long directoyLengthInBytes(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += directoyLengthInBytes(file);
            }
        }
        return length;
    }

    /**
     * get the file list except for log and files
     * @param destination
     * @return
     */
    public static List<String> getFileList(String destination) throws StorageFileNotFoundException {
        Path dst = Paths.get(destination);
        if (Files.notExists(dst) || !Files.isDirectory(dst)) {
            throw new StorageFileNotFoundException("No this directory");
        }
        List<String> result = new ArrayList<>();
        File folder = new File(destination);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String name = file.getName();
                if (!ignoredFiles.contains(name)) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    public static List<String> deleteFiles(String destination, List<String> files) throws StorageFileNotFoundException {
        Path dst = Paths.get(destination);
        if (Files.notExists(dst) || !Files.isDirectory(dst)) {
            throw new StorageFileNotFoundException("No this directory");
        }
        for (String file : files) {
            try {
                Files.deleteIfExists(Paths.get(destination, file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileList(destination);
    }

    public static Resource getResourceOfFile(String destination, String file)
            throws StorageFileNotFoundException, IOException {
        Path dst = Paths.get(destination, file);
        if (Files.notExists(dst) || Files.isDirectory(dst)) {
            throw new StorageFileNotFoundException("No this directory");
        }
        return new ByteArrayResource(Files.readAllBytes(dst));
    }
}
