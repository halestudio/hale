package eu.esdihumboldt.hale.rcp.wizards.functions.core.numeric;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * Numeric function wizard
 * 
 * @author ?, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NumericFunctionWizard extends AbstractSingleCellWizard {

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public NumericFunctionWizard(AlignmentInfo selection) {
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
