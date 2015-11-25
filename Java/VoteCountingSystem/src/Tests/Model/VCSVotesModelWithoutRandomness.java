package Tests.Model;

import Model.VCSCandidate;
import Model.VCSVotesModel;

import java.util.ArrayList;

/**
 * The purpose of this class is to remove the random choice of a loosing candidate.
 * Therefore tests can be written ensuring the test can be repeated under the same conditions.
 *
 * Created by Oliver Poole(12022846) on 28/10/15.
 */
public class VCSVotesModelWithoutRandomness extends VCSVotesModel {

    @Override
    public VCSCandidate getRandomCandidateToRemoveFromCandidatesWithLowestVotes(ArrayList<VCSCandidate> candidates) {
        // To remove the random nature for testing, always return the first candidate from the list
        return candidates.get(0);
    }
}
