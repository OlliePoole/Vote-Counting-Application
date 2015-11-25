package Tests.Model;

import Model.VCSBallot;
import Model.VCSCandidate;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Class to test the VCSBallot Class
 *
 * Created by Oliver Poole(12022846) on 24/10/15.
 */
public class VCSBallotTests {

    private final VCSCandidate[] candidates = {
            new VCSCandidate("Ollie"),
            new VCSCandidate("Alicia"),
            new VCSCandidate("George"),
            new VCSCandidate("Robert")};

    @Test
    public void testHasCandidateAtIndex() throws Exception {

        // Add four candidates to the ballot
        VCSBallot ballot = new VCSBallot(candidates);

        assertTrue(ballot.hasCandidateAtIndex(0));
        assertFalse(ballot.hasCandidateAtIndex(10));

        // Add no candidates to ballot
        VCSBallot emptyBallot = new VCSBallot(new VCSCandidate[0]);

        assertFalse(emptyBallot.hasCandidateAtIndex(0));
    }

    @Test
    public void testGetCandidateChoiceAtIndex() throws Exception {
        VCSBallot ballot = new VCSBallot(candidates);

        // Get candidate to check against
        VCSCandidate firstCandidate = candidates[0];
        assertEquals(firstCandidate, ballot.getCandidateChoiceAtIndex(0));

        // Get third candidate to check
        VCSCandidate thirdCandidate = candidates[2];
        assertEquals(thirdCandidate, ballot.getCandidateChoiceAtIndex(2));
    }

    @Test
    public void testAddCandidateToBallot() throws Exception {
        VCSBallot ballot = new VCSBallot();

        // Add the first candidate to the list
        ballot.addCandidateToBallot(candidates[0]);

        // Indicate that we've finished adding candidates
        ballot.didFinishAddingCandidatesToBallot();

        assertEquals(ballot.getCandidateChoiceAtIndex(0), candidates[0]);
    }

    @Test
    public void testHasAtLeastOneCandidate() throws Exception {

        VCSBallot ballot = new VCSBallot();

        // Add a candidate
        ballot.addCandidateToBallot(new VCSCandidate("Ollie"));

        // Ballot has one candidate
        assertTrue(ballot.hasAtLeastOneCandidate());


        // Reset ballot
        ballot = new VCSBallot();

        // Ballot has no candidates
        assertFalse(ballot.hasAtLeastOneCandidate());
    }
}
