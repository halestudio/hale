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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.geometric;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.cst.corefunctions.NetworkExpansionFunction;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

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
			Messages.NetworkExpansionFunctionWizard_0); 
		super.setWindowTitle(Messages.NetworkExpansionFunctionWizard_1); 
		super.setNeedsProgressMonitor(true);
		
		ICell cell = getResultCell();
		
		String expression = null;
		
		// init expression from cell
		if (cell.getEntity1().getTransformation() != null) {
			List<IParameter> parameters = cell.getEntity1().getTransformation().getParameters();
			
			if (parameters != null) {
				Iterator<IParameter> it = parameters.iterator();
				while (it.hasNext() && expression == null) {
					IParameter param = it.next();
					if (param.getName().equals(NetworkExpansionFunction.BUFFERWIDTH)) {
						expression = param.getValue();
						break;
					}
				}
			}
		}
		if (expression != null) {
			this.mainPage.setInitialExpression(expression);
		}
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		Entity entity1 = (Entity) cell.getEntity1();
		
		Transformation transformation = new Transformation();
		transformation.setService(new Resource(NetworkExpansionFunction.class.getName()));
		transformation.getParameters().add(
				new Parameter(NetworkExpansionFunction.BUFFERWIDTH, mainPage.getExpansion()));
		
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
