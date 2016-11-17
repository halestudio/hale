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

package eu.esdihumboldt.hale.io.gml.ui;

import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for basic {@link StreamGmlWriter} settings.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class GmlWriterSettingsPage
		extends AbstractConfigurationPage<StreamGmlWriter, IOWizard<StreamGmlWriter>> {

	private Button prettyPrint;
	private Button simplify;
	private Button nilReason;
	private Text formattedText;

	/**
	 * Default constructor
	 */
	public GmlWriterSettingsPage() {
		super("gml.basicSettings");

		setTitle("XML/GML settings");
		setDescription("Basic XML and GML encoding settings");
	}

	@Override
	public boolean updateConfiguration(StreamGmlWriter provider) {
		if (!validate()) {
			setErrorMessage("Format is not valid");
			return false;
		}
		setErrorMessage("");
		setMessage("Basic XML and GML encoding settings");
		provider.setPrettyPrint(prettyPrint.getSelection());
		provider.setParameter(StreamGmlWriter.PARAM_SIMPLIFY_GEOMETRY,
				Value.of(simplify.getSelection()));
		provider.setParameter(StreamGmlWriter.PARAM_OMIT_NIL_REASON,
				Value.of(nilReason.getSelection()));
		provider.setGeometryWriteFormat(formattedText.getText());
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group xml = new Group(page, SWT.NONE);
		xml.setLayout(new GridLayout(1, false));
		xml.setText("XML");
		groupData.applyTo(xml);

		prettyPrint = new Button(xml, SWT.CHECK);
		prettyPrint.setText("Pretty print XML");
		// default
		prettyPrint.setSelection(false);

		Group geom = new Group(page, SWT.NONE);
		geom.setLayout(new GridLayout(1, false));
		geom.setText("Simplify geometries");
		groupData.applyTo(geom);

		simplify = new Button(geom, SWT.CHECK);
		simplify.setText("Use single geometries for geometry collections with only one element");
		// default
		simplify.setSelection(true);
		Label desc = new Label(geom, SWT.NONE);
		desc.setText(
				"(for example for a MultiPolygon with only one Polygon use only the contained Polygon)");

		Group nil = new Group(page, SWT.NONE);
		nil.setLayout(new GridLayout(1, false));
		nil.setText("nilReason");
		groupData.applyTo(nil);

		nilReason = new Button(nil, SWT.CHECK);
		nilReason.setText("Omit nilReason attributes for elements that are not nil");
		// default
		nilReason.setSelection(true);

		Group writeFormat = new Group(page, SWT.NONE);
		writeFormat.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		writeFormat.setText("Formatted number output for geometry");
		groupData.applyTo(writeFormat);

		Label lbl = new Label(writeFormat, SWT.NONE);
		lbl.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		lbl.setText("Format: ");

		formattedText = new Text(writeFormat, SWT.SINGLE | SWT.BORDER);
		formattedText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());
		// filler
		Label lblfiller = new Label(writeFormat, SWT.NONE);
		lblfiller.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		Label formatDesc = new Label(writeFormat, SWT.NONE);
		formatDesc.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());
		formatDesc.setText("(for example 00000.000)");

		// filler
		new Label(page, SWT.NONE);

		setPageComplete(true);
	}

	private boolean validate() {
		String txt = formattedText.getText();
		if (txt == null || txt.equals("")) {
			return true;
		}
		else {
			String regEx = "0{1,13}(\\.0*)?";
			return Pattern.matches(regEx, txt);
		}
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
