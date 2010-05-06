/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.views.map;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.rcp.views.map.tiles.AbstractTilePainter;

/**
 * <p>Title: PositionStatus</p>
 * <p>Description: Shows the current mouse position in the status line</p>
 * @author Simon Templer
 * @version $Id$
 */
public class PositionStatus extends MouseTrackAdapter implements MouseMoveListener {

	private final AbstractTilePainter map;
	
	private final IViewSite site;
	
	private final Image image;
	
	private final Control control;
	
	private final DecimalFormat format = new DecimalFormat(
			"0.####", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	/**
	 * Constructor
	 * 
	 * @param map the map tile painter
	 * @param control the map control
	 * @param site the view site
	 * @param image the message image
	 */
	public PositionStatus(AbstractTilePainter map, Control control, IViewSite site, 
			Image image) {
		this.map = map;
		this.site = site;
		this.image = image;
		this.control = control;
		
		control.addMouseMoveListener(this);
		control.addMouseTrackListener(this);
	}

	/**
	 * @see MouseMoveListener#mouseMove(MouseEvent)
	 */
	@Override
	public void mouseMove(MouseEvent e) {
		Point2D pos = map.toGeoCoordinates(e.x, e.y);
		if (pos != null) {
			String crsString = "";
			CoordinateReferenceSystem crs = SelectCRSDialog.getValue();
			if (crs != null) {
				crsString = crs.getName().toString() + " - ";
			}
			
			site.getActionBars().getStatusLineManager().setMessage(
					image,
					crsString +
					format.format(pos.getX()) + " / " + 
					format.format(pos.getY()));
		}
		else {
			site.getActionBars().getStatusLineManager().setMessage(null);
		}
	}
	
	/**
	 * @see MouseTrackAdapter#mouseExit(MouseEvent)
	 */
	@Override
	public void mouseExit(MouseEvent e) {
		site.getActionBars().getStatusLineManager().setMessage(null);
	}
	
	/**
	 * Perform clean up operations
	 */
	public void dispose() {
		if (!control.isDisposed()) {
			control.removeMouseMoveListener(this);
			control.removeMouseTrackListener(this);
		}
	}

}
