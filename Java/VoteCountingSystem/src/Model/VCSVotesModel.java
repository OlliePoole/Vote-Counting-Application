package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The model part of MVC, responsible for managing the data store and performing logic operations
 *
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VCSVotesModel extends Observable implements VCSModelInterface {

    // Class variables
    private /*@ spec_public @*/ ArrayList<VCSBallot> ballots;
    private /*@ spec_public @*/ VCSCandidate[] candidates;
    private /*@ spec_public @*/ HashMap<VCSCandidate, Integer> candidateVotes;

    private /*@ spec_public @*/ Scanner ballotsFileScanner;

    private /*@ spec_public @*/ final int NUMBER_OF_CANDIDATES = 4;

    private /*@ spec_public @*/ VCSCandidate winningCandidate;

    //@ public invariant ballots >= 0
    //@ public invariant candidates.length == NUMBER_OF_CANDIDATES

    /**
     * Default initializer
     */
    //@ ensures candidates.length == NUMBER_OF_CANDIDATES
    public VCSVotesModel() {

        candidates = new VCSCandidate[] {
                new VCSCandidate("Ollie"),
                new VCSCandidate("Alicia"),
                new VCSCandidate("George"),
                new VCSCandidate("Robert")};

        createCandidateVotesStructure();
    }

    public void addBallot(VCSBallot ballot) {
        assert ballot != null : "Ballot is null";

        if (ballots == null) {
            ballots = new ArrayList<>();
        }

        // Notify the ballot that there will be no more changes
        ballot.didFinishAddingCandidatesToBallot();

        // Add the ballot
        ballots.add(ballot);

        updateView();
    }

    public boolean canAddNewCandidateField(int currentFieldCount) {
        return currentFieldCount < NUMBER_OF_CANDIDATES;
    }

    public boolean isValidCandidateWithName(String name) {
        assert !name.equals("") : "Name is empty";

        // Iterate through the remaining candidates in the vote
        // Check if the name matches anyone
        for (VCSCandidate candidate : getAllCandidates()) {
            if (candidate.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public VCSCandidate getCandidateWithName(String name) {
        assert !name.equals("") : "Name is empty";
        assert isValidCandidateWithName(name) : "Name is not valid";

        VCSCandidate selectedCandidate = null;

        // Iterate through candidates and return the candidate with that name
        for (VCSCandidate candidate : candidates) {
            if (candidate.getName().equals(name)) {
                selectedCandidate = candidate;
                break;
            }
        }

        return selectedCandidate;
    }

    public VCSCandidate[] getAllCandidates() {
        ArrayList<VCSCandidate> remainingCandidates = new ArrayList<>();

        // For each of the original candidates
        for (VCSCandidate candidate : candidates) {

            // Is the candidate still in the vote?
            if (candidateVotes.containsKey(candidate)) {
                remainingCandidates.add(candidate);
            }
        }
        return remainingCandidates.toArray(new VCSCandidate[remainingCandidates.size()]);
    }

    public boolean canStartCountingVotes() {
        return ballots != null && ballots.size() > 0;
    }

    public void shouldStartCountingVotes() {
        // Reset the votes to 0
        resetCandidateVotes();

        // For each ballot, find the highest remaining candidate and add the vote
        for (VCSBallot ballot : ballots) {

            int candidateIndex = 0;

            while (ballot.hasCandidateAtIndex(candidateIndex)) {
                // Get the candidate from the ballot
                VCSCandidate candidate = ballot.getCandidateChoiceAtIndex(candidateIndex);

                if (candidateVotes.containsKey(candidate)) {
                    int votes = candidateVotes.get(candidate);
                    candidateVotes.replace(candidate, votes, ++votes);
                    break;
                }
                else {
                    // That candidate doesn't appear in the hash map, so has been removed
                    // Take the next choice instead
                    candidateIndex++;
                }
            }
        }

        checkForVoteWinner();

        updateView();
    }

    public int getVotesForCandidate(VCSCandidate candidate) {
        assert isValidCandidateWithName(candidate.getName()) : "Candidate does not exist";

        return candidateVotes.get(candidate);
    }

    public void redistributeVotesForCandidates() {

        int lowestVote = Integer.MAX_VALUE;
        ArrayList<VCSCandidate> potentialLowestCandidates = new ArrayList<>();

        // remove the candidate with the least votes
        for (VCSCandidate candidate : getAllCandidates()) {
            int voteCount = candidateVotes.get(candidate);

            if (voteCount < lowestVote) {
                // A new lowest vote, so reset the list of candidates with the same vote
                potentialLowestCandidates = new ArrayList<>();

                // Add candidate to list of lowest votes
                potentialLowestCandidates.add(candidate);

                // Update the lowest vote + candidate
                lowestVote = voteCount;
            }
            else if (voteCount == lowestVote) {
                // Candidate has joint lowest vote, add to lowest vote list
                potentialLowestCandidates.add(candidate);
            }
        }

        // Do more than one candidate have the lowest votes?
        if (potentialLowestCandidates.size() > 1) {
            // Fetch a random candidate, remove them
            VCSCandidate lowestCandidate = getRandomCandidateToRemoveFromCandidatesWithLowestVotes(potentialLowestCandidates);
            candidateVotes.remove(lowestCandidate);
        }
        else { //Only one candidate with lowest votes

            // remove from structure
            candidateVotes.remove(potentialLowestCandidates.get(0));
        }

        // Recount the votes
        shouldStartCountingVotes();

    }

    public VCSBallot[] getBallots() {
        if (ballots == null) {
            ballots = new ArrayList<>();
        }

        VCSBallot[] ballotArray = new VCSBallot[ballots.size()];
        return ballots.toArray(ballotArray);
    }

    public boolean canLoadFileAtPath(File file) {
        try {
            ballotsFileScanner = new Scanner(file);
            return true;

        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public void loadBallots() {
        assert ballotsFileScanner != null : "File can not be loaded";

        VCSBallot ballot = new VCSBallot();

        while (ballotsFileScanner.hasNextLine()) {
            String line = ballotsFileScanner.nextLine();

            int commaPosition = line.indexOf(",");
            String candidatePreference = line.substring(0, commaPosition);
            String candidateName = line.substring(commaPosition + 1);

            // If we have got to the start of a new ballot
            if (candidatePreference.equals("1")) {

                // If the ballot has at least one candidate, add it to the list of completed ballots
                if (ballot.hasAtLeastOneCandidate()) {
                    addBallot(ballot);
                }

                // Initialise a new ballot
                ballot = new VCSBallot();
            }

            ballot.addCandidateToBallot(getCandidateWithName(candidateName));

        }
        // Add the final ballot
        addBallot(ballot);

        // Close the file
        ballotsFileScanner.close();

        ballotsFileScanner = null;

    }

    public void shouldResetVotingProcedure() {
        createCandidateVotesStructure();
    }

    public boolean voteHasWinner() {
        return checkForVoteWinner();
    }

    public VCSCandidate getWinningCandidate() {
        assert voteHasWinner() : "Vote does not have winner";

        return winningCandidate;
    }

    /**
     * Picks a random candidate to be eliminated from the list
     * @param candidates - the list of candidates to pick from
     * @return The candidate to be eliminated
     *
     * @pre. candidates.size() > 1
     */
    public VCSCandidate getRandomCandidateToRemoveFromCandidatesWithLowestVotes(ArrayList<VCSCandidate> candidates) {

        // Return a random candidate from the lowest candidates
        return candidates.get(new Random().nextInt(candidates.size()));
    }

    private boolean checkForVoteWinner() {
        // If there is one candidate left, vote is over
        if (getAllCandidates().length == 1) {
            winningCandidate = getAllCandidates()[0];
            return true;
        }

        // If one candidate has more than 50% of vote, vote is over
        int ballotCount = getBallots().length;

        VCSCandidate provisionallyWinningCandidate = null;
        int provisionallyWinningScore = 51;

        // For each candidate, check if they have more than 50% of the vote
        for (VCSCandidate candidate : getAllCandidates()) {
            int candidateVotes = getVotesForCandidate(candidate);

            int percentageOfVote = (candidateVotes * 100) / ballotCount;

            if (percentageOfVote >= provisionallyWinningScore) {
                provisionallyWinningCandidate = candidate;
                provisionallyWinningScore = percentageOfVote;
            }
        }

        winningCandidate = provisionallyWinningCandidate;

        return provisionallyWinningCandidate != null;
    }

    private void createCandidateVotesStructure() {

        candidateVotes = new HashMap<>();

        // Add the candidates to a hash map, with the candidate as a key
        for (VCSCandidate candidate : candidates) {
            candidateVotes.put(candidate, 0);
        }
    }

    /**
     * Resets all the candidate votes left in the vote to 0
     */
    private void resetCandidateVotes() {

        for (VCSCandidate candidate : getAllCandidates()) {
            int votes = candidateVotes.get(candidate);
            candidateVotes.replace(candidate, votes, 0);
        }
    }

    /**
     * Signals an update to the view
     */
    private void updateView() {
        setChanged();
        notifyObservers();
    }

}
