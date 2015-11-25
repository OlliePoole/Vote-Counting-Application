package Tests.Model;

import Model.VCSBallot;
import Model.VCSCandidate;
import Model.VCSVotesModel;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test class to test the VCSVotesModel
 *
 * Created by Oliver Poole(12022846) on 27/10/15.
 */
public class VCSVotesModelTest {

    private File firstBallotFile; // Ballot file containing 3 ballots
    private File secondBallotFile; // Ballot file containing 5 ballots
    private File thirdBallotFile; // Ballot file containing 8 ballots
    private File fourthBallotFile; // Ballot file containing 20 ballots

    @Before
    public void setUp() throws Exception {
        firstBallotFile = new File(getClass().getResource("/Resources/ballots.csv").toURI());
        secondBallotFile = new File(getClass().getResource("/Resources/ballots-testing-only.csv").toURI());
        thirdBallotFile = new File(getClass().getResource("/Resources/ballots-testing-only(2).csv").toURI());
        fourthBallotFile = new File(getClass().getResource("/Resources/ballots-testing-only(3).csv").toURI());
    }

    /* Functionality tests */

    @Test
    public void testOverallFunctionality1() {
        VCSVotesModel model = new VCSVotesModel();

        // Load the ballots
        if (model.canLoadFileAtPath(secondBallotFile)) {
            model.loadBallots();
        }

        // Ask the model to start counting the votes
        if (model.canStartCountingVotes()) {
            model.shouldStartCountingVotes();

            // At this point:
            //               Ollie  : 0 votes
            //               Alicia : 1 vote
            //               George : 0 votes
            //               Robert : 4 votes

            // Check that "Ollie" has 0 votes
            assertEquals(model.getVotesForCandidate(model.getCandidateWithName("Ollie")), 0);

            // Check that "Robert" has 4 votes
            assertEquals(model.getVotesForCandidate(model.getCandidateWithName("Robert")), 4);

            // Robert has more than 50% of the votes, he should be winner
            assertTrue(model.voteHasWinner());

            // Check that winner is Robert
            assertEquals(model.getWinningCandidate(), model.getCandidateWithName("Robert"));
        }
    }

    @Test
    public void testOverallFunctionality2() {

        // use the votes model that doesn't use random, so the results can be repeated
        VCSVotesModel model = new VCSVotesModelWithoutRandomness();

        // Load the ballots
        if (model.canLoadFileAtPath(thirdBallotFile)) {
            model.loadBallots();
        }

        // Start counting
        if (model.canStartCountingVotes()) {
            model.shouldStartCountingVotes();

            // At this point:
            //               Ollie  : 3 votes
            //               Alicia : 3 votes
            //               George : 1 vote
            //               Robert : 1 vote

            // Check that "Alicia" has 3 votes
            assertEquals(model.getVotesForCandidate(model.getCandidateWithName("Alicia")), 3);

            // Check that there is no current winner
            assertFalse(model.voteHasWinner());

            // Redistribute the votes
            model.redistributeVotesForCandidates();

            // George will be removed (as the model without random picks the first lowest)

            // Check "Robert" is still in the vote
            assertTrue(model.isValidCandidateWithName(model.getCandidateWithName("Robert").getName()));

            // At this point:
            //               Ollie  : 4 votes (As the second choice of George voter was Ollie)
            //               Alicia : 3 votes
            //               Robert : 1 vote

            // Check Ollie has four votes
            assertEquals(model.getVotesForCandidate(model.getCandidateWithName("Ollie")), 4);

            // Check that there is no current winner
            assertFalse(model.voteHasWinner());

            // Redistribute votes
            model.redistributeVotesForCandidates();

            // At this point:
            //               Ollie  : 5 votes (As the second choice of Robert voter was Ollie)
            //               Alicia : 3 votes

            // Ollie now has over 50%
            assertTrue(model.voteHasWinner());

            assertEquals(model.getWinningCandidate(), model.getCandidateWithName("Ollie"));
        }
    }

    @Test
    public void testOverallFunctionality3() {

        // Use the model without randomness so the result can be predicted
        VCSVotesModel model = new VCSVotesModelWithoutRandomness();

        if (model.canLoadFileAtPath(fourthBallotFile)) {
            model.loadBallots();
        }

        // Start counting
        if (model.canStartCountingVotes()) {
            model.shouldStartCountingVotes();
        }

        // While the vote doesn't have a winner, redistribute the votes
        while (!model.voteHasWinner()) {
            model.redistributeVotesForCandidates();
        }

        // A winner has been detected, should be Ollie
        assertEquals(model.getWinningCandidate(), model.getCandidateWithName("Ollie"));

        // Ollie should have 13 votes
        assertEquals(model.getVotesForCandidate(model.getCandidateWithName("Ollie")), 13);

        // Only candidate remaining should be Alicia
        assertTrue(model.isValidCandidateWithName("Alicia"));
    }



    /* Test class methods */

    @Test
    public void testAddBallot() throws Exception {
        VCSCandidate[] candidates = {new VCSCandidate("Ollie"), new VCSCandidate("George")};
        VCSBallot ballot = new VCSBallot(candidates);

        VCSVotesModel model = new VCSVotesModel();

        // Add a ballot
        model.addBallot(ballot);

        // Fetch the ballots
        VCSBallot[] ballots = model.getBallots();

        // Ensure the ballot is returned in getBallots()
        assertTrue(Arrays.asList(ballots).contains(ballot));
    }

    @Test
    public void testIsValidCandidateWithName() throws Exception {

        VCSVotesModel model = new VCSVotesModel();

        // Fetch candidates
        VCSCandidate[] candidates = model.getAllCandidates();

        // Check if names are valid
        assertTrue(model.isValidCandidateWithName(candidates[0].getName()));

        assertTrue(model.isValidCandidateWithName(candidates[1].getName()));

        // Assert false, name is not valid
        assertFalse(model.isValidCandidateWithName("."));
    }

    @Test
    public void testGetCandidateWithName() throws Exception {

        VCSVotesModel model = new VCSVotesModel();

        // Fetch candidates
        VCSCandidate[] candidates = model.getAllCandidates();

        assertNotNull(model.getCandidateWithName(candidates[0].getName()));

        assertNotNull(model.getCandidateWithName(candidates[1].getName()));
    }

    @Test
    public void testCanStartCountingVotes() throws Exception {

        VCSVotesModel model = new VCSVotesModel();

        // Currently no ballots
        assertFalse(model.canStartCountingVotes());

        // Add ballot
        VCSCandidate[] candidates = {new VCSCandidate("Ollie")};
        model.addBallot(new VCSBallot(candidates));

        // Now there's a ballot
        assertTrue(model.canStartCountingVotes());
    }

    @Test
    public void testShouldStartCountingVotes() throws Exception {
        VCSVotesModel model = new VCSVotesModel();

        // Get candidates + create ballot
        VCSCandidate[] candidates = model.getAllCandidates();
        model.addBallot(new VCSBallot(candidates));

        if (model.canStartCountingVotes()) {
            model.shouldStartCountingVotes();

            // In the ballot, the first candidate should have one vote
            int votes = model.getVotesForCandidate(candidates[0]);
            assertEquals(votes, 1);

            // Add another ballot
            model.addBallot(new VCSBallot(candidates));

            // reset the vote counts
            model.shouldResetVotingProcedure();

            if (model.canStartCountingVotes()) {
                model.shouldStartCountingVotes();

                // The first candidate should now have two votes
                int secondVotes = model.getVotesForCandidate(candidates[0]);
                assertEquals(secondVotes, 2);
            }
        }
    }

    @Test
    public void testRedistributeVotesForCandidates() throws Exception {
        VCSVotesModel model = new VCSVotesModel();

        VCSCandidate[] candidates = model.getAllCandidates();

        VCSCandidate[] firstBallotCandidates = {candidates[0]};
        VCSCandidate[] secondBallotCandidates = {candidates[0], candidates[1]};
        VCSCandidate[] thirdBallotCandidates = {candidates[1], candidates[2]};
        VCSCandidate[] fourthBallotCandidates = {candidates[1]};
        VCSCandidate[] fifthBallotCandidates = {candidates[0], candidates[1]};
        VCSCandidate[] sixthBallotCandidates = {candidates[2], candidates[0]};

        model.addBallot(new VCSBallot(firstBallotCandidates));
        model.addBallot(new VCSBallot(secondBallotCandidates));
        model.addBallot(new VCSBallot(thirdBallotCandidates));
        model.addBallot(new VCSBallot(fourthBallotCandidates));
        model.addBallot(new VCSBallot(fifthBallotCandidates));
        model.addBallot(new VCSBallot(sixthBallotCandidates));

        if (model.canStartCountingVotes()) {
            model.shouldStartCountingVotes();

            // At this point, candidate[0] has 3 votes
            //                candidate[1] has 2 votes
            //                candidate[2] has 1 vote
            //                candidate[3] has 0 votes

            model.redistributeVotesForCandidates();

            // Candidate[3] is removed, no other users had that as their first choice
            assertFalse(model.isValidCandidateWithName(candidates[3].getName()));

            model.redistributeVotesForCandidates();

            // Candidate[2] is removed. Candidate[0] gets an extra vote
            assertFalse(model.isValidCandidateWithName(candidates[2].getName()));

            // At this point, candidate[0] has 4 votes
            //                candidate[1] has 2 votes

            model.redistributeVotesForCandidates();

            // Candidate[1] is removed
            assertFalse(model.isValidCandidateWithName(candidates[1].getName()));

            // candidate[0] is the winner!
            assertEquals(model.getWinningCandidate(), candidates[0]);
        }
    }

    @Test
    public void testCanLoadFileAtPath() throws Exception {
        VCSVotesModel model = new VCSVotesModel();

        assertTrue(model.canLoadFileAtPath(firstBallotFile));

        assertFalse(model.canLoadFileAtPath(new File("")));
    }

    @Test
    public void testLoadBallotsAtPath() throws Exception {
        VCSVotesModel model = new VCSVotesModel();

        // Load the ballots
        if (model.canLoadFileAtPath(firstBallotFile)) {
            model.loadBallots();
        }

        // Fetch ballots
        VCSBallot[] ballots = model.getBallots();

        assertNotNull(ballots);
    }

    @Test
    public void testShouldResetVotingProcedure() throws Exception {
        VCSVotesModel model = new VCSVotesModel();

        // Add a ballot + candidates
        VCSCandidate[] candidates = model.getAllCandidates();
        VCSBallot ballot = new VCSBallot(candidates);

        model.addBallot(ballot);

        if (model.canStartCountingVotes()) {
            // Count the first round of votes
            model.shouldStartCountingVotes();
        }

        // Reset the votes
        model.shouldResetVotingProcedure();

        // Check votes are 0
        assertEquals(model.getVotesForCandidate(candidates[0]), 0);
    }
}