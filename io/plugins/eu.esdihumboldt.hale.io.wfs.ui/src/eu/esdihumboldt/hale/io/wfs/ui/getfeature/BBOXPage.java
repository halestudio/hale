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
import java.awt.Color;
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
import org.eclipse.swt.widgets.Display;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.tiles.CustomTileMapServer;
import de.fhg.igd.mapviewer.view.MapToolAction;
import eu.esdihumboldt.hale.io.wfs.capabilities.BBox;
import eu.esdihumboldt.hale.io.wfs.capabilities.FeatureTypeInfo;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.io.wfs.ui.getfeature.internal.BBoxTool;
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

	private BBoxTool bboxTool;

	/**
	 * Create a new wizard page for specifying the request bounding box.
	 * 
	 * @param wizard the parent wizard
	 * @param capabilitiesPage the capabilities page
	 */
	public BBOXPage(ConfigurationWizard<? extends WFSGetFeatureConfig> wizard,
			AbstractWFSCapabilitiesPage<? extends WFSGetFeatureConfig> capabilitiesPage) {
		super(wizard, "wfsBBOX");
		setTitle("Query bounding box");
		setDescription("Please set bounding box corners with left click, remove with right click.");

		this.capabilitiesPage = capabilitiesPage;

		// no BBOX is allowed
		setPageComplete(true);
	}

	@Override
	public boolean updateConfiguration(WFSGetFeatureConfig configuration) {
		if (bboxTool != null) {
			if (bboxTool.getPositions().size() == 0) {
				// no bounding box
				configuration.setBbox(null);
			}
			else if (bboxTool.getPositions().size() == 2) {
				// derive BB
				GeoPosition pos1 = bboxTool.getPositions().get(0);
				GeoPosition pos2 = bboxTool.getPositions().get(1);
				int epsg = pos1.getEpsgCode();

				double x1 = Math.min(pos1.getX(), pos2.getX());
				double y1 = Math.min(pos1.getY(), pos2.getY());
				double x2 = Math.max(pos1.getX(), pos2.getX());
				double y2 = Math.max(pos1.getY(), pos2.getY());

				configuration.setBbox(new BBox(x1, y1, x2, y2));

				// FIXME what kind of representation is best
				// i.e. most likely to work with implementations?
				// urn:ogc:def:crs:EPSG:: <- not working with tested GeoServer
				configuration.setBboxCrsUri("EPSG:" + epsg);
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onShowPage() {
		String capUrl = capabilitiesPage.getCapabilitiesURL();
		if (!Objects.equals(capUrl, lastCapUrl)) {
			// capabilities changed

			// update map
			WFSCapabilities caps = capabilitiesPage.getCapabilities();
			bboxTool.reset();
			updateMap(caps);
		}
		else {
			// capabilities stayed the same

		}

		lastCapUrl = capUrl;

		updateState();
	}

	/**
	 * Update the map (i.e. set the map server).
	 * 
	 * @param caps the WFS capabilities
	 */
	private void updateMap(WFSCapabilities caps) {
		CustomTileMapServer tileServer = new CustomTileMapServer();

		// use OpenStreetMap as default map here
		tileServer.setUrlPattern("https://tile.openstreetmap.org/{z}/{x}/{y}.png");
		tileServer.setAttributionText("Map tiles by OpenStreetMap, under CC BY-SA 2.0");
		tileServer.setZoomLevel(20);

		MapServer server = tileServer;

		Set<GeoPosition> positions = null;
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

					Color back = mapKit.getBackground();
					server = new ClippingMapServer(server, topLeft, bottomRight,
							new Color(back.getRed(), back.getGreen(), back.getBlue(), 170));

					positions = new HashSet<>();
					positions.add(topLeft);
					positions.add(bottomRight);
				}
				else {
					// ignore BBs, provide full map
				}
			}
		}

		mapKit.setServer(server, true);
		if (positions != null) {
			try {
				mapKit.zoomToPositions(positions);
			} catch (Exception e) {
				// ignore error
			}
		}
		mapKit.refresh();
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		SwingComposite wrapper = new SwingComposite(page);
		GridDataFactory.fillDefaults().grab(true, true).hint(600, 400).applyTo(wrapper);
		wrapper.getContentPane().setLayout(new BorderLayout());

		// create map kit
		mapKit = new BasicMapKit();

		// configure map
		updateMap(null);

//		mapKit.addCustomPainter(mypainter);

		// add map kit
		wrapper.getContentPane().add(mapKit, BorderLayout.CENTER);

		// create tool
		final Display display = Display.getCurrent();
		final Runnable updateRunner = new Runnable() {

			@Override
			public void run() {
				updateState();
			}
		};
		bboxTool = new BBoxTool() {

			@Override
			protected void addPosition(GeoPosition pos) {
				super.addPosition(pos);
				display.asyncExec(updateRunner);
			}

			@Override
			public void reset() {
				super.reset();
				display.asyncExec(updateRunner);
			}

		};

		// activate tool
		new MapToolAction(bboxTool, mapKit, true);

		setControl(page);
	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		// either an empty or complete bb is allowed
		boolean valid = bboxTool.getPositions().size() == 0 || bboxTool.getPositions().size() == 2;
		setPageComplete(valid);
	}

}
