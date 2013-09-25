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

package eu.esdihumboldt.hale.ui.codelist.service;

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface CodeListService {

	/**
	 * Tries to find the code list with the given namespace and identifier.
	 * 
	 * @param namespace the namespace
	 * @param identifier the identifier
	 * @return the code list or <code>null</code>
	 */
	public CodeList findCodeListByIdentifier(String namespace, String identifier);

	/**
	 * Tries to find a code list associated to the property referenced by the
	 * given entity definition.
	 * 
	 * @param entity the entity definition
	 * @return the code list or <code>null</code>
	 */
	public CodeList findCodeListByEntity(EntityDefinition entity);

	/**
	 * Assign a code list for a property entity definition.
	 * 
	 * @param entity the entity definition
	 * @param code the code list to assign or <code>null</code> if the
	 *            assignment shall be deleted
	 */
	public void assignEntityCodeList(EntityDefinition entity, CodeList code);

	/**
	 * Get the code lists.
	 * 
	 * @return the code lists
	 */
	public List<CodeList> getCodeLists();

	/**
	 * Add a code list to the service.
	 * 
	 * @param code the code list to add
	 */
	public void addCodeList(CodeList code);

}
