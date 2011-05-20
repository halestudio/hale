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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.augmentation.nilreason;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.NilReasonFunction;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizard;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * Wizard for the {@link NilReasonFunction} augmentation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonWizard extends AugmentationWizard {
	
	private static final Logger log = Logger.getLogger(NilReasonWizard.class);
	
	private NilReasonWizardPage page;

	/**
	 * @see AugmentationWizard#AugmentationWizard(SchemaItem, ICell)
	 */
	public NilReasonWizard(SchemaItem item, ICell augmentation) {
		super(item, augmentation);
	}

	/**
	 * @see AugmentationWizard#init()
	 */
	@Override
	protected void init() {
		Cell cell = getResultCell();
		
		// get initial value from the cell (if available)
		String nilReason = null;
		
		if (cell.getEntity2().getTransformation() != null) {
			List<IParameter> parameters = cell.getEntity2().getTransformation().getParameters();
			
			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();
				while (it.hasNext() && nilReason == null) {
					IParameter param = it.next();
					if (param.getName().equals(
							NilReasonFunction.PARAMETER_NIL_REASON_TYPE)) {
						try {
							nilReason = param.getValue();
						} catch (IllegalArgumentException e) {
							log.warn("Illegal value for Nil reason type"); //$NON-NLS-1$
						}
					}
				}
			}
		}
		
		if (nilReason == null) {
			// default value
			nilReason = "unknown"; //$NON-NLS-1$
		}
		
		page = new NilReasonWizardPage("mainPage", "Select a Nil reason", null, //$NON-NLS-1$ //$NON-NLS-2$
				nilReason);
	}

	@Override
	public void addPages() {
		addPage(page);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		String nilReason = page.getNilReason();
		
		Cell result = getResultCell();
		
		Entity entity = (Entity) result.getEntity2();
		
		Transformation transformation = new Transformation();
		transformation.setService(new Resource(NilReasonFunction.class.getName()));
		transformation.getParameters().add(
				new Parameter(
						NilReasonFunction.PARAMETER_NIL_REASON_TYPE, 
						nilReason));
		
		entity.setTransformation(transformation);
		
		return true;
	}

}
