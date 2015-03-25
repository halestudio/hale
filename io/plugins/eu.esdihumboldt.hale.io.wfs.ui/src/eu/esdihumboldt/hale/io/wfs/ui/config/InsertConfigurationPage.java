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

package eu.esdihumboldt.hale.io.wfs.ui.config;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.wfs.SimpleWFSWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for WFS-T request partitioning.
 * 
 * @author Simon Templer
 */
public class InsertConfigurationPage extends
		AbstractConfigurationPage<ExportProvider, IOWizard<ExportProvider>> {

	private Combo format;
	private Combo idgen;

	/**
	 * Default constructor.
	 */
	public InsertConfigurationPage() {
		super("wfsInsert");

		setTitle("WFS Insert");
		setDescription("Configuration settings for WFS Insert transaction");
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

	@Override
	public boolean updateConfiguration(ExportProvider provider) {
		String formatText = format.getText();
		if (formatText != null && formatText.isEmpty()) {
			formatText = null;
		}
		provider.setParameter(SimpleWFSWriter.PARAM_INPUT_FORMAT, Value.of(formatText));

		String idgenText = idgen.getText();
		if (idgenText != null && idgenText.isEmpty()) {
			idgenText = null;
		}
		provider.setParameter(SimpleWFSWriter.PARAM_ID_GEN, Value.of(idgenText));

		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group formatGroup = new Group(page, SWT.NONE);
		formatGroup.setLayout(new GridLayout(1, false));
		formatGroup.setText("Data format");
		groupData.applyTo(formatGroup);

		format = new Combo(formatGroup, SWT.BORDER);
		groupData.applyTo(format);
		format.setItems(new String[] { "text/xml; subtype=gml/3.2.1", "text/xml; subtype=gml/3.1.1" });
		// TODO try to determine default from target schema?
		format.setText(SimpleWFSWriter.DEFAULT_INPUT_FORMAT);

		Group idGroup = new Group(page, SWT.NONE);
		idGroup.setLayout(new GridLayout(1, false));
		idGroup.setText("ID generation (WFS 1.1 only)");
		groupData.applyTo(idGroup);

		idgen = new Combo(idGroup, SWT.READ_ONLY);
		idgen.setItems(new String[] { "UseExisting", "GenerateNew", "ReplaceDuplicate" });

		setPageComplete(true);
	}

}
