import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileCombiner {
    public void run() {
        do {
            handleUserInput();
        } while(!isUserSendExitSignal());
    }

    private static boolean isUserSendExitSignal() {
        System.out.print("Do you want to use the program again? [y/n]\n>> ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().toLowerCase(Locale.ROOT);
            return !line.contains("y");
        }
        return true;
    }
    private static void handleUserInput() {
        Optional<Path> currentWorkingDirectory = getWorkingDirectoryPath();

        if (currentWorkingDirectory.isEmpty()) {
            return;
        }

        System.out.println("Current directory: " + currentWorkingDirectory.get());
        System.out.print(">> ");

        Optional<Path> rootPath = ScanPath(System.in);
        if (rootPath.isEmpty()) {
            return;
        }
        File rootDirectory = rootPath.get().toFile();

        Optional<DependencyGraph<File>> dependencyGraph = tryGetDependencies(rootDirectory);
        if (dependencyGraph.isEmpty()) {
            return;
        }

        if (dependencyGraph.get().hasCycles()) {
            System.out.println("Error! The files contain cyclic dependencies.");
            return;
        }

        List<File> finalList = dependencyGraph.get().toOrderedList();
        for (var fileObject : finalList) {
            System.out.println(rootPath.get().relativize(fileObject.toPath()));
        }

        System.out.print("Please, specify output file:\n>> ");
        Optional<Path> outputPath = ScanPath(System.in);
        if (outputPath.isEmpty()) {
            return;
        }

        if (!tryCombineFiles(finalList, outputPath.get())) {
            System.out.println();
        }
    }

    public static Optional<Path> getWorkingDirectoryPath() {
        try {
            return Optional.of(Paths.get("").toAbsolutePath().normalize());
        } catch (SecurityException securityException) {
            System.out.println();
            return Optional.empty();
        }
    }

    private static boolean tryCombineFiles(List<File> sortedList, Path outputPath) {
        try {
            FileUtils.combineFiles(sortedList, outputPath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Optional<Path> ScanPath(InputStream inputStream) throws RuntimeException {
        try {
            Scanner scanner = new Scanner(inputStream);
            String input = scanner.nextLine();
            return Optional.of(Paths.get(input).toAbsolutePath().normalize());
        } catch (InvalidPathException invalidPathException) {
            return Optional.empty();
        }
    }

    private static Optional<DependencyGraph<File>> tryGetDependencies(File rootDirectory) {
        try {
            return Optional.of(DependencyUtils.getDependencies(rootDirectory));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
