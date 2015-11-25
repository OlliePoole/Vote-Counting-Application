package Model;

import java.io.File;

/**
 * An interface showing the public methods of the model.
 * This aims to make it clear what the model can offer in terms of functionality.
 *
 * Created by Oliver Poole(12022846) on 26/10/15.
 */
public interface VCSModelInterface {

    /**
     * Adds a ballot to the current ballot store and signals a view update
     * @param ballot - The ballot to be added
     *
     * @pre. The ballot is not null
     *
     * @post. The ballot will be added to the model ballot store
     * @post. Observers are notified
     */
    void addBallot(VCSBallot ballot);


    /**
     * @return The ballots currently stored in the model
     */
    VCSBallot[] getBallots();


    /**
     * Used to check if the file can be loaded by the view.
     * Also initialises the scanner for the file
     *
     * @param file - The file to load
     *
     * @return True, if the file can be loaded
     *
     * @post. The file will be loaded ready for reading
     */
    boolean canLoadFileAtPath(File file);


    /**
     * Loads the ballots from a file passed as parameter.
     *
     * The ballots from the file will be added to those already stored in the model
     *
     * @pre. The file has been loaded using canLoadFileAtPath(String:)
     * @post. The new ballots (if any) will be added to the model's ballot storage
     * @post. The model's observers will be notified
     */
    void loadBallots();


    /**
     * Based on the number of candidates, can a new input field be added
     *
     * @param currentFieldCount - The current count of fields
     * @return True, if a new field can be added
     */
    boolean canAddNewCandidateField(int currentFieldCount);


    /**
     * Checks if the candidate name entered is a valid candidate
     *
     * @param name - the getName of the entered candidate
     * @return True, if the candidate getName is valid
     *
     * @pre. The name should not be nil
     */
    boolean isValidCandidateWithName(String name);


    /**
     * Finds a candidate with a specific getName
     *
     * @param name - The name of the candidate to find
     *
     * @pre. The name is not nil
     * @pre. The name is a valid candidate getName
     *
     * @return The found candidate
     */
    VCSCandidate getCandidateWithName(String name);


    /**
     * @return All the candidates who are still in the vote
     */
    VCSCandidate[] getAllCandidates();


    /**
     * Checks if the model is ready to start counting votes.
     * If not ballots have been added, this will return false
     *
     * @return True, if the model is ready to start counting
     */
    boolean canStartCountingVotes();


    /**
     * Informs the model that it should start counting votes in preparation
     * to displaying them on the screen
     *
     * @pre. The model should be ready to start counting votes (canStartCountingVotes())
     *
     * @post. The votes are ready to be queried.
     */
    void shouldStartCountingVotes();


    /**
     * Returns the number of votes for a candidate
     *
     * @param candidate - The candidate to get the votes for
     * @return The number of votes that candidate has
     *
     * @pre. The candidate is a valid candidate
     */
    int getVotesForCandidate(VCSCandidate candidate);


    /**
     * Takes the last placed candidate and removes it,
     * any ballots which had that candidate as first choice
     * are re-examined and their second choices taken
     *
     * @pre. The model should have already counted the votes (shouldStartCountingVotes())
     * @post. The new candidate vote counts will be ready to use
     * @post. The observers will be notified
     */
    void redistributeVotesForCandidates();

    /**
     * @return True, if the vote has a winner
     */
    boolean voteHasWinner();


    /**
     * @return The winning candidate of the vote
     *
     * @pre. The vote has a winner (voteHasWinner())
     */
    VCSCandidate getWinningCandidate();


    /**
     * When the process has finished, asks the model to reset it
     *
     * @post. All candidates will be re-added to the draw, votes will be reset to 0
     */
    void shouldResetVotingProcedure();
}
