/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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