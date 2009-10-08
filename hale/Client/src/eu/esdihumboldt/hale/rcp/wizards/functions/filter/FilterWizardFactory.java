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

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory;

/**
 * Factory for {@link FilterWizard}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FilterWizardFactory implements FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(CellSelection)
	 */
	@Override
	public FunctionWizard createWizard(CellSelection cellSelection) {
		return new FilterWizard(cellSelection);
	}

	/**
	 * @see FunctionWizardFactory#createWizard(SchemaSelection, AlignmentService)
	 */
	@Override
	public FunctionWizard createWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		return new FilterWizard(schemaSelection, alignmentService);
	}

	/**
	 * @see FunctionWizardFactory#supports(CellSelection)
	 */
	@Override
	public boolean supports(CellSelection cellSelection) {
		SchemaItem source = cellSelection.getCellInfo().getSourceItem();
		SchemaItem target = cellSelection.getCellInfo().getTargetItem();
		
		if (!source.isFeatureType() || !target.isFeatureType()) {
			// only feature types supported
			return false;
		}
		
		return true;
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
		else if (!source.isFeatureType() || !target.isFeatureType()) {
			// only feature types supported
			return false;
		}
		
		ICell cell = alignmentService.getCell(
				source.getEntity(), target.getEntity());
		
		return cell != null;
	}

}
