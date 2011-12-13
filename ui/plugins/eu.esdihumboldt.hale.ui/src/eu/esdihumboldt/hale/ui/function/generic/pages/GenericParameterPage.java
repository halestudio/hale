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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.util.Pair;

/**
 * Page for configuring function parameters.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class GenericParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {
	private ListMultimap<String, String> initialValues;
	private Set<FunctionParameter> params;
	private ListMultimap<FunctionParameter, Pair<Text, Button>> inputFields;
	private HashMap<FunctionParameter, Button> addButtons;
	private static final Image removeImage = HALEUIPlugin.getImageDescriptor("icons/remove.gif").createImage();

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
		updateState();
	}

	/**
	 * Update the page state.
	 */
	private void updateState() {
		for (Map.Entry<FunctionParameter, Pair<Text, Button>> entry : inputFields.entries())
			if (entry.getKey().getValidator() != null
					&& entry.getKey().getValidator().validate(entry.getValue().getFirst().getText()) != null) {
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
	public void setParameter(Set<FunctionParameter> params, ListMultimap<String, String> initialValues) {
		this.params = params;
		if (initialValues == null)
			initialValues = ArrayListMultimap.create();
		this.initialValues = initialValues;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, String> getConfiguration() {
		ListMultimap<String, String> conf = ArrayListMultimap.create();
		for (Map.Entry<FunctionParameter, Pair<Text, Button>> entry : inputFields.entries())
			conf.put(entry.getKey().getName(), entry.getValue().getFirst().getText());
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
			group.setLayout(GridLayoutFactory.swtDefaults().extendedMargins(5, 0, 0, 0).numColumns(2).create());
			if (fp.getDescription() != null) {
				Label description = new Label(group, SWT.NONE);
				description.setText(fp.getDescription());
			}

			// walk over data of initial cell while creating input fields
			List<String> initialData = initialValues.get(fp.getName());
			Iterator<String> initialDataIter = initialData.iterator();

			// create a minimum number of input fields
			int i;
			for (i = 0; i < fp.getMinOccurrence(); i++)
				if (initialDataIter.hasNext())
					createField(group, fp, initialDataIter.next());
				else
					createField(group, fp, ""); // important "" not null! runs validator.

			// create further fields if initial cell has more
			for (; initialDataIter.hasNext()
					&& (fp.getMaxOccurrence() == AbstractParameter.UNBOUNDED || i < fp.getMaxOccurrence()); i++)
				createField(group, fp, initialDataIter.next());

			// create control buttons if max occurrence != min occurrence
			if (fp.getMaxOccurrence() != fp.getMinOccurrence())
				createAddButton(group, fp, i < fp.getMaxOccurrence());

			// enable remove buttons if initial cell added more fields than required
			if (i > fp.getMinOccurrence())
				for (Pair<Text, Button> pair : inputFields.get(fp))
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
	private void createAddButton(final Composite parent, final FunctionParameter fp, boolean addEnabled) {
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
				List<Pair<Text, Button>> texts = inputFields.get(fp);
				boolean removeButtonsDisabled = texts.size() == fp.getMinOccurrence();
				Pair<Text, Button> added = createField(addButton.getParent(), fp, null);
				added.getFirst().moveAbove(addButton);
				added.getSecond().moveAbove(addButton);
				
				// update add button
				if (texts.size() == fp.getMaxOccurrence())
					addButton.setEnabled(false);

				// need to enable all remove buttons or only the new one?
				if (removeButtonsDisabled)
					for (Pair<Text, Button> pair : texts)
						pair.getSecond().setEnabled(true);
				else
					added.getSecond().setEnabled(true);

				// do layout
				((Composite) getControl()).layout();
				// run validator to update ControlDecoration and updateState
				added.getFirst().setText("");
				// pack to make wizard larger if necessary
				getWizard().getShell().pack();
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
	private Pair<Text, Button> createField(Composite parent, final FunctionParameter fp, String initialData) {
		// create text, button and pair
		final Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		final Button removeButton = new Button(parent, SWT.NONE);
		final Pair<Text, Button> pair = new Pair<Text, Button>(text, removeButton);

		// configure text
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// add validator 
		if (fp.getValidator() != null) {
			final ControlDecoration decorator = new ControlDecoration(text, SWT.LEFT | SWT.TOP);

			// set initial status
			decorator.hide();

			// set image
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
					FieldDecorationRegistry.DEC_ERROR);
			decorator.setImage(fieldDecoration.getImage());

			// add modify listener
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					// update decorator and state
					String result = fp.getValidator().validate(text.getText());
					if (result == null)
						decorator.hide();
					else {
						decorator.setDescriptionText(result);
						decorator.show();
					}
					updateState();
				}
			});
		}

		// set initial text
		if (initialData != null)
			text.setText(initialData);

		// configure button
		removeButton.setImage(removeImage);
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// remove last text field
				List<Pair<Text, Button>> texts = inputFields.get(fp);
				texts.remove(pair);
				updateState();
				removeButton.dispose();
				text.dispose();
				addButtons.get(fp).setEnabled(true);
				if (texts.size() == fp.getMinOccurrence())
					for (Pair<Text, Button> otherPair : texts)
						otherPair.getSecond().setEnabled(false);

				// do layout
				((Composite) getControl()).layout();
				// pack to make wizard smaller if possible
				getWizard().getShell().pack();
			}
		});

		// add field to map
		inputFields.put(fp, pair);

		return pair;
	}
}
