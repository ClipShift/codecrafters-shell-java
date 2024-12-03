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
            switch (command[0]){
                case "exit": {
                    if("0".equals(command[1]))
                        break repl;
                }

                case "echo": {
                    System.out.println(command[1]);
                    break;
                }

                default: {
                    System.out.printf("%s: command not found%n", input);
                }
            }
        }
    }
}
