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

package eu.esdihumboldt.hale.ui.function.contribution;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
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
		super();

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
	 * @see SchemaSelectionFunctionContribution#checkCount(int, Set, boolean)
	 */
	@Override
	protected boolean checkCount(int count, Set<? extends AbstractParameter> entities,
			boolean isTarget) {
		// ignore the count
		return true;
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
