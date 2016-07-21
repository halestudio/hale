/*
 * JXMapKit.java
 *
 * Created on November 19, 2006, 3:52 AM
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSlider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;
import org.jdesktop.swingx.painter.Painter;

/**
 * <p>
 * The JXMapKit is a pair of JXMapViewers preconfigured to be easy to use with
 * common features built in. This includes zoom buttons, a zoom slider, and a
 * mini-map in the lower right corner showing an overview of the map. Each
 * feature can be turned off using an appropriate <CODE>is<I>X</I>visible</CODE>
 * property. For example, to turn off the minimap call
 *
 * <PRE>
 * <CODE>jxMapKit.setMiniMapVisible(false);</CODE>
 * </PRE>
 * </p>
 *
 * <p>
 * The JXMapViewer is preconfigured to connect to maps.swinglabs.org which
 * serves up global satellite imagery from NASA's
 * <a href="http://earthobservatory.nasa.gov/Newsroom/BlueMarble/">Blue Marble
 * NG</a> image collection.
 * </p>
 * 
 * @author joshy
 */
public class JXMapKit extends JXPanel {

	private static final long serialVersionUID = -1096864144135804141L;

	private boolean miniMapVisible = true;
	private boolean zoomSliderVisible = true;
	private boolean zoomButtonsVisible = true;
	private final boolean sliderReversed = false;

	// private boolean addressLocationShown = true;

	// private boolean dataProviderCreditShown = true;

	private static final Log log = LogFactory.getLog(JXMapKit.class);

	/**
	 * Creates a new JXMapKit
	 */
	public JXMapKit() {
		initComponents();

		zoomSlider.setOpaque(false);
		try {
			Icon minusIcon = new ImageIcon(JXMapKit.class.getResource("resources/minus.png"));
			this.zoomOutButton.setIcon(minusIcon);
			this.zoomOutButton.setText("");
			Icon plusIcon = new ImageIcon(JXMapKit.class.getResource("resources/plus.png"));
			this.zoomInButton.setIcon(plusIcon);
			this.zoomInButton.setText("");
		} catch (Throwable thr) {
			System.out.println("error: " + thr.getMessage());
			thr.printStackTrace();
		}

		setTileFactory(new EmptyTileFactory());

		mainMap.setCenterPosition(new GeoPosition(0, 0, GeoPosition.WGS_84_EPSG));
		miniMap.setCenterPosition(new GeoPosition(0, 0, GeoPosition.WGS_84_EPSG));
		mainMap.setRestrictOutsidePanning(true);
		miniMap.setRestrictOutsidePanning(true);

		// update mini map center
		mainMap.addPropertyChangeListener("center", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Point2D mapCenter = (Point2D) evt.getNewValue();
				TileFactory tf = mainMap.getTileFactory();
				GeoPosition mapPos = tf.getTileProvider().getConverter().pixelToGeo(mapCenter,
						mainMap.getZoom());
				miniMap.setCenterPosition(mapPos);
			}
		});

		// XXX centerPosition change means center change
		/*
		 * mainMap.addPropertyChangeListener("centerPosition", new
		 * PropertyChangeListener() { public void
		 * propertyChange(PropertyChangeEvent evt) { mapCenterPosition =
		 * (GeoPosition)evt.getNewValue();
		 * miniMap.setCenterPosition(mapCenterPosition); try { Point2D pt =
		 * miniMap.getTileFactory().getTileProvider().getConverter().geoToPixel(
		 * mapCenterPosition,miniMap.getZoom()); miniMap.setCenter(pt);
		 * miniMap.repaint(); } catch (IllegalGeoPositionException e) {
		 * log.warn("Error setting mini map center position", e); } } });
		 */

		// update mini map zoom
		mainMap.addPropertyChangeListener("zoom", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				zoomSlider.setValue(mainMap.getZoom());
				miniMap.setZoom(Math.min(mainMap.getZoom() + 4,
						mainMap.getTileFactory().getTileProvider().getMaximumZoom()));
			}
		});

		// an overlay for the mini-map which shows a rectangle representing the
		// main map
		miniMap.setOverlayPainter(new Painter<JXMapViewer>() {

			@Override
			public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
				// get the viewport rect of the main map
				Rectangle mainMapBounds = mainMap.getViewportBounds();

				// convert to Point2Ds
				Point2D upperLeft2D = mainMapBounds.getLocation();
				Point2D lowerRight2D = new Point2D.Double(
						upperLeft2D.getX() + mainMapBounds.getWidth(),
						upperLeft2D.getY() + mainMapBounds.getHeight());

				// convert to GeoPostions
				GeoPosition upperLeft = mainMap.getTileFactory().getTileProvider().getConverter()
						.pixelToGeo(upperLeft2D, mainMap.getZoom());
				GeoPosition lowerRight = mainMap.getTileFactory().getTileProvider().getConverter()
						.pixelToGeo(lowerRight2D, mainMap.getZoom());

				// convert to Point2Ds on the mini-map
				try {
					upperLeft2D = map.getTileFactory().getTileProvider().getConverter()
							.geoToPixel(upperLeft, map.getZoom());
					lowerRight2D = map.getTileFactory().getTileProvider().getConverter()
							.geoToPixel(lowerRight, map.getZoom());

					g = (Graphics2D) g.create();
					Rectangle rect = map.getViewportBounds();
					g.translate(-rect.x, -rect.y);
					// Point2D centerpos =
					// map.getTileFactory().getTileProvider().getConverter().geoToPixel(mapCenterPosition,
					// map.getZoom());
					g.setPaint(Color.RED);
					// g.drawRect((int)centerpos.getX()-30,(int)centerpos.getY()-30,60,60);
					g.drawRect((int) upperLeft2D.getX(), (int) upperLeft2D.getY(),
							(int) (lowerRight2D.getX() - upperLeft2D.getX()),
							(int) (lowerRight2D.getY() - upperLeft2D.getY()));
					g.setPaint(new Color(255, 0, 0, 50));
					g.fillRect((int) upperLeft2D.getX(), (int) upperLeft2D.getY(),
							(int) (lowerRight2D.getX() - upperLeft2D.getX()),
							(int) (lowerRight2D.getY() - upperLeft2D.getY()));
					// g.drawOval((int)lowerRight2D.getX(),(int)lowerRight2D.getY(),1,1);
					g.dispose();
				} catch (IllegalGeoPositionException e) {
					log.warn("Error painting mini map overlay");
				}
			}
		});

		setZoom(3);// joshy: hack, i shouldn't need this here
		// this.setCenterPosition(new GeoPosition(0,0));
	}

	// private GeoPosition mapCenterPosition = new GeoPosition(0, 0,
	// GeoPosition.WGS_84_EPSG);

	private boolean zoomChanging = false;

	/**
	 * Set the current zoomlevel for the main map. The minimap will be updated
	 * accordingly
	 * 
	 * @param zoom the new zoom level
	 */
	public void setZoom(int zoom) {
		zoomChanging = true;
		mainMap.setZoom(zoom);
		// XXX is done by property change listener -
		// miniMap.setZoom(mainMap.getZoom()+4);
		if (sliderReversed) {
			zoomSlider.setValue(zoomSlider.getMaximum() - zoom);
		}
		else {
			zoomSlider.setValue(zoom);
		}
		zoomChanging = false;
	}

	/**
	 * Returns an action which can be attached to buttons or menu items to make
	 * the map zoom out
	 * 
	 * @return a preconfigured Zoom Out action
	 */
	@SuppressWarnings("serial")
	public Action getZoomOutAction() {
		Action act = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setZoom(mainMap.getZoom() - 1);
			}
		};
		act.putValue(Action.NAME, "-");
		return act;
	}

	/**
	 * Returns an action which can be attached to buttons or menu items to make
	 * the map zoom in
	 * 
	 * @return a preconfigured Zoom In action
	 */
	@SuppressWarnings("serial")
	public Action getZoomInAction() {
		Action act = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setZoom(mainMap.getZoom() + 1);
			}
		};
		act.putValue(Action.NAME, "+");
		return act;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		mainMap = new org.jdesktop.swingx.mapviewer.JXMapViewer();
		miniMap = new org.jdesktop.swingx.mapviewer.JXMapViewer();
		jPanel1 = new javax.swing.JPanel();
		zoomInButton = new javax.swing.JButton();
		zoomOutButton = new javax.swing.JButton();
		zoomSlider = new javax.swing.JSlider();

		setLayout(new java.awt.GridBagLayout());

		mainMap.setLayout(new java.awt.GridBagLayout());

		miniMap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		miniMap.setMinimumSize(new java.awt.Dimension(100, 100));
		miniMap.setPreferredSize(new java.awt.Dimension(100, 100));
		miniMap.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		mainMap.add(miniMap, gridBagConstraints);

		jPanel1.setOpaque(false);
		jPanel1.setLayout(new java.awt.GridBagLayout());

		zoomInButton.setAction(getZoomOutAction());
		zoomInButton.setIcon(
				new javax.swing.ImageIcon(JXMapKit.class.getResource("resources/plus.png")));
		zoomInButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		zoomInButton.setMaximumSize(new java.awt.Dimension(20, 20));
		zoomInButton.setMinimumSize(new java.awt.Dimension(20, 20));
		zoomInButton.setOpaque(false);
		zoomInButton.setPreferredSize(new java.awt.Dimension(20, 20));
		zoomInButton.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				zoomInButtonActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(zoomInButton, gridBagConstraints);

		zoomOutButton.setAction(getZoomInAction());
		zoomOutButton.setIcon(
				new javax.swing.ImageIcon(JXMapKit.class.getResource("resources/minus.png")));
		zoomOutButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		zoomOutButton.setMaximumSize(new java.awt.Dimension(20, 20));
		zoomOutButton.setMinimumSize(new java.awt.Dimension(20, 20));
		zoomOutButton.setOpaque(false);
		zoomOutButton.setPreferredSize(new java.awt.Dimension(20, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(zoomOutButton, gridBagConstraints);

		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setMaximum(15);
		zoomSlider.setMinimum(10);
		zoomSlider.setMinorTickSpacing(1);
		zoomSlider.setOrientation(javax.swing.JSlider.VERTICAL);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setMinimumSize(new java.awt.Dimension(35, 100));
		zoomSlider.setPreferredSize(new java.awt.Dimension(35, 190));
		zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {

			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				zoomSliderStateChanged(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		jPanel1.add(zoomSlider, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
		mainMap.add(jPanel1, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(mainMap, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	private void zoomInButtonActionPerformed(
			@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {// GEN-FIRST:event_zoomInButtonActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_zoomInButtonActionPerformed

	private void zoomSliderStateChanged(
			@SuppressWarnings("unused") javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_zoomSliderStateChanged
		if (!zoomChanging) {
			setZoom(zoomSlider.getValue());
		}
		// TODO add your handling code here:
	}// GEN-LAST:event_zoomSliderStateChanged

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel jPanel1;
	private org.jdesktop.swingx.mapviewer.JXMapViewer mainMap;
	private org.jdesktop.swingx.mapviewer.JXMapViewer miniMap;
	private javax.swing.JButton zoomInButton;
	private javax.swing.JButton zoomOutButton;
	private javax.swing.JSlider zoomSlider;
	// End of variables declaration//GEN-END:variables

	/**
	 * Indicates if the mini-map is currently visible
	 * 
	 * @return the current value of the mini-map property
	 */
	public boolean isMiniMapVisible() {
		return miniMapVisible;
	}

	/**
	 * Sets if the mini-map should be visible
	 * 
	 * @param miniMapVisible a new value for the miniMap property
	 */
	public void setMiniMapVisible(boolean miniMapVisible) {
		boolean old = this.isMiniMapVisible();
		this.miniMapVisible = miniMapVisible;
		miniMap.setVisible(miniMapVisible);
		firePropertyChange("miniMapVisible", old, this.isMiniMapVisible());
	}

	/**
	 * Indicates if the zoom slider is currently visible
	 * 
	 * @return the current value of the zoomSliderVisible property
	 */
	public boolean isZoomSliderVisible() {
		return zoomSliderVisible;
	}

	/**
	 * Sets if the zoom slider should be visible
	 * 
	 * @param zoomSliderVisible the new value of the zoomSliderVisible property
	 */
	public void setZoomSliderVisible(boolean zoomSliderVisible) {
		boolean old = this.isZoomSliderVisible();
		this.zoomSliderVisible = zoomSliderVisible;
		zoomSlider.setVisible(zoomSliderVisible);
		firePropertyChange("zoomSliderVisible", old, this.isZoomSliderVisible());
	}

	/**
	 * Indicates if the zoom buttons are visible. This is a bound property and
	 * can be listed for using a PropertyChangeListener
	 * 
	 * @return current value of the zoomButtonsVisible property
	 */
	public boolean isZoomButtonsVisible() {
		return zoomButtonsVisible;
	}

	/**
	 * Sets if the zoom buttons should be visible. This ia bound property.
	 * Changes can be listened for using a PropertyChaneListener
	 * 
	 * @param zoomButtonsVisible new value of the zoomButtonsVisible property
	 */
	public void setZoomButtonsVisible(boolean zoomButtonsVisible) {
		boolean old = this.isZoomButtonsVisible();
		this.zoomButtonsVisible = zoomButtonsVisible;
		zoomInButton.setVisible(zoomButtonsVisible);
		zoomOutButton.setVisible(zoomButtonsVisible);
		firePropertyChange("zoomButtonsVisible", old, this.isZoomButtonsVisible());
	}

	/**
	 * Sets the tile factory for both embedded JXMapViewer components. Calling
	 * this method will also reset the center and zoom levels of both maps, as
	 * well as the bounds of the zoom slider.
	 * 
	 * @param fact the new TileFactory
	 */
	public void setTileFactory(TileFactory fact) {
		zoomChanging = true;
		zoomSlider.setMinimum(fact.getTileProvider().getMinimumZoom());
		zoomSlider.setMaximum(fact.getTileProvider().getMaximumZoom());
		zoomChanging = false;

		mainMap.setTileFactory(fact);
		// mainMap.setCenterPosition(TileProviderUtils.getMapCenter(fact.getTileProvider()));
		mainMap.setZoom(fact.getTileProvider().getDefaultZoom());

		miniMap.setTileFactory(fact);
		// miniMap.setCenterPosition(TileProviderUtils.getMapCenter(fact.getTileProvider()));

		setZoom(fact.getTileProvider().getDefaultZoom());
	}

	/**
	 * Set the center position
	 * 
	 * @param pos the center position to set
	 */
	public void setCenterPosition(GeoPosition pos) {
		mainMap.setCenterPosition(pos);
		// XXX is done by property change listener -
		// miniMap.setCenterPosition(pos);
	}

	/**
	 * Get the center position
	 * 
	 * @return the center position
	 */
	public GeoPosition getCenterPosition() {
		return mainMap.getCenterPosition();
	}

	/**
	 * Returns a reference to the main embedded JXMapViewer component
	 * 
	 * @return the main map
	 */
	public JXMapViewer getMainMap() {
		return this.mainMap;
	}

	/**
	 * Returns a reference to the mini embedded JXMapViewer component
	 * 
	 * @return the minimap JXMapViewer component
	 */
	public JXMapViewer getMiniMap() {
		return this.miniMap;
	}

	/**
	 * returns a reference to the zoom in button
	 * 
	 * @return a jbutton
	 */
	public JButton getZoomInButton() {
		return this.zoomInButton;
	}

	/**
	 * returns a reference to the zoom out button
	 * 
	 * @return a jbutton
	 */
	public JButton getZoomOutButton() {
		return this.zoomOutButton;
	}

	/**
	 * returns a reference to the zoom slider
	 * 
	 * @return a jslider
	 */
	public JSlider getZoomSlider() {
		return this.zoomSlider;
	}

	/*
	 * public static void main(String ... args) { SwingUtilities.invokeLater(new
	 * Runnable() { public void run() { JXMapKit kit = new JXMapKit();
	 * 
	 * final int max = 17; TileFactoryInfo info = new
	 * TileFactoryInfo(1,max-2,max, 256, true, true, // tile size is 256 and x/y
	 * orientation is normal "http://tile.openstreetmap.org",//5/15/10.png",
	 * "x","y","z") { public String getTileUrl(int x, int y, int zoom) { zoom =
	 * max-zoom; String url = this.baseURL +"/"+zoom+"/"+x+"/"+y+".png"; return
	 * url; }
	 * 
	 * }; TileFactory tf = new DefaultTileFactory(new
	 * TileFactoryInfoTileProvider(info, SimpleGeoConverter.INSTANCE));
	 * kit.setTileFactory(tf); kit.setZoom(14); //kit.setAddressLocation(new
	 * GeoPosition(51.5,0)); kit.getMainMap().setDrawTileBorders(true);
	 * kit.getMainMap().setRestrictOutsidePanning(true);
	 * kit.getMainMap().setHorizontalWrapped(false);
	 * 
	 * ((DefaultTileFactory)kit.getMainMap().getTileFactory()).setThreadPoolSize
	 * (8); JFrame frame = new JFrame("JXMapKit test"); frame.add(kit);
	 * frame.pack(); frame.setSize(500,300); frame.setVisible(true); } }); }
	 */
}
