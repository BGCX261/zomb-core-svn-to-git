package zomb.core;

/**
 *
 * @author Shrimp
 */
public class LoadTest {
    public static void main(String[] args) {
        int runs = 0;
        while (true) {
            try {
                System.out.println(HttpUtils.httpPost("http://localhost:8080/", ""));
                runs++;
            } catch (Exception e) {
                break;
            } finally {
                System.out.println("Runs: " + runs);
            }
        }
    }
}
