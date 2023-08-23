/**
 * The main class that initializes the EmailGUI.
 */
public class main {

    /**
     * The main method that starts the application.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            EmailGUI gui = new EmailGUI();
        } catch (Exception e) {
            // Handle the exception specific to EmailGUI initialization
            e.printStackTrace();
        }
    }

}
