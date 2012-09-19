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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.codelist;

import java.net.URI;
import java.util.Collection;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface CodeList {

	/**
	 * Represents a code list entry
	 */
	public static class CodeEntry {

		private final String name;

		private final String description;

		private final String identifier;

		private final String namespace;

		/**
		 * Create a code entry
		 * 
		 * @param name the name
		 * @param description the description
		 * @param identifier the identifier
		 * @param namespace the namespace
		 */
		public CodeEntry(String name, String description, String identifier, String namespace) {
			super();
			this.name = name;
			this.description = description;
			this.identifier = identifier;
			this.namespace = namespace;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the identifier
		 */
		public String getIdentifier() {
			return identifier;
		}

		/**
		 * @return the namespace
		 */
		public String getNamespace() {
			return namespace;
		}

	}

	/**
	 * Get the code list entries
	 * 
	 * @return the code list entries, changes to the returned collection will
	 *         not be reflected in the code list
	 */
	public Collection<CodeEntry> getEntries();

	/**
	 * Get the namespace
	 * 
	 * @return the namespace
	 */
	public String getNamespace();

	/**
	 * Get the identifier
	 * 
	 * @return the identifier
	 */
	public String getIdentifier();

	/**
	 * Get the description
	 * 
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Get the entry with the given name
	 * 
	 * @param name the entry name
	 * 
	 * @return the entry or <code>null</code>
	 */
	public CodeEntry getEntryByName(String name);

	/**
	 * Get the entry with the given identifier
	 * 
	 * @param identifier the entry identifier
	 * 
	 * @return the entry or <code>null</code>
	 */
	public CodeEntry getEntryByIdentifier(String identifier);

	/**
	 * Get the code list location
	 * 
	 * @return the code list location
	 */
	public URI getLocation();

}
