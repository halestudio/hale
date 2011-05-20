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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.filter;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.AlignmentInfo;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * This {@link Wizard} is used to invoke a Renaming CstFunction for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class FilterWizard extends AbstractSingleCellWizard {

	private static Logger _log = Logger.getLogger(FilterWizard.class);

	private FilterWizardMainPage mainPage;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(AlignmentInfo)
	 */
	public FilterWizard(AlignmentInfo selection) {
		super(selection);
	}
	
	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new FilterWizardMainPage(
				Messages.FilterWizard_0, Messages.FilterWizard_1);
		super.setWindowTitle(Messages.FilterWizard_2);
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete()); //$NON-NLS-1$
		return true;
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		
		Restriction r = new Restriction(null);
		r.setCqlStr(mainPage.buildCQL());
		
		if (cell.getEntity1() instanceof FeatureClass) {
			FeatureClass fc = (FeatureClass)cell.getEntity1();
			if (fc.getAttributeValueCondition() == null) {
				fc.setAttributeValueCondition(new ArrayList<Restriction>());
			}
			fc.getAttributeValueCondition().add(r);
		}
		if (cell.getEntity1() instanceof Property) {
			((Property)cell.getEntity1()).getValueCondition().add(r);
		}
		
		return true;
	}

	/**
	 * @see IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
