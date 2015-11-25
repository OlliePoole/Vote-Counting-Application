package Tests.Model;

import Model.VCSCandidate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Oliver Poole(12022846) on 27/10/15.
 */
public class VCSCandidateTests {

    @Test
    public void testVCSCandidate() throws Exception {
        // Test constructor sets the name of the candidate
        VCSCandidate candidate = new VCSCandidate("Ollie");

        assertEquals(candidate.getName(), "Ollie");
    }
}
