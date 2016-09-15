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

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
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
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Creates a control for selection of source and target type entity definitions.
 * The selection can happen one by one, but also by selecting a type cell.<br>
 * Listeners can be added individually to the source and target selectors or to
 * this selector, which provides (incomplete) cells.
 * 
 * @author Kai Schwierczek
 */
public class SourceTargetTypeSelector implements ISelectionProvider {

	// TODO This selector for now only supports one source type!

	private final Composite main;
	private final TypeEntitySelector targetTypeSelector;
	private final TypeEntitySelector sourceTypeSelector;
	private final Button selectCellButton;
	private Cell selectedCell;
	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	private final ListenerList selectionChangedListeners = new ListenerList();
	boolean inUpdate = false;

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
		main.setLayout(new GridLayout(4, false));

		ISelectionChangedListener listener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!inUpdate)
					fireSelectionChanged();
			}
		};

		GridDataFactory selectorgd = GridDataFactory.fillDefaults().grab(true, false)
				.hint(200, SWT.DEFAULT);

		sourceTypeSelector = new TypeEntitySelector(SchemaSpaceID.SOURCE, null, main, false);
		selectorgd.applyTo(sourceTypeSelector.getControl());
		sourceTypeSelector.addSelectionChangedListener(listener);

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
					inUpdate = true;
					setSelection(selected);
					inUpdate = false;
					fireSelectionChanged();
				}
			}
		});

		Button resetSelectionButton = new Button(main, SWT.PUSH);
		resetSelectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelection(StructuredSelection.EMPTY);
			}
		});
		final Image resetSelectionImage = HALEUIPlugin.getImageDescriptor("icons/remove.gif")
				.createImage();
		resetSelectionButton.setImage(resetSelectionImage);
		resetSelectionButton.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				resetSelectionImage.dispose();
			}
		});
		resetSelectionButton.setToolTipText("Reset selection");

		targetTypeSelector = new TypeEntitySelector(SchemaSpaceID.TARGET, null, main, false);
		selectorgd.applyTo(targetTypeSelector.getControl());
		targetTypeSelector.addSelectionChangedListener(listener);
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
	 * Sets the selected type entity definitions.<br>
	 * If an existing cell was selected, that selection is undone.
	 * 
	 * @param type the type to select, may be <code>null</code>
	 * @param ssid the schema space to set the type to (needed because type may
	 *            be null)
	 */
	public void setSelection(TypeEntityDefinition type, SchemaSpaceID ssid) {
		inUpdate = true;
		// undo cell selection
		setSelection((Cell) null);

		ISelection selection = (type == null) ? StructuredSelection.EMPTY
				: new StructuredSelection(type);
		if (ssid == SchemaSpaceID.SOURCE)
			sourceTypeSelector.setSelection(selection);
		else if (ssid == SchemaSpaceID.TARGET)
			targetTypeSelector.setSelection(selection);
		inUpdate = false;
		fireSelectionChanged();
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
	private void setSelection(Cell cell) {
		if (cell != null) {
			// in case of a real join cell there are multiple source types
			if (cell.getSource() == null || cell.getSource().isEmpty())
				sourceTypeSelector.setSelection(StructuredSelection.EMPTY);
			else if (cell.getSource().size() > 1)
				sourceTypeSelector.showText("<multiple types>");
			else {
				Entity source = CellUtil.getFirstEntity(cell.getSource());
				ISelection selection = new StructuredSelection(AlignmentUtil.getTypeEntity(source
						.getDefinition()));
				sourceTypeSelector.setSelection(selection);
			}
			// target can only be one or none
			Entity target = CellUtil.getFirstEntity(cell.getTarget());
			ISelection selection = (target == null) ? StructuredSelection.EMPTY
					: new StructuredSelection(AlignmentUtil.getTypeEntity(target.getDefinition()));
			targetTypeSelector.setSelection(selection);
		}

		if (cell != null && AlignmentUtil.isTypeCell(cell) && cell.getId() != null) {
			// a real type cell
			selectedCell = cell;

			String label;
			String functionId = cell.getTransformationIdentifier();
			FunctionDefinition<?> function = FunctionUtil.getFunction(functionId,
					HaleUI.getServiceProvider());
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

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return new StructuredSelection(getSelectedCell());
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		inUpdate = true;
		if (selection.isEmpty()) {
			setSelection((Cell) null);
			sourceTypeSelector.setSelection(selection);
			targetTypeSelector.setSelection(selection);
		}
		else if (selection instanceof IStructuredSelection)
			setSelection((Cell) ((IStructuredSelection) selection).getFirstElement());
		inUpdate = false;
		fireSelectionChanged();
	}

	/**
	 * Fire a selection changed event to all listeners.
	 */
	private void fireSelectionChanged() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, new StructuredSelection(
				getSelectedCell()));
		for (Object listener : selectionChangedListeners.getListeners())
			((ISelectionChangedListener) listener).selectionChanged(event);
	}
}
