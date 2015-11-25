package View.ViewVotes;

import Controller.VCSVotesController;
import View.VCSPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A panel that allows the user to select their candidates in order of preference
 *
 * Created by Oliver Poole(12022846) on 24/10/15.
 */
public class VCSAddVotePanel extends VCSPanel {

    // MVC Components
    private VCSVotesController controller;

    // Global User Interface Components
    private ArrayList<JTextField> candidateTextFields;
    private JPanel candidateTextFieldsPanel;

    public VCSAddVotePanel(VCSVotesController controller) {
        super();

        this.controller = controller;

        buildPanelInterface();
    }

    private void buildPanelInterface() {

        setLayout(new BorderLayout());

        candidateTextFieldsPanel = new JPanel();
        candidateTextFieldsPanel.setLayout(new BoxLayout(candidateTextFieldsPanel, BoxLayout.Y_AXIS));

        // Add the candidate text fields
        candidateTextFields = new ArrayList<>();

        // Draw first candidate entry box, add 'Plus' icon to add more
        addCandidateChoicePanelAtIndex(candidateTextFields.size());

        // Add 'plus' button to add another field
        BufferedImage plusImage = null;
        try {
            plusImage = ImageIO.read(getClass().getResource("/Resources/plus-button.png"));

        } catch (IOException e) {
            System.out.println("Image could not be loaded");
        }

        // Add a button that allows the user to add another selection
        JButton plusButton = new JButton();

        if (plusImage != null) {
            // Add the icon and remove the border
            plusButton.setIcon(new ImageIcon(plusImage));
            plusButton.setBorder(null);
        }

        plusButton.addActionListener(event -> addCandidateButtonClicked(plusButton));

        candidateTextFieldsPanel.add(plusButton);

        // Add candidate text field panel
        add(candidateTextFieldsPanel, BorderLayout.NORTH);

        // Add the voting button
        JButton addVoteButton = new JButton("Add Vote");
        addVoteButton.addActionListener(event -> addVoteButtonClicked());

        add(addVoteButton, BorderLayout.SOUTH);
    }

    /**
     * Creates and adds a new candidate choice panel to the candidate text fields panel
     *
     * @param index - The index of the choice
     */
    private void addCandidateChoicePanelAtIndex(int index) {

        JTextField textField = new JTextField();
        textField.setSize(100, 50);

        candidateTextFields.add(textField);

        JPanel candidatePanel = getPanelForCandidateChoiceWith(new JLabel("" + (index + 1)), textField);

        candidateTextFieldsPanel.add(candidatePanel);
    }

    /**
     * Creates a new JPanel containing a JLabel and JTextField
     *
     * @param choiceNumberLabel - The JLabel indicating with the choice number
     * @param candidateTextField - The JTextField allowing the user to enter their choice
     *
     * @return A JPanel containing the two elements
     */
    private JPanel getPanelForCandidateChoiceWith(JLabel choiceNumberLabel, JTextField candidateTextField) {
        // Create a new JPanel
        JPanel candidatePanel = new JPanel();

        candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.X_AXIS));

        // Add the JLabel and the JTextField
        candidatePanel.add(choiceNumberLabel);
        candidatePanel.add(candidateTextField);

        return candidatePanel;
    }

    /*********** Button Handlers ***********/

    private void addCandidateButtonClicked(JButton sender) {

        // Remove button
        candidateTextFieldsPanel.remove(sender);

        // Add new panel
        addCandidateChoicePanelAtIndex(candidateTextFields.size());

        // Now we've added another field, query the controller to see if we should show the button to add another
        if (controller.canAddNewCandidateTextField(candidateTextFields.size())) {
            candidateTextFieldsPanel.add(sender);
        }

        // Reload the panel
        candidateTextFieldsPanel.revalidate();
        candidateTextFieldsPanel.repaint();

    }

    private void addVoteButtonClicked() {
        ArrayList<String> validatedNames = new ArrayList<>();

        // Validate the entries
        for (JTextField textField : candidateTextFields) {
            String textFieldEntry = textField.getText();

            // If a field is blank, and a vote has been added, take votes above blank field
            if (textFieldEntry.equals("") && validatedNames.size() > 0) break;

            // Is the entry a valid candidate?
            if (!controller.isValidBallotEntryWithCandidateName(textFieldEntry)) {
                // Indicate error
                showErrorMessageWithMessageBody("Error: Candidate " + textFieldEntry + " Not found", "Error Message");
                return;
            }
            // Has the entry already been entered?
            else if (validatedNames.contains(textFieldEntry)) {
                // Indicate error
                showErrorMessageWithMessageBody("Error: Candidate " + textFieldEntry + " already included in ballot", "Error Message");
                return;
            }
            else {
                validatedNames.add(textFieldEntry);
            }
        }

        // Update controller
        controller.addVoteWithBallotEntries(validatedNames.toArray(new String[validatedNames.size()]));

        // Reset the text in the input fields
        for (JTextField textField : candidateTextFields) {
            textField.setText("");
        }
    }

}
