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

		label = new Label(page, SWT.NONE);
		label.setText("Choose your Type:");

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
					Boolean skip = conf.skipFirst();
					button.setSelection(skip);
					label.getParent().layout();
				}
			}
		});

		button = new Button(page, SWT.CHECK);
		button.setText("Skip first line");
		button.setSelection(true);

		page.pack();

		setPageComplete(false);
	}

	@Override
	public boolean updateConfiguration(InstanceReader provider) {

		provider.setParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE,
				Value.of(button.getSelection()));
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
