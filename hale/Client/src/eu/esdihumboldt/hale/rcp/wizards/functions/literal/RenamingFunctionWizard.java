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
package eu.esdihumboldt.hale.rcp.wizards.functions.literal;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.transformer.impl.RenameAttributeTransformer;
import eu.esdihumboldt.cst.transformer.impl.RenameFeatureTransformer;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * This {@link Wizard} is used to invoke a Renaming CstFunction for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class RenamingFunctionWizard extends AbstractSingleCellWizard {

	private static Logger _log = Logger.getLogger(RenamingFunctionWizard.class);

	RenamingFunctionWizardMainPage mainPage;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public RenamingFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new RenamingFunctionWizardMainPage(
				"Configure Feature Type Renaming Function",
				"Configure Feature Type Renaming Function");
		super.setWindowTitle("Configure Function");
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
		return this.mainPage.isPageComplete();
	}

	/**
	 * @see Wizard#performFinish()
	 */

	@Override
	public boolean performFinish() {
		Cell c = (Cell) getResultCell();
		
		Entity source = (Entity) c.getEntity1();
		Entity target = (Entity) c.getEntity2();
		
		Transformation t = new Transformation();
		
		if (source instanceof FeatureClass && target instanceof FeatureClass) {
			// Type renaming
			t.setLabel(RenameFeatureTransformer.class.getName());
			
			//TODO any parameters needed?
		}
		else if (source instanceof Property && target instanceof Property) {
			// Attribute renaming
			t.setLabel(RenameAttributeTransformer.class.getName());
			
			//Add old attribute name
			t.getParameters().add(new Parameter(
					RenameAttributeTransformer.OLD_ATTRIBUTE_NAME_PARAMETER, 
					source.getLabel().get(source.getLabel().size() - 1)));
			t.getParameters().add(new Parameter(
					RenameAttributeTransformer.NEW_ATTRIBUTE_NAME_PARAMETER, 
					target.getLabel().get(target.getLabel().size() - 1)));
		}
		else {
			//TODO error message?
			return false;
		}

		source.setTransformation(t);

		return true;
	}

	/**
	 * @see IWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
