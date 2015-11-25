package View.ViewVotes;

import Controller.VCSVotesController;
import Model.VCSBallot;
import Model.VCSVotesModel;
import View.VCSPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * A panel responsible for displaying the current getBallots
 *
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VCSViewVotesPanel extends VCSPanel {

    // MVC Components
    private VCSVotesModel model;
    private VCSVotesController controller;

    // User Interface Components
    private JPanel verticalBallotPanel;

    public VCSViewVotesPanel(VCSVotesModel model, VCSVotesController controller) {
        super();

        this.model = model;
        this.controller = controller;

        buildPanelInterface();
    }

    /**
     * Builds the interface for the panel
     */
    private void buildPanelInterface() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        verticalBallotPanel = new JPanel();
        verticalBallotPanel.setLayout(new BoxLayout(verticalBallotPanel, BoxLayout.Y_AXIS));

        createBallotsVerticalPanel();

        // Create a new scroll pane to house the completed getBallots
        JScrollPane scrollPane = new JScrollPane(verticalBallotPanel);

        // Add the scroll pane and the interaction buttons
        add(scrollPane);
        add(createButtonsPanel());
    }

    private void createBallotsVerticalPanel() {

        // For each ballot, add it to the completed getBallots
        for (VCSBallot ballot : model.getBallots()) {
            addBallotToVerticalPanelWithBallot(ballot);
        }
    }

    /***
     * Adds a ballot to the vertical ballot view
     *
     * @param ballot - The ballot to add to the view
     *
     * @pre. Ballot is not null
     */
    private void addBallotToVerticalPanelWithBallot(VCSBallot ballot) {
        assert ballot != null : "Ballot is null";

        // Add ballot panel
        verticalBallotPanel.add(getBallotPanelWithBallot(ballot));

        // Add horizontal struct
        verticalBallotPanel.add(Box.createVerticalStrut(10));
    }


    private JPanel getBallotPanelWithBallot(VCSBallot ballot) {

        JPanel ballotPanel = new JPanel();
        ballotPanel.setLayout(new BoxLayout(ballotPanel, BoxLayout.Y_AXIS));

        ballotPanel.add(new JLabel("Ballot"));
        ballotPanel.add(Box.createVerticalStrut(10));

        int candidateNumber = 0;

        while (ballot.hasCandidateAtIndex(candidateNumber)) {
            JPanel candidatePanel = new JPanel();
            candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.X_AXIS));

            // Add the label and text field
            candidatePanel.add(new JLabel(ballot.getCandidateChoiceAtIndex(candidateNumber).getName()));
            candidatePanel.add(new JLabel("  " + (candidateNumber + 1)));

            ballotPanel.add(candidatePanel);

            candidateNumber++;
        }

        // Add a border to the panel
        ballotPanel.setBorder(BorderFactory.createEtchedBorder());

        return ballotPanel;
    }

    private JPanel createButtonsPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JButton loadVotesButton = new JButton("Load Votes");
        loadVotesButton.addActionListener(e -> loadVotesButtonClicked());

        panel.add(loadVotesButton);

        JButton countVotesButton = new JButton("Count Votes");
        countVotesButton.addActionListener(event -> controller.openCountVotesButtonClicked());

        panel.add(countVotesButton);

        return panel;
    }

    private void loadVotesButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv", "text"));
        int fileChooserResponse = fileChooser.showOpenDialog(this);

        if (fileChooserResponse == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            controller.loadVotes(file.getAbsoluteFile());
        }

    }

    public void updatePanel() {
        verticalBallotPanel.removeAll();

        createBallotsVerticalPanel();

        verticalBallotPanel.revalidate();
        verticalBallotPanel.repaint();
    }
}
