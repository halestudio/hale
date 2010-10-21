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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.models.style.StylePreferences;
import eu.esdihumboldt.hale.rcp.views.map.tiles.AbstractTilePainter;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileBackground;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileCache;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Painter for Features
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureTilePainter extends AbstractTilePainter implements TileBackground {
	
	private static final Log log = LogFactory.getLog(FeatureTilePainter.class);
	
	/**
	 * The reference data tile cache
	 */
	private final TileCache referenceCache;
	
	/**
	 * The reference data renderer
	 */
	private final FeatureTileRenderer referenceRenderer;
	
	/**
	 * The transformed data tile cache
	 */
	private final TileCache transformedCache;
	
	/**
	 * The transformed data renderer
	 */
	private final FeatureTileRenderer transformedRenderer;
	
	/**
	 * The reference data tile cache
	 */
	private final TileCache referenceSelectionCache;
	
	/**
	 * The reference data renderer
	 */
	private final FeatureTileRenderer referenceSelectionRenderer;
	
	/**
	 * The transformed data tile cache
	 */
	private final TileCache transformedSelectionCache;
	
	/**
	 * The transformed data renderer
	 */
	private final FeatureTileRenderer transformedSelectionRenderer;
	
	private final FeaturePaintStatus status;
	
	/**
	 * How the map is split
	 */
	private SplitStyle splitStyle = SplitStyle.SOURCE;
	
	private boolean invertSplit = false;
	
	/**
	 * The background color
	 */
	private RGB background = null;
	
	private final FeatureSelector selector;

	private final InstanceServiceListener instanceListener;

	private final HaleServiceListener styleListener;

	private final HaleServiceListener alignmentListener;
	
	/**
	 * Creates a Feature painter for the given control
	 * 
	 * @param canvas the control
	 */
	public FeatureTilePainter(Control canvas) {
		status = new FeaturePaintStatus();
		selector = new FeatureSelector(canvas, this);
		
		referenceRenderer = new FeatureTileRenderer(DatasetType.reference, 
				status, selector, false);
		referenceCache = new TileCache(referenceRenderer, this, true);
		
		transformedRenderer = new FeatureTileRenderer(DatasetType.transformed, 
				status, selector, false);
		transformedCache = new TileCache(transformedRenderer, this, true);
		
		referenceSelectionRenderer = new FeatureTileRenderer(DatasetType.reference, 
				status, selector, true);
		referenceSelectionCache = new TileCache(referenceSelectionRenderer, this, false);
		
		transformedSelectionRenderer = new FeatureTileRenderer(DatasetType.transformed, 
				status, selector, true);
		transformedSelectionCache = new TileCache(transformedSelectionRenderer, this, false);
		
		init(canvas, determineMapArea());
		
		final InstanceService instances = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		instances.addListener(instanceListener = new InstanceServiceListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				// ignore
			}
			
			@Override
			public void datasetChanged(DatasetType type) {
				switch (type) {
				case reference:
					if (Display.getCurrent() != null) {
						updateMap(determineMapArea());
						updateTransformation();
					}
					else {
						final Display display = PlatformUI.getWorkbench().getDisplay();
						display.syncExec(new Runnable() {
							
							@Override
							public void run() {
								updateMap(determineMapArea());
								updateTransformation();
							}
						});
					}
					break;
				case transformed:
					if (Display.getCurrent() != null) {
						resetTransformedTiles();
						refresh();
					}
					else {
						final Display display = PlatformUI.getWorkbench().getDisplay();
						display.syncExec(new Runnable() {
							
							@Override
							public void run() {
								resetTransformedTiles();
								refresh();
							}
						});
					}
					break;
				}
			}
		});
		
		final StyleService styles = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		styles.addListener(styleListener = new HaleServiceListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				synchronized (FeatureTilePainter.this) {
					if (Display.getCurrent() != null) {
						background = styles.getBackground();
						resetTiles();
						refresh();
					}
					else {
						final Display display = PlatformUI.getWorkbench().getDisplay();
						display.syncExec(new Runnable() {
							
							@Override
							public void run() {
								background = styles.getBackground();
								resetTiles();
								refresh();
							}
							
						});
					}
				}	
			}
			
		});
		
		final AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		alService.addListener(alignmentListener = new HaleServiceListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				if (Display.getCurrent() != null) {
					updateTransformation();	
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							updateTransformation();
						}
					});
				}
			}
		});
		
		referenceCache.addTileListener(this);
		transformedCache.addTileListener(this);
		
		referenceSelectionCache.addTileListener(this);
		transformedSelectionCache.addTileListener(this);
	}

	/**
	 * Update the transformed instances
	 */
	@SuppressWarnings("unchecked")
	protected void updateTransformation() {
		final InstanceService instances = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		final AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		final SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		synchronized (this) {
			FeatureCollection<FeatureType, Feature> fc_reference = 
				instances.getFeatures(DatasetType.reference);
			if (fc_reference != null && fc_reference.size() > 0 
					&& alService.getAlignment() != null 
					&& alService.getAlignment().getMap() != null 
					&& alService.getAlignment().getMap().size() > 0) {
				CstService ts = (CstService) 
					PlatformUI.getWorkbench().getService(
							CstService.class);
				instances.cleanInstances(DatasetType.transformed);
				
				Collection<SchemaElement> targetElements = schemaService.getTargetSchema();
				Set<FeatureType> fts = new HashSet<FeatureType>();
				for (SchemaElement element : targetElements) {
					FeatureType type = element.getFeatureType();
					if (type != null) {
						fts.add(type);
					}
					else {
						log.debug("No feature type for element " + element.getDisplayName());
					}
				}
				
				FeatureCollection features = ts.transform(
						fc_reference, // Input Features
						alService.getAlignment(), // Alignment
						fts);
				instances.addInstances(DatasetType.transformed, features); // target schema
			}
			else {
				instances.cleanInstances(DatasetType.transformed);
			}
			
		}
	}

	/**
	 * @return the map area
	 */
	private ReferencedEnvelope determineMapArea() {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		if (is != null) {
			FeatureCollection<FeatureType, Feature> features = is.getFeatures(DatasetType.reference);
			if (features != null && !features.isEmpty()) {
				ReferencedEnvelope env = features.getBounds();
				if (env.getCoordinateReferenceSystem() == null) {
					env = new ReferencedEnvelope(env, MapUtils.determineCRS(features));
				}
				return env;
			}
		}
		
		return null;
	}

	/**
	 * @see AbstractTilePainter#paintControl(PaintEvent)
	 */
	@Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
		
		// paint status
		GC gc = e.gc;
		
		int failed = status.getReferenceFailed() + status.getTransformedFailed();
		if (failed > 0) {
			String ident;
			if (status.getReferenceFailed() == 0) {
				ident = "transformed";
			}
			else if (status.getTransformedFailed() == 0) {
				ident = "source";
			}
			else {
				ident = "source and transformed";
			}
			String text = failed + " " + ident + " features have no default geometry";
			
			Image errorImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
			
			Color bg = gc.getBackground();
			
			Color black = new Color(gc.getDevice(), 0, 0, 0);
			Color white = new Color(gc.getDevice(), 255, 255, 255);
			try {
				gc.setBackground(white);
				gc.setForeground(black);
				
				Point textExtent = gc.textExtent(text);
				Rectangle imageExtent = (errorImage == null)?(new Rectangle(0, 0, 0, 0)):(errorImage.getBounds());
				
				int width = textExtent.x + imageExtent.width;
				int height = Math.max(textExtent.y, imageExtent.height);
				
				// draw box
				gc.fillRectangle(e.width - width - 3, e.height - height - 3, width + 2, height + 2);
				gc.drawRectangle(e.width - width - 3, e.height - height - 3, width + 2, height + 2);
				
				// draw image
				if (errorImage != null) {
					gc.drawImage(errorImage, e.width - width - 2, e.height - height - 1 + ((height - imageExtent.height) / 2));
				}
				
				// draw text
				gc.drawText(text, e.width - width - 1 + imageExtent.width, e.height - height - 2 + ((height - textExtent.y) / 2), true);
			}
			finally {
				black.dispose();
				white.dispose();
				
				gc.setBackground(bg);
			}
		}
	}

	/**
	 * @see AbstractTilePainter#paintTile(GC, int, int, int, int, int, int, int)
	 */
	@Override
	protected void paintTile(GC gc, int tileX, int tileY, int zoom, int x,
			int y, int tileWidth, int tileHeight) {
		
		boolean drawReference;
		boolean drawTransformed;
		
		Region referenceRegion = null;
		Region transformedRegion = null;
		
		int[] separator = null;
		
		final Rectangle tileRect = new Rectangle(x, y, tileWidth, tileHeight);
		final Rectangle control = getControl().getBounds();
		
		switch (splitStyle) {
		case OVERLAY:
			// draw both
			drawReference = true;
			drawTransformed = true;
			break;
		case HORIZONTAL:
			Rectangle upperHalf = new Rectangle(0, 0, control.width, control.height / 2);
			Rectangle lowerHalf = new Rectangle(0, control.height / 2, control.width, control.height - control.height / 2);
			
			drawReference = tileRect.intersects(upperHalf);
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(upperHalf);
			
			drawTransformed = tileRect.intersects(lowerHalf);
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(lowerHalf);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						0, control.height / 2 - 2,
						control.width, control.height / 2 - 2,
						control.width, control.height / 2 + 2,
						0, control.height / 2 + 2};
			}
			
			break;
		case VERTICAL:
			Rectangle leftHalf = new Rectangle(0, 0, control.width / 2, control.height);
			Rectangle rightHalf = new Rectangle(control.width / 2, 0, control.width - control.width / 2, control.height);
			
			drawReference = tileRect.intersects(leftHalf);
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(leftHalf);
			
			drawTransformed = tileRect.intersects(rightHalf);
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(rightHalf);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						control.width / 2 - 2, 0,
						control.width / 2 - 2, control.height,
						control.width / 2 + 2, control.height,
						control.width / 2 + 2, 0};
			}
			
			break;
		case DIAGONAL_UP:
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(new int[]{0, 0, control.width, 0, 0, control.height});
			drawReference = referenceRegion.intersects(tileRect);
			
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(new int[]{0, control.height, control.width, 0, control.width, control.height});
			drawTransformed = transformedRegion.intersects(tileRect);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						0, control.height,
						0, control.height - 2,
						control.width - 2, 0,
						control.width, 0,
						control.width, 2,
						2, control.height};
			}
			
			break;
		case DIAGONAL_DOWN:
			referenceRegion = new Region(gc.getDevice());
			referenceRegion.add(new int[]{0, 0, control.width, control.height, 0, control.height});
			drawReference = referenceRegion.intersects(tileRect);
			
			transformedRegion = new Region(gc.getDevice());
			transformedRegion.add(new int[]{0, 0, control.width, 0, control.width, control.height});
			drawTransformed = transformedRegion.intersects(tileRect);
			
			if (drawReference && drawTransformed) {
				separator = new int[]{
						0, 0,
						2, 0,
						control.width, control.height - 2,
						control.width, control.height,
						control.width - 2, control.height,
						0, 2};
			}
			
			break;
		case TARGET:
			// only draw transformed
			drawReference = false;
			drawTransformed = true;
			break;
		case SOURCE:
			// fall through
		default:
			// only draw reference
			drawReference = true;
			drawTransformed = false;
			break;
		}
		
		// handle inversion (only if both regions are set)
		if (referenceRegion != null && transformedRegion != null && invertSplit) {
			Region tmpRegion = referenceRegion;
			boolean tmpDraw = drawReference;
			
			referenceRegion = transformedRegion;
			drawReference = drawTransformed;
			
			transformedRegion = tmpRegion;
			drawTransformed = tmpDraw;
		}
		
		try {
			// configure GC
			gc.setAntialias(SWT.ON);
			
			// paint background
			if (drawReference || drawTransformed) {
				drawTileBackground(gc, x, y, tileWidth, tileHeight);
			}
			
			// reference
			if (drawReference) {
				drawTile(gc, referenceCache, referenceRegion, tileX, tileY, zoom, 
						x, y, tileWidth, tileHeight, false);
			}
			
			// transformed
			if (drawTransformed) {
				drawTile(gc, transformedCache, transformedRegion, tileX, tileY, zoom, 
						x, y, tileWidth, tileHeight, false);
			}
			
			// reference selection
			if (drawReference) {
				drawTile(gc, referenceSelectionCache, referenceRegion, tileX, tileY, 
						zoom, x, y, tileWidth, tileHeight, true);
			}
			
			// transformed selection
			if (drawTransformed) {
				drawTile(gc, transformedSelectionCache, transformedRegion, tileX, 
						tileY, zoom, x, y, tileWidth, tileHeight, true);
			}
			
			// separator
			if (separator != null) {
				gc.fillPolygon(separator);
			}
		}
		finally {
			if (referenceRegion != null) {
				referenceRegion.dispose();
			}
			if (transformedRegion != null) {
				transformedRegion.dispose();
			}
		}
	}

	/**
	 * Draw a tile
	 * 
	 * @param gc the graphics object
	 * @param cache the tile cache
	 * @param region the clipping region (it will be disposed)
	 * @param tileX the tile x ordinate
	 * @param tileY the tile y ordinate
	 * @param zoom the tile zoom level
	 * @param x the tile x position
	 * @param y the tile y position
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 * @param overlay if this is an overlay
	 */
	private void drawTile(GC gc, TileCache cache,
			Region region, int tileX, int tileY, int zoom, int x, int y,
			int tileWidth, int tileHeight, boolean overlay) {
		ImageData imageData;
		try {
			imageData = cache.getTile(this, zoom, tileX, tileY);
		} catch (Exception e) {
			imageData = null;
			log.error("Error getting the tile image", e); //$NON-NLS-1$
		}
		
		Image image = null;
		if (imageData != null) {
			image = new Image(gc.getDevice(), imageData);
		}
		
		try {
			Rectangle oldClipping = gc.getClipping();
			if (region != null) {
				gc.setClipping(region);
			}
			
			if (image != null) {
				gc.drawImage(image, x, y);
			}
			else if (!overlay) {
				Color bg = gc.getBackground();
				
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY));
				gc.fillRectangle(x, y, tileWidth, tileHeight);
				
				gc.setBackground(bg);
			}
			
			if (region != null) {
				gc.setClipping(oldClipping);
			}
		} finally {
			if (image != null) {
				image.dispose();
			}
		}
	}
	
	/**
	 * @see TileBackground#drawTileBackground(GC, int, int, int, int)
	 */
	@Override
	public void drawTileBackground(GC gc, int x, int y, int tileWidth, int tileHeight) {
		Color bg = gc.getBackground();
		
		Color color = new Color(gc.getDevice(), getBackground());
		gc.setBackground(color);
		gc.fillRectangle(x, y, tileWidth, tileHeight);
		color.dispose();
		
		gc.setBackground(bg);
	}

	/**
	 * @see AbstractTilePainter#resetTiles()
	 */
	@Override
	protected void resetTiles() {
		referenceRenderer.updateMapContext(getCRS());
		transformedRenderer.updateMapContext(getCRS());
		
		referenceCache.clear();
		transformedCache.clear();
		
		resetSelectionTiles();
	}
	
	private void resetSelectionTiles() {
		referenceSelectionRenderer.updateMapContext(getCRS());
		transformedSelectionRenderer.updateMapContext(getCRS());
		
		referenceSelectionCache.clear();
		transformedSelectionCache.clear();
	}

	private void resetTransformedTiles() {
		transformedRenderer.updateMapContext(getCRS());
		transformedCache.clear();

		transformedSelectionRenderer.updateMapContext(getCRS());
		transformedSelectionCache.clear();
	}

	/**
	 * Get the split style
	 * 
	 * @return the split style
	 */
	public SplitStyle getSplitStyle() {
		return splitStyle;
	}

	/**
	 * Set the split style
	 * 
	 * @param splitStyle the split style
	 */
	public void setSplitStyle(SplitStyle splitStyle) {
		this.splitStyle = splitStyle;
		
		refresh();
	}

	/**
	 * @see AbstractTilePainter#updateMap()
	 */
	@Override
	public void updateMap() {
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		background = ss.getBackground();
			
		super.updateMap();
	}

	/**
	 * @return the invertSplit
	 */
	public boolean isInvertSplit() {
		return invertSplit;
	}

	/**
	 * @param invertSplit the invertSplit to set
	 */
	public void setInvertSplit(boolean invertSplit) {
		this.invertSplit = invertSplit;
		
		refresh();
	}
	
	/**
	 * Get if the background is the default background
	 * 
	 * @return if the background is the default background
	 */
	public boolean isDefaultBackground() {
		return background == null;
	}

	/**
	 * @return the background
	 */
	private RGB getBackground() {
		if (background != null) {
			return background;
		}
		else {
			return StylePreferences.getDefaultBackground();
		}
	}

	/**
	 * @param background the background to set, <code>null</code> to reset it to
	 *   the default background
	 */
	private void setBackground(RGB background) {
		if (background == null) return;
		
		this.background = background;
		
		synchronized (this) {
			resetTiles();
			refresh();
		}
	}
	
	/**
	 * Dispose the painter
	 */
	public void dispose() {
		selector.dispose();
		
		final InstanceService instances = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		instances.removeListener(instanceListener);
		
		StyleService styles = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		styles.removeListener(styleListener);
		
		final AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		alService.removeListener(alignmentListener);
	}

	/**
	 * Repaint the selection overlay
	 */
	public void updateSelection() {
		resetSelectionTiles();
		refresh();
	}

	/**
	 * Get the selection provider
	 * 
	 * @return the selection provider or <code>null</code>
	 */
	public ISelectionProvider getSelectionProvider() {
		return selector;
	}

}
