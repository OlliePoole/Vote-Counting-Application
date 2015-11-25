package View;

import javax.swing.*;

/**
 * A subclass of JPanel to add functionality to display an alert from the panel
 *
 * Created by Oliver Poole(12022846) on 26/10/15.
 */
public class VCSPanel extends JPanel {

    public void showErrorMessageWithMessageBody(String body, String title) {
        JOptionPane.showMessageDialog(null,
                body, title,
                JOptionPane.ERROR_MESSAGE);
    }
}
