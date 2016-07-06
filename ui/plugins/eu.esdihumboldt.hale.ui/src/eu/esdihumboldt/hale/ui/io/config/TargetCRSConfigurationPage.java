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

package eu.esdihumboldt.hale.ui.io.config;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.geotools.gml2.SrsSyntax;
import org.geotools.referencing.CRS;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.GeoInstanceWriter;
import eu.esdihumboldt.hale.common.instance.io.util.EnumWindingOrderTypes;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.instance.crs.SelectCRSDialog;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;

/**
 * Configuration page for the character encoding.
 * 
 * @param <W> the concrete I/O wizard type
 * @param
 * 			<P>
 *            the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 */
public class TargetCRSConfigurationPage<P extends GeoInstanceWriter, W extends IOWizard<P>>
		extends AbstractConfigurationPage<P, W> {

	private Label crsLabel;
	private Button checkConvert;
	private Button selectCrs;
	private CRSDefinition crsDef;
	private Button checkPrefix;
	private ComboViewer prefixCombo;
	private ComboViewer windingorderCombo;

	/**
	 * Default constructor.
	 */
	public TargetCRSConfigurationPage() {
		super("targetCRS");

		setTitle("Coordinate reference system");
		setDescription("Configure the target coordinate reference system and winding order");

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

			String prefix = getWizard().getProvider().getCustomEPSGPrefix();
			SrsSyntax prefixValue = null;
			if (prefix != null) {
				try {
					for (SrsSyntax srsSyntax : SrsSyntax.values()) {
						if (srsSyntax.getPrefix().equals(prefix)) {
							prefixValue = srsSyntax;
							break;
						}
					}
				} catch (Exception e) {
					// ignore
				}
			}
			if (prefixValue != null) {
				prefixCombo.setSelection(new StructuredSelection(prefixValue));
			}
			checkPrefix.setSelection(prefixValue != null);

			EnumWindingOrderTypes windingorder = getWizard().getProvider().getWindingOrder();
			if (windingorder != null) {
				windingorderCombo.setSelection(new StructuredSelection(windingorder));
			}

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

		String prefix = null;
		if (checkPrefix.getSelection()) {
			ISelection sel = prefixCombo.getSelection();
			if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
				Object selected = ((IStructuredSelection) prefixCombo.getSelection())
						.getFirstElement();
				if (selected instanceof SrsSyntax) {
					prefix = ((SrsSyntax) selected).getPrefix();
				}
			}
		}
		provider.setCustomEPSGPrefix(prefix);

		EnumWindingOrderTypes windingorder = null;
		ISelection order = windingorderCombo.getSelection();
		if (!order.isEmpty() && order instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) windingorderCombo.getSelection())
					.getFirstElement();
			if (selected instanceof EnumWindingOrderTypes) {
				windingorder = (EnumWindingOrderTypes) selected;
			}
		}
		provider.setWindingOrder(windingorder);

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

		Group prefixGroup = new Group(page, SWT.NONE);
		prefixGroup.setText("EPSG prefix");
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(prefixGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(prefixGroup);

		checkPrefix = new Button(prefixGroup, SWT.CHECK);
		checkPrefix.setText("Use a specific EPSG prefix for SRS names");
		GridDataFactory.swtDefaults().applyTo(checkConvert);
		checkPrefix.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});

		prefixCombo = new ComboViewer(prefixGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(prefixCombo.getControl());
		prefixCombo.setContentProvider(EnumContentProvider.getInstance());
		prefixCombo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof SrsSyntax) {
					return ((SrsSyntax) element).getPrefix();
				}
				return super.getText(element);
			}

		});
		prefixCombo.setInput(SrsSyntax.class);
		prefixCombo.setSelection(new StructuredSelection(SrsSyntax.OGC_HTTP_URI));

		// Winding Order

		Group windingOrderGroup = new Group(page, SWT.NONE);
		windingOrderGroup.setText("Unify winding order");
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(windingOrderGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(windingOrderGroup);

		windingorderCombo = new ComboViewer(windingOrderGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(windingorderCombo.getControl());
		windingorderCombo.setContentProvider(EnumContentProvider.getInstance());
		windingorderCombo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof EnumWindingOrderTypes) {
					return ((EnumWindingOrderTypes) element).getWindingOrder();
				}
				return super.getText(element);
			}

		});
		windingorderCombo.setInput(EnumWindingOrderTypes.class);

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

		prefixCombo.getControl().setEnabled(checkPrefix.getSelection());

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
