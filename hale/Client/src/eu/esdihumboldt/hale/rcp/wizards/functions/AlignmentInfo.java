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
package eu.esdihumboldt.hale.rcp.wizards.functions;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;

/**
 * Alignment information on a selection of source and target
 *   {@link SchemaItem}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface AlignmentInfo {
	
	/**
	 * Get the number of source items
	 * 
	 * @return the number of source items
	 */
	public int getSourceItemCount();
	
	/**
	 * Get the number of target items
	 * 
	 * @return the number of target items
	 */
	public int getTargetItemCount();
	
	/**
	 * Get the source items
	 * 
	 * @return the source items
	 */
	public Iterable<SchemaItem> getSourceItems();
	
	/**
	 * Get the target items
	 * 
	 * @return the target items
	 */
	public Iterable<SchemaItem> getTargetItems();
	
	/**
	 * Get the first selected source item
	 * 
	 * @return the first selected source item or <code>null</code>
	 */
	public SchemaItem getFirstSourceItem();
	
	/**
	 * Get the first selected target item
	 * 
	 * @return the first selected target item or <code>null</code>
	 */
	public SchemaItem getFirstTargetItem();
	
	/**
	 * Determines if there is an alignment cell for the given
	 *   combination of source and target item
	 *   
	 * @param source the source item
	 * @param target the target item
	 * 
	 * @return if there is an alignment cell for the given
	 *   source and target item
	 */
	public boolean hasAlignment(SchemaItem source, SchemaItem target);
	
	/**
	 * Get the alignment cell for the given
	 *   combination of source and target item
	 *   
	 * @param source the source item
	 * @param target the target item
	 * 
	 * @return the alignment cell for the given
	 *   source and target item or <code>null</code>
	 */
	public ICell getAlignment(SchemaItem source, SchemaItem target);

}
