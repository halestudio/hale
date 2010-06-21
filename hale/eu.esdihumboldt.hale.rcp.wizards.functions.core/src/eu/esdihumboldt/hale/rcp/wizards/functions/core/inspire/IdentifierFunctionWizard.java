/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class IdentifierFunctionWizard 
	extends AbstractSingleCellWizard {
	
	private IdentifierFunctionWizardPage mainPage = null;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public IdentifierFunctionWizard(AlignmentInfo selection) {
		super(selection);
		this.mainPage = new IdentifierFunctionWizardPage("Identifier Definition");
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		// TODO allow for editing of preexisting cell
		
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(IdentifierFunction.class.getName()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.COUNTRY_PARAMETER_NAME, 
						mainPage.getCountryCode()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.DATA_PROVIDER_PARAMETER_NAME, 
						mainPage.getProviderName()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.PRODUCT_PARAMETER_NAME, 
						mainPage.getProductName()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.VERSION, 
						mainPage.getVersion()));
		t.getParameters().add(
				new Parameter(
						IdentifierFunction.VERSION_NIL_REASON, 
						mainPage.getVersionNilReason()));
		
		((Entity) cell.getEntity1()).setTransformation(t);
		
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
