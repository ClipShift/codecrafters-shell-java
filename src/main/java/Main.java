import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
        Path currentPath = Paths.get("");
        String pwd = currentPath.toAbsolutePath().toString();

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
                        break;
                    }

                    case pwd: {
                        System.out.println(pwd);
                        break;
                    }

                    case cd: {
                        try{
                            Path newPath;
                            if (command[1].charAt(0) == '~'){
                                newPath = Paths.get(System.getenv("HOME"), command[1].substring(1));
                            }
                            else if (command[1].charAt(0) == '/')
                                newPath = Paths.get(command[1]);
                            else
                                newPath = Paths.get(pwd, command[1]);
                            if(Files.notExists(newPath)){
                                System.out.printf("cd: %s: No such file or directory%n", command[1]);
                            } else {
                                pwd = newPath.toRealPath().toAbsolutePath().toString();
                            }
                        } catch (InvalidPathException e){
                            System.out.printf("cd: %s: No such file or directory%n", command[1]);
                        }
                        break;
                    }
                }
            } else if(scripts.containsKey(command[0])){
                ProcessBuilder processBuilder = new ProcessBuilder(command[0]);
                if(command.length > 1){
                    processBuilder.command(command[0], command[1]);
                } else if("ls".equals(command[0])){
                    processBuilder.command(command[0], pwd);
                }
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
            }
            else {
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
    type,
    pwd,
    cd
}
