/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import de.fhg.igd.mapviewer.server.wms.Messages;
import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilities;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilitiesException;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSUtil;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSWizardPage;

/**
 * Wizard page for configuring the spatial reference system to use.
 * 
 * @author Simon Templer
 */
public class SRSConfigurationPage extends WMSWizardPage<WMSConfiguration> {

	private static class SRSLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			String text = element.toString();

			if (text.equals(SRS_ANY)) {
				return text;
			}
			else {
				try {
					CoordinateReferenceSystem crs = CRS.decode(text);
					return crs.getName().toString() + " (" + text + ")";
				} catch (Throwable e) {
					return text;
				}
			}
		}

	}

	private static final Log log = LogFactory.getLog(SRSConfigurationPage.class);

	private static final String SRS_ANY = Messages.SRSConfigurationPage_0;

	private final BasicConfigurationPage conf;

	private ComboViewer viewer;

	private WMSCapabilities capabilities = null;

	/**
	 * Constructor
	 * 
	 * @param conf the basic WMS configuration
	 * @param configuration the WMS client configuration
	 */
	public SRSConfigurationPage(final BasicConfigurationPage conf, WMSConfiguration configuration) {
		super(configuration, "SRS"); //$NON-NLS-1$

		this.conf = conf;

		setTitle(Messages.SRSConfigurationPage_2);
		setMessage(Messages.SRSConfigurationPage_3);
	}

	/**
	 * @see WMSWizardPage#createContent(Composite)
	 */
	@Override
	public void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		page.setLayout(new GridLayout(1, false));

		// SRS
		Combo combo = new Combo(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		viewer = new ComboViewer(combo);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new SRSLabelProvider());
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				update();
			}

		});

		setControl(page);

		update();
	}

	private void update() {
		setPageComplete(!viewer.getSelection().isEmpty());
	}

	/**
	 * @see WMSWizardPage#updateConfiguration(WMSConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WMSConfiguration configuration) {
		ISelection selection = viewer.getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			try {
				String srs = (String) ((IStructuredSelection) selection).getFirstElement();
				if (srs.equals(SRS_ANY)) {
					configuration.setPreferredEpsg(0);
				}
				else {
					configuration.setPreferredEpsg(Integer.parseInt(srs.substring(5)));
				}
				return true;
			} catch (Exception e) {
				log.warn("Error setting SRS", e); //$NON-NLS-1$
			}
		}

		return false;
	}

	/**
	 * @see WMSWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		// get last selection
		ISelection selection = viewer.getSelection();
		String lastSelection = null;
		if (selection != null && selection instanceof IStructuredSelection) {
			lastSelection = (String) ((IStructuredSelection) selection).getFirstElement();
		}

		// update input
		String url = conf.getServiceURL();

		List<String> input = new ArrayList<String>();
		input.add(SRS_ANY);
		try {
			capabilities = WMSUtil.getCapabilities(url);

//			for (String srs : capabilities.getBoundingBoxes().keySet()) {
//				input.add(srs);
//			}
			for (String srs : capabilities.getSupportedSRS()) {
				try {
					CRS.decode(srs); // test if known
					input.add(srs);
				} catch (Exception e) {
					// ignore - unknown
				}
			}

		} catch (WMSCapabilitiesException e) {
			// ignore
		}

		viewer.setInput(input);

		// update selection
		if (lastSelection == null) {
			String def = "EPSG:" + getConfiguration().getPreferredEpsg(); //$NON-NLS-1$
			if (input.contains(def)) {
				lastSelection = def;
			}
			else {
				lastSelection = SRS_ANY;
			}
		}

		viewer.setSelection(new StructuredSelection(lastSelection));
	}

}
