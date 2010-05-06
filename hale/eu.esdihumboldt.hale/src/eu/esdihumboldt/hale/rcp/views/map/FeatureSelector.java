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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.views.map.tiles.AbstractTilePainter;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureSelector implements FeatureSelectionProvider, MouseListener {
	
	private static final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
	
	private static final int SELECTION_BUFFER = 2;

	private final Control mapControl;
	
	private final AbstractTilePainter mapPainter;
	
	private Set<FeatureId> selectedFeatures = new HashSet<FeatureId>();
	
	private boolean lastWasDown = false;
	
	private int lastX;
	
	private int lastY;

	/**
	 * @param mapControl
	 * @param mapPainter
	 */
	public FeatureSelector(Control mapControl, AbstractTilePainter mapPainter) {
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
	 * @param x
	 * @param y
	 * @param add if the features shall be added current selection or replace it 
	 */
	protected void selectFeatures(int x, int y, boolean add) {
		Set<FeatureId> ids = getFeatures(x, y);
		if (add) {
			selectedFeatures.addAll(ids);
		}
		else {
			selectedFeatures = ids;
		}
		
		//System.out.println(Arrays.toString(selectedFeatures.toArray()));
		
		mapPainter.updateMap();
	}
	
	/**
	 * Get the features at the given position
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return the ids of the selected features 
	 */
	protected Set<FeatureId> getFeatures(int x, int y) {
		int xMin = x - SELECTION_BUFFER;
		int yMin = y - SELECTION_BUFFER;
		int xMax = x + SELECTION_BUFFER;
		int yMax = y + SELECTION_BUFFER;
		
		Point2D p1 = mapPainter.toGeoCoordinates(xMax, yMax);
		Point2D p2 = mapPainter.toGeoCoordinates(xMin, yMin);

		Set<FeatureId> result = new HashSet<FeatureId>();
		
		if (p2 != null && p1 != null) {
			ReferencedEnvelope bbox = new ReferencedEnvelope(
					Math.min(p2.getX(), p1.getX()), 
					Math.max(p2.getX(), p1.getX()), 
					Math.min(p2.getY(), p1.getY()), 
					Math.max(p2.getY(), p1.getY()), 
					mapPainter.getCRS());
			
			// source
			selectFeatures(bbox, true, result);
			// target
			selectFeatures(bbox, false, result);
		}

		return result;
	}

	private void selectFeatures(ReferencedEnvelope bbox, boolean source, Set<FeatureId> result) {
		DatasetType datasetType = (source)?(DatasetType.reference):(DatasetType.transformed);
		SchemaType schemaType = (source)?(SchemaType.SOURCE):(SchemaType.TARGET);
		
		InstanceService instances = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		SchemaService schemas = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		Collection<SchemaElement> schema = schemas.getSchema(schemaType);
		for (SchemaElement element : schema) {
			FeatureType ft = element.getFeatureType();
			if (ft != null && ft.getGeometryDescriptor() != null) {
				String geometryAtrtribute = ft.getGeometryDescriptor().getLocalName();
				
				Filter geometryFilter = ff.intersects(ff.property(geometryAtrtribute), ff.literal(bbox));
				FeatureCollection<FeatureType, Feature> all = instances.getFeatures(datasetType);
				if (all != null) {
					FeatureCollection<FeatureType, Feature> matches = all.subCollection(geometryFilter);
					
					Iterator<Feature> it = matches.iterator();
					while (it.hasNext()) {
						Feature feature = it.next();
						//TODO check matching feature type?
						result.add(feature.getIdentifier());
					}
				}
			}
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.rcp.views.map.FeatureSelectionProvider#getSelectedFeatures()
	 */
	public Set<FeatureId> getSelectedFeatures() {
		return new HashSet<FeatureId>(selectedFeatures);
	}

	/**
	 * Clean up
	 */
	public void dispose() {
		if (!mapControl.isDisposed()) {
			mapControl.removeMouseListener(this);
		}
	}
	
}
