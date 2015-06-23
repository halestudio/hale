/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xsd.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for basic {@link XmlSchemaReader} settings.
 * 
 * @author Simon Templer
 */
public class XsdReaderSettingsPage extends
		AbstractConfigurationPage<XmlSchemaReader, IOWizard<XmlSchemaReader>> {

	private Button onlyElementsMappable;

	/**
	 * Default constructor
	 */
	public XsdReaderSettingsPage() {
		super("gml.basicSettings");

		setTitle("XML/GML settings");
		setDescription("Basic XML and GML reader settings");
	}

	@Override
	public boolean updateConfiguration(XmlSchemaReader provider) {
		provider.setParameter(XmlSchemaReader.PARAM_ONLY_ELEMENTS_MAPPABLE,
				Value.of(onlyElementsMappable.getSelection()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group types = new Group(page, SWT.NONE);
		types.setLayout(new GridLayout(1, false));
		types.setText("Mappable types");
		groupData.applyTo(types);

		onlyElementsMappable = new Button(types, SWT.CHECK);
		onlyElementsMappable.setText("Only types with associated global elements may be mapped");
		// default
		onlyElementsMappable.setSelection(true);

		setPageComplete(true);
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing
	}

}
