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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.userdefined;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.CoreFunctionWizardsPlugin;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;

/**
 * Wizard for the User Defined Function (UDF). A UDF is more or less a 
 * placeholder function that is not executed in lieu of missing transformation 
 * capability, but documents what should be done.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class UserDefinedFunctionWizard 
	extends AbstractSingleComposedCellWizard {
	
	private UserDefinedFunctionWizardPage mainPage;
		
	/**
	 * @param selection
	 */
	public UserDefinedFunctionWizard(AlignmentInfo selection) {
		super(selection);
		super.setWindowTitle(Messages.UserDefinedFunctionWizard_0); 
	}


	@Override
	protected void init() {
		
		this.mainPage = new UserDefinedFunctionWizardPage(
						Messages.UserDefinedFunctionWizard_1);
		
		ITransformation t = getResultCell().getEntity1().getTransformation();
		
		// init expression from cell
		if (t != null) {
			List<IParameter> parameters = t.getParameters();
			String udfName = t.getService().getLocation();
			if (udfName != null && parameters != null) {
				this.mainPage.setInitialConfiguration(udfName, parameters);
			}
		}
	}


	@Override
	public boolean performFinish() {
		IPreferenceStore preferences = CoreFunctionWizardsPlugin.plugin.getPreferenceStore();
		preferences.putValue("eu.esdihumboldt.udfPreferences", this.mainPage.getEncodedUdfTemplates()); //$NON-NLS-1$
		
		// create adequate cell
		ICell cell = super.getResultCell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(Messages.UserDefinedFunctionWizard_3 + this.mainPage.getUdfName()));
		t.setParameters(this.mainPage.getUdfParameters());
		((Property)cell.getEntity1()).setTransformation(t);
		
		return true;
	}
	
	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
