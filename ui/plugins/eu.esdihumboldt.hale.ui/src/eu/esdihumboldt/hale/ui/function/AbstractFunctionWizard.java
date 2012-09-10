/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Abstract function wizard
 * 
 * @author Simon Templer
 */
public abstract class AbstractFunctionWizard extends Wizard implements FunctionWizard {

	private final Cell initCell;

	private final SchemaSelection initSelection;

	/**
	 * Create a function wizard based on an existing cell
	 * 
	 * @param cell the existing cell
	 */
	public AbstractFunctionWizard(Cell cell) {
		super();

		this.initCell = cell;
		this.initSelection = null;
	}

	/**
	 * Create a function wizard based on a schema selection
	 * 
	 * @param selection the schema selection, may be <code>null</code>
	 */
	public AbstractFunctionWizard(SchemaSelection selection) {
		super();

		this.initCell = null;
		this.initSelection = selection;
	}

	/**
	 * Calls {@link #init(Cell)} or {@link #init(SchemaSelection)}
	 * 
	 * @see FunctionWizard#init()
	 */
	@Override
	public void init() {
		if (initCell != null) {
			init(initCell);
		}
		else {
			init(initSelection);
		}
	}

	/**
	 * @return the initCell
	 */
	public Cell getInitCell() {
		return initCell;
	}

	/**
	 * @return the initSelection
	 */
	public SchemaSelection getInitSelection() {
		return initSelection;
	}

	/**
	 * Initialize the wizard based on a schema selection.
	 * 
	 * @param selection the schema selection, may be <code>null</code>
	 */
	protected void init(SchemaSelection selection) {
		// override me
	}

	/**
	 * Initialize the wizard based on an existing cell.
	 * 
	 * @param cell the cell
	 */
	protected void init(Cell cell) {
		// override me
	}

}
