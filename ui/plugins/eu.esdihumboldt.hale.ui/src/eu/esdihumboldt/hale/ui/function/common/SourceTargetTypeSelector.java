/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;

/**
 * Creates a control for selection of source and target type entity definitions.
 * The selection can happen one by one, but also by selecting a type cell.
 * 
 * @author Kai Schwierczek
 */
public class SourceTargetTypeSelector {

	// TODO This selector for now only supports one source type!

	private final Composite main;
	private final TypeEntitySelector targetTypeSelector;
	private final TypeEntitySelector sourceTypeSelector;
	private final Button selectCellButton;
	private Cell selectedCell;
	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	/**
	 * Creates a new selector.
	 * 
	 * @param parent the parent composite
	 */
	public SourceTargetTypeSelector(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				functionLabels.dispose();
			}
		});
		main.setLayout(new GridLayout(3, false));

		sourceTypeSelector = new TypeEntitySelector(SchemaSpaceID.SOURCE, null, main, false);
		sourceTypeSelector.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		selectCellButton = new Button(main, SWT.PUSH);
		selectCellButton.setText("Select cell");
		selectCellButton.setToolTipText("Select a type cell");
		selectCellButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TypeCellSelectionDialog dialog = new TypeCellSelectionDialog(main.getShell()
						.getShell(), "Select a type cell", selectedCell);
				if (dialog.open() == TypeCellSelectionDialog.OK) {
					Cell selected = dialog.getObject();
					setSelection(selected);
				}
			}
		});

		targetTypeSelector = new TypeEntitySelector(SchemaSpaceID.TARGET, null, main, false);
		targetTypeSelector.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	/**
	 * Adds a listener for selection changes in the specified selection. Has no
	 * effect if an identical listener is already registered.
	 * 
	 * @param listener a selection changed listener
	 * @param ssid the selection to add the listener to
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener, SchemaSpaceID ssid) {
		if (ssid == SchemaSpaceID.SOURCE)
			sourceTypeSelector.addSelectionChangedListener(listener);
		else
			targetTypeSelector.addSelectionChangedListener(listener);
	}

	/**
	 * Removes the given selection change listener from the specified selection
	 * provider. Has no effect if an identical listener is not registered.
	 * 
	 * @param listener a selection changed listener
	 * @param ssid the selection to remove the listener from
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener,
			SchemaSpaceID ssid) {
		if (ssid == SchemaSpaceID.SOURCE)
			sourceTypeSelector.removeSelectionChangedListener(listener);
		else
			targetTypeSelector.removeSelectionChangedListener(listener);
	}

	/**
	 * Returns the selected type entity definition of the given schema space.
	 * 
	 * @param ssid the schema space in question
	 * @return the selected type entity definition, may be <code>null</code>
	 */
	public TypeEntityDefinition getSelection(SchemaSpaceID ssid) {
		if (ssid == SchemaSpaceID.SOURCE)
			return (TypeEntityDefinition) sourceTypeSelector.getSelectedObject();
		else
			return (TypeEntityDefinition) targetTypeSelector.getSelectedObject();
	}

	/**
	 * Returns the selected cell. If no cell is selected a dummy cell with the
	 * selected source and target is returned. Both source and target may or may
	 * not be empty in that case.
	 * 
	 * @return the selected cell (or a dummy)
	 */
	public Cell getSelectedCell() {
		if (selectedCell != null)
			return selectedCell;

		DefaultCell cell = new DefaultCell();
		if (sourceTypeSelector.getSelectedObject() != null) {
			ListMultimap<String, Type> sources = ArrayListMultimap.create(1, 1);
			sources.put(null,
					new DefaultType((TypeEntityDefinition) sourceTypeSelector.getSelectedObject()));
			cell.setSource(sources);
		}
		if (targetTypeSelector.getSelectedObject() != null) {
			ListMultimap<String, Type> targets = ArrayListMultimap.create(1, 1);
			targets.put(null,
					new DefaultType((TypeEntityDefinition) targetTypeSelector.getSelectedObject()));
			cell.setTarget(targets);
		}
		return cell;
	}

	/**
	 * Returns whether an existing cell is selected.
	 * 
	 * @return whether an existing cell is selected
	 */
	public boolean isCellSelected() {
		return selectedCell != null;
	}

	/**
	 * Sets the selected type entity definitions.
	 * 
	 * @param type the type to select, may be <code>null</code>
	 * @param ssid the schema space to set the type to (needed because type may
	 *            be null)
	 */
	private void internalSetSelection(TypeEntityDefinition type, SchemaSpaceID ssid) {
		IStructuredSelection selection;
		if (type == null)
			selection = StructuredSelection.EMPTY;
		else
			selection = new StructuredSelection(type);
		if (ssid == SchemaSpaceID.SOURCE)
			sourceTypeSelector.setSelection(selection);
		else
			targetTypeSelector.setSelection(selection);
	}

	/**
	 * Sets the selected type entity definitions.<br>
	 * If an existing cell was selected, that selection is undone.
	 * 
	 * @param type the type to select, may be <code>null</code>
	 * @param ssid the schema space to set the type to (needed because type may
	 *            be null)
	 */
	public void setSelection(TypeEntityDefinition type, SchemaSpaceID ssid) {
		// undo cell selection
		setSelection(null);
		// actually set selection
		internalSetSelection(type, ssid);
	}

	/**
	 * Sets the selection according to the given cell.<br>
	 * If the cell is a property cell, it will select the types whom the
	 * properties belong to.<br>
	 * If <code>cell</code> is <code>null</code> the selected types aren't
	 * changed, but is an existing type cell was selected, that selection is
	 * undone.
	 * 
	 * @param cell the cell to set the selection to
	 */
	public void setSelection(Cell cell) {
		if (cell != null) {
			Entity source = CellUtil.getFirstEntity(cell.getSource());
			TypeEntityDefinition sourceDef = source == null ? null : AlignmentUtil
					.getTypeEntity(source.getDefinition());
			internalSetSelection(sourceDef, SchemaSpaceID.SOURCE);
			Entity target = CellUtil.getFirstEntity(cell.getTarget());
			TypeEntityDefinition targetDef = target == null ? null : AlignmentUtil
					.getTypeEntity(target.getDefinition());
			internalSetSelection(targetDef, SchemaSpaceID.TARGET);
		}

		if (cell != null && AlignmentUtil.isTypeCell(cell) && cell.getId() != null) {
			// a real type cell
			selectedCell = cell;

			String label;
			String functionId = cell.getTransformationIdentifier();
			AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
			if (function != null)
				label = functionLabels.getText(function);
			else
				label = functionId;
			selectCellButton.setText(label);

			sourceTypeSelector.getControl().setEnabled(false);
			targetTypeSelector.getControl().setEnabled(false);
		}
		else {
			selectedCell = null;

			selectCellButton.setText("Select cell");

			sourceTypeSelector.getControl().setEnabled(true);
			targetTypeSelector.getControl().setEnabled(true);
		}
	}

	/**
	 * Returns the main selector control.
	 * 
	 * @return the main selector control
	 */
	public Control getControl() {
		return main;
	}
}
