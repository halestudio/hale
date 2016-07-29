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
package de.fhg.igd.mapviewer.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewSite;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.JXMapViewer;

import de.fhg.igd.mapviewer.view.arecalculation.AreaCalc;

/**
 * Shows the current mouse position in the status line
 * 
 * @author Simon Templer
 */
public class PositionStatus extends MouseAdapter {

	/**
	 * EPSG provider interface
	 */
	public interface EpsgProvider {

		/**
		 * Get the EPSG code of a CRS
		 * 
		 * @return the EPSG code, zero for no code
		 */
		public int getEpsgCode();
	}

	private final JXMapViewer map;

	private final IViewSite site;

	private final Image image;

	private final EpsgProvider epsgProvider;

	private final DecimalFormat format = new DecimalFormat("0.####", //$NON-NLS-1$
			DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	/**
	 * Constructor
	 * 
	 * @param map the map component
	 * @param site the view site
	 * @param image the message image
	 * @param epsgProvider the EPSG provider
	 */
	public PositionStatus(JXMapViewer map, IViewSite site, Image image,
			final EpsgProvider epsgProvider) {
		this.map = map;
		this.site = site;
		this.image = image;
		this.epsgProvider = epsgProvider;

		map.addMouseMotionListener(this);
		map.addMouseListener(this);

		//
		AreaCalc.getInstance().setMap(map);
	}

	/**
	 * @see MouseAdapter#mouseMoved(MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		GeoPosition pos = map.convertPointToGeoPosition(e.getPoint());

		// convert if needed and possible
		int target = epsgProvider.getEpsgCode();
		if (target != 0) {
			try {
				pos = GeotoolsConverter.getInstance().convert(pos, target);
			} catch (Exception x) {
				// ignore
			}
		}

		final GeoPosition position = pos;

		// set current GeoPosition
		AreaCalc.getInstance().setCurrentGeoPos(pos);

		site.getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				site.getActionBars().getStatusLineManager().setMessage(image,
						"EPSG:" + position.getEpsgCode() + " - " + //$NON-NLS-1$ //$NON-NLS-2$
								format.format(position.getX()) + " / " + //$NON-NLS-1$
								format.format(position.getY()));
			}
		});
	}

	/**
	 * @see MouseAdapter#mouseExited(MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		site.getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				site.getActionBars().getStatusLineManager().setMessage(null);
			}
		});
	}

}
