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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.augmentation.constantvalue;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.ConstantValueFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizard;

/**
 * Wizard for the {@link ConstantValueFunction} augmentation
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class ConstantValueWizard extends AugmentationWizard {

	private ConstantValueWizardPage page;

	/**
	 * @see AugmentationWizard#AugmentationWizard(SchemaItem, ICell)
	 */
	public ConstantValueWizard(SchemaItem item, ICell augmentation) {
		super(item, augmentation);

	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizard#init()
	 */
	@Override
	protected void init() {

		// get and validate the initial value from the cell (if available)
		page = new ConstantValueWizardPage("mainPage",
				"Configure Constant Value Augmentation", null);
		super.setWindowTitle("Constant Value Augmentation Wizard");

	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// String parameterName = page.getParamName();
		String parameterValue = page.getParamValue();
		Cell result = getResultCell();
		Entity entity = (Entity) result.getEntity2();
		Transformation transformation = new Transformation();
		transformation.setService(new Resource(ConstantValueFunction.class
				.getName()));
		// Simon: replaced parameterName with constant that is used in
		// ConstantValueFunction as the parameter name
		transformation
				.getParameters()
				.add(new Parameter(
							ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME,
							parameterValue));
		entity.setTransformation(transformation);

		return true;
	}

	@Override
	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}

}
