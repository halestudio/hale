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

import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.util.format.DecimalFormatUtil;

/**
 * Configuration page for basic {@link StreamGmlWriter} settings.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class GmlWriterSettingsPage
		extends AbstractConfigurationPage<StreamGmlWriter, IOWizard<StreamGmlWriter>> {

	private static final String DEFAULT_COORDINATE_FORMAT = "0.000";
	private static final String DEFAULT_DECIMAL_FORMAT = "0.############";

	private Button prettyPrint;
	private Button simplify;
	private Button nilReason;
	private Button enableCoordinateFormat;
	private Text coordinateFormat;
	private Label coordinateFormatWarning;
	private Button enableDecimalFormat;
	private Text decimalFormat;
	private Label decimalFormatExample;
	private Button addCodespace;

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
			enableCoordinateFormat.setSelection(false);
			enableDecimalFormat.setSelection(false);
			updateCoordinateFormat();
			updateDecimalFormat();
		}
	}

	@Override
	public boolean updateConfiguration(StreamGmlWriter provider) {
		if (enableCoordinateFormat.getSelection() && !validateFormats()) {
			return false;
		}
		setMessage("Basic XML and GML encoding settings");
		provider.setPrettyPrint(prettyPrint.getSelection());
		provider.setParameter(StreamGmlWriter.PARAM_SIMPLIFY_GEOMETRY,
				Value.of(simplify.getSelection()));
		provider.setParameter(StreamGmlWriter.PARAM_OMIT_NIL_REASON,
				Value.of(nilReason.getSelection()));
		provider.setParameter(StreamGmlWriter.PARAM_ADD_CODESPACE,
				Value.of(addCodespace.getSelection()));

		if (enableCoordinateFormat.getSelection()) {
			provider.setGeometryWriteFormat(coordinateFormat.getText().trim());
		}

		if (enableDecimalFormat.getSelection()) {
			provider.setDecimalWriteFormat(decimalFormat.getText().trim());
		}

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
		prettyPrint.setSelection(true);

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

		Group codespace = new Group(page, SWT.NONE);
		codespace.setLayout(new GridLayout(1, false));
		codespace.setText("addCodespace");
		groupData.applyTo(codespace);

		addCodespace = new Button(codespace, SWT.CHECK);
		addCodespace.setText("Automatically fill-in the codespace attribute");
		// default
		addCodespace.setSelection(true);

		Group writeFormat = new Group(page, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(writeFormat);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(writeFormat);
		writeFormat.setText("Output formatting");

		enableCoordinateFormat = new Button(writeFormat, SWT.CHECK);
		enableCoordinateFormat.setText("Use a formatted number output for geometry coordinates");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(enableCoordinateFormat);
		enableCoordinateFormat.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateCoordinateFormat();
			}
		});

		Label coordinateFormatLabel = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(coordinateFormatLabel);
		coordinateFormatLabel.setText("Format: ");

		coordinateFormat = new Text(writeFormat, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(coordinateFormat);
		coordinateFormat.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (enableCoordinateFormat.getSelection()) {
					updateCoordinateFormat();
				}
			}
		});
		// filler
		Label cfFiller = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(cfFiller);

		Label coordFormatDesc = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(coordFormatDesc);
		coordFormatDesc.setText("(e.g. 00000.000)");

		enableDecimalFormat = new Button(writeFormat, SWT.CHECK);
		enableDecimalFormat.setText("Use a formatted output for decimal values");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(enableDecimalFormat);
		enableDecimalFormat.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDecimalFormat();
			}
		});

		Label decimalFormatLabel = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(decimalFormatLabel);
		decimalFormatLabel.setText("Format: ");

		decimalFormat = new Text(writeFormat, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(decimalFormat);
		decimalFormat.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateDecimalFormat();
			}
		});

		Label dfFiller2 = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(dfFiller2);

		decimalFormatExample = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(decimalFormatExample);

		// filler
		Label dfFiller = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(dfFiller);

		Label decFormatDesc = new Label(writeFormat, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(decFormatDesc);
		decFormatDesc
				.setText("(e.g. use 0.000## to write at least 3 and at most 5 decimal places)");

		coordinateFormatWarning = new Label(page, SWT.WRAP);
		GridDataFactory.swtDefaults().span(2, 2).applyTo(coordinateFormatWarning);
		coordinateFormatWarning.setText(
				"Reducing the number of significant figures of the coordinates may result in the inadvertent\n" //
						+ "duplication of points. Resulting invalid geometries (e.g. a polygon that has less than three\n" //
						+ "distinct points) will not be fixed or warned about by hale studio.");
		coordinateFormatWarning
				.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		coordinateFormatWarning.setVisible(false);

		// filler
		new Label(page, SWT.NONE);

		setPageComplete(true);
	}

	private boolean validateFormats() {
		boolean result = true;

		if (enableCoordinateFormat.getSelection()) {
			result &= validateFormat(coordinateFormat.getText());
		}

		if (result && enableDecimalFormat.getSelection()) {
			result &= validateFormat(decimalFormat.getText());
		}

		return result;
	}

	private boolean validateFormat(String format) {
		try {
			DecimalFormatUtil.getFormatter(format);
		} catch (Exception e) {
			setErrorMessage("Invalid format: " + e.getMessage());
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	private void updateCoordinateFormat() {
		if (enableCoordinateFormat.getSelection()
				&& StringUtils.isEmpty(coordinateFormat.getText())) {
			coordinateFormat.setText(DEFAULT_COORDINATE_FORMAT);
		}

		coordinateFormat.setEnabled(enableCoordinateFormat.getSelection());
		coordinateFormatWarning.setVisible(enableCoordinateFormat.getSelection());
		validateFormats();
	}

	private void updateDecimalFormat() {
		if (enableDecimalFormat.getSelection() && StringUtils.isEmpty(decimalFormat.getText())) {
			decimalFormat.setText(DEFAULT_DECIMAL_FORMAT);
		}

		setDecimalFormatExample(decimalFormat.getText());

		decimalFormat.setEnabled(enableDecimalFormat.getSelection());
		if (!decimalFormat.isEnabled() && !StringUtils.isEmpty(decimalFormat.getText())) {
			decimalFormat.setText("");
		}

		validateFormats();
	}

	private void setDecimalFormatExample(String pattern) {
		final String text = "Test: 123456789.6543 will be represented as {0}";
		final double exampleValue = 123456789.6543;

		if (decimalFormatExample != null && !decimalFormatExample.isDisposed()) {
			if (!enableDecimalFormat.getSelection()
					|| StringUtils.isEmpty(decimalFormat.getText())) {
				// default example
				decimalFormatExample
						.setText(MessageFormat.format(text, Double.toString(exampleValue)));
			}
			else if (!validateFormat(pattern)) {
				decimalFormatExample.setText("Invalid pattern");
			}
			else {
				DecimalFormat formatter = DecimalFormatUtil.getFormatter(pattern);
				decimalFormatExample
						.setText(MessageFormat.format(text, formatter.format(exampleValue)));
			}
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
