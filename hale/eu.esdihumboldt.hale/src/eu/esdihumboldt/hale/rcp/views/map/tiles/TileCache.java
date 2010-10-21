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
package eu.esdihumboldt.hale.rcp.views.map.tiles;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.rcp.views.map.Messages;

import gnu.trove.TIntObjectHashMap;

/**
 * Cache for tile images
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TileCache implements TileProvider {
	
	private static final Log log = LogFactory.getLog(TileCache.class);
	
	/**
	 * Tile status listener interface
	 */
	public interface TileListener {

		/**
		 * Called when a tile has been loaded
		 * 
		 * @param zoom the tile zoom level
		 * @param x the tile x ordinate
		 * @param y the tile y ordinate
		 */
		public void tileLoaded(int zoom, int x, int y);
		
	}

	private static final ImageData EMPTY_IMAGE = new ImageData(1, 1, 1, new PaletteData(1, 1, 1));
	
	private final TIntObjectHashMap<TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>>> cache = new TIntObjectHashMap<TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>>>();
	
	private final TileProvider tileProvider;
	
	private final TileBackground tileBackground;
	
	private final Set<TileListener> listeners = new HashSet<TileListener>();

	private ISchedulingRule exclusiveRule = new ISchedulingRule() {
		
		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return this == rule;
		}
		
		@Override
		public boolean contains(ISchedulingRule rule) {
			return this == rule;
		}
	};
	
	/**
	 * A reference to the main image for it not to be garbaged
	 */
	private ImageData mainImage;
	
	private final boolean useLoadingImage;
	
	/**
	 * Creates a tile cache for the given tile provider
	 * 
	 * @param tileProvider the tile provider
	 * @param tileBackground the tile background painter
	 * @param useLoadingImage if a (non-transparent) loading image shall be
	 *   tried to calculate from other tiles
	 */
	public TileCache(TileProvider tileProvider, TileBackground tileBackground,
			boolean useLoadingImage) {
		super();
		
		this.tileProvider = tileProvider;
		this.tileBackground = tileBackground;
		this.useLoadingImage = useLoadingImage;
	}
	
	/**
	 * Adds a tile listener
	 * 
	 * @param listener the tile listener
	 */
	public void addTileListener(TileListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Removes a tile listener
	 * 
	 * @param listener the tile listener
	 */
	public void removeTileListener(TileListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Notify listeners that a tile has been loaded
	 * 
	 * @param zoom the tile zoom level
	 * @param x the tile x ordinate
	 * @param y the tile y ordinate
	 */
	protected void notifyTileLoaded(int zoom, int x, int y) {
		synchronized (listeners) {
			for (TileListener listener : listeners) {
				listener.tileLoaded(zoom, x, y);
			}
		}
	}

	/**
	 * @see TileProvider#getTile(TileConstraints, int, int, int)
	 */
	@Override
	public ImageData getTile(final TileConstraints constraints, final int zoom,
			final int x, final int y) throws Exception {
		
		synchronized (cache) {
			ImageData img = null;
			TIntObjectHashMap<SoftReference<ImageData>> xCache = null;
			
			if (zoom > 0) {
				TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>> zoomCache = cache.get(zoom);
				
				if (zoomCache == null) {
					zoomCache = new TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>>();
					cache.put(zoom, zoomCache);
				}
				
				xCache = zoomCache.get(x);
				
				if (xCache == null) {
					xCache = new TIntObjectHashMap<SoftReference<ImageData>>();
					zoomCache.put(x, xCache);
				}
				
				SoftReference<ImageData> imgCache = xCache.get(y);
				
				if (imgCache != null) {
					img = imgCache.get();
				}
			}
			else {
				img = mainImage;
			}
			
			if (img == EMPTY_IMAGE) {
				return null;
			}
			else if (img != null) {
				return img;
			}
			else {
				// create job for tile loading
				final TIntObjectHashMap<SoftReference<ImageData>> jobCache = xCache;
				final Display display = Display.getCurrent();
				
				Job tileJob = new Job(Messages.TileCache_JobLoadTitle) {
					
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							ImageData img = tileProvider.getTile(constraints, zoom, x, y);
							synchronized (cache) {
								if (img == null) {
									if (jobCache != null) {
										jobCache.put(y, new SoftReference<ImageData>(EMPTY_IMAGE));
									}
									else {
										mainImage = null;
									}
								}
								else {
									if (jobCache != null) {
										jobCache.put(y, new SoftReference<ImageData>(img));
									}
									else {
										mainImage = img;
									}
								}
							}
							return Status.OK_STATUS;
						} catch (Exception e) {
							log.error("Error drawing tile image", e); //$NON-NLS-1$
							return Status.CANCEL_STATUS;
						}
					}
					
				};
				
				tileJob.setSystem(true);
				tileJob.setRule(exclusiveRule);
				
				tileJob.addJobChangeListener(new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						if (event.getResult() == Status.OK_STATUS) {
							display.asyncExec(new Runnable() {
								
								@Override
								public void run() {
									notifyTileLoaded(zoom, x, y);
								}
							});
							
						}
					}
					
				});
				
				tileJob.schedule();
					
				// determine loading image
				ImageData loadingImage = (useLoadingImage)?(getLoadingImage(constraints, zoom, x, y)):(null);
				
				// put loading (dummy) image in cache
				if (xCache != null) {
					xCache.put(y, new SoftReference<ImageData>((loadingImage != null)?(loadingImage):(EMPTY_IMAGE)));
				}
				
				return loadingImage;
			}
		}
	}

	/**
	 * Get the loading image for a tile
	 * 
	 * @param constraints the tile constraints
	 * @param zoom the tile zoom level
	 * @param x the tile x ordinate
	 * @param y the tile y ordinate
	 * @return the loading image (may be null)
	 */
	protected ImageData getLoadingImage(TileConstraints constraints, int zoom,
			int x, int y) {
		
		if (tileBackground == null)
			return null;
		
		int partWidth = constraints.getTileWidth();
		int partHeight = constraints.getTileHeight();
		
		int tileX = x;
		int tileY = y;
		
		while (zoom > 0) {
			partWidth /= 2;
			partHeight /= 2;
			
			zoom -= 1;
			
			tileX /= 2;
			tileY /= 2;
			
			int partX = (x - 2*tileX)* partWidth;
			int partY = (y - 2*tileY) * partHeight;
			
			
			if (zoom == 0) {
				return drawLoadingImage(mainImage, constraints, partX, partY, partWidth, partHeight);
			}
			else {
				TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>> zoomCache = cache.get(zoom);
				
				if (zoomCache != null) {
					TIntObjectHashMap<SoftReference<ImageData>> xCache = zoomCache.get(tileX);
					
					if (xCache != null) {
						SoftReference<ImageData> imgCache = xCache.get(tileY);
						
						if (imgCache != null) {
							ImageData img = imgCache.get();
							if (img != null) {
								return drawLoadingImage(img, constraints, partX, partY, partWidth, partHeight);
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Draw a loading image
	 * 
	 * @param img
	 * @param constraints 
	 * @param partX 
	 * @param partY 
	 * @param partWidth 
	 * @param partHeight 
	 * 
	 * @return the loading image's image data or <code>null</code>
	 */
	private ImageData drawLoadingImage(ImageData img, TileConstraints constraints,
			int partX, int partY, int partWidth, int partHeight) {
		if (img == null)
			return null;
		
		final int width = constraints.getTileWidth();
		final int height = constraints.getTileHeight();
		
		Image sourceImage = new Image(Display.getCurrent(), img);
		Image drawImage = new Image(Display.getCurrent(), width, height);
		
		final GC gc = new GC(drawImage);
		
		try {
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			
			/*
			 * FIXME
			 * Drawing on a transparent image didn't yield the expected results
			 * (either the image was still fully transparent or the content was
			 * visible but pixels that should be transparent were black/white)
			 * 
			 * Solution: draw background first (see below)
			 * Problem: loading images for transformed data in overlay mode
			 *   will paint over reference data tiles 
			 */
			tileBackground.drawTileBackground(gc, 0, 0, width, height);
			
			try {
				gc.drawImage(
						sourceImage,
						partX,
						partY,
						partWidth,
						partHeight,
						0,
						0,
						width,
						height);
			} catch (IllegalArgumentException e) {
				log.debug("Error drawing loading image", e);
			}
			
			return drawImage.getImageData();
		}
		finally {
			gc.dispose();
			sourceImage.dispose();
			drawImage.dispose();
		}
	}

	/**
	 * Resets all cached tiles
	 */
	public void clear() {
		synchronized (cache) {
			cache.clear();
			mainImage = null;
		}
	}

}
