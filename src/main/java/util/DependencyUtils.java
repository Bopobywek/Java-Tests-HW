package util;

import graph.DependencyGraph;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Класс, содержащий статические методы для работы с зависимостями.
 */
public class DependencyUtils {
    /**
     * Возвращает построенный граф зависимостей по данной корневой директории.
     *
     * @param rootDirectory корневая директория, по которой нужно построить граф зависимостей.
     * @return построенный граф зависимостей.
     * @throws IOException если возникает некоторая ошибка, связанная с файловым вводом и выводом.
     */
    public static DependencyGraph<File> getDependencies(File rootDirectory) throws IOException {
        DependencyGraph<File> dependencyGraph = new DependencyGraph<>();

        for (var file : FileUtils.getAllFiles(rootDirectory)) {
            var fileDependencies = findDependenciesInFile(rootDirectory, file);
            dependencyGraph.addDependencies(file, fileDependencies);
        }

        return dependencyGraph;
    }

    /**
     * Нахоодит все зависимости в указанном файле.
     *
     * @param rootDirectory корневая директория, относительно которой располагаются зависимости.
     * @param file          файл, в котором производится поиск зависимостей.
     * @return список зависимостей файла.
     * @throws InvalidPathException если в директиве require указан некорректный путь.
     * @throws IOException          если возникает некоторая ошибка, связанная с файловым вводом и выводом. Например,
     *                              если файл для чтения не существует/перестал существовать.
     */
    public static List<File> findDependenciesInFile(File rootDirectory, File file) throws InvalidPathException,
            IOException {
        ArrayList<File> result = new ArrayList<>();
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                Scanner scanner = new Scanner(line);
                var matches = scanner.findAll("require '(.*)'").map(x -> x.group(1)).toList();
                for (var match : matches) {
                    Path path = rootDirectory.toPath().resolve(Paths.get(match).normalize());
                    File dependency = path.toAbsolutePath().normalize().toFile();
                    if (!dependency.exists()) {
                        throw new FileNotFoundException("Error in file " + file.toPath() +
                                " | Dependency \"" + match + "\" doesn't exist.");
                    }
                    result.add(dependency);
                }

                line = bufferedReader.readLine();
            }
        } catch (InvalidPathException invalidPathException) {
            throw new InvalidPathException("In file "
                    + file.toPath()
                    + " the path to the dependency is specified incorrectly.",
                    invalidPathException.getReason());
        }

        return result;
    }
}
