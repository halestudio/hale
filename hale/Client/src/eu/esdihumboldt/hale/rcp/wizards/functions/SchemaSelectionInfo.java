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
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * {@link AlignmentInfo} based on a {@link SchemaSelection}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaSelectionInfo implements AlignmentInfo {
	
	private final SchemaSelection selection;
	
	private final AlignmentService alignment;

	/**
	 * Constructor
	 * 
	 * @param selection
	 * @param alignment
	 */
	public SchemaSelectionInfo(SchemaSelection selection,
			AlignmentService alignment) {
		super();
		this.selection = selection;
		this.alignment = alignment;
	}

	/**
	 * @see AlignmentInfo#getAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public ICell getAlignment(SchemaItem source, SchemaItem target) {
		return alignment.getCell(source.getEntity(), target.getEntity());
	}

	/**
	 * @see AlignmentInfo#getSourceItemCount()
	 */
	@Override
	public int getSourceItemCount() {
		return selection.getSourceItems().size();
	}

	/**
	 * @see AlignmentInfo#getSourceItems()
	 */
	@Override
	public Iterable<SchemaItem> getSourceItems() {
		return selection.getSourceItems();
	}

	/**
	 * @see AlignmentInfo#getTargetItemCount()
	 */
	@Override
	public int getTargetItemCount() {
		return selection.getTargetItems().size();
	}

	/**
	 * @see AlignmentInfo#getTargetItems()
	 */
	@Override
	public Iterable<SchemaItem> getTargetItems() {
		return selection.getTargetItems();
	}

	/**
	 * @see AlignmentInfo#hasAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public boolean hasAlignment(SchemaItem source, SchemaItem target) {
		return getAlignment(source, target) != null;
	}

	/**
	 * @see AlignmentInfo#getFirstSourceItem()
	 */
	@Override
	public SchemaItem getFirstSourceItem() {
		return selection.getFirstSourceItem();
	}

	/**
	 * @see AlignmentInfo#getFirstTargetItem()
	 */
	@Override
	public SchemaItem getFirstTargetItem() {
		return selection.getFirstTargetItem();
	}

}
