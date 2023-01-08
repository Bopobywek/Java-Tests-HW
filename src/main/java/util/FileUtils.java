package util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс, содержащий статические методы для работы с файлами.
 */
public class FileUtils {

    /**
     * Возвращает путь к текущей рабочей директории.
     *
     * @return путь к текущей рабочей директории.
     * @throws SecurityException если недостаточно прав для получения абсолютного пути.
     */
    public static Path getWorkingDirectoryPath() throws SecurityException {
        return Paths.get("").toAbsolutePath().normalize();
    }

    /**
     * Соединяет в одну строку имена файлов, вставляя между каждой парой строку-разделитель.
     *
     * @param delimiter строка-разделитель, которая будет вставлена между именами каждой пары файлов.
     * @param files     файлы, имена которых нужно соединить в одну строку.
     * @return строка с именами файлов, разделенными заданной строкой-разделителем.
     */
    public static String joinFilenames(String delimiter, List<File> files) {
        return files.stream()
                .map(File::toString)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Компонует несколько файлов в один.
     *
     * @param filesToCombine файлы, которые нужно скомпоновать.
     * @param outputPath     путь к файлу, в который нужно скомпоновать файлы.
     * @throws IOException если возникает некоторая ошибка, связанная с файловым вводом и выводом. Например, если файл
     *                     для чтения не существует/перестал существовать.
     */
    public static void combineFiles(List<File> filesToCombine, Path outputPath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputPath.toFile(), false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (var fileObject : filesToCombine) {
                writeFileContent(printWriter, fileObject);
            }
        }
    }

    /**
     * @param printWriter поток на запись в выходной файл.
     * @param fileObject  файл, который нужно записать в поток.
     * @throws IOException если возникает некоторая ошибка, связанная с файловым вводом и выводом. Например, если файл
     *                     для чтения не существует/перестал существовать.
     */
    public static void writeFileContent(PrintWriter printWriter, File fileObject) throws IOException {
        try (FileReader fileReader = new FileReader(fileObject);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                printWriter.println(line);
                line = bufferedReader.readLine();
            }
            printWriter.println();
        }
    }

    /**
     * Возвращает все файлы в директории и всех её поддиректориях, если в качестве параметра {@code file} была
     * передана директория. Иначе возвращается список из одного элемента -- переданного файла.
     *
     * @param file файловый объект, для которого нужно найти все содержащиеся в нем файлы.
     * @return файлы в директории и всех её поддиректориях, если в качестве параметра {@code file} была
     * передана директория. Иначе возвращается список из одного элемента -- переданного файла.
     * @throws SecurityException если возникла ошибка при попытке прочитать содержимое директории/при попытке
     *                           обратиться к файлу.
     */
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
