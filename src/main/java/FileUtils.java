import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class FileUtils {
    public static void combineFiles(List<File> sortedList, Path outputPath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputPath.toFile(), false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (var fileObject : sortedList) {
                try (FileReader fileReader = new FileReader(fileObject);
                     BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        printWriter.println(line);
                        line = bufferedReader.readLine();
                    }
                }
            }
        }
    }

    public static List<File> getAllFiles(File file) {
        ArrayList<File> files = new ArrayList<>();
        Queue<File> queue = new ArrayDeque<>();
        queue.add(file);

        while (!queue.isEmpty()) {
            File fileObject = queue.poll();
            if (fileObject.isFile()) {
                files.add(fileObject);
            } else {
                File[] children = fileObject.listFiles();
                if (children != null) {
                    queue.addAll(Arrays.stream(children).toList());
                }
            }
        }

        return files;
    }

}
