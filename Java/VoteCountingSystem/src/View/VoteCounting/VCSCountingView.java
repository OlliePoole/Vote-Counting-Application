package View.VoteCounting;

import Controller.VCSVotesController;
import Model.VCSVotesModel;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VCSCountingView implements Observer {

    private final VCSVotesModel model;
    private final VCSVotesController controller;

    private final VCSVoteCountingPanel voteCountingPanel;

    JFrame frame;

    public VCSCountingView(VCSVotesModel model, VCSVotesController controller) {
        this.model = model;
        this.controller = controller;
        this.voteCountingPanel = new VCSVoteCountingPanel(model, controller);

        // Tell the controller about this view
        this.controller.setCountingView(this);

        this.model.addObserver(this);

        buildInterface();
    }

    private void buildInterface() {
        frame = new JFrame("Vote Counting");

        frame.setSize(450, 300);
        frame.setResizable(false);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        contentPane.add(voteCountingPanel);

        frame.setVisible(true);
    }

    public void setViewVisible(Boolean isVisible) {
        frame.setVisible(isVisible);
    }

    public void setVoteCountingButtonEnabled(Boolean isEnabled) {
        voteCountingPanel.setVoteCountingButtonEnabled(isEnabled);
    }

    public void setDistributeVotesButtonEnabled(Boolean isEnabled) {
        voteCountingPanel.setRedistributeVotesButtonEnabled(isEnabled);
    }

    public void update(Observable o, Object arg) {
        // Ask the panel to update the vote counts
        voteCountingPanel.updateVoteCounts();
    }

}
