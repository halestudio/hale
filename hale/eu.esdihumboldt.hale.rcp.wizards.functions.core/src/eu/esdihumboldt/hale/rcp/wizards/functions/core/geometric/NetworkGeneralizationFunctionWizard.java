package eu.esdihumboldt.hale.rcp.wizards.functions.core.geometric;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.ui.model.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;

/**
 * Network generalization wizard
 * 
 * @author ?, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkGeneralizationFunctionWizard extends AbstractSingleCellWizard  {

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public NetworkGeneralizationFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
