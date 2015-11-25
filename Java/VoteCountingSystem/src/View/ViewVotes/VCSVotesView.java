package View.ViewVotes;

import Controller.VCSVotesController;
import Model.VCSVotesModel;
import View.ViewVotes.VCSAddVotePanel;
import View.ViewVotes.VCSViewVotesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VCSVotesView implements Observer {

    private VCSVotesModel model;
    private VCSVotesController controller;

    private JFrame frame;
    private VCSViewVotesPanel viewVotesPanel;
    private VCSAddVotePanel addVotesPanel;

    /**
     * Default Initializer, builds the view's interface
     */
    public VCSVotesView(VCSVotesModel model, VCSVotesController controller) {
        this.model = model;
        this.controller = controller;

        // Tell the controller about this view
        this.controller.setVotesView(this);

        this.model.addObserver(this);

        buildInterface();
    }

    private void buildInterface() {

        // Initialize the frame
        frame = new JFrame("Alternative Voting Application");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setSize(450, 300);
        frame.setResizable(false);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        // Initialize the custom votes panel
        viewVotesPanel = new VCSViewVotesPanel(model, controller);
        addVotesPanel = new VCSAddVotePanel(controller);

        // Create a split pane and set the left and right parts
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(addVotesPanel);
        splitPane.setRightComponent(viewVotesPanel);

        // Set divider to 1/3 of screen
        splitPane.setDividerLocation(150);

        contentPane.add(splitPane);

        frame.setVisible(true);
    }

    public void setViewEnabled(Boolean isEnabled) {
        frame.setEnabled(isEnabled);

        viewVotesPanel.setEnabled(isEnabled);
        addVotesPanel.setEnabled(isEnabled);

    }

    public void update(Observable o, Object arg) {
        viewVotesPanel.updatePanel();
    }

    public void displayErrorAlertWithMessage(String message) {
        JOptionPane.showMessageDialog(frame,
                message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void displayAlertWithMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}
