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
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellInfo;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * Abstract function wizard working with a single {@link Cell}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class AbstractSingleCellWizard extends Wizard implements FunctionWizard {

	private final Cell cell;
	
	private final SchemaItem sourceItem;
	
	private final SchemaItem targetItem;
	
	/**
	 * Creates a wizard that creates a new {@link Cell} for the first source
	 *   and target items in the given {@link SchemaSelection}
	 *   
	 * @param schemaSelection the {@link SchemaItem} selection
	 * @param alignmentService the alignment service
	 */
	public AbstractSingleCellWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		super();
		
		sourceItem = schemaSelection.getFirstSourceItem();
		targetItem = schemaSelection.getFirstTargetItem();
		
		ICell oldCell = alignmentService.getCell(
				sourceItem.getEntity(), targetItem.getEntity());
		
		if (oldCell == null) {
			cell = new Cell();
			cell.setEntity1(sourceItem.getEntity());
			cell.setEntity2(targetItem.getEntity());
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
	 * Creates a wizard that edits an {@link ICell} specified by the given
	 *  {@link CellSelection}
	 *   
	 * @param cellSelection the {@link ICell} selection
	 */
	public AbstractSingleCellWizard(CellSelection cellSelection) {
		super();
		
		CellInfo info = cellSelection.getCellInfo();
		ICell selectedCell = info.getCell();
		
		// copy the cell
		cell = new Cell();
		cell.setEntity1(selectedCell.getEntity1());
		cell.setEntity2(selectedCell.getEntity2());
		cell.setAbout(selectedCell.getAbout());
		cell.setLabel(selectedCell.getLabel());
		cell.setMeasure(selectedCell.getMeasure());
		cell.setRelation(selectedCell.getRelation());
		
		sourceItem = info.getSourceItem();
		targetItem = info.getTargetItem();
		
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
		if (page instanceof AbstractSingleCellWizardPage) {
			((AbstractSingleCellWizardPage) page).setParent(this);
		}
		
		super.addPage(page);
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

	/**
	 * Get the cell to edit
	 * 
	 * @return the cell to edit
	 */
	protected final Cell getResultCell() {
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
