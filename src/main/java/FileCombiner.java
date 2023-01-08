import graph.DependencyGraph;
import util.DependencyUtils;
import util.FileUtils;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Класс, реализующий консольное приложение "Файловый компоновщик".
 */
public class FileCombiner {
    /**
     * Запускает основной цикл приложения.
     */
    public void run() {
        do {
            handleOperations();
        } while (!isUserSendExitSignal());
    }

    /**
     * Запрашивает у пользователя информацию о том, хочет ли он продолжить работу с программой.
     *
     * @return {@code true}, если пользователь хочет продолжить работу, {@code false} иначе.
     */
    private boolean isUserSendExitSignal() {
        System.out.print("Do you want to continue working with the program? [y/n]\n>> ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().toLowerCase(Locale.ROOT);
            return !line.contains("y");
        }
        return false;
    }

    /**
     * Метод, который последовательно запрашивает у пользователя информацию для ввода и обрабатывает её.
     * Необходим для удобного прерывания работы с помощью {@code return}, а также для того, чтобы не помещать
     * громоздкий код в основной цикл приложения.
     */
    private void handleOperations() {
        Optional<Path> currentWorkingDirectory = tryGetWorkingDirectoryPath();
        if (currentWorkingDirectory.isEmpty()) {
            return;
        }

        System.out.println("Specify the path to the root directory (absolute or relative to the current directory)");
        System.out.println("Current directory: " + currentWorkingDirectory.get());
        Optional<Path> rootPath = tryScanPath();
        Optional<File> rootDirectory = rootPath.map(Path::toFile);
        if (rootDirectory.isEmpty() || !validateRootDirectory(rootDirectory.get())) {
            return;
        }

        Optional<DependencyGraph<File>> dependencyGraph = tryGetDependencies(rootDirectory.get());
        if (dependencyGraph.isEmpty() || !validateDependencyGraph(dependencyGraph.get())) {
            return;
        }

        List<File> finalList = dependencyGraph.get().toOrderedList();
        System.out.println("The files will be combined in the following order:");
        for (var fileObject : finalList) {
            System.out.println(rootPath.get().relativize(fileObject.toPath()));
        }

        System.out.println("Specify the path to the output file (absolute or relative to the current directory).");
        System.out.println("Current directory: " + currentWorkingDirectory.get());
        Optional<Path> outputPath = tryScanPath();
        if (outputPath.isEmpty()) {
            return;
        }

        if (tryCombineFiles(finalList, outputPath.get())) {
            System.out.println("Files have been successfully combined");
        }
    }

    /**
     * Проверяет, что построенный граф зависимостей соответствует требованиям.
     *
     * @param fileDependencyGraph граф зависимостей.
     * @return {@code true}, если граф зависимостей удовлетворяет условиям, иначе в стандартный поток вывода
     * печатается информация о несоответствии и из метода возвращается {@code false}.
     */
    private boolean validateDependencyGraph(DependencyGraph<File> fileDependencyGraph) {
        if (fileDependencyGraph.hasCycles()) {
            System.out.println("Error! The files contain cyclic dependencies.");
            List<List<File>> stronglyConnectedComponents = fileDependencyGraph.findStronglyConnectedComponents();
            System.out.println("The following groups of files cause cyclic dependencies:");
            int groupIndex = 1;
            for (var component : stronglyConnectedComponents) {
                if (component.size() == 1 && fileDependencyGraph.isDependencyLooped(component.get(0))) {
                    System.out.println(component.get(0) + " refers to itself.");
                    ++groupIndex;
                } else if (component.size() > 1) {
                    System.out.println(groupIndex + ". " + FileUtils.joinFilenames(" | ", component));
                    ++groupIndex;
                }
            }
            return false;
        } else if (fileDependencyGraph.isEmpty()) {
            System.out.println("Nothing to combine: there are no text files" +
                    " in the root directory and all its subdirectories.");
            return false;
        }

        return true;
    }

    /**
     * Проверяет, что указанная пользователем корневая директория соответствует требованиям.
     *
     * @param rootDirectory файл, располагающийся по пути, который ввёл пользователь.
     * @return {@code true}, если по введенному пути располагается директория и она удовлетворяет всем условиям,
     * иначе в стандартный поток вывода печатается информация о несоответствии и из метода возвращается {@code false}.
     */
    private boolean validateRootDirectory(File rootDirectory) {
        if (!rootDirectory.exists()) {
            System.out.println("The specified directory does not exist.");
            return false;
        } else if (!rootDirectory.canRead()) {
            System.out.println("There are no rights to read the specified directory.");
            return false;
        } else if (!rootDirectory.isDirectory()) {
            System.out.println("Instead of the path to the directory, the path to the text file was specified.");
            return false;
        }

        return true;
    }

    /**
     * Пытается получить путь к текущей рабочей директории.
     *
     * @return путь к текущей рабочей директории, если не возникло исключения. Иначе в стандартный поток вывода
     * печатается информация об ошибке и возвращается {@code Optional.empty()}.
     */
    private Optional<Path> tryGetWorkingDirectoryPath() {
        try {
            return Optional.of(FileUtils.getWorkingDirectoryPath());
        } catch (SecurityException securityException) {
            System.out.println("Not enough rights to get an absolute path. " +
                    "Please, restart the program with administrator rights.");
        }

        return Optional.empty();
    }

    /**
     * Пытается скомпоновать файлы.
     *
     * @param sortedList упорядоченный список с файлами, которые нужно скомпоновать.
     * @param outputPath путь к файлу, в который нужно скомпоновать файлы.
     * @return {@code true}, если компоновка произошла успешно и не возникло исключения. Иначе в стандартный поток
     * вывода печатается информация об ошибке и возвращается {@code Optional.empty()}.
     */
    private boolean tryCombineFiles(List<File> sortedList, Path outputPath) {
        try {
            FileUtils.combineFiles(sortedList, outputPath);
            return true;
        } catch (SecurityException securityException) {
            System.out.println("Not enough rights to get an absolute path. " +
                    "Please, restart the program with administrator rights.");
        } catch (IOException ioException) {
            System.out.println("""
                    When trying to combine files, a write error occurred.
                    Please, make sure that the path to the specified file exists and the program has enough rights
                    (the output file itself may not exist, in which case it will be created by the program)""");
        }

        return false;
    }

    /**
     * Пытается считать путь к файлу или директории у пользователя.
     *
     * @return путь к файлу или директории, если не возникло исключения при считывании. Иначе в стандартный поток
     * вывода печатается информация об ошибке и возвращается {@code Optional.empty()}.
     */
    private Optional<Path> tryScanPath() {
        try {
            System.out.print(">> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            return Optional.of(Paths.get(input).toAbsolutePath().normalize());
        } catch (NoSuchElementException noSuchElementException) {
            System.out.println("Could not get a string with the path.");
        } catch (InvalidPathException invalidPathException) {
            System.out.println("The specified path is incorrect. Please, try again.");
        } catch (SecurityException securityException) {
            System.out.println("Not enough rights to get an absolute path. " +
                    "Please, restart the program with administrator rights.");
        }

        return Optional.empty();
    }

    /**
     * Пытается построить граф зависимостей по данной корневой директории.
     *
     * @param rootDirectory корневая директория, относительно которой нужно искать файлы с зависимостями.
     * @return граф зависимостей, если при его построении не возникло исключений. Иначе в стандартный поток
     * вывода печатается информация об ошибке и возвращается {@code Optional.empty()}.
     */
    private Optional<DependencyGraph<File>> tryGetDependencies(File rootDirectory) {
        try {
            return Optional.of(DependencyUtils.getDependencies(rootDirectory));
        } catch (InvalidPathException invalidPathException) {
            System.out.println(invalidPathException.getInput());
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(fileNotFoundException.getMessage());
            System.out.println("Please, update the incorrect dependencies and try again.");
        } catch (IOException ioException) {
            System.out.println("It is not possible to get all dependencies due to an I/O error." +
                    " Make sure that all files in the directory are accessible and specified in UTF-8 encoding," +
                    " and the dependencies are specified correctly");
        }

        return Optional.empty();
    }
}
