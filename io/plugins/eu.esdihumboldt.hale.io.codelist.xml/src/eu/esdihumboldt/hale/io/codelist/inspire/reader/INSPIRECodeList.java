/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.codelist.inspire.reader;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * Representation of a INSPIRE code list.
 * 
 * @author Kai Schwierczek
 */
public class INSPIRECodeList implements CodeList {

	private final String namespace;
	private final String identifier;
	private final String description;
	private final URI location;

	private final Map<String, CodeEntry> entriesByName = new LinkedHashMap<String, CodeEntry>();
	private final Map<String, CodeEntry> entriesByIdentifier = new LinkedHashMap<String, CodeEntry>();

	/**
	 * Creates a codelist with the specified information.
	 * 
	 * @param namespace the codelist's namespace
	 * @param identifier the codelist's identifier
	 * @param description the codelist's description
	 * @param location the codelist's location
	 */
	public INSPIRECodeList(String namespace, String identifier, String description, URI location) {
		this.namespace = namespace;
		this.identifier = identifier;
		this.description = description;
		this.location = location;
	}

	/**
	 * Add the given entry to this codelist, no checks are performed here.
	 * 
	 * @param entry the entry to add
	 */
	public void addEntry(CodeEntry entry) {
		entriesByName.put(entry.getName(), entry);
		entriesByIdentifier.put(entry.getIdentifier(), entry);
	}

	@Override
	public Collection<CodeEntry> getEntries() {
		return new ArrayList<>(entriesByIdentifier.values());
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public CodeEntry getEntryByName(String name) {
		return entriesByName.get(name);
	}

	@Override
	public CodeEntry getEntryByIdentifier(String identifier) {
		return entriesByIdentifier.get(identifier);
	}

	@Override
	public URI getLocation() {
		return location;
	}
}
