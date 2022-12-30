import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DependencyUtils {
    public static DependencyGraph<File> getDependencies(File rootDirectory) throws IOException {
        DependencyGraph<File> dependencyGraph = new DependencyGraph<>();

        for (var file : FileUtils.getAllFiles(rootDirectory)) {
            var fileDependencies = findDependencies(rootDirectory, file);
            dependencyGraph.addDependencies(file, fileDependencies);
        }

        return dependencyGraph;
    }

    public static List<File> findDependencies(File rootDirectory, File file) throws IOException {
        ArrayList<File> result = new ArrayList<>();
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                Scanner sc = new Scanner(line);
                var matches = sc.findAll("require '(.*)'").map(x -> x.group(1)).toList();
                for (var match : matches) {
                    Path path = rootDirectory.toPath().resolve(Paths.get(match).normalize());
                    File dependency = path.toAbsolutePath().normalize().toFile(); // Exception in thread "main" java.nio.file.InvalidPathException: Illegal char <*> at index 2: (.*)
                    result.add(dependency);
                }

                line = bufferedReader.readLine();
            }
        }

        return result;
    }
}
