import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Git {
    void main(String[] args) throws IOException {
        if (args.length == 0)
            return;
        switch (args[0]) {
            case "init":
                init();
                break;
            case "add":
                if (args.length < 2) {
                    System.out.println("add needs parameter");
                    return;
                }
                addFile(args[1]);
                break;
            case "commit":
                break;
        }
        System.out.println();
    }

    void addFile(String path_to_add) throws IOException {
        var hash = FileHasher.hashFile(path_to_add);
        var path = Path.of("./git/objects/" + hash);

        var hashes_files = Files.readAllLines(Path.of("./git/INDEX")).stream().map(line -> {
            var split = line.split(" ");
            if (split.length != 2) {
                System.err.println("error parsing index");
                System.exit(1);
            }
            return split;
        }).toList();

        boolean exists = false;

        for (var hash_file : hashes_files) {
            var file = hash_file[1];
            if (path_to_add.equals(file)) {
                hash_file[0] = hash;
                exists = true;
            }
        }

        if (!exists) {
            Files.write(path, Files.readAllBytes(Paths.get(path_to_add)));
            Files.writeString(Path.of("./git/INDEX"), hash + " " + path_to_add + "\n",
                    StandardOpenOption.APPEND);
        } else {
            Files.write(path, Files.readAllBytes(Paths.get(path_to_add)));
            var builder = new StringBuilder();
            for (var hash_file : hashes_files)
                builder.append(hash_file[0] + " " + hash_file[1] + "\n");
            Files.writeString(Path.of("./git/INDEX"), builder.toString());
        }
    }

    void init() throws IOException {
        Path path = Paths.get("git/");
        Path objects = Paths.get("./git/objects/");
        Path index = Paths.get("./git/INDEX");
        Path head = Paths.get("./git/HEAD");
        String currentPath = System.getProperty("user.dir");

        if (Files.isDirectory(path) && Files.isDirectory(objects) && Files.exists(index)
                && Files.exists(head))
            System.out.println("Git Repository Already Exists");
        else {
            Files.createDirectories(path);
            System.out.println("Git Repository Created");
            Files.createDirectories(objects);
            Files.createFile(index);
            Files.createFile(head);
        }
    }

    // generates a tree based upon the Path of a directory
    public String generateTree(String pathToAdd) {
        // checks if pathToAdd is file, if it is then it adds it like a file from before
        // project
        Path pathToUse = Path.of(pathToAdd);
        if (!pathToUse.toFile().isDirectory()) {
            try {
                String hash = FileHasher.hashFile(pathToAdd);
                FileWriter fw = new FileWriter("./git/objects/" + hash);
                fw.write(Files.readString(pathToUse));
                fw.close();
                return hash;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // turns the contents in folder into array

        File folder = new File(pathToAdd);
        File[] filesInFolder = folder.listFiles();
        Path pathToObjects = Path.of("./git/objects/");
        StringBuilder contentsOfTree = new StringBuilder();
        if (filesInFolder != null) {
            for (int i = 0; i < filesInFolder.length; i++) {
                if (!filesInFolder[i].isDirectory()) {
                    try {
                        // finds file hash of the file
                        String fileHash = FileHasher.hashFile(filesInFolder[i].getPath().toString());
                        FileWriter blobWriter = new FileWriter("./git/objects/" + fileHash);
                        blobWriter.write(Files.readString(filesInFolder[i].toPath()));
                        blobWriter.close();
                        // writes line for file
                        contentsOfTree.append("blob " + fileHash + " " + filesInFolder[i].getName() + "\n");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    // finds file hash of tree
                    String hashOfSmallerTree = generateTree(filesInFolder[i].getPath().toString());
                    contentsOfTree.append("tree " + hashOfSmallerTree + " " + filesInFolder[i].getName() + "\n");
                }
            }

        }

        try {
            String stringContents = contentsOfTree.toString();
            String hash = FileHasher.hashString(stringContents);

            // Write the tree file using Java's built-in Files utility
            Files.writeString(Path.of("./git/objects/" + hash), stringContents);

            return hash;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String createTreeFromIndex(String pathToWorkingList) {
        // build and sort working list
        Path path = Path.of(pathToWorkingList);
        File file = path.toFile();

        BufferedReader br = new BufferedReader();
        br.readAllLines();

        return "";
    }
}
