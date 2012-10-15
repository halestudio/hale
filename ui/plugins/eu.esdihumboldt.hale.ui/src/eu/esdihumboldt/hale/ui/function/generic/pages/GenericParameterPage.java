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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.function.extension.ParameterEditorExtension;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.util.Pair;

/**
 * Page for configuring function parameters.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class GenericParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage {

	private ListMultimap<String, ParameterValue> initialValues;
	private Set<FunctionParameter> params;
	private final ListMultimap<FunctionParameter, Pair<Editor<?>, Button>> inputFields;
	private final HashMap<FunctionParameter, Button> addButtons;
	private static final Image removeImage = HALEUIPlugin.getImageDescriptor("icons/remove.gif")
			.createImage();

	/**
	 * Default constructor.
	 */
	public GenericParameterPage() {
		super("parameters");

		setTitle("Function parameters");
		setDescription("Specify the parameters for the relation");

		inputFields = ArrayListMultimap.create();
		addButtons = new HashMap<FunctionParameter, Button>();

		setPageComplete(false);
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		Cell cell = getWizard().getUnfinishedCell();
		// update variables as they could have changed
		if (!AlignmentUtil.isTypeCell(cell)) {
			Set<PropertyEntityDefinition> variables = new HashSet<PropertyEntityDefinition>();
			for (Entity e : cell.getSource().values()) {
				// Cell is no type cell, so entities are Properties.
				variables.add(((Property) e).getDefinition());
			}
			for (Pair<Editor<?>, Button> pair : inputFields.values())
				pair.getFirst().setVariables(variables);
		}

		updateState();
	}

	/**
	 * Update the page state.
	 */
	private void updateState() {
		for (Map.Entry<FunctionParameter, Pair<Editor<?>, Button>> entry : inputFields.entries())
			if (!entry.getValue().getFirst().isValid()) {
				setPageComplete(false);
				return;
			}
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		this.params = params;
		if (initialValues == null)
			initialValues = ArrayListMultimap.create();
		this.initialValues = initialValues;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> conf = ArrayListMultimap.create();
		for (Map.Entry<FunctionParameter, Pair<Editor<?>, Button>> entry : inputFields.entries())
			conf.put(entry.getKey().getName(), new ParameterValue(entry.getValue().getFirst()
					.getValueType(), entry.getValue().getFirst().getAsText()));
		return conf;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().create());

		// create section for each function parameter
		for (final FunctionParameter fp : params) {
			Group group = new Group(page, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			group.setText(fp.getDisplayName());
			group.setLayout(GridLayoutFactory.swtDefaults().extendedMargins(5, 0, 0, 0)
					.numColumns(2).create());
			if (fp.getDescription() != null) {
				Label description = new Label(group, SWT.NONE);
				description.setText(fp.getDescription());
				description.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
			}

			// walk over data of initial cell while creating input fields
			List<ParameterValue> initialData = initialValues.get(fp.getName());
			Iterator<ParameterValue> initialDataIter = initialData.iterator();

			// create a minimum number of input fields
			int i;
			for (i = 0; i < fp.getMinOccurrence(); i++)
				if (initialDataIter.hasNext())
					createField(group, fp, initialDataIter.next().getValue());
				else
					createField(group, fp, ""); // important "" not null! runs
												// validator.

			// create further fields if initial cell has more
			for (; initialDataIter.hasNext()
					&& (fp.getMaxOccurrence() == AbstractParameter.UNBOUNDED || i < fp
							.getMaxOccurrence()); i++)
				createField(group, fp, initialDataIter.next().getValue());

			// create control buttons if max occurrence != min occurrence
			if (fp.getMaxOccurrence() != fp.getMinOccurrence())
				createAddButton(group, fp, fp.getMaxOccurrence() == AbstractParameter.UNBOUNDED
						|| i < fp.getMaxOccurrence());

			// enable remove buttons if initial cell added more fields than
			// required
			if (i > fp.getMinOccurrence())
				for (Pair<Editor<?>, Button> pair : inputFields.get(fp))
					pair.getSecond().setEnabled(true);
		}

		// update state now that all texts (with validators) are generated
		updateState();
	}

	/**
	 * Create add and remove buttons for the given function parameter in the
	 * given composite with the given initial visibility.
	 * 
	 * @param parent the composite
	 * @param fp the function parameter
	 * @param addEnabled whether the add button is enabled in the beginning
	 */
	private void createAddButton(final Composite parent, final FunctionParameter fp,
			boolean addEnabled) {
		// create add button -> left
		final Button addButton = new Button(parent, SWT.PUSH);
		addButtons.put(fp, addButton);
		addButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));
		addButton.setText("Add parameter value");
		addButton.setEnabled(addEnabled);

		// create selection listeners
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// add text field
				List<Pair<Editor<?>, Button>> texts = inputFields.get(fp);
				boolean removeButtonsDisabled = texts.size() == fp.getMinOccurrence();
				Pair<Editor<?>, Button> added = createField(addButton.getParent(), fp, null);
				added.getFirst().getControl().moveAbove(addButton);
				added.getSecond().moveAbove(addButton);

				// update add button
				if (texts.size() == fp.getMaxOccurrence())
					addButton.setEnabled(false);

				// need to enable all remove buttons or only the new one?
				if (removeButtonsDisabled)
					for (Pair<Editor<?>, Button> pair : texts)
						pair.getSecond().setEnabled(true);
				else
					added.getSecond().setEnabled(true);

				// do layout
				((Composite) getControl()).layout();
				// run validator to update ControlDecoration and updateState
				added.getFirst().setAsText(""); // XXX would prefer if we would
												// use a more straightforward
												// way for this
				// TODO use a default value? could be included with parameter
				// specification (e.g. default = 'true')

				// pack to make wizard larger if necessary
				pack();
			}
		});
	}

	/**
	 * Creates a text field for the given function parameter and given initial
	 * value. Does not call updateState!
	 * 
	 * @param parent the composite in which to place the text field
	 * @param fp the function parameter
	 * @param initialData initial value or null
	 * @return the created text field
	 */
	private Pair<Editor<?>, Button> createField(Composite parent, final FunctionParameter fp,
			String initialData) {
		// create editor, button and pair

		// use attribute editors - FIXME currently disregarding the internal
		// editor validation
		final Editor<?> editor = ParameterEditorExtension.getInstance().createEditor(parent,
				getWizard().getFunctionId(), fp);
		editor.getControl().addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				((Composite) getControl()).layout();
				pack();
			}

			@Override
			public void controlMoved(ControlEvent e) {
				// ignore
			}
		});
		final Button removeButton = new Button(parent, SWT.NONE);
		final Pair<Editor<?>, Button> pair = new Pair<Editor<?>, Button>(editor, removeButton);

		// configure text
		editor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// add validator
		if (fp.getValidator() != null) {
			final ControlDecoration decorator = new ControlDecoration(editor.getControl(), SWT.LEFT
					| SWT.TOP);

			// set initial status
			decorator.hide();

			// set image
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
			decorator.setImage(fieldDecoration.getImage());

			// add modify listener
			editor.setPropertyChangeListener(new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(Editor.VALUE)) {
						// update decorator and state
						String result = fp.getValidator().validate(editor.getAsText());
						if (result == null)
							decorator.hide();
						else {
							decorator.setDescriptionText(result);
							decorator.show();
						}
						updateState();
					}
				}
			});
		}

		// set initial text
		if (initialData != null)
			editor.setAsText(initialData);

		// configure button
		removeButton.setImage(removeImage);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// remove last text field
				List<Pair<Editor<?>, Button>> texts = inputFields.get(fp);
				texts.remove(pair);
				updateState();
				removeButton.dispose();
				editor.getControl().dispose();
				addButtons.get(fp).setEnabled(true);
				if (texts.size() == fp.getMinOccurrence())
					for (Pair<Editor<?>, Button> otherPair : texts)
						otherPair.getSecond().setEnabled(false);

				// do layout
				((Composite) getControl()).layout();
				// pack to make wizard smaller if possible
				pack();
			}
		});

		// add field to map
		inputFields.put(fp, pair);

		return pair;
	}

	/**
	 * Packs the wizards shell, only updating the height, not the width.
	 */
	private void pack() {
		Shell shell = getWizard().getShell();
		Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		shell.setSize(shell.getSize().x, preferredSize.y);
	}
}
