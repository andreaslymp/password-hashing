import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {

    public static Connection connect(String dbPath) {
        Connection conn = null;
        String path = "jdbc:sqlite:" + dbPath;
        try {
            conn = DriverManager.getConnection(path);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createKeysTable(Connection conn) {
        String createKeysTable = "CREATE TABLE IF NOT EXISTS keys (key text PRIMARY KEY);";
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate(createKeysTable);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertKey(Connection conn, String key) {
        String insertKey = "INSERT INTO keys (key) VALUES (?)";
        try {
            PreparedStatement pstm = conn.prepareStatement(insertKey);
            pstm.setString(1, key);
            pstm.executeUpdate();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createKeyLengthTable(Connection conn) {
        String createKeyLengthTable = "CREATE TABLE IF NOT EXISTS keys (" +
                "key text PRIMARY KEY," +
                "length integer NOT NULL);";
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate(createKeyLengthTable);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertKeyAndLength(Connection conn, String key, int keyLength) {
        String insertKeyAndLength = "INSERT INTO keys (key,length) VALUES (?,?)";
        try {
            PreparedStatement pstm = conn.prepareStatement(insertKeyAndLength);
            pstm.setString(1, key);
            pstm.setInt(2, keyLength);
            pstm.executeUpdate();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int getLength(Connection conn, String key) {
        int length = -1;
        String selectLength = "SELECT length FROM keys WHERE key=\""+ key + "\"";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(selectLength);
            length = rs.getInt("length");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return length;
    }

    public static void insertSalt(Connection conn, String salt) {
        String createSaltTable = "CREATE TABLE IF NOT EXISTS salt (salt text PRIMARY KEY);";
        String insertSalt = "INSERT INTO salt (salt) VALUES (?)";
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate(createSaltTable);

            PreparedStatement pstm = conn.prepareStatement(insertSalt);
            pstm.setString(1, salt);
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getSalt(Connection conn) {
        String salt = null;
        String selectSalt = "SELECT salt FROM salt";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(selectSalt);
            salt = rs.getString("salt");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return salt;
    }

    public static boolean keyExists(Connection conn, String inputKey) {
        boolean exists = false;
        String selectKey = "SELECT key FROM keys WHERE key=\"" + inputKey +"\"";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(selectKey);
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return exists;
    }

}
