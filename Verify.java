import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Verify {
    public static void main(String[] args) {

        Git git = new Git();

        try {
            cleanDirectory(Paths.get("git"));
            // Test 1: Initialize repository
            git.init();

            // // Test 2: Verify required files/directories exist
            Path repository = Path.of("git");
            Path objects = repository.resolve("objects");
            Path index = repository.resolve("index");
            Path head = repository.resolve("HEAD");

            if (Files.isDirectory(repository)
                    && Files.isDirectory(objects)
                    && Files.isRegularFile(index)
                    && Files.isRegularFile(head)) {

                System.out.println("Test Passed: Git repository initialized");

            } else {
                System.out.println("Test failed: repository is missing files/directories");
            }

        } catch (IOException e) {
            System.out.println("Test Failed: " + e.getMessage());
        }
        Path testPath = Path.of("test.txt");
        try {
            git.add(testPath.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Path testPath1 = Path.of("test1.txt");
        try {
            git.add(testPath1.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void cleanDirectory(Path path) {
        if (!Files.exists(path))
            return;
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.err.println("Failed to cleanup: " + path);
        }
    }
}
