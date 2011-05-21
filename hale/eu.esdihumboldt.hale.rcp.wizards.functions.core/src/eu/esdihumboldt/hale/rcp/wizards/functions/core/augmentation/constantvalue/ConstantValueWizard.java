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

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.ConstantValueFunction;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.ui.model.augmentations.AugmentationWizard;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Wizard for the {@link ConstantValueFunction} augmentation
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
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
	 * @see AugmentationWizard#init()
	 */
	@Override
	protected void init() {

		// get and validate the initial value from the cell (if available)
		page = new ConstantValueWizardPage("mainPage", //$NON-NLS-1$
				Messages.ConstantValueWizard_1, null);
		super.setWindowTitle(Messages.ConstantValueWizard_2);

	}

	/**
	 * @see Wizard#performFinish()
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
