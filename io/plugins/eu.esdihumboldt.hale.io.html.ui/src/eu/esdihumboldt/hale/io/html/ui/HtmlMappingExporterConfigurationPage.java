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

package eu.esdihumboldt.hale.io.html.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.html.HtmlMappingExporter;
import eu.esdihumboldt.hale.io.html.HtmlMappingTemplateConstants;
import eu.esdihumboldt.hale.ui.io.align.AlignmentWriterConfigurationPage;

/**
 * Configuration page for the {@link HtmlMappingExporter}
 * 
 * @author Patrick Lieb
 */
public class HtmlMappingExporterConfigurationPage extends AlignmentWriterConfigurationPage
		implements HtmlMappingTemplateConstants {

	private Button tooltip;

	/**
	 * Default constructor
	 */
	public HtmlMappingExporterConfigurationPage() {
		super("sel.htmlMappingConfiguration");
		setTitle("HTML Mapping Configuration");
		setDescription("Please select optional configuration.");
	}

	@Override
	public boolean updateConfiguration(AlignmentWriter provider) {
		provider.setParameter(TOOLTIP, Value.of(tooltip.getSelection()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, true));
		tooltip = new Button(page, SWT.CHECK);
		tooltip.setText("Add tooltip to show complete name of source and target");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not needed here

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not needed here

	}
}
