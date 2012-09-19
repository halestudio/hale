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

package eu.esdihumboldt.hale.ui.selection;

import java.util.Set;

import org.eclipse.jface.viewers.ISelection;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Schema selection interface
 * 
 * @author Simon Templer
 */
public interface SchemaSelection extends ISelection {

	/**
	 * @return the sourceItems
	 */
	public Set<EntityDefinition> getSourceItems();

	/**
	 * @return the targetItems
	 */
	public Set<EntityDefinition> getTargetItems();

	/**
	 * Get the first selected source item
	 * 
	 * @return the first selected source item or <code>null</code>
	 */
	public EntityDefinition getFirstSourceItem();

	/**
	 * Get the first selected target item
	 * 
	 * @return the first selected target item or <code>null</code>
	 */
	public EntityDefinition getFirstTargetItem();

	/**
	 * Get the first selected item of the given schema
	 * 
	 * @param schema the schema type
	 * @return the first selected item or <code>null</code>
	 */
	public EntityDefinition getFirstItem(SchemaSpaceID schema);

}
