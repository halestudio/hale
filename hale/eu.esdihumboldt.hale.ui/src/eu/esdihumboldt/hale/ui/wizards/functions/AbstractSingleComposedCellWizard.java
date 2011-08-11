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
package eu.esdihumboldt.hale.ui.wizards.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.hale.ui.wizards.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Abstract function wizard working with a single {@link Cell}
 *   where an entity can consist of multiple composed entities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class AbstractSingleComposedCellWizard extends Wizard implements FunctionWizard {

	private final Cell cell;
	
	private final Set<SchemaItem> sourceItems = new TreeSet<SchemaItem>();
	
	private final Set<SchemaItem> targetItems = new TreeSet<SchemaItem>();
	
	/**
	 * Creates a wizard that creates a new {@link Cell} or
	 *   copies the existing {@link ICell} for the
	 *   source an target items of the given {@link AlignmentInfo}
	 *   
	 * @param selection the {@link AlignmentInfo} of the selection
	 */
	public AbstractSingleComposedCellWizard(AlignmentInfo selection) {
		super();
		
		for (SchemaItem item : selection.getSourceItems()) {
			sourceItems.add(item);
		}
		for (SchemaItem item : selection.getTargetItems()) {
			targetItems.add(item);
		}
		
		ICell oldCell = selection.getAlignment(
				sourceItems, targetItems);
		
		if (oldCell == null) {
			cell = new Cell();
			cell.setEntity1(SchemaSelectionInfo.determineEntity(sourceItems));
			cell.setEntity2(SchemaSelectionInfo.determineEntity(targetItems));
		}
		else {
			// copy the cell
			cell = new Cell();
			cell.setEntity1(oldCell.getEntity1());
			cell.setEntity2(oldCell.getEntity2());
			cell.setAbout(oldCell.getAbout());
			cell.setLabel(oldCell.getLabel());
			cell.setMeasure(oldCell.getMeasure());
			cell.setRelation(oldCell.getRelation());
		}
		
		init();
	}
	
	/**
	 * Initialize after the cell has been set
	 */
	protected abstract void init();
	
	/**
	 * @see Wizard#addPage(IWizardPage)
	 */
	@Override
	public void addPage(IWizardPage page) {
		if (page instanceof AbstractSingleComposedCellWizardPage) {
			((AbstractSingleComposedCellWizardPage) page).setParent(this);
		}
		
		super.addPage(page);
	}

	/**
	 * @return the sourceItem
	 */
	public Set<SchemaItem> getSourceItems() {
		return sourceItems;
	}

	/**
	 * @return the targetItem
	 */
	public Set<SchemaItem> getTargetItems() {
		return targetItems;
	}
	
	/**
	 * Get the first source item
	 * 
	 * @return the first source item
	 */
	public SchemaItem getFirstSourceItem() {
		return sourceItems.iterator().next();
	}
	
	/**
	 * Get the first target item
	 * 
	 * @return the first target item
	 */
	public SchemaItem getFirstTargetItem() {
		return targetItems.iterator().next();
	}

	/**
	 * Get the cell to edit
	 * 
	 * @return the cell to edit
	 */
	public final Cell getResultCell() {
		return cell;
	}
	
	/**
	 * @see FunctionWizard#getResult()
	 */
	@Override
	public List<ICell> getResult() {
		List<ICell> result = new ArrayList<ICell>();
		result.add(getResultCell());
		return result;
	}

}
