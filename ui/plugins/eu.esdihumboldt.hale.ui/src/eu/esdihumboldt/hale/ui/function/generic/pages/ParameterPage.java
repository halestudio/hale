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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;

/**
 * Page for configuring function parameters.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class ParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements FunctionWizardPage {
	private final Cell initialCell;
	private final ListMultimap<FunctionParameter, Text> inputFields;

	/**
	 * Default constructor
	 * 
	 * @param initialCell the initial cell, may be <code>null</code>
	 */
	public ParameterPage(Cell initialCell) {
		super("parameters");

		setTitle("Function parameters");
		setDescription("Specify the parameters for the relation");

		this.initialCell = initialCell;
		inputFields = ArrayListMultimap.create();

		setPageComplete(false);
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		updateState();
	}

	/**
	 * Update the page state
	 */
	private void updateState() {
		// XXX how to validate fields? -> bindings!?
		// for testing: check whether all fields aren't empty.
		for (Map.Entry<FunctionParameter, Text> entry : inputFields.entries())
			if (entry.getValue().getText().isEmpty()) {
				setPageComplete(false);
				return;
			}
		setPageComplete(true);
	}

	/**
	 * @see FunctionWizardPage#configureCell(MutableCell)
	 */
	@Override
	public void configureCell(MutableCell cell) {
		ListMultimap<String, String> map = cell.getTransformationParameters();
		if (map == null) {
			map = ArrayListMultimap.create();
			cell.setTransformationParameters(map);
		}
		// XXX What if two functions use the same parameter name?
		// remove all parameters that get set by this wizards function
		for (FunctionParameter fp : getWizard().getFunction().getDefinedParameters())
			map.removeAll(fp.getName());
		// and add those now
		for (Map.Entry<FunctionParameter, Text> entry : inputFields.entries())
			map.put(entry.getKey().getName(), entry.getValue().getText());
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().create());

		// create section for each function parameter
		for (final FunctionParameter fp : getWizard().getFunction().getDefinedParameters()) {
			Group group = new Group(page, SWT.NONE);
			group.setText(fp.getDisplayName());
			group.setLayout(GridLayoutFactory.swtDefaults().create());
			if (fp.getDescription() != null) {
				Label description = new Label(group, SWT.NONE);
				description.setText(fp.getDescription());
			}

			// walk over data of initial cell while creating input fields
			List<String> initialData = new ArrayList<String>(0);
			if (initialCell != null && initialCell.getTransformationParameters() != null)
				initialData = initialCell.getTransformationParameters().get(fp.getName());
			Iterator<String> initialDataIter = initialData.iterator();

			// create a minimum number of input fields
			int i;
			for (i = 0; i < fp.getMinOccurrence(); i++)
				if (initialDataIter.hasNext())
					createField(group, fp, initialDataIter.next());
				else
					createField(group, fp, null);

			// create further fields if initial cell has more
			// XXX check for max occurrence necessary? Is initialCell always correct?
			for (; initialDataIter.hasNext()
					&& (fp.getMaxOccurrence() == AbstractParameter.UNBOUNDED || i < fp.getMaxOccurrence()); i++)
				createField(group, fp, initialDataIter.next());

			// create control buttons if max occurrence != min occurrence
			if (fp.getMaxOccurrence() != fp.getMinOccurrence()) {
				final Composite buttons = new Composite(group, SWT.NONE);
				buttons.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
				buttons.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
				createButtons(buttons, fp, i < fp.getMaxOccurrence()
						|| fp.getMaxOccurrence() == AbstractParameter.UNBOUNDED, i > fp.getMinOccurrence());
			}

			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		}
	}

	/**
	 * Create add and remove buttons for the given function parameter in the
	 * given composite with the given initial visibility.
	 * 
	 * @param parent the composite
	 * @param fp the function parameter
	 * @param addVisible whether the add button is visible in the beginning
	 * @param removeVisible whether the remove button is visible in the
	 *            beginning
	 */
	private void createButtons(final Composite parent, final FunctionParameter fp, boolean addVisible,
			boolean removeVisible) {
		// create add button -> left
		final Button addButton = new Button(parent, SWT.PUSH);
		addButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
		addButton.setText("Add parameter value");
		addButton.setVisible(addVisible);

		// create remove button -> right
		final Button removeButton = new Button(parent, SWT.PUSH);
		removeButton.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));
		removeButton.setText("Remove parameter value");
		removeButton.setVisible(removeVisible);

		// create selection listeners
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// remove last text field
				List<Text> texts = inputFields.get(fp);
				Text removed = texts.remove(texts.size() - 1);
				updateState();
				removed.dispose();
				addButton.setVisible(true);
				if (texts.size() == fp.getMinOccurrence())
					removeButton.setVisible(false);
				((Composite) getControl()).layout();
				getWizard().getShell().pack();
			}
		});

		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// add text field
				List<Text> texts = inputFields.get(fp);
				Text added = createField(parent.getParent(), fp, null);
				updateState();
				added.moveAbove(parent);
				if (texts.size() == fp.getMaxOccurrence())
					addButton.setVisible(false);
				removeButton.setVisible(true);
				((Composite) getControl()).layout();
				getWizard().getShell().pack();
			}
		});
	}

	/**
	 * Creates a text field for the given function parameter and given initial
	 * value.
	 * 
	 * @param parent the composite in which to place the text field
	 * @param fp the function parameter
	 * @param initialData initial value or null
	 * @return the created text field
	 */
	private Text createField(Composite parent, FunctionParameter fp, String initialData) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		if (initialData != null)
			text.setText(initialData);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateState();
			}
		});
		inputFields.put(fp, text);
		return text;
	}

	/**
	 * @return the initialCell
	 */
	public Cell getInitialCell() {
		return initialCell;
	}
}
