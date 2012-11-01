/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.transform;

import javax.annotation.concurrent.Immutable;

import org.eclipse.core.runtime.jobs.Job;

/**
 * Abstract transformation job. It allows setting a process ID to combine
 * multiple transformation jobs to a job family.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationJob extends Job {

	/**
	 * Internal class wrapping a process identifier to ensure that only
	 * transformation jobs can be in the same family.
	 */
	@Immutable
	private static class Token {

		private final Object identifier;

		/**
		 * Create a family token based on the given identifier.
		 * 
		 * @param identifier the identifier
		 */
		public Token(Object identifier) {
			super();
			this.identifier = identifier;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (identifier == null) {
				if (other.identifier != null)
					return false;
			}
			else if (!identifier.equals(other.identifier))
				return false;
			return true;
		}

	}

	private Token family;

	/**
	 * @see Job#Job(String)
	 */
	public AbstractTransformationJob(String name) {
		super(name);
	}

	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(Object processId) {
		this.family = new Token(processId);
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
	 */
	@Override
	public boolean belongsTo(Object family) {
		if (this.family != null) {
			return this.family.equals(family);
		}
		else
			return super.belongsTo(family);
	}

}
