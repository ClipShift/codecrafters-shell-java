import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {

        String pathEnv = System.getenv("PATH");
        String[] paths = pathEnv.split(":");
        Map<String, File> scripts = new HashMap<>();
        for(String path: paths){
            try (Stream<Path> directories = Files.walk(Paths.get(path))){
                directories
                        .filter(Files::isRegularFile)
                        .filter(Files::isExecutable)
                        .forEach(file -> scripts.put(String.valueOf(file.getFileName()), file.toFile()));
            } catch (Exception _){

            }
        }

        // Uncomment this block to pass the first stage
        repl:
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] command = input.split(" ", 2);
            if(isBuiltin(command[0])){
                Builtin builtin = Builtin.valueOf(command[0]);
                switch (builtin){
                    case exit: {
                        if("0".equals(command[1]))
                            break repl;
                    }

                    case echo: {
                        System.out.println(command[1]);
                        break;
                    }

                    case type: {
                        if(isBuiltin(command[1])){
                            System.out.printf("%s is a shell builtin%n", command[1]);
                        } else if (scripts.containsKey(command[1])){
                            System.out.printf("%s is %s%n", command[1], scripts.get(command[1]).getPath());
                        }
                        else {
                            System.out.printf("%s: not found%n", command[1]);
                        }
                    }
                }
            } else {
                System.out.printf("%s: command not found%n", input);
            }
        }
    }

    private static boolean isBuiltin(String command){
        try{
            Builtin.valueOf(command);
            return true;
        } catch (IllegalArgumentException _){
            return false;
        }
    }
}

enum Builtin {
    exit,
    echo,
    type
}
