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

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.cst.transformer.impl.NetworkExpansionTransformer;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;

/**
 * Factory for {@link NetworkExpansionFunctionWizard}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class NetworkExpansionFunctionWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(CellSelection)
	 */
	@Override
	public FunctionWizard createWizard(CellSelection cellSelection) {
		return new NetworkExpansionFunctionWizard(cellSelection);
	}

	/**
	 * @see FunctionWizardFactory#createWizard(SchemaSelection, AlignmentService)
	 */
	@Override
	public FunctionWizard createWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		return new NetworkExpansionFunctionWizard(schemaSelection,
				alignmentService);
	}

	/**
	 * @see FunctionWizardFactory#supports(CellSelection)
	 */
	@Override
	public boolean supports(CellSelection cellSelection) {
		if (!cellSelection.isEmpty()) {
			ITransformation trans = cellSelection.getCellInfo().getCell().getEntity1().getTransformation();
			return trans.getLabel().equals(NetworkExpansionTransformer.class.getName());
		}
		
		return false;
	}

	/**
	 * @see FunctionWizardFactory#supports(SchemaSelection, AlignmentService)
	 */
	@Override
	public boolean supports(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		SchemaItem source = schemaSelection.getFirstSourceItem();
		SchemaItem target = schemaSelection.getFirstTargetItem();
		
		if (source == null || target == null) {
			return false;
		}
		
		ICell cell = alignmentService.getCell(
				source.getEntity(), target.getEntity());
		
		if (cell != null) {
			// only allow editing matching transformation
			return cell.getEntity1().getTransformation().getLabel().equals(
					NetworkExpansionTransformer.class.getName());
		}
		else if (source.isAttribute() && target.isAttribute()) {
			//TODO more sophisticated check
			return true;
		}
		
		return false;
	}

}
