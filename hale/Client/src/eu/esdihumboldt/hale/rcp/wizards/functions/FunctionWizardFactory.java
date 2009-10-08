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
package eu.esdihumboldt.hale.rcp.wizards.functions;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * Factory for function wizards
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface FunctionWizardFactory {
	
	/**
	 * Determine if the wizard defined by this factory supports
	 *   creating new cells or editing existing cells based on
	 *   the given {@link SchemaSelection}
	 * 
	 * @param schemaSelection the {@link SchemaItem} selection
	 * @param alignmentService the alignment service
	 * 
	 * @return if the wizard supports creating/editing cells
	 *   cells based on the given selection
	 */
	public boolean supports(SchemaSelection schemaSelection,
			AlignmentService alignmentService);
	
	/**
	 * Determine if the wizard defined by this factory supports
	 *   editing the {@link ICell} specified by the given
	 *   {@link CellSelection}
	 * 
	 * @param cellSelection the {@link ICell} selection
	 * 
	 * @return if the wizard supports editing the cell specified
	 *   by the given selection
	 */
	public boolean supports(CellSelection cellSelection);
	
	/**
	 * Creates a wizard for creating new cells or editing existing
	 *   cells based on the given {@link SchemaSelection}
	 * 
	 * @param schemaSelection the {@link SchemaItem} selection
	 * @param alignmentService the alignment service
	 * 
	 * @return the new wizard instance
	 */
	public FunctionWizard createWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService);
	
	/**
	 * Creates a wizard for editing the {@link ICell} specified by
	 *   the given {@link CellSelection}
	 * 
	 * @param cellSelection the {@link ICell} selection
	 * 
	 * @return the edit wizard instance
	 */
	public FunctionWizard createWizard(CellSelection cellSelection);

}
