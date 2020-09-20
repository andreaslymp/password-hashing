import java.util.Scanner;


public class Main {

    public static final String MASTER_PASSWORD = "";

    public static void main(String[] args) {
        String option;
        do {
            System.out.println("\nChoose one of the following modes:\n" +
                    "g = generate one-time keys\n" +
                    "v = password verification\n" +
                    "m = password manager\n" +
                    "q = quit program\n");
            Scanner scanner = new Scanner(System.in);
            option = scanner.nextLine();

            switch (option) {
                case "g":
                    Modes.generateOneTimeKey();
                    break;
                case "v":
                    Modes.passwordVerification();
                    break;
                case "m":
                    Modes.passwordManager(MASTER_PASSWORD);
                    break;
                case "q":
                    System.out.println("Bye!");
                    scanner.close();
                    break;
                default:
                    System.out.println("Invalid option, try again.");
                    break;
            }
        } while (!option.equals("q"));
    }
}
