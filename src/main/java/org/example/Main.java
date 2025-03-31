import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
  private static final String DEFAULT_URL =
      "jdbc:oracle:thin:@tcif.htl-villach.at:1521/orcl";
  private static Properties getProperties() {
    String filename = System.getProperties().get("user.dir").toString() +
                      System.getProperties().get("file.separator") +
                      "db.properties";
    Properties p = new Properties();
    try {
      p.load(new FileInputStream(filename));
    } catch (IOException ignore) {
      try {
        p.setProperty("URL", DEFAULT_URL);
        p.store(new FileOutputStream(filename), "Database login credentials");
        System.out.println("New creds file written");
      } catch (IOException innerEx) {
        System.err.println("Error writing properties: " + innerEx);
      }
    }
    return p;
  }
  public static void main(String[] args) {}
}
