using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AlternativeVotingSystem.Model
{
    class VCSCandidate
    {
        private string name;

        /**
         * Initialises a new candidate and sets the name
         *
         * @param name - The name of the candidate
         *
         * @post. The candidate has a name and is ready to use.
         */
        public VCSCandidate(string name)
        {
            this.name = name;
        }

        /**
         * @return The name of the candidate
         */
        public String getName()
        {
            return this.name;
        }

    }
}
