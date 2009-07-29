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
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

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
	 * Creates a tile cache for the given tile provider
	 * 
	 * @param tileProvider the tile provider
	 */
	public TileCache(TileProvider tileProvider) {
		super();
		
		this.tileProvider = tileProvider;
	}
	
	/**
	 * Adds a tile listener
	 * 
	 * @param listener the tile listener
	 */
	public void addTileListener(TileListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a tile listener
	 * 
	 * @param listener the tile listener
	 */
	public void removeTileListener(TileListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyTileLoaded(int zoom, int x, int y) {
		for (TileListener listener : listeners) {
			listener.tileLoaded(zoom, x, y);
		}
	}

	/**
	 * @see TileProvider#getTile(TileConstraints, int, int, int)
	 */
	@Override
	public ImageData getTile(final TileConstraints constraints, final int zoom,
			final int x, final int y) throws Exception {
		
		synchronized (cache) {
			TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>> zoomCache = cache.get(zoom);
			
			if (zoomCache == null) {
				zoomCache = new TIntObjectHashMap<TIntObjectHashMap<SoftReference<ImageData>>>();
				cache.put(zoom, zoomCache);
			}
			
			TIntObjectHashMap<SoftReference<ImageData>> xCache = zoomCache.get(x);
			
			if (xCache == null) {
				xCache = new TIntObjectHashMap<SoftReference<ImageData>>();
				zoomCache.put(x, xCache);
			}
			
			SoftReference<ImageData> imgCache = xCache.get(y);
			ImageData img = null;
			
			if (imgCache != null) {
				img = imgCache.get();
			}
			
			if (img == EMPTY_IMAGE) {
				return null;
			}
			else if (img != null) {
				return img;
			}
			else {
				// put dummy image in cache
				xCache.put(y, new SoftReference<ImageData>(EMPTY_IMAGE));
				
				// create job for tile loading
				final TIntObjectHashMap<SoftReference<ImageData>> jobCache = xCache;
				final Display display = Display.getCurrent();
				
				Job tileJob = new Job("Loading tile") {
					
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							ImageData img = tileProvider.getTile(constraints, zoom, x, y);
							if (img == null) {
								jobCache.put(y, new SoftReference<ImageData>(EMPTY_IMAGE));
							}
							else {
								jobCache.put(y, new SoftReference<ImageData>(img));
							}
							return Status.OK_STATUS;
						} catch (Exception e) {
							log.error("Error drawing tile image", e);
							return Status.CANCEL_STATUS;
						}
					}
					
				};
				
				tileJob.setSystem(true);
				tileJob.setRule(exclusiveRule );
				
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
					
				// return loading image
				return getLoadingImage(constraints, zoom, x, y);
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
		return null;
	}

	/**
	 * Resets all cached tiles
	 */
	public void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}

}
