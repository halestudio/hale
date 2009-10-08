package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizard;

/**
 * Network fusion wizard
 * 
 * @author ?, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkFusionFunctionWizard extends AbstractSingleCellWizard {

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(CellSelection)
	 */
	public NetworkFusionFunctionWizard(CellSelection cellSelection) {
		super(cellSelection);
	}

	/**
	 * @see AbstractSingleCellWizard#AbstractSingleCellWizard(SchemaSelection, AlignmentService)
	 */
	public NetworkFusionFunctionWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		super(schemaSelection, alignmentService);
	}

	/**
	 * @see AbstractSingleCellWizard#init()
	 */
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
