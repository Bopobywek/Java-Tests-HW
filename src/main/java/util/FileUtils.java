package util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtils {

    public static Path getWorkingDirectoryPath() throws SecurityException {
        return Paths.get("").toAbsolutePath().normalize();
    }

    public static void combineFiles(List<File> filesToCombine, Path outputPath) throws SecurityException, IOException {
        try (FileWriter fileWriter = new FileWriter(outputPath.toFile(), false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (var fileObject : filesToCombine) {
                writeFileContent(printWriter, fileObject);
            }
        }
    }

    public static void writeFileContent(PrintWriter destination, File fileObject) throws IOException {
        try (FileReader fileReader = new FileReader(fileObject);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                destination.println(line);
                line = bufferedReader.readLine();
            }
        }
    }

    public static List<File> getAllFiles(File file) throws SecurityException {
        ArrayList<File> files = new ArrayList<>();
        Queue<File> queue = new ArrayDeque<>();
        queue.add(file);

        while (!queue.isEmpty()) {
            File fileObject = queue.poll();
            if (fileObject.isFile()) {
                files.add(fileObject);
                continue;
            }
            File[] children = fileObject.listFiles();
            if (children != null) {
                queue.addAll(Arrays.stream(children).toList());
            }
        }

        return files;
    }

}
