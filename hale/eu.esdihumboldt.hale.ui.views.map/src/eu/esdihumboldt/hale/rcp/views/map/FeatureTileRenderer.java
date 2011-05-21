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

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.ImageData;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileConstraints;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileProvider;
import eu.esdihumboldt.hale.ui.common.swing.SwingRcpUtilities;

/**
 * Renderer for feature tiles
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class FeatureTileRenderer implements TileProvider {
	
	private final DatasetType type;
	
	private GTRenderer renderer;
	
	private boolean contextInitialized = false;
	
	private final FeaturePaintStatus status;
	
	private final FeatureSelectionProvider selection;
	
	private final boolean selectionOnly;
	
	/**
	 * Creates a new renderer
	 * 
	 * @param type the data set type
	 * @param status 
	 * @param selection 
	 * @param selectionOnly 
	 */
	public FeatureTileRenderer(final DatasetType type, final FeaturePaintStatus status,
			final FeatureSelectionProvider selection, boolean selectionOnly) {
		super();
		
		this.type = type;
		this.status = status;
		this.selection = selection;
		this.selectionOnly = selectionOnly;
		
		configureRenderer();
	}
	
	@SuppressWarnings("unchecked")
	private void configureRenderer() {
		this.renderer = new StreamingRenderer();
		Map hints = renderer.getRendererHints();
        if (hints == null) {
            hints = new HashMap();
        }
        renderer.setRendererHints(hints);
        hints.put("memoryPreloadingEnabled", Boolean.FALSE); //$NON-NLS-1$
        this.renderer.setRendererHints(hints);
    }
	
	/**
	 * Update the map context
	 * 
	 * @param crs the coordinate reference system (may be null)
	 */
	public void updateMapContext(CoordinateReferenceSystem crs) {
		//TODO would the performance be better if we create a map context for each tile?
		renderer.setContext(MapUtils.buildMapContext(crs, type, status, 
				selection.getSelectedFeatures(), selectionOnly));
		contextInitialized = true;
	}

	/**
	 * @see TileProvider#getTile(TileConstraints, int, int, int)
	 */
	@Override
	public ImageData getTile(TileConstraints constraints, int zoom, int x, int y)
			throws Exception {
		
		int width = constraints.getTileWidth();
		int height = constraints.getTileHeight();
		
		BufferedImage image;
		
		// create a compatible translucent image
		if (GraphicsEnvironment.isHeadless()) {
			image = new BufferedImage(
					width,
					height,
	                BufferedImage.TYPE_INT_ARGB);
		}
		else {
			image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		}
		
        Graphics2D graphics = image.createGraphics();
        
        try {
	        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        if (!contextInitialized) {
	        	updateMapContext(constraints.getCRS());
	        }
	        
	        renderer.paint(graphics,
	    			new Rectangle(0, 0, width, height),
	    			constraints.getTileArea(zoom, x, y));
	        
	        return SwingRcpUtilities.convertToSWT(image);
        }
        finally {
        	graphics.dispose();
        }
	}

}
