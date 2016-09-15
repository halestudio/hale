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

package eu.esdihumboldt.hale.ui.function.contribution;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.function.contribution.internal.AbstractWizardAction;
import eu.esdihumboldt.hale.ui.function.contribution.internal.ReplaceFunctionWizardAction;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;

/**
 * Contribution item offering possibilities to replace an existing cell with
 * another function.
 * 
 * @author Simon Templer
 */
public class ReplaceFunctionWizardContribution extends SchemaSelectionFunctionContribution {

	private final Cell originalCell;

	/**
	 * Constructor
	 * 
	 * @param originalCell the original cell
	 */
	public ReplaceFunctionWizardContribution(Cell originalCell) {
		super(new SchemaSelectionFunctionMatcher() {

			@Override
			protected boolean checkCount(int count, Set<? extends ParameterDefinition> entities,
					boolean isTarget) {
				// ignore the count
				return true;
			}

			@Override
			protected boolean checkMandatoryConditions(Set<EntityDefinition> schemaEntities,
					Iterable<? extends ParameterDefinition> functionEntities) {
				// ignore conditions
				return true;
			}
		});

		this.originalCell = originalCell;
	}

	/**
	 * Default constructor. Uses the first cell in the current
	 * {@link IStructuredSelection}.
	 */
	public ReplaceFunctionWizardContribution() {
		this(null);
	}

	/**
	 * @see SchemaSelectionFunctionContribution#createWizardAction(FunctionWizardDescriptor,
	 *      AlignmentService)
	 */
	@Override
	protected AbstractWizardAction<?> createWizardAction(FunctionWizardDescriptor<?> descriptor,
			AlignmentService alignmentService) {
		return new ReplaceFunctionWizardAction(this, getOriginalCell(), descriptor,
				alignmentService);
	}

	/**
	 * @see SchemaSelectionFunctionContribution#getSelection()
	 */
	@Override
	public SchemaSelection getSelection() {
		// derive a schema selection from the cell

		DefaultSchemaSelection sel = new DefaultSchemaSelection();
		Cell orgCell = getOriginalCell();

		if (orgCell != null) {
			if (orgCell.getSource() != null) {
				for (Entity source : getOriginalCell().getSource().values()) {
					sel.addSourceItem(source.getDefinition());
				}
			}

			if (orgCell.getTarget() != null) {
				for (Entity target : getOriginalCell().getTarget().values()) {
					sel.addTargetItem(target.getDefinition());
				}
			}
		}

		return sel;
	}

	/**
	 * @see SchemaSelectionFunctionContribution#isActive(FunctionWizardDescriptor)
	 */
	@Override
	public boolean isActive(FunctionWizardDescriptor<?> descriptor) {
		if (descriptor.getFunctionId().equals(getOriginalCell().getTransformationIdentifier())) {
			// replacing the same function not allowed - use edit
			return false;
		}

		return super.isActive(descriptor);
	}

	/**
	 * @see AbstractFunctionWizardContribution#fill(ToolBar, int)
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		if (getOriginalCell() == null)
			return;
		super.fill(parent, index);
	}

	/**
	 * @see AbstractFunctionWizardContribution#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		if (getOriginalCell() == null)
			return;
		super.fill(menu, index);
	}

	/**
	 * Get the cell
	 * 
	 * @return the cell
	 */
	public Cell getOriginalCell() {
		if (this.originalCell != null) {
			return originalCell;
		}

		// retrieve first selected cell
		IStructuredSelection sel = SelectionTrackerUtil.getTracker().getSelection(
				IStructuredSelection.class);
		for (Object object : sel.toList()) {
			if (object instanceof Cell) {
				return (Cell) object;
			}
		}
		return null;
	}

}
