import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
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
                        } else {
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
