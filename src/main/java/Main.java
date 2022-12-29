import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Current directory: " + Paths.get("").toAbsolutePath().normalize());
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Path rootPath = Paths.get(input).toAbsolutePath().normalize();

        DependencyGraph<File> dependencyGraph = new DependencyGraph<>();
        if (Files.exists(rootPath)) {
            System.out.println("File exists: " + rootPath);
        }
        File file = getFileFromPath(rootPath);
        for (var filename : getAllFiles(file)) {
            System.out.println(rootPath.relativize(filename.toPath()));
            var dps = findDependencies(rootPath, filename);
            dependencyGraph.addDependencies(filename, dps);
//            System.out.println(dps);
        }
    }

    public static File getFileFromPath(Path path) {
        return path.toAbsolutePath().normalize().toFile();
    }

    public static List<File> getAllFiles(File directory) {
        ArrayList<File> files = new ArrayList<>();
        Queue<File> queue = new ArrayDeque<>();
        queue.add(directory);

        while (!queue.isEmpty()) {
            File file = queue.poll();
            if (file.isFile()) {
                files.add(file);
            } else {
                File[] files1 = file.listFiles();
                if (files1 != null) {
                    queue.addAll(Arrays.stream(files1).toList());
                }
            }
        }

        return files;
    }

    public static List<File> findDependencies(Path root, File file) {
        ArrayList<File> result = new ArrayList<>();
        try (FileReader fr = new FileReader(file);
             Scanner sc = new Scanner(fr)) {
            var matches = sc.findAll("require '(.*)'").toList();
            for (var match : matches) {
                Path path = root.resolve(Path.of(match.group(1)).normalize());
                System.out.println(path);
                File file1 = getFileFromPath(path); // Exception in thread "main" java.nio.file.InvalidPathException: Illegal char <*> at index 2: (.*)
                result.add(file1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
