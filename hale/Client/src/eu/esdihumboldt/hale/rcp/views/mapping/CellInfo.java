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
package eu.esdihumboldt.hale.rcp.views.mapping;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;

/**
 * Cell information structure
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellInfo {
	
	private final ICell cell;
	
	private final SchemaItem sourceItem;
	
	private final SchemaItem targetItem;

	/**
	 * Constructor
	 * 
	 * @param cell the cell
	 * @param sourceItem the source item
	 * @param targetItem the target item
	 */
	public CellInfo(ICell cell, SchemaItem sourceItem, SchemaItem targetItem) {
		super();
		this.cell = cell;
		this.sourceItem = sourceItem;
		this.targetItem = targetItem;
	}

	/**
	 * @return the cell
	 */
	public ICell getCell() {
		return cell;
	}

	/**
	 * @return the sourceItem
	 */
	public SchemaItem getSourceItem() {
		return sourceItem;
	}

	/**
	 * @return the targetItem
	 */
	public SchemaItem getTargetItem() {
		return targetItem;
	}

}
