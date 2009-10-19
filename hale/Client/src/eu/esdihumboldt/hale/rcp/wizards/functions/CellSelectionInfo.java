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

import java.util.ArrayList;
import java.util.Collection;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.rcp.views.mapping.CellInfo;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;

/**
 * {@link AlignmentInfo} based on {@link CellSelection}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellSelectionInfo implements AlignmentInfo {
	
	private final CellSelection selection;

	/**
	 * Constructor
	 * 
	 * @param selection the cell selection
	 */
	public CellSelectionInfo(CellSelection selection) {
		super();
		this.selection = selection;
	}

	/**
	 * @see AlignmentInfo#getAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public ICell getAlignment(SchemaItem source, SchemaItem target) {
		CellInfo info = selection.getCellInfo();
		
		if (source.equals(info.getSourceItem()) && target.equals(info.getTargetItem())) {
			return info.getCell();
		}
		
		return null;
	}

	/**
	 * @see AlignmentInfo#getFirstSourceItem()
	 */
	@Override
	public SchemaItem getFirstSourceItem() {
		return selection.getCellInfo().getSourceItem();
	}

	/**
	 * @see AlignmentInfo#getFirstTargetItem()
	 */
	@Override
	public SchemaItem getFirstTargetItem() {
		return selection.getCellInfo().getTargetItem();
	}

	/**
	 * @see AlignmentInfo#getSourceItemCount()
	 */
	@Override
	public int getSourceItemCount() {
		return 1;
	}

	/**
	 * @see AlignmentInfo#getSourceItems()
	 */
	@Override
	public Iterable<SchemaItem> getSourceItems() {
		Collection<SchemaItem> result = new ArrayList<SchemaItem>();
		result.add(getFirstSourceItem());
		return result;
	}

	/**
	 * @see AlignmentInfo#getTargetItemCount()
	 */
	@Override
	public int getTargetItemCount() {
		return 1;
	}

	/**
	 * @see AlignmentInfo#getTargetItems()
	 */
	@Override
	public Iterable<SchemaItem> getTargetItems() {
		Collection<SchemaItem> result = new ArrayList<SchemaItem>();
		result.add(getFirstTargetItem());
		return result;
	}

	/**
	 * @see AlignmentInfo#hasAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public boolean hasAlignment(SchemaItem source, SchemaItem target) {
		return getAlignment(source, target) != null;
	}

}
