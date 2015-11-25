package View.VoteCounting;

import Controller.VCSVotesController;
import Model.VCSCandidate;
import Model.VCSVotesModel;
import View.VCSPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Oliver Poole(12022846) on 26/10/15.
 */
public class VCSVoteCountingPanel extends VCSPanel {

    private VCSVotesModel model;
    private VCSVotesController controller;

    private JPanel candidatesPanel;

    private JButton startCountingButton;
    private JButton redistributeVotesButton;

    public VCSVoteCountingPanel(VCSVotesModel model, VCSVotesController controller) {
        super();

        this.model = model;
        this.controller = controller;

        buildInterface();
    }

    private void buildInterface() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        candidatesPanel = new JPanel();
        candidatesPanel.setLayout(new BoxLayout(candidatesPanel, BoxLayout.X_AXIS));

        addCandidatesToCandidatePanel();

        add(candidatesPanel);

        add(createButtonPanel());
    }


    private void addCandidatesToCandidatePanel() {

        VCSCandidate[] candidates = model.getAllCandidates();

        if (model.voteHasWinner()) {
            controller.oneCandidateRemainingWithCandidate(model.getWinningCandidate());
            return;
        }

        for (VCSCandidate candidate : candidates) {
            candidatesPanel.add(getCandidatePanelWithCandidate(candidate));
        }
    }

    private JPanel getCandidatePanelWithCandidate(VCSCandidate candidate) {
        JPanel candidatePanel = new JPanel(new BorderLayout());

        candidatePanel.add(new JLabel(candidate.getName()), BorderLayout.NORTH);
        candidatePanel.add(new JLabel("" + model.getVotesForCandidate(candidate)), BorderLayout.CENTER);

        candidatePanel.setBorder(BorderFactory.createEtchedBorder());

        return candidatePanel;
    }

    /**
     * Creates the two buttons, "Start Counting" and "Redistribute Votes"
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        startCountingButton = new JButton("Start Counting");
        startCountingButton.addActionListener(e -> controller.startVoteCountButtonClicked());

        redistributeVotesButton = new JButton("Redistribute Votes");
        redistributeVotesButton.addActionListener(e -> controller.redistributeVotesButtonClicked());

        panel.add(startCountingButton);
        panel.add(redistributeVotesButton);

        return panel;
    }

    public void updateVoteCounts() {
        candidatesPanel.removeAll();

        addCandidatesToCandidatePanel();

        candidatesPanel.revalidate();
        candidatesPanel.repaint();
    }

    public void setVoteCountingButtonEnabled(Boolean isEnabled) {
        startCountingButton.setEnabled(isEnabled);
    }

    public void setRedistributeVotesButtonEnabled(Boolean isEnabled) {
        redistributeVotesButton.setEnabled(isEnabled);
    }
}
