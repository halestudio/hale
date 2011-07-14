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

package eu.esdihumboldt.hale.ui.views.map;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Feature selector
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FeatureSelector implements FeatureSelectionProvider, MouseListener, ISelectionProvider {
	
	private static final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
	
	private static final int SELECTION_BUFFER = 3;

	private final Control mapControl;
	
	private final FeatureTilePainter mapPainter;
	
	private Set<InstanceReference> selectedFeatures = new HashSet<InstanceReference>();
	
	private boolean lastWasDown = false;
	
	private ISelection selection = null;
	
	private final Set<ISelectionChangedListener> selectionListeners = new HashSet<ISelectionChangedListener>();
	
	private int lastX;
	
	private int lastY;

	/**
	 * Constructor
	 * 
	 * @param mapControl the map control
	 * @param mapPainter the map painter
	 */
	public FeatureSelector(Control mapControl, FeatureTilePainter mapPainter) {
		super();
		this.mapControl = mapControl;
		this.mapPainter = mapPainter;
		
		mapControl.addMouseListener(this);
	}

	/**
	 * @see MouseAdapter#mouseDown(MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent e) {
		lastX = e.x;
		lastY = e.y;
		
		lastWasDown = true;
	}

	/**
	 * @see MouseAdapter#mouseUp(MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent e) {
		if (lastWasDown && lastX == e.x && lastY == e.y) {
			// only react on clicks, not on drags
			
			selectFeatures(e.x, e.y, false);
		}
		
		lastWasDown = false;
	}

	/**
	 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// ignore - selectFeatures(e.x, e.y, false);
	}

	/**
	 * Select the features at the given position
	 * 
	 * @param x the canvas pixel x ordinate
	 * @param y the canvas pixel y ordinate
	 * @param add if the features shall be added current selection or replace it 
	 */
	protected void selectFeatures(int x, int y, boolean add) {
		Set<InstanceReference> ids = getFeatures(x, y);
		if (add) {
			selectedFeatures.addAll(ids);
		}
		else {
			selectedFeatures = ids;
		}
		
		//System.out.println(Arrays.toString(selectedFeatures.toArray()));
		
		updateSelection();
	}
	
	/**
	 * Get the features at the given position
	 * 
	 * @param x the canvas pixel x ordinate
	 * @param y the canvas pixel y ordinate
	 * 
	 * @return the ids of the selected features 
	 */
	protected Set<InstanceReference> getFeatures(int x, int y) {
		int xMin = x - SELECTION_BUFFER;
		int yMin = y - SELECTION_BUFFER;
		int xMax = x + SELECTION_BUFFER;
		int yMax = y + SELECTION_BUFFER;
		
		Point2D p1 = mapPainter.toGeoCoordinates(xMax, yMax);
		Point2D p2 = mapPainter.toGeoCoordinates(xMin, yMin);

		Set<InstanceReference> result = new HashSet<InstanceReference>();
		
		if (p2 != null && p1 != null) {
			ReferencedEnvelope bbox = new ReferencedEnvelope(
					Math.min(p2.getX(), p1.getX()), 
					Math.max(p2.getX(), p1.getX()), 
					Math.min(p2.getY(), p1.getY()), 
					Math.max(p2.getY(), p1.getY()), 
					mapPainter.getCRS());
			
			// source
			selectFeatures(bbox, DataSet.SOURCE, result);
			// target
			selectFeatures(bbox, DataSet.TRANSFORMED, result);
		}

		return result;
	}

	private void selectFeatures(ReferencedEnvelope bbox, DataSet dataSet, Set<InstanceReference> result) {
		InstanceService instances = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		FeatureType ft = MapUtils.getGeometryFeatureType();
		if (ft != null && ft.getGeometryDescriptor() != null) {
			String geometryAtrtribute = ft.getGeometryDescriptor().getLocalName();
			
			Filter geometryFilter = ff.intersects(ff.property(geometryAtrtribute), ff.literal(bbox));
			FeatureCollection<SimpleFeatureType, SimpleFeature> all = MapUtils.getFeatures(dataSet);
			if (all != null) {
				FeatureCollection<SimpleFeatureType, SimpleFeature> matches = all.subCollection(geometryFilter);
				
				Iterator<SimpleFeature> it = matches.iterator();
				while (it.hasNext()) {
					Feature feature = it.next();
					result.add((InstanceReference) feature.getProperty(MapUtils.REFERENCE_PROPERTY).getValue());
				}
			}
		}
	}

	/**
	 * @see FeatureSelectionProvider#getSelectedFeatures()
	 */
	@Override
	public Set<InstanceReference> getSelectedFeatures() {
		return new HashSet<InstanceReference>(selectedFeatures);
	}

	/**
	 * Clean up
	 */
	public void dispose() {
		if (!mapControl.isDisposed()) {
			mapControl.removeMouseListener(this);
		}
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	/**
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return selection;
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
	}
	
	/**
	 * Update the selection, notify any listeners and repaint the selection in the map
	 */
	protected void updateSelection() {
		selection = new StructuredSelection(new ArrayList<InstanceReference>(selectedFeatures));

		SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
		for (ISelectionChangedListener listener : selectionListeners) {
			listener.selectionChanged(event );
		}
		
		mapPainter.updateSelection();
	}
	
}
