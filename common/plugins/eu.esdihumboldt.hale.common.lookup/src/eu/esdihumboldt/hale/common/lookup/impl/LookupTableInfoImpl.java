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

package eu.esdihumboldt.hale.common.lookup.impl;

import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;

/**
 * Simple LookupTableInfo implementation
 * 
 * @author Dominik Reuter
 */
public class LookupTableInfoImpl implements LookupTableInfo {

	private final LookupTableImpl lookupTable;

	private final String name;

	private final String description;

	/**
	 * Create a lookup table based on the given table
	 * 
	 * @param lookupTable the lookuptable
	 * @param name the name
	 * @param description the description
	 */
	public LookupTableInfoImpl(LookupTableImpl lookupTable, String name, String description) {
		super();
		this.lookupTable = lookupTable;
		this.name = name;
		this.description = description;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableInfo#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableInfo#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableInfo#getTable()
	 */
	@Override
	public LookupTable getTable() {
		return lookupTable;
	}
}
