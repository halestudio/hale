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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.geometric;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.corefunctions.OrdinatesToPointFunction;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OrdinatesPointWizard 
		extends AbstractSingleComposedCellWizard  {
	
	private OrdinatesPointWizardPage mainPage;

	/**
	 * Constructor
	 * 
	 * @param selection the AlignmentInfo
	 */
	public OrdinatesPointWizard(AlignmentInfo selection) {
		super(selection);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		this.mainPage = new OrdinatesPointWizardPage("Ordinates To Point Function");
		mainPage.setDescription("Calculates a point out of two Doubles.") ;
		super.setWindowTitle("Ordinates To Point Function"); 
	}

	@Override
	public boolean performFinish() {
		ICell cell = super.getResultCell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(OrdinatesToPointFunction.class.getName()));
		List<IParameter> parameters = new ArrayList<IParameter>();
		parameters.add(new Parameter(
				OrdinatesToPointFunction.X_EXPRESSION_PARAMETER, 
				this.mainPage.getExpansionX()));
		parameters.add(new Parameter(
				OrdinatesToPointFunction.Y_EXPRESSION_PARAMETER, 
				this.mainPage.getExpansionY()));
		t.setParameters(parameters);
		((ComposedProperty)cell.getEntity1()).setTransformation(t);
		
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
