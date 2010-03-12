package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.opengis.feature.Feature;
import org.opengis.feature.type.PropertyType;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.strtree.STRtree;

public class FeatureIndex {
	
	private final PropertyType propertyType;
	private STRtree tree = null;
	private Collection<Feature> sourceFeatures;
	
	public FeatureIndex(PropertyType pt, Collection<Feature> sourceFeatures) {
		this.propertyType = pt;
		this.sourceFeatures = sourceFeatures;
		if (Geometry.class.isAssignableFrom(pt.getBinding()))  {
			// es handelt sich um einen spatial join/aggregate
			this.tree = new STRtree(sourceFeatures.size());
			for (Feature f : sourceFeatures) {
				BoundingBox bbox = f.getBounds();
				tree.insert(new Envelope(
						bbox.getMinX(), bbox.getMaxX(), 
						bbox.getMinY(), bbox.getMaxY()), f);
			}
		}
		else {
			// TODO alphanumerisches Attribut
		}
	}
	
	private Collection<Feature> groupBy(Feature queryFeature) {
		if (queryFeature.getProperty(propertyType.getName()) == null) {
			throw new RuntimeException("You have to use a Feature that has the " +
					"Property used for this index (" 
					+ this.propertyType.getName() + ")");
		}
		
		Collection<Feature> result = new ArrayList<Feature>();
		
		if (Geometry.class.isAssignableFrom(this.propertyType.getBinding()))  {
			BoundingBox bbox = queryFeature.getBounds();
			List<?> filtered = tree.query(new Envelope(
					bbox.getMinX(), bbox.getMaxX(), 
					bbox.getMinY(), bbox.getMaxY()));
			for (Object o : filtered) {
				result.add((Feature)o);
			}
		}
		else {
			// TODO get objects for alphanumeric operations
		}
		
		return result;
	}
	
	public Collection<Feature> getQueryFeatures() {
		if (Geometry.class.isAssignableFrom(this.propertyType.getBinding()))  {
			return this.sourceFeatures;
		}
		else {
			// get objects for alphanumeric operations
			SortedSet<Feature> result = new TreeSet<Feature>(new Comparator<Feature>() {
				public int compare(Feature arg0, Feature arg1) {
					// TODO Auto-generated method stub
					// Hiermit für Bedingungen wenn Attr1 = Attr2
					//use hasCode on value of property in o1 and 02
					return 0;
				}});	
			return result;
		}
	}

}
