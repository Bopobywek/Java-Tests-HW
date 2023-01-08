package util;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DependencyUtils {
    public static DependencyGraph<File> getDependencies(File rootDirectory) throws IOException {
        DependencyGraph<File> dependencyGraph = new DependencyGraph<>();

        for (var file : FileUtils.getAllFiles(rootDirectory)) {
            var fileDependencies = findDependenciesInFile(rootDirectory, file);
            dependencyGraph.addDependencies(file, fileDependencies);
        }

        return dependencyGraph;
    }

    public static List<File> findDependenciesInFile(File rootDirectory, File file) throws InvalidPathException, IOException {
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
                        throw new FileNotFoundException("In file " + file.toPath() +
                                " dependency \"" + match + "\" doesn't exist ");
                    }
                    result.add(dependency);
                }

                line = bufferedReader.readLine();
            }
        } catch (InvalidPathException invalidPathException) {
            throw new InvalidPathException("In file "
                    + file.toPath()
                    + " , the path to the dependency is specified incorrectly.",
                    invalidPathException.getReason());
        }

        return result;
    }
}
