/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
