using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;

namespace AlternativeVotingSystem.Model
{
    class VCSVotesModel : VCSModelInterface
    {
        // Class variables
        private List<VCSBallot> ballots;
        private VCSCandidate[] candidates;
        private Dictionary<VCSCandidate, int> candidateVotes;

        private StreamReader ballotsFileScanner;

        private int NUMBER_OF_CANDIDATES = 4;

        private VCSCandidate winningCandidate;

        //@ public invariant ballots >= 0
        //@ public invariant candidates.length == NUMBER_OF_CANDIDATES

        /**
         * Default initializer
         */
        //@ ensures candidates.length == NUMBER_OF_CANDIDATES
        public VCSVotesModel()
        {
            candidates = new VCSCandidate[] {
                new VCSCandidate("Ollie"),
                new VCSCandidate("Alicia"),
                new VCSCandidate("George"),
                new VCSCandidate("Robert")};

            createCandidateVotesStructure();
        }

        public void addBallot(VCSBallot ballot)
        {
            Debug.Assert(ballot != null, "Ballot is null");

            if (ballots == null)
            {
                ballots = new List<VCSBallot>();
            }

            // Notify the ballot that there will be no more changes
            ballot.didFinishAddingCandidatesToBallot();

            // Add the ballot
            ballots.Add(ballot);

            updateView();
        }

        public bool canAddNewCandidateField(int currentFieldCount)
        {
            return currentFieldCount < NUMBER_OF_CANDIDATES;
        }

        public bool isValidCandidateWithName(string name)
        {
           Debug.Assert(!name.Equals(""), "Name is empty");

            foreach (VCSCandidate candidate in getAllCandidates())
            {
                if (candidate.getName().Equals(name))
                {
                    return true;
                }
            }

            return false;
        }

        public VCSCandidate getCandidateWithName(string name)
        {
            Debug.Assert(!name.Equals(""), "Name is empty");
            Debug.Assert(isValidCandidateWithName(name), "Name is not valid");

            VCSCandidate selectedCandidate = null;

            foreach (VCSCandidate candidate in candidates)
            {
                if (candidate.getName().Equals(name))
                {
                    selectedCandidate = candidate;
                    break;
                }
            }

            return selectedCandidate;
        }

        public VCSCandidate[] getAllCandidates()
        {
            List<VCSCandidate> remainingCandidates = new List<VCSCandidate>();

            // For each of the original candidates
            foreach (VCSCandidate candidate in candidates)
            {
                // Is the candidate still in the vote?
                if (candidateVotes.ContainsKey(candidate))
                {
                    remainingCandidates.Add(candidate);
                }
            }

            return remainingCandidates.ToArray();
        }

        public bool canStartCountingVotes()
        {
            return ballots != null && ballots.Count() > 0;
        }

        public void shouldStartCountingVotes()
        {
            // Reset the votes to 0
            resetCandidateVotes();

            // For each ballot, find the highest remaining candidate and add the vote
            foreach (VCSBallot ballot in ballots)
            {
                int candidateIndex = 0;

                while (ballot.hasCandidateAtIndex(candidateIndex))
                {
                    // Get the candidate from the ballot
                    VCSCandidate candidate = ballot.getCandidateChoiceAtIndex(candidateIndex);

                    if (candidateVotes.ContainsKey(candidate))
                    {
                        int votes = candidateVotes[candidate];
                        candidateVotes[candidate] = ++votes;
                        break;
                    }
                    else
                    {
                        // That candidate doesn't appear in the structure, so has been removed
                        // Take the next choice instead
                        candidateIndex++;
                    }
                }
            }

            checkForVoteWinner();

            updateView();
        }

        public int getVotesForCandidate(VCSCandidate candidate)
        {
            Debug.Assert(isValidCandidateWithName(candidate.getName()), "Candidate does not exist");

            return candidateVotes[candidate];
        }

        public void redistributeVotesForCandidates()
        {
            int lowestVote = int.MaxValue;
            List<VCSCandidate> potentialLowestCandidates = new List<VCSCandidate>();

            // remove the candidate with the least votes
            foreach (VCSCandidate candidate in getAllCandidates())
            {
                int voteCount = candidateVotes[candidate];

                if (voteCount < lowestVote)
                {
                    // A new lowest vote, so reset the list of candidates with the same vote
                    potentialLowestCandidates = new List<VCSCandidate>();

                    // Add candidate to list of lowest votes
                    potentialLowestCandidates.Add(candidate);

                    // Update the lowest vote + candidate
                    lowestVote = voteCount;
                }
                else if (voteCount == lowestVote)
                {
                    // Candidate has joint lowest vote, add to lowest vote list
                    potentialLowestCandidates.Add(candidate);
                }
            }

            // Do more than one candidate have the lowest votes?
            if (potentialLowestCandidates.Count() > 1)
            {
                // Fetch a random candidate, remove them
                VCSCandidate lowestCandidate = getRandomCandidateToRemoveFromCandidatesWithLowestVotes(potentialLowestCandidates);
                candidateVotes.Remove(lowestCandidate);
            }
            else
            { //Only one candidate with lowest votes

                // remove from structure
                candidateVotes.Remove(potentialLowestCandidates[0]);
            }

            // Recount the votes
            shouldStartCountingVotes();

        }

        public VCSBallot[] getBallots()
        {
            if (ballots == null)
            {
                ballots = new List<VCSBallot>();
            }

            VCSBallot[] ballotArray = new VCSBallot[ballots.Count()];
            return ballots.ToArray();
        }

        public bool canLoadFileAtPath(string filePath)
        {
            try
            {
                ballotsFileScanner = new StreamReader(filePath);
                return true;

            }
            catch (FileNotFoundException e)
            {
                return false;
            }
        }

        public void loadBallots()
        {
            Debug.Assert(ballotsFileScanner != null, "File can not be loaded");

            VCSBallot ballot = new VCSBallot();
            string line;

            while ((line = ballotsFileScanner.ReadLine()) != null)
            {
                int commaPosition = line.IndexOf(",");
                string candidatePreference = line.Substring(0, commaPosition);
                string candidateName = line.Substring(commaPosition + 1);

                // If we have got to the start of a new ballot
                if (candidatePreference.Equals("1"))
                {

                    // If the ballot has at least one candidate, add it to the list of completed ballots
                    if (ballot.hasAtLeastOneCandidate())
                    {
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
            ballotsFileScanner.Close();

            ballotsFileScanner = null;

        }

        public void shouldResetVotingProcedure()
        {
            createCandidateVotesStructure();
        }

        public bool voteHasWinner()
        {
            return checkForVoteWinner();
        }

        public VCSCandidate getWinningCandidate()
        {
            Debug.Assert(voteHasWinner(), "Vote does not have winner");

            return winningCandidate;
        }

        /**
         * Picks a random candidate to be eliminated from the list
         * @param candidates - the list of candidates to pick from
         * @return The candidate to be eliminated
         *
         * @pre. candidates.size() > 1
         */
        public VCSCandidate getRandomCandidateToRemoveFromCandidatesWithLowestVotes(List<VCSCandidate> candidates)
        {
            // Return a random candidate from the lowest candidates
            return candidates[new Random().Next(candidates.Count())];
        }

        private bool checkForVoteWinner()
        {
            // If there is one candidate left, vote is over
            if (getAllCandidates().Count() == 1)
            {
                winningCandidate = getAllCandidates()[0];
                return true;
            }

            // If one candidate has more than 50% of vote, vote is over
            int ballotCount = getBallots().Count();

            VCSCandidate provisionallyWinningCandidate = null;
            int provisionallyWinningScore = 51;

            // For each candidate, check if they have more than 50% of the vote
            foreach (VCSCandidate candidate in getAllCandidates())
            {
                int candidateVotes = getVotesForCandidate(candidate);

                int percentageOfVote = (candidateVotes * 100) / ballotCount;

                if (percentageOfVote >= provisionallyWinningScore)
                {
                    provisionallyWinningCandidate = candidate;
                    provisionallyWinningScore = percentageOfVote;
                }
            }

            winningCandidate = provisionallyWinningCandidate;

            return provisionallyWinningCandidate != null;
        }

        private void createCandidateVotesStructure()
        {
            candidateVotes = new Dictionary<VCSCandidate, int>();

            // Add the candidates to a hash map, with the candidate as a key
            foreach (VCSCandidate candidate in candidates)
            {
                candidateVotes.Add(candidate, 0);
            }
        }

        /**
         * Resets all the candidate votes left in the vote to 0
         */
        private void resetCandidateVotes()
        {
            foreach (VCSCandidate candidate in getAllCandidates())
            {
                int votes = candidateVotes[candidate];
                candidateVotes[candidate] = 0;
            }
        }

        /**
         * Signals an update to the view
         */
        private void updateView()
        {
            //TODO: Translate this part

            //setChanged();
            //notifyObservers();
        }
    }
}
