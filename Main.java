package Energy;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel for better appearance
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start the login form
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}
