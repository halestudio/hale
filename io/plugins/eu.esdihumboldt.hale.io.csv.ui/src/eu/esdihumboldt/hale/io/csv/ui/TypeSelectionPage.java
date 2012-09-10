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

package eu.esdihumboldt.hale.io.csv.ui;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConfiguration;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.instance.InstanceReaderConfigurationPage;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advanced configuration for the instance reader
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class TypeSelectionPage extends InstanceReaderConfigurationPage implements CSVConstants {

	private TypeDefinitionSelector sel;
	private Button button;
	private Label label;

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
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData();
		layoutData.widthHint = 200;

		label = new Label(page, SWT.NONE);
		label.setText("Choose your Type:");

		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
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
					Boolean skip = conf.skipFirst();
					button.setSelection(skip);
				}
			}
		});

		button = new Button(page, SWT.CHECK);
		button.setText("Skip first line");
		button.setSelection(true);

		page.pack();

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {

		provider.setParameter(CSVInstanceReader.PARAM_SKIP_FIRST_LINE,
				String.valueOf(button.getSelection()));
		if (sel.getSelectedObject() != null) {
			QName name = sel.getSelectedObject().getName();
			String param_name = name.toString();
			provider.setParameter(CSVConstants.PARAM_TYPENAME, param_name);
		}
		else {
			return false;
		}

		return true;
	}

}
