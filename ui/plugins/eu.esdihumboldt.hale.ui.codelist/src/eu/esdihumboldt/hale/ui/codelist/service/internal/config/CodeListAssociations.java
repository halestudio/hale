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

package eu.esdihumboldt.hale.ui.codelist.service.internal.config;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * Configuration object holding code list associations.
 * 
 * @author Simon Templer
 */
public class CodeListAssociations {

	private final Map<DummyEntityKey, CodeListReference> associations = new HashMap<>();

	/**
	 * Get a reference to the code list associated to the given entity.
	 * 
	 * @param entity the entity definition
	 * @return the code list reference or <code>null</code>
	 */
	public CodeListReference getCodeList(EntityDefinition entity) {
		return associations.get(new DummyEntityKey(entity, true));
	}

	/**
	 * Assign a code list for an entity.
	 * 
	 * @param entity the entity definition
	 * @param codeList the code list
	 */
	public void assignCodeList(EntityDefinition entity, CodeList codeList) {
		associations.put(new DummyEntityKey(entity, true),
				new CodeListReference(codeList.getNamespace(), codeList.getIdentifier()));
	}

	/**
	 * @return the map with code list associations
	 */
	public Map<DummyEntityKey, CodeListReference> getAssociations() {
		return associations;
	}

}
