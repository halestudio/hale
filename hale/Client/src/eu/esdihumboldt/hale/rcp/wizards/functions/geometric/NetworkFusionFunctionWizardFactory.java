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
package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * Factory for {@link NetworkFusionFunctionWizard}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkFusionFunctionWizardFactory implements FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(CellSelection)
	 */
	@Override
	public FunctionWizard createWizard(CellSelection cellSelection) {
		return new NetworkFusionFunctionWizard(cellSelection);
	}

	/**
	 * @see FunctionWizardFactory#createWizard(SchemaSelection, AlignmentService)
	 */
	@Override
	public FunctionWizard createWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		return new NetworkFusionFunctionWizard(schemaSelection,
				alignmentService);
	}

	/**
	 * @see FunctionWizardFactory#supports(CellSelection)
	 */
	@Override
	public boolean supports(CellSelection cellSelection) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see FunctionWizardFactory#supports(SchemaSelection, AlignmentService)
	 */
	@Override
	public boolean supports(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		// TODO Auto-generated method stub
		return false;
	}

}
