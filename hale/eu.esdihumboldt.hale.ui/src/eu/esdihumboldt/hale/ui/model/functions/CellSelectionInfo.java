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
package eu.esdihumboldt.hale.ui.model.functions;

import java.util.ArrayList;
import java.util.Collection;

import eu.esdihumboldt.hale.ui.model.mapping.CellInfo;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.selection.CellSelection;
import eu.esdihumboldt.specification.cst.align.ICell;

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
		
		if (info.getSourceItems().size() == 1 && info.getTargetItems().size() == 1 &&
				source.equals(getFirstSourceItem()) && target.equals(getFirstTargetItem())) {
			return info.getCell();
		}
		
		return null;
	}

	/**
	 * @see AlignmentInfo#getFirstSourceItem()
	 */
	@Override
	public SchemaItem getFirstSourceItem() {
		return selection.getCellInfo().getSourceItems().iterator().next();
	}

	/**
	 * @see AlignmentInfo#getFirstTargetItem()
	 */
	@Override
	public SchemaItem getFirstTargetItem() {
		return selection.getCellInfo().getTargetItems().iterator().next();
	}

	/**
	 * @see AlignmentInfo#getSourceItemCount()
	 */
	@Override
	public int getSourceItemCount() {
		return selection.getCellInfo().getSourceItems().size();
	}

	/**
	 * @see AlignmentInfo#getSourceItems()
	 */
	@Override
	public Collection<SchemaItem> getSourceItems() {
		return new ArrayList<SchemaItem>(selection.getCellInfo().getSourceItems());
	}

	/**
	 * @see AlignmentInfo#getTargetItemCount()
	 */
	@Override
	public int getTargetItemCount() {
		return selection.getCellInfo().getTargetItems().size();
	}

	/**
	 * @see AlignmentInfo#getTargetItems()
	 */
	@Override
	public Collection<SchemaItem> getTargetItems() {
		return new ArrayList<SchemaItem>(selection.getCellInfo().getTargetItems());
	}

	/**
	 * @see AlignmentInfo#hasAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public boolean hasAlignment(SchemaItem source, SchemaItem target) {
		return getAlignment(source, target) != null;
	}

	/**
	 * @see AlignmentInfo#getAlignment(Collection, Collection)
	 */
	@Override
	public ICell getAlignment(Collection<SchemaItem> source,
			Collection<SchemaItem> target) {
		CellInfo info = selection.getCellInfo();
		
		if (info.getSourceItems().containsAll(source) && info.getTargetItems().containsAll(target)) {
			return info.getCell();
		}
			
		return null;
	}

	/**
	 * @see AlignmentInfo#hasAlignment(Collection, Collection)
	 */
	@Override
	public boolean hasAlignment(Collection<SchemaItem> source,
			Collection<SchemaItem> target) {
		return getAlignment(source, target) != null;
	}

}
