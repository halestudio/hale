/*
 * Copyright (c) 2020 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.config;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.geotools.referencing.CRS;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.GeoInstanceWriter;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.instance.crs.SelectCRSDialog;

/**
 * Configuration page for the target CRS.
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 */
public class SimpleTargetCRSConfigurationPage<P extends GeoInstanceWriter, W extends IOWizard<P>>
		extends AbstractConfigurationPage<P, W> {

	private Label crsLabel;
	private Button checkConvert;
	private Button selectCrs;
	private CRSDefinition crsDef;

	/**
	 * Default constructor.
	 */
	public SimpleTargetCRSConfigurationPage() {
		super("targetCRS");

		setTitle("Coordinate reference system");
		setDescription("Configure the target coordinate reference system");

		setPageComplete(false);
	}

	@Override
	public void enable() {
		// nothing to do
	}

	@Override
	public void disable() {
		// nothing to do
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow) {
			// init from provider
			crsDef = getWizard().getProvider().getTargetCRS();
			checkConvert.setSelection(crsDef != null);

			update();
		}
	}

	@Override
	public boolean updateConfiguration(GeoInstanceWriter provider) {
		if (checkConvert.getSelection()) {
			provider.setTargetCRS(crsDef);
		}
		else {
			provider.setTargetCRS(null);
		}

		return true;
	}

	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		Group convertGroup = new Group(page, SWT.NONE);
		convertGroup.setText("Convert to CRS");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(convertGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(convertGroup);

		checkConvert = new Button(convertGroup, SWT.CHECK);
		checkConvert.setText("Convert all geometries to the given target CRS:");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(checkConvert);
		checkConvert.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});

		crsLabel = new Label(convertGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(crsLabel);

		selectCrs = new Button(convertGroup, SWT.PUSH);
		selectCrs.setText("Select...");
		GridDataFactory.swtDefaults().applyTo(selectCrs);

		selectCrs.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectCRSDialog dlg = new SelectCRSDialog(e.display.getActiveShell(), crsDef);
				if (dlg.open() == Dialog.OK) {
					crsDef = dlg.getValue();
				}
				update();
			}

		});

		// only update on first show
		// update();
	}

	/**
	 * Update the page state.
	 */
	private void update() {
		// update the CRS label
		if (crsDef == null || crsDef.getCRS() == null) {
			crsLabel.setText("<None selected>");
		}
		else {
			String name = crsDef.getCRS().getName().toString();
			name = name.replaceAll("EPSG:", "");
			name += " (" + CRS.toSRS(crsDef.getCRS()) + ")";
			crsLabel.setText(name);
		}

		// button status
		selectCrs.setEnabled(checkConvert.getSelection());
		crsLabel.setEnabled(checkConvert.getSelection());

		// page status
		if (checkConvert.getSelection()) {
			if (crsDef == null) {
				setPageComplete(false);
				setMessage("Specify a valid target CRS to convert to", DialogPage.ERROR);
			}
			else {
				setPageComplete(true);
				setMessage(null);
			}
		}
		else {
			setPageComplete(true);
			setMessage(null);
		}
	}
}
