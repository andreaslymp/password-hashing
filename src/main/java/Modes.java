import java.io.File;
import java.sql.Connection;
import java.util.Scanner;


public class Modes {

    public static void generateOneTimeKey() {
        String option;
        do {
            System.out.println("\n--- Generate one-time keys mode ---\n" +
                    "Choose one of the following options:\n" +
                    "c = create a key\n" +
                    "e = exit mode\n");
            Scanner scanner = new Scanner(System.in);
            option = scanner.nextLine();

            switch (option) {
                case "c":
                    int length;
                    System.out.println("Type some text:");
                    String password = scanner.nextLine();
                    do {
                        System.out.println("Type the preferred length for your key " +
                                "(it should be between 1 and 86 characters): ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("That's not an integer, try again: ");
                            scanner.next();
                        }
                        length = scanner.nextInt();
                    } while (length < 1 || length > 86);

                    String salt = PasswordHashing.generateSalt();
                    String key = PasswordHashing.generateKey(password, salt, length);
                    System.out.println("\nThe generated key is: " + key);
                    break;
                case "e":
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } while (!option.equals("e"));
    }

    public static void passwordVerification() {
        Connection conn = null;
        String option;
        do {
            System.out.println("\n--- Password verification mode ---\n" +
                    "Choose one of the following options:\n" +
                    "c = create a new password\n" +
                    "v = verify that a password is valid\n" +
                    "e = exit mode\n");
            Scanner scanner = new Scanner(System.in);
            option = scanner.nextLine();

            String dir = System.getProperty("user.dir");
            String dbName = "password-verification.db";
            String dbPath = dir + "/" + dbName;
            File f = new File(dbPath);
            boolean dbExists = f.isFile();

            switch (option) {
                case "c":
                    System.out.println("Type the password: ");
                    String password = scanner.nextLine();
                    String salt;
                    // If this is the first time you create a password, generate the salt and save it into the database.
                    if (!dbExists) {
                        salt = PasswordHashing.generateSalt();
                        if (conn == null) {
                            conn = Database.connect(dbPath);
                        }
                        Database.insertSalt(conn, salt);
                        Database.createKeysTable(conn);
                    // Else, get the salt from the database.
                    } else {
                        if (conn == null) {
                            conn = Database.connect(dbPath);
                        }
                        salt = Database.getSalt(conn);
                    }
                    String key = PasswordHashing.generateKey(password, salt);
                    if (!Database.keyExists(conn, key)) {
                        Database.insertKey(conn, key);
                    }
                    System.out.println("\nYour password was created successfully.");
                    break;
                case "v":
                    System.out.println("Type the password: ");
                    password = scanner.nextLine();
                    if (!dbExists) {
                        System.out.println("\nThe password is not valid.");
                    } else {
                        if (conn == null) {
                            conn = Database.connect(dbPath);
                        }
                        salt = Database.getSalt(conn);
                        key = PasswordHashing.generateKey(password, salt);
                        if (Database.keyExists(conn, key)) {
                            System.out.println("\nThe password is valid.");
                        } else {
                            System.out.println("\nThe password is not valid.");
                        }
                    }
                    break;
                case "e":
                    Database.disconnect(conn);
                    break;
                default:
                    System.out.println("Invalid option, try again.");
                    break;
            }
        } while (!option.equals("e"));
    }

    public static void passwordManager(String masterPassword) {
        System.out.println("\nType your master password: ");
        Scanner scanner = new Scanner(System.in);
        String masterPass = scanner.nextLine();
        if (!masterPassword.equals(masterPass)) {
            System.out.println("\nInvalid master password.");
        } else {
            Connection conn = null;
            String option;
            do {
                System.out.println("\n--- Password manager mode ---\n" +
                        "Choose one of the following options:\n" +
                        "c = create a password for a service\n" +
                        "g = get the password of a service\n" +
                        "e = exit mode\n");
                scanner = new Scanner(System.in);
                option = scanner.nextLine();

                String dir = System.getProperty("user.dir");
                String dbName = "password-manager.db";
                String dbPath = dir + "/" + dbName;
                File f = new File(dbPath);
                boolean dbExists = f.isFile();
                String salt, key;
                int length;

                switch (option) {
                    case "c":
                        System.out.println("Type the service that you want the password for:");
                        String service = scanner.nextLine().toLowerCase();

                        if (!dbExists) {
                            salt = PasswordHashing.generateSalt();
                            if (conn == null) {
                                conn = Database.connect(dbPath);
                            }
                            Database.insertSalt(conn, salt);
                            Database.createKeyLengthTable(conn);
                        } else {
                            if (conn == null) {
                                conn = Database.connect(dbPath);
                            }
                            salt = Database.getSalt(conn);
                        }
                        key = PasswordHashing.generateKey(service + masterPassword, salt);

                        if (Database.keyExists(conn, key)) {
                            System.out.println("\nA password already exists for the service \"" + service + "\".");
                            continue;
                        }

                        do {
                            System.out.println("Type the preferred length for your password " +
                                    "(it should be between 1 and 86 characters): ");
                            while (!scanner.hasNextInt()) {
                                System.out.println("That's not an integer, try again: ");
                                scanner.next();
                            }
                            length = scanner.nextInt();
                        } while (length < 1 || length > 86);

                        Database.insertKeyAndLength(conn, key, length);

                        String finalKey = PasswordHashing.generateKey(key, salt, length);
                        System.out.println("\nThe password for \"" + service + "\" is: " + finalKey);
                        break;
                    case "g":
                        if (!dbExists) {
                            System.out.println("\nIt seems you haven't saved any passwords yet. " +
                                    "First, go create one!");
                        } else {
                            System.out.println("Type the service that your password is used for:");
                            service = scanner.nextLine().toLowerCase();

                            if (conn == null) {
                                conn = Database.connect(dbPath);
                            }

                            salt = Database.getSalt(conn);
                            key = PasswordHashing.generateKey(service + masterPassword, salt);

                            if (!Database.keyExists(conn, key)) {
                                System.out.println("\nYou haven't saved a password for the service " +
                                        "\"" + service + "\".");
                            } else {
                                length = Database.getLength(conn, key);

                                finalKey = PasswordHashing.generateKey(key, salt, length);
                                System.out.println("\nThe password for \"" + service + "\" is: " + finalKey);
                            }
                        }
                        break;
                    case "e":
                        Database.disconnect(conn);
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                        break;
                }
            } while (!option.equals("e"));
        }
    }

}