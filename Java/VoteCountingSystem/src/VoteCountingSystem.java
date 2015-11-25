import Controller.VCSVotesController;
import Model.VCSVotesModel;
import View.ViewVotes.VCSVotesView;

/**
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VoteCountingSystem {

    public static void main(String args[]) {


        javax.swing.SwingUtilities.invokeLater( new Runnable() {
            public void run () {
                createAndShowGUI();
            }

            private void createAndShowGUI() {
                // Create the MVC application
                VCSVotesModel model = new VCSVotesModel();
                VCSVotesController controller = new VCSVotesController(model);

                new VCSVotesView(model, controller);
            }
        } );
    }
}
