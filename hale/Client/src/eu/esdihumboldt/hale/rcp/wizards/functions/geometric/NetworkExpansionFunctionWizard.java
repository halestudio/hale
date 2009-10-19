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
package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.impl.NetworkExpansionTransformer;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;

/**
 * A simplified Wizard for the configuration of the Network Expansion function,
 * which takes any MultiLineString and buffers it to a MultiPolygon.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkExpansionFunctionWizard 
		extends AbstractSingleCellWizard {
	
	//private static Logger _log = Logger.getLogger(NetworkExpansionFunctionWizard.class);
	
	private NetworkExpansionFunctionWizardPage mainPage;
	
	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public NetworkExpansionFunctionWizard(AlignmentInfo selection) {
		super(selection);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new NetworkExpansionFunctionWizardPage(
			"Configure Network Expansion"); 
		super.setWindowTitle("Configure Function"); 
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		Entity entity1 = (Entity) cell.getEntity1();
		
		Transformation transformation = new Transformation();
		transformation.setLabel(NetworkExpansionTransformer.class.getName()); //FIXME
		transformation.getParameters().add(new Parameter("Expansion", mainPage.getExpansion()));
		
		entity1.setTransformation(transformation);

		return true;
	}
	
	/**
	 * @see IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(this.mainPage);
    }

}
