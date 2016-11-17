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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

	private static final String DEFAULT_FORMAT = "0.000";

	private Button prettyPrint;
	private Button simplify;
	private Button nilReason;
	private Button checkNumberFormat;
	private Text formattedText;

	/**
	 * Default constructor
	 */
	public GmlWriterSettingsPage() {
		super("gml.basicSettings");

		setTitle("XML/GML settings");
		setDescription("Basic XML and GML encoding settings");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		if (firstShow) {
			checkNumberFormat.setSelection(false);
			update();
		}
	}

	@Override
	public boolean updateConfiguration(StreamGmlWriter provider) {
		if (checkNumberFormat.getSelection() && !validate()) {
			setErrorMessage("Number format is not valid!");
			return false;
		}
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
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(writeFormat);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(writeFormat);
		writeFormat.setText("Formatted Number");

		checkNumberFormat = new Button(writeFormat, SWT.CHECK);
		checkNumberFormat.setText("Use a formatted number output for geometry coordinates");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(checkNumberFormat);
		checkNumberFormat.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});

		Label lbl = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(lbl);
		lbl.setText("Format: ");

		formattedText = new Text(writeFormat, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(formattedText);
		// filler
		Label lblfiller = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(lblfiller);

		Label formatDesc = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(formatDesc);
		formatDesc.setText("(e.g. 00000.000)");

		// filler
		new Label(page, SWT.NONE);

		setPageComplete(true);
	}

	private boolean validate() {
		String txt = formattedText.getText();
		String regEx = "0{1,13}(\\.0*)?";
		return Pattern.matches(regEx, txt);
	}

	private void update() {
		if (checkNumberFormat.getSelection())
			formattedText.setText(DEFAULT_FORMAT);
		else
			formattedText.setText("");

		formattedText.setEnabled(checkNumberFormat.getSelection());
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
