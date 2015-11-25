package Model;

/**
 * This class is used to hold the getName of a candidate.
 *
 * Created by Oliver Poole(12022846) on 23/10/15.
 */
public class VCSCandidate {

    private String name;

    /**
     * Initialises a new candidate and sets the name
     *
     * @param name - The name of the candidate
     *
     * @post. The candidate has a name and is ready to use.
     */
    public VCSCandidate(String name) {
        this.name = name;
    }

    /**
     * @return The name of the candidate
     */
    public String getName() {
        return this.name;
    }
}
