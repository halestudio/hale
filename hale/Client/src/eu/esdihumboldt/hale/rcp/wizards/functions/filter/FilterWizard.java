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
package eu.esdihumboldt.hale.rcp.wizards.functions.filter;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;

/**
 * This {@link Wizard} is used to invoke a Renaming Transformer for the Source
 * Feature Type
 * 
 * @author Anna Pitaev, Logica; Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
public class FilterWizard extends AbstractSingleCellWizard {

	private static Logger _log = Logger.getLogger(FilterWizard.class);

	FilterWizardMainPage mainPage;
	FilterWizardSecondPage secondPage;

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(CellSelection)
	 */
	public FilterWizard(CellSelection cellSelection) {
		super(cellSelection);
	}

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(SchemaSelection, AlignmentService)
	 */
	public FilterWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		super(schemaSelection, alignmentService);
	}
	
	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		this.mainPage = new FilterWizardMainPage("Configure Filter Expression",
		"Configure Filter Expression");
		this.secondPage = new FilterWizardSecondPage(
				"Configure Filter Expression", "Configure Filter Expression");
		super.setWindowTitle("Configure Function");
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.secondPage.isPageComplete());
		return true;
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ICell cell = getResultCell();
		
		Restriction r = new Restriction(null, null);
		r.setCqlStr(secondPage.buildCQL());
		
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
		addPage(secondPage);
	}

}
