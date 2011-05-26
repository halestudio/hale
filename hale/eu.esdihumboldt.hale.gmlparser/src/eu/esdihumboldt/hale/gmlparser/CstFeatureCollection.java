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

package eu.esdihumboldt.hale.gmlparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.geotools.data.store.FilteringFeatureCollection;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.collection.FeatureIteratorImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.BoundingBox;
import org.opengis.util.ProgressListener;

/**
 * {@link FeatureCollection} implementation used in the CstService and in HALE,
 * with some specific capabilites as required there. Among other things, it can 
 * be typed with generic, allows Features of multiple FeatureTypes and contains 
 * some optimizations.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class CstFeatureCollection 
	extends ArrayList<Feature>
	implements FeatureCollection<FeatureType, Feature> {

	private static final long serialVersionUID = 4747629013800309132L;
	
	private ReferencedEnvelope bounds = null;

	private String id;

	/**
     * @see org.geotools.feature.FeatureCollection#accepts(org.opengis.feature.FeatureVisitor,
     *      org.opengis.util.ProgressListener)
     */
	@Override
	public void accepts(FeatureVisitor visitor, ProgressListener progress) throws IOException {
        for (Feature feature : this) {
            visitor.visit(feature);
        }
    }

	/**
     * @see org.geotools.feature.FeatureCollection#addAll(org.geotools.feature.FeatureCollection)
     */
	@Override
	public boolean addAll(
			FeatureCollection<? extends FeatureType, ? extends Feature> resource) {
		boolean changed = false;
        for (Iterator<? extends Feature> iterator = resource.iterator(); iterator.hasNext();) {
            if (this.add(iterator.next())) {
                changed = true;
            }
        }
        return changed;
	}

    /**
     * @see org.geotools.feature.FeatureCollection#features()
     */
	@Override
	public FeatureIterator<Feature> features() {
		return new FeatureIteratorImpl<Feature>(this);
	}

	/**
	 * will return null if no Features have been added yet.
	 */
	@Override
	public ReferencedEnvelope getBounds() {
		synchronized (this) {
			if (this.bounds == null && this.size() > 0) {
				this.bounds = new ReferencedEnvelope();
				for (Feature f : this) {
					BoundingBox featureBounds = f.getBounds();
	                if ( ! featureBounds.isEmpty() ) {
	                    this.bounds.include(featureBounds);
	                }
				}
			}
		}
		return this.bounds;
	}

	/**
	 * @see org.geotools.feature.FeatureCollection#getID()
	 */
	@Override
	public String getID() {
		return this.id;
	}

	/**
	 * returns the {@link FeatureType} of the first Feature added to this 
	 * {@link CstFeatureCollection}.
	 */
	@Override
	public FeatureType getSchema() {
		FeatureType result = null;
		if (this.size() > 0) {
			result = this.get(0).getType();
		}
		return result;
	}

	/**
     * Unsupported operation.
     * 
     * @see org.geotools.feature.FeatureCollection#sort(org.opengis.filter.sort.SortBy)
     */
    @Override
	public FeatureCollection<FeatureType, Feature> sort(SortBy order) {
        throw new UnsupportedOperationException();
    }

	/**
	 * @see org.geotools.feature.FeatureCollection#subCollection(org.opengis.filter.Filter)
	 */
	@Override
	public FeatureCollection<FeatureType, Feature> subCollection(Filter filter) {
		return new FilteringFeatureCollection<FeatureType, Feature>(this, filter);
	}
	

	@Override
	public void addListener(CollectionListener listener)
			throws NullPointerException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeListener(CollectionListener listener)
		throws NullPointerException {
		// TODO Auto-generated method stub
	
	}
	
	/**
     * Nothing to close.
     * 
     * @see org.geotools.feature.FeatureCollection#close(org.geotools.feature.FeatureIterator)
     */
    @Override
	public void close(FeatureIterator<Feature> close) {
        // do nothing
    }

    /**
     * Nothing to close.
     * 
     * @see org.geotools.feature.FeatureCollection#close(java.util.Iterator)
     */
    @Override
	public void close(Iterator<Feature> close) {
        // do nothing
    }

	/**
     * @see org.geotools.feature.FeatureCollection#purge()
     */
    @Override
	public void purge() {
        // do nothing
    }

}
