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

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Abstract function wizard
 * 
 * @author Simon Templer
 */
public abstract class AbstractFunctionWizard extends Wizard implements FunctionWizard {

	private final Cell initCell;
	private final SchemaSelection initSelection;
	private final ListMultimap<String, ParameterValue> initParameters;

	/**
	 * Create a function wizard based on an existing cell
	 * 
	 * @param cell the existing cell
	 */
	public AbstractFunctionWizard(Cell cell) {
		super();

		this.initCell = cell;
		this.initSelection = null;
		this.initParameters = null;
	}

	/**
	 * Create a function wizard based on a schema selection
	 * 
	 * @param selection the schema selection, may be <code>null</code>
	 */
	public AbstractFunctionWizard(SchemaSelection selection) {
		this(selection, null);
	}

	/**
	 * Create a function wizard based on a schema selection
	 * 
	 * @param selection the schema selection, may be <code>null</code>
	 * @param parameters the initial function parameters
	 */
	public AbstractFunctionWizard(SchemaSelection selection,
			ListMultimap<String, ParameterValue> parameters) {
		super();

		this.initCell = null;
		this.initSelection = selection;
		this.initParameters = parameters;
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
		else if (initParameters == null) {
			init(initSelection);
		}
		else {
			init(initSelection, initParameters);
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
	 * @return the initial parameters
	 */
	public ListMultimap<String, ParameterValue> getInitParameters() {
		return initParameters;
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
	 * Initialize the wizard based on a schema selection and prefill the
	 * function parameters.
	 * 
	 * @param selection the schema selection, may be <code>null</code>
	 * @param parameters the function parameters to prefill
	 */
	protected void init(SchemaSelection selection,
			ListMultimap<String, ParameterValue> parameters) {
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
