package Controller;

import Model.VCSBallot;
import Model.VCSCandidate;
import Model.VCSVotesModel;
import View.VoteCounting.VCSCountingView;
import View.ViewVotes.VCSVotesView;

import java.io.File;


/**
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VCSVotesController {

    private VCSVotesModel model;
    private VCSVotesView votesView;
    private VCSCountingView countingView;


    public VCSVotesController(VCSVotesModel model) {
        this.model = model;
    }

    /**
     * Called by the view, asks the controller to update the model with
     * a new ballot
     *
     * @param candidateNames - An array of candidate names in order of preference
     */
    public void addVoteWithBallotEntries(String[] candidateNames) {

        VCSCandidate[] candidates = new VCSCandidate[candidateNames.length];

        for (int x = 0; x < candidateNames.length; x++) {
            candidates[x] = model.getCandidateWithName(candidateNames[x]);
        }

        VCSBallot ballot = new VCSBallot(candidates);

        model.addBallot(ballot);
    }

    /**
     * Called by the view, asks the controller to check if a candidate
     * entered in the voting panel is valid
     *
     * @param name - The getName of the candidate entered
     *
     * @return True, if the candidate is valid
     */
    public Boolean isValidBallotEntryWithCandidateName(String name) {
        return model.isValidCandidateWithName(name);
    }

    public Boolean canAddNewCandidateTextField(int currentTextFieldCount) {
        return model.canAddNewCandidateField(currentTextFieldCount);
    }

    public void loadVotes(File file) {

        // Ask the model if it can load the file
        if (model.canLoadFileAtPath(file)) {

            // Load the file with the model
            model.loadBallots();
        }
        else {
            // The model can't load the file, show an error message
            votesView.displayErrorAlertWithMessage("Could not load file");
        }
    }

    /**
     * Asks the model if it's ready to start counting votes
     *
     * @post. if the model is ready, the counting view will be shown
     */
    public void openCountVotesButtonClicked() {
        if (model.canStartCountingVotes()) {
            this.countingView = new VCSCountingView(model, this);
            countingView.setDistributeVotesButtonEnabled(false);

            // Disable the votes view to stop further ballots
            votesView.setViewEnabled(false);
        }
        else {
            votesView.displayErrorAlertWithMessage("Add a ballot before you can start counting");
        }
    }

    /**
     * Updates the model to tell it to start counting votes
     *
     * @pre. The model is ready to start counting votes (model.canStartCountingVotes())
     *
     * @post. The model will have counted the votes
     * @post. The vote counting button will be disabled
     * @post. The redistribute button will be enabled
     */
    public void startVoteCountButtonClicked() {
        model.shouldStartCountingVotes();

        countingView.setVoteCountingButtonEnabled(false);
        countingView.setDistributeVotesButtonEnabled(true);
    }

    /**
     * Updates the model to tell it to redistribute the votes
     *
     * @pre. The model should have started counting the votes (model.shouldStartCountingVotes())
     *
     * @post. The next round of votes will be calculated
     */
    public void redistributeVotesButtonClicked() {
        model.redistributeVotesForCandidates();
    }

    /**
     * Called by the view when one candidate is remaining
     * Updates the state of the views and resets the ballot
     *
     * @post. The counting votes view will be hidden
     * @post. The votes view will be enabled
     * @post. The model will reset the voting process
     */
    public void oneCandidateRemainingWithCandidate(VCSCandidate candidate) {
        countingView.setViewVisible(false);

        votesView.setViewEnabled(true);

        votesView.displayAlertWithMessage(candidate.getName() + " has won with " + model.getVotesForCandidate(candidate) + " votes");

        model.shouldResetVotingProcedure();
    }


    public void setVotesView(VCSVotesView votesView) {
        this.votesView = votesView;
    }

    public void setCountingView(VCSCountingView countingView) {
        this.countingView = countingView;
    }
}