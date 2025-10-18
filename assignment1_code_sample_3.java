import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;

public class VulnerableApp {

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static String getUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter your name: ");
            return scanner.nextLine();
        } catch (Exception e) {
            return "";
        }
    }

    public static void sendEmail(String to, String subject, String body) {
        try {
            ProcessBuilder pb = new ProcessBuilder("mail", "-s", subject, to);
            Process p = pb.start();
            try (OutputStream os = p.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }
            p.waitFor();
        } catch (Exception e) {
            System.out.println("Error sending email.");
        }
    }

    public static String getData() {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://insecure-api.com/get-data");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (InputStream inputStream = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching data.");
        }

        return result.toString();
    }

    public static void saveToDb(String data) {
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            System.out.println("Database credentials not provided.");
            return;
        }

        String query = "INSERT INTO mytable (column1, column2) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, data);
            pstmt.setString(2, "Another Value");
            pstmt.executeUpdate();
            System.out.println("Data saved to database.");

        } catch (SQLException e) {
            System.out.println("Database error.");
        }
    }

    public static void main(String[] args) {
        String userInput = getUserInput();
        String data = getData();
        saveToDb(data);
        sendEmail("admin@example.com", "User Input", userInput);
    }
}