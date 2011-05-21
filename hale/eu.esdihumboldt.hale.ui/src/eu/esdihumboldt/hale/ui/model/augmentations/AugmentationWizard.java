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
package eu.esdihumboldt.hale.ui.model.augmentations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Abstract function wizard working with a single {@link Cell}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AugmentationWizard extends Wizard implements FunctionWizard {

	private final Cell cell;
	
	private final SchemaItem item;
	
	/**
	 * Creates a wizard that creates or edits an augmentation
	 *   for the given item
	 *   
	 * @param item the item
	 * @param augmentation the current augmentation, may be <code>null</code>
	 */
	public AugmentationWizard(SchemaItem item, ICell augmentation) {
		super();
		
		this.item = item;
		
		if (augmentation == null) {
			cell = new Cell();
			cell.setEntity1(Entity.NULL_ENTITY);
			cell.setEntity2(item.getEntity());
		}
		else {
			// copy the cell
			cell = new Cell();
			cell.setEntity1(augmentation.getEntity1());
			cell.setEntity2(augmentation.getEntity2());
			cell.setAbout(augmentation.getAbout());
			cell.setLabel(augmentation.getLabel());
			cell.setMeasure(augmentation.getMeasure());
			cell.setRelation(augmentation.getRelation());
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
		if (page instanceof AugmentationWizardPage) {
			((AugmentationWizardPage) page).setParent(this);
		}
		
		super.addPage(page);
	}

	/**
	 * @return the item to augment
	 */
	public SchemaItem getItem() {
		return item;
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
