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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname;

import org.eclipse.jface.wizard.Wizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * Wizard for the {@link GeographicNameFunction}.
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class GeographicNameFunctionWizard extends AbstractSingleCellWizard {

	private GeographicNamePage page;

	/**
	 * @param selection
	 */
	public GeographicNameFunctionWizard(AlignmentInfo selection) {
		super(selection);

	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {

		this.page = new GeographicNamePage("main",
				"Configure Geographic Name Function", null);
		super.setWindowTitle("INSPIRE Geographic Name Function Wizard");
		

	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		return page.isPageComplete();

	}
	/*
	 * @Override public void createPageControls(Composite pageContainer) {
	 * //System.out.println("createPageControls"); pageContainer.setSize(1000,
	 * 1000); page.createControl(pageContainer);
	 * Assert.isNotNull(page.getControl());
	 * 
	 * }
	 */

}
