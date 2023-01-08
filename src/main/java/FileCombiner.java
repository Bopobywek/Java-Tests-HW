import util.DependencyGraph;
import util.DependencyUtils;
import util.FileUtils;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileCombiner {
    public void run() {
        do {
            handleOperations();
        } while(!isUserSendExitSignal());
    }

    private boolean isUserSendExitSignal() {
        System.out.print("Do you want to exit the program? [y/n]\n>> ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().toLowerCase(Locale.ROOT);
            return line.contains("y");
        }
        return false;
    }

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
        System.out.println("The files will be merged in the following order:");
        for (var fileObject : finalList) {
            System.out.println(rootPath.get().relativize(fileObject.toPath()));
        }

        System.out.print("Specify the path to the output file (absolute or relative to the current directory).");
        System.out.println("Current directory: " + currentWorkingDirectory.get());
        Optional<Path> outputPath = tryScanPath();
        if (outputPath.isEmpty() || !validateOutputPath(outputPath.get())) {
            return;
        }

        if (tryCombineFiles(finalList, outputPath.get())) {
            System.out.println("Files have been successfully combined");
        }
    }

    private boolean validateDependencyGraph(DependencyGraph<File> fileDependencyGraph) {
        if (fileDependencyGraph.hasCycles()) {
            System.out.println("Error! The files contain cyclic dependencies.");
            return false;
        } else if (fileDependencyGraph.isEmpty()) {
            System.out.println("Nothing to combine: there are no text files" +
                    " in the root directory and all its subdirectories.");
            return false;
        }

        return true;
    }

    private boolean validateOutputPath(Path outputPath) {
        return true;
    }

    private boolean validateRootDirectory(File rootDirectory) {
        if (!rootDirectory.isDirectory()) {
            System.out.println("Instead of the path to the directory, the path to the text file was specified.");
            return false;
        } else if (!rootDirectory.canRead()) {
            System.out.println("There are no rights to read the specified directory.");
            return false;
        }

        return true;
    }

    private Optional<Path> tryGetWorkingDirectoryPath() {
        try {
            return Optional.of(FileUtils.getWorkingDirectoryPath());
        } catch (SecurityException securityException) {
            System.out.println("Not enough rights to get an absolute path.");
        }

        return Optional.empty();
    }

    private boolean tryCombineFiles(List<File> sortedList, Path outputPath) {
        try {
            FileUtils.combineFiles(sortedList, outputPath);
            return true;
        } catch (IOException e) {
            System.out.println("");
        }

        return false;
    }

    private Optional<Path> tryScanPath() {
        try {
            System.out.print(">> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            return Optional.of(Paths.get(input).toAbsolutePath().normalize());
        } catch (NoSuchElementException noSuchElementException) {
            System.out.println("Could not get a string with the path.");
        } catch (InvalidPathException invalidPathException) {
            System.out.println("The specified path is incorrect.");
        } catch (SecurityException securityException) {
            System.out.println("Not enough rights to get an absolute path.");
        }

        return Optional.empty();
    }

    private Optional<DependencyGraph<File>> tryGetDependencies(File rootDirectory) {
        try {
            return Optional.of(DependencyUtils.getDependencies(rootDirectory));
        } catch(InvalidPathException e) {
          System.out.println(e.getInput());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("Please, update the incorrect dependencies and try again");
        } catch (IOException e) {
            System.out.println("");
        }
        return Optional.empty();
    }
}
