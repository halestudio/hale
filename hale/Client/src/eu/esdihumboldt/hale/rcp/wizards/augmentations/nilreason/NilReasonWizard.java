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

package eu.esdihumboldt.hale.rcp.wizards.augmentations.nilreason;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.impl.NilReasonFunction;
import eu.esdihumboldt.cst.transformer.impl.NilReasonFunction.NilReasonType;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizard;

/**
 * Wizard for the {@link NilReasonFunction} augmentation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonWizard extends AugmentationWizard {
	
	private static final Log log = LogFactory.getLog(NilReasonWizard.class);
	
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
		NilReasonType type = null;
		
		if (cell.getEntity1().getTransformation() != null) {
			List<IParameter> parameters = cell.getEntity1().getTransformation().getParameters();
			
			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();
				while (it.hasNext() && type == null) {
					IParameter param = it.next();
					if (param.getName().equals(
							NilReasonFunction.PARAMETER_NIL_REASON_TYPE)) {
						try {
							type = NilReasonType.valueOf(param.getValue());
						} catch (IllegalArgumentException e) {
							log.warn("Illegal value for Nil reason type");
						}
					}
				}
			}
		}
		
		page = new NilReasonWizardPage("mainPage", "Select a Nil reason", null);
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
		NilReasonType type = page.getType();
		
		Cell result = getResultCell();
		
		Entity entity = (Entity) result.getEntity2();
		
		Transformation transformation = new Transformation();
		transformation.setService(new Resource(NilReasonFunction.class.getName()));
		transformation.getParameters().add(
				new Parameter(
						NilReasonFunction.PARAMETER_NIL_REASON_TYPE, 
						type.toString()));
		
		entity.setTransformation(transformation);
		
		return true;
	}

}
