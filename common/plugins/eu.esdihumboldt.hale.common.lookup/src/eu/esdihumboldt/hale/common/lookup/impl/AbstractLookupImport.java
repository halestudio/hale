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

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.lookup.LookupTableImport;

/**
 * TODO Type description
 * 
 * @author simon
 */
public abstract class AbstractLookupImport extends AbstractImportProvider implements
		LookupTableImport {

	private String name;

	private String description;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the configured lookup table name.
	 * 
	 * @return the lookup table name
	 */
	protected String getName() {
		return name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the configured lookup table description.
	 * 
	 * @return the lookup table description
	 */
	protected String getDescription() {
		return description;
	}

	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		// store name
		configuration.put(PARAM_NAME, Value.of(getName()));

		// store description if applicable
		String desc = getDescription();
		if (desc != null && !desc.isEmpty()) {
			configuration.put(PARAM_DESCRIPTION, Value.of(desc));
		}

		super.storeConfiguration(configuration);
	}

	@Override
	public void setParameter(String name, Value value) {
		if (name.equals(PARAM_NAME)) {
			setName(value.as(String.class, "Unnamed"));
		}
		else if (name.equals(PARAM_DESCRIPTION)) {
			setDescription(value.as(String.class));
		}
		else {
			super.setParameter(name, value);
		}
	}

}
