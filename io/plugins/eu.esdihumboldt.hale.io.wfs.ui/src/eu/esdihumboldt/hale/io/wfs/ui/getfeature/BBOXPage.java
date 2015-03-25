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

package eu.esdihumboldt.hale.io.wfs.ui.getfeature;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.openstreetmap.OpenStreetMapServer;
import eu.esdihumboldt.hale.io.wfs.capabilities.BBox;
import eu.esdihumboldt.hale.io.wfs.capabilities.FeatureTypeInfo;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.io.wfs.ui.getfeature.internal.ClippingMapServer;
import eu.esdihumboldt.hale.ui.util.swing.SwingComposite;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Page for specifying a bounding box.
 * 
 * @author Simon Templer
 */
public class BBOXPage extends ConfigurationWizardPage<WFSGetFeatureConfig> {

	private final AbstractWFSCapabilitiesPage<? extends WFSGetFeatureConfig> capabilitiesPage;

	private BasicMapKit mapKit;
	private String lastCapUrl;

	/**
	 * @param wizard
	 * @param capabilitiesPage
	 */
	public BBOXPage(ConfigurationWizard<? extends WFSGetFeatureConfig> wizard,
			AbstractWFSCapabilitiesPage<? extends WFSGetFeatureConfig> capabilitiesPage) {
		super(wizard, "wfsBBOX");
		setTitle("Query bounding box");
		setDescription("Please set bounding box corners with left click, remove with right click.");

		this.capabilitiesPage = capabilitiesPage;

		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage#updateConfiguration(java.lang.Object)
	 */
	@Override
	public boolean updateConfiguration(WFSGetFeatureConfig configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		String capUrl = capabilitiesPage.getCapabilitiesURL();
		if (!Objects.equals(capUrl, lastCapUrl)) {
			// capabilities changed

			// update map
			WFSCapabilities caps = capabilitiesPage.getCapabilities();
			updateMap(caps);
		}
		else {
			// capabilities stayed the same

		}

		lastCapUrl = capUrl;
	}

	/**
	 * @param caps
	 */
	private void updateMap(WFSCapabilities caps) {
		MapServer server = new OpenStreetMapServer();
		if (caps != null) {
			/*
			 * TODO optimal solution would be using the WMS that serves the
			 * layers corresponding to the feature types
			 */

			// collect BBs from feature types
			Set<QName> types = new HashSet<>(getWizard().getConfiguration().getTypeNames());
			if (types.isEmpty()) {
				// no features will be selected
			}
			else {
				List<BBox> bbs = new ArrayList<>();
				for (QName type : types) {
					FeatureTypeInfo info = caps.getFeatureTypes().get(type);
					if (info != null && info.getWgs84BBox() != null) {
						bbs.add(info.getWgs84BBox());
					}
				}

				if (!bbs.isEmpty()) {
					double minX, maxX, minY, maxY;

					Iterator<BBox> it = bbs.iterator();
					BBox bb = it.next();
					minX = bb.getX1();
					minY = bb.getY1();
					maxX = bb.getX2();
					maxY = bb.getY2();
					while (it.hasNext()) {
						bb = it.next();
						minX = Math.min(minX, bb.getX1());
						minY = Math.min(minY, bb.getY1());
						maxX = Math.max(maxX, bb.getX2());
						maxY = Math.max(maxY, bb.getY2());
					}

					GeoPosition topLeft = new GeoPosition(minX, maxY, 4326);
					GeoPosition bottomRight = new GeoPosition(maxX, minY, 4326);

					server = new ClippingMapServer(server, topLeft, bottomRight);
				}
				else {
					// ignore BBs, provide full map
				}
			}
		}

		mapKit.setServer(server, true);
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		SwingComposite wrapper = new SwingComposite(page);
		GridDataFactory.fillDefaults().grab(true, true).hint(500, 400).applyTo(wrapper);
		wrapper.getContentPane().setLayout(new BorderLayout());

		// create map kit
		mapKit = new BasicMapKit();

		// configure map
		updateMap(null);

//		mapKit.addCustomPainter(mypainter);

		// add map kit
		wrapper.getContentPane().add(mapKit, BorderLayout.CENTER);

		// new MapToolAction(tool, mapKit, true);

		setControl(page);
	}

	protected void updateState() {

	}

}
