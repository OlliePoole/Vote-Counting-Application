﻿using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace AlternativeVotingSystem.Model
{
    class VCSBallot
    {
        private VCSCandidate[] candidateOrder;

        /**
         * Used when adding candidates one at a time.
         * When adding is complete,
         *     contents are copied to candidateOrder array
         *     candidates is set to null
         */
        private List<VCSCandidate> candidates;

        /**
         * Used to initialise a ballot with a completed list of candidates
         * @param candidatesSelected - The candidates selected
         *
         * @post. The ballot will have the candidates in order and be ready to use
         */
        public VCSBallot(VCSCandidate[] candidatesSelected)
        {
            this.candidateOrder = candidatesSelected;
        }

        /**
         * Used when initialising the ballot to add candidates one at time.
         * When finished adding candidates, ensure to call didFinishAddingCandidates()
         *
         * Does not require all candidates to be found prior to initialisation.
         */
        public VCSBallot() { }

        /**
         * Used to check if a ballot slip has a candidate at a specific index
         *
         * @param index - The index to check
         * @return True, if a candidate exists at that index
         */
        public Boolean hasCandidateAtIndex(int index)
        {
            Debug.Assert(index >= 0, "index selected is less that zero");

            return (index < candidateOrder.Count());
        }

        /**
         * Returns the candidate at the selected index
         *
         * @param index - The index of the candidate to be accessed
         * @pre. - The ballot has a candidate at that index (hasCandidateAtIndex(int:))
         *
         * @return - The candidate at the index
         */
        public VCSCandidate getCandidateChoiceAtIndex(int index)
        {
            Debug.Assert(hasCandidateAtIndex(index), "No Candidates on slip");

            return candidateOrder[index];
        }

        /**
         * Adds a new candidate to the ballot
         *
         * @param candidate - The candidate to add
         *
         * @pre. candidate != null
         *
         * @post. candidates != null
         * @post. candidates contains candidate
         */
        public void addCandidateToBallot(VCSCandidate candidate)
        {
            Debug.Assert(candidate != null, "Candidate is null");

            if (candidates == null)
            {
                candidates = new List<VCSCandidate>();
            }

            candidates.Add(candidate);
        }

        /**
         * When adding candidates to a ballot, one at a time.
         * This method is used to tell the ballot that there is no
         * more new candidates for that ballot
         *
         * @post. The candidates ArrayList will be copied to the candidateOrder array
         * @post. The candidates ArrayList will be null
         */
        public void didFinishAddingCandidatesToBallot()
        {

            // If there are no candidates, return
            if (!hasAtLeastOneCandidate()) return;

            // Copy the array list to the candidate order array
            candidateOrder = new VCSCandidate[candidates.Count()];
            candidateOrder = candidates.ToArray();

            // Clear the array list
            candidates = null;
        }

        /**
         * Used to check if, when adding candidates manually, there is at least one candidate on the ballot
         * @return True, if there is at least one candidate on the ballot
         */
        public Boolean hasAtLeastOneCandidate()
        {
            return candidates != null;
        }
    }
}
