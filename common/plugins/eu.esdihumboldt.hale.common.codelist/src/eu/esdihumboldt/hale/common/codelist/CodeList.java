/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
