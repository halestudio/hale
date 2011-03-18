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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.literal;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;

/**
 * Wizard for the {@link RenameAttributeFunction}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class RenameAttributeWizard extends AbstractSingleCellWizard {
	
	private RenameAttributeWizardMainPage mainPage;
	
	/**
	 * @param selection the current selection in the Schema Explorer
	 */
	public RenameAttributeWizard(AlignmentInfo selection) {
		super(selection);
		
		this.mainPage = new RenameAttributeWizardMainPage(
				Messages.RenameAttributeWizard_0,
				Messages.RenameAttributeWizard_1);
		super.setWindowTitle(Messages.RenameAttributeWizard_2);
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		// do nothing
	}
	
	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Cell c = getResultCell();
		Entity source = (Entity) c.getEntity1();
		Transformation t = new Transformation();
		t.setLabel(RenameFeatureFunction.class.getName());
		t.setService(new Resource(RenameAttributeFunction.class.getName()));
		/*String path = mainPage.getNestedAttributePath();
		if (path != null && !path.equals("")) {
			t.getParameters().add(new Parameter(
					RenameAttributeFunction.NESTED_ATTRIBUTE_PATH, path));
		}*/
		c.setRelation(RelationType.Equivalence);
		source.setTransformation(t);
		return true;
	}
	
	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return this.mainPage.isPageComplete();
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
