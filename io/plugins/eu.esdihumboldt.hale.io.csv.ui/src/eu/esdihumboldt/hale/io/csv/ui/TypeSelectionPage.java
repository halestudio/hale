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

package eu.esdihumboldt.hale.io.csv.ui;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConfiguration;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.instance.InstanceReaderConfigurationPage;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advanced configuration for the instance reader
 * 
 * @author Kevin Mais
 */
public class TypeSelectionPage extends InstanceReaderConfigurationPage implements CSVConstants {

	/**
	 * Parameter for the the custom date label
	 */
	public static final String CUSTOM_FORMAT_LABEL = "Custom format";
	/**
	 * Parameter for combo box for an empty selection
	 */
	public static final String EMPTY_SELECTION = "";

	private TypeDefinitionSelector sel;
	private Spinner skipNlinesSpinner;
	private Label setTypeLabel;
	private Label skipNlinesLabels;
	private ComboViewer dateFormatterCombo;
	private Text customFormat;
	public Label labelCustomFormat;
	private Label howToRepresentCustomFormatLabel;
	private Label howToUseCustomFormatLabel;
	protected Label dateFormatterLabel;

	/**
	 * default constructor
	 */
	public TypeSelectionPage() {
		super("InstanceReader");

		setTitle("Type Settings");
		setDescription("Select your Type and Data reading setting");

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not required

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not required

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));
		// XXX needed?
		GridData layoutData = new GridData();
		layoutData.widthHint = 200;

		setTypeLabel = new Label(page, SWT.NONE);
		setTypeLabel.setText("Choose your Type:");

		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
		sel = new TypeDefinitionSelector(page, "Select the corresponding schema type",
				ss.getSchemas(SchemaSpaceID.SOURCE), null);
		sel.getControl().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		sel.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(!(event.getSelection().isEmpty()));
				if (sel.getSelectedObject() != null) {
					TypeDefinition type = sel.getSelectedObject();
					CSVConfiguration conf = type.getConstraint(CSVConfiguration.class);
					int skipNlines = conf.skipNlines();
					skipNlinesSpinner.setSelection(skipNlines);
					setTypeLabel.getParent().layout();
				}
			}
		});

		skipNlinesLabels = new Label(page, SWT.NONE);
		skipNlinesLabels.setText("No. of lines to skip");
		skipNlinesSpinner = new Spinner(page, SWT.BORDER);
		skipNlinesSpinner.setMinimum(0);
		skipNlinesSpinner.setMaximum(1000000);
		skipNlinesSpinner.setIncrement(1);
		skipNlinesSpinner.setPageIncrement(10);

//		create date formatter combo
		dateFormatterLabel = new Label(page, SWT.NONE);
		dateFormatterLabel.setText("Reformatting of dates\r\n"
				+ "(date values are tried to be detected automatically)");
		dateFormatterCombo = new ComboViewer(page, SWT.READ_ONLY);
		dateFormatterCombo.setContentProvider(ArrayContentProvider.getInstance());
		List<String> list = createDatePatternsList();
		dateFormatterCombo.setInput(list);
		dateFormatterCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0 && labelCustomFormat != null && customFormat != null) {
					String currentSelection = (String) selection.getFirstElement();
					boolean isVisible = false;
					if (currentSelection.equals(CUSTOM_FORMAT_LABEL)) {
						// show the custom formatting label/text widget
						isVisible = true;
					}
					labelCustomFormat.setVisible(isVisible);
					customFormat.setVisible(isVisible);
					howToRepresentCustomFormatLabel.setVisible(isVisible);
					howToUseCustomFormatLabel.setVisible(isVisible);
				}
			}
		});
		dateFormatterCombo.setSelection(new StructuredSelection(createDatePatternsList().get(0)));

		// add label and text area for a custom formatting
		labelCustomFormat = new Label(page, SWT.NONE);
		labelCustomFormat.setText("");
		labelCustomFormat
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).create());

		customFormat = new Text(page, SWT.BORDER | SWT.SINGLE);
		customFormat.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());

		labelCustomFormat.setVisible(false);
		customFormat.setVisible(false);

		howToRepresentCustomFormatLabel = new Label(page, SWT.NONE);
		howToRepresentCustomFormatLabel.setText("How to represent");
		howToRepresentCustomFormatLabel
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).create());

		howToUseCustomFormatLabel = new Label(page, SWT.NONE);
		howToUseCustomFormatLabel.setText("dd: Day of the month\r\n" + "MM: Numerical month\r\n"
				+ "yyyy: Year in four digits\r\n" + "hh: Hour in 24-hour format\r\n"
				+ "mm: Minutes\r\n" + "ss: Seconds.");
		howToUseCustomFormatLabel
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).create());
		howToRepresentCustomFormatLabel.setVisible(false);
		howToUseCustomFormatLabel.setVisible(false);

		page.pack();

		setPageComplete(false);
	}

	/**
	 * @return list of formatting for date drop down
	 */
	protected List<String> createDatePatternsList() {
		return Arrays.asList(EMPTY_SELECTION,
				// Standard date formats
				"yyyy-MM-dd", "yy-MM-dd", "dd-MM-yyyy", "MM-dd-yyyy", "yyyy/MM/dd", "dd/MM/yyyy",
				"dd/MMM/yyyy", "MM/dd/yyyy", "yyyy.MM.dd", "dd.MM.yyyy", "MM.dd.yyyy", "yyyyMMdd",
				// Custom date format
				"MMMM d, yyyy", CUSTOM_FORMAT_LABEL);
	}

	@Override
	public boolean updateConfiguration(InstanceReader provider) {

		provider.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES,
				Value.of(skipNlinesSpinner.getSelection()));

		if (customFormat.isVisible()) {

			if (customFormat.getText().isBlank()) {
				return false;
			}

			provider.setParameter(CSVConstants.PARAMETER_DATE_FORMAT,
					Value.of(customFormat.getText()));
		}
		else {
			// Get the selection from the combo viewer
			String selectedValue = (String) ((IStructuredSelection) dateFormatterCombo
					.getSelection()).getFirstElement();

			if (selectedValue.equals("")) {
				// The empty string is selected
				provider.setParameter(CSVConstants.PARAMETER_DATE_FORMAT, null);
			}
			else {
				provider.setParameter(CSVConstants.PARAMETER_DATE_FORMAT,
						Value.of(dateFormatterCombo.getSelection()));
			}
		}

		if (sel.getSelectedObject() != null) {
			QName name = sel.getSelectedObject().getName();
			String param_name = name.toString();
			provider.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(param_name));
		}
		else {
			return false;
		}

		return true;
	}

}
