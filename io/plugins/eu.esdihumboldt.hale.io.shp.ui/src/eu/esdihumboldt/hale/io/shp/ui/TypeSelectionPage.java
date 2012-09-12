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

package eu.esdihumboldt.hale.io.shp.ui;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceReaderConfigurationPage;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Configuration page for selecting the schema type for Shapefile instances.
 * 
 * @author Simon Templer
 */
public class TypeSelectionPage extends InstanceReaderConfigurationPage implements
		ShapefileConstants {

	private TypeDefinitionSelector selector;

	/**
	 * default constructor
	 */
	public TypeSelectionPage() {
		super("selectType");

		setTitle("Schema type");
		setDescription("Select the schema type matching your data.");
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// nothing
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData();
		layoutData.widthHint = 200;

		Label label = new Label(page, SWT.NONE);
		label.setText("Schema type:");

		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		// TODO provide any filter?
		selector = new TypeDefinitionSelector(page, "Select the corresponding schema type",
				ss.getSchemas(SchemaSpaceID.SOURCE), null);
		selector.getControl().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		selector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(!(event.getSelection().isEmpty()));
			}
		});

		page.pack();

		setPageComplete(false);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {
		if (selector.getSelectedObject() != null) {
			QName name = selector.getSelectedObject().getName();
			provider.setParameter(PARAM_TYPENAME, name.toString());
		}
		else {
			return false;
		}

		return true;
	}

}
