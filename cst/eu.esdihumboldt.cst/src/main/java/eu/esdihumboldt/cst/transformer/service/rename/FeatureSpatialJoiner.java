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
package eu.esdihumboldt.cst.transformer.service.rename;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Geometry;

/**
* @author Ulrich Schaeffler 
* @partner 14 / TUM
* @version $Id$ 
**/

public class FeatureSpatialJoiner {
	public static final String INTERSECTS ="intersects";
	public static final String DWITHIN ="dWithin";
	public static final String BEYOND ="beyond";
	public static final String NOT ="not";	
	
	private String onAttributeName0 = null;
	private String onAttributeName1 = null;
	private boolean spatial;
	private String joinRule = null;
	private double value;
	private String UOM = null;
	
	
	public FeatureSpatialJoiner(String onAttributeName0, String onAttributename1, boolean spatial, String joinRule) {
		this.onAttributeName0 = onAttributeName0;
		this.onAttributeName1 = onAttributeName1;
		this.spatial = spatial;
		this.joinRule = joinRule;
		
		
		String[] jRule = joinRule.split(":");
		if (jRule[0].equals("join")) {
			if (jRule[1].startsWith("intersects")) {
				this.joinRule = FeatureSpatialJoiner.INTERSECTS;
			}
			else if (jRule[1].startsWith("dWithin")) {
				this.joinRule = FeatureSpatialJoiner.DWITHIN;
				this.value = Double.valueOf(jRule[1].substring(
						jRule[1].indexOf("(") + 1, 
						jRule[1].indexOf(";")));
				this.UOM = jRule[1].substring(
						jRule[1].indexOf(";") + 1, 
						jRule[1].indexOf(")"));
			}
			else if (jRule[1].startsWith("beyond")) {
				this.joinRule = FeatureSpatialJoiner.BEYOND;
				this.value = Double.valueOf(jRule[1].substring(
						jRule[1].indexOf("(") + 1, 
						jRule[1].indexOf(";")));
				this.UOM = jRule[1].substring(
						jRule[1].indexOf(";") + 1, 
						jRule[1].indexOf(")"));
			}
			else if (jRule[1].startsWith("not")) {
				this.joinRule = FeatureSpatialJoiner.NOT;
				this.value = Double.valueOf(jRule[1].substring(
						jRule[1].indexOf("(") + 1, 
						jRule[1].indexOf(";")));
				this.UOM = jRule[1].substring(
						jRule[1].indexOf(";") + 1, 
						jRule[1].indexOf(")"));
			}
			else{
				throw new RuntimeException (jRule[1] + " is not a valid join rule.");
			}

		}
		else {
			throw new RuntimeException("You can only create a " +
				"Feature Joiner from a valid join rule.");
		}

	}
	
	
	public List<Feature> join (List<Collection<Feature>> sources, FeatureType targetType){
		if (spatial){
			System.out.println("spatial");
			return spatialJoin (sources, targetType);
		}
		if (!spatial) return alphaJoin (sources, targetType);
		else throw new RuntimeException("Error specifying spatial or non spatial join");
	}
	
	private List<Feature> alphaJoin (List<Collection<Feature>> sources, FeatureType targetType){
		List<Feature> result = new ArrayList<Feature>();
		
		FeatureCollection sourceFeatures0 = FeatureCollections.newCollection(); 
		for (Feature f : sources.get(0)){
			sourceFeatures0.add(f);
		}
		FeatureSource source0 = DataUtilities.source( sourceFeatures0 );
	 	FeatureType schema0 = sourceFeatures0.getSchema();
        String typeName0 = schema0.getName().getLocalPart();
		FeatureCollection sourceFeatures1 = FeatureCollections.newCollection(); 
		for (Feature f : sources.get(1)){
			sourceFeatures1.add(f);
		}
		FeatureSource source1 = DataUtilities.source( sourceFeatures1 );
	 	FeatureType schema1 = sourceFeatures1.getSchema();
        String typeName1 = schema1.getName().getLocalPart();
        
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);        
        DefaultQuery outerSelection = new DefaultQuery( typeName0, Filter.INCLUDE, new String[]{ this.onAttributeName0 } );        
        FeatureCollection<SimpleFeatureType, SimpleFeature> outerFeatures = null;
        try {
        	outerFeatures = source0.getFeatures( outerSelection );
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        FeatureIterator<SimpleFeature> iterator = outerFeatures.features();
        try {
        	while( iterator.hasNext() ){
        		SimpleFeature feature = (SimpleFeature) iterator.next();
        		System.out.println("erstes outer "+feature) ;
        		try {

        			Filter innerFilter=  ff.equal( ff.property(this.onAttributeName1), ff.literal( feature.getAttribute(this.onAttributeName0) ));
        			
        			DefaultQuery innerQuery = new DefaultQuery( typeName1, innerFilter, new String[]{ this.onAttributeName1 } );
        			FeatureCollection join = source1.getFeatures( innerQuery );
        			System.out.println("Join Size " +join.size());
        			for ( Iterator<Feature> i = join.iterator(); i.hasNext(); )
        			{
        				System.out.println("Add result "+ i.next());
        			  result.add(i.next());
        			}

        		}
        		catch( Exception skipBadData ){
        		}
        	}
        }
        finally {
        	iterator.close();            
        }

    return result;
        
	}
	
	
	
	private List<Feature> spatialJoin(List<Collection<Feature>> sources, FeatureType targetType) {
		
			List<Feature> result = new ArrayList<Feature>();
			
			FeatureCollection sourceFeatures0 = FeatureCollections.newCollection(); 
			for (Feature f : sources.get(0)){
				sourceFeatures0.add(f);
			}
			FeatureSource source0 = DataUtilities.source( sourceFeatures0 );
		 	FeatureType schema0 = sourceFeatures0.getSchema();
	        String typeName0 = schema0.getName().getLocalPart();
	        String geomName0 = schema0.getGeometryDescriptor().getLocalName();
	        System.out.println("GeomName0 " + geomName0);
	
	        
			FeatureCollection sourceFeatures1 = FeatureCollections.newCollection(); 
			for (Feature f : sources.get(1)){
				sourceFeatures1.add(f);
			}
			FeatureSource source1 = DataUtilities.source( sourceFeatures1 );
		 	FeatureType schema1 = sourceFeatures1.getSchema();
	        String typeName1 = schema1.getName().getLocalPart();
	        String geomName1 = schema1.getGeometryDescriptor().getLocalName();   
		
	        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);        
	        DefaultQuery outerGeometry = new DefaultQuery( typeName0, Filter.INCLUDE, new String[]{ geomName0 } );        
	        FeatureCollection<SimpleFeatureType, SimpleFeature> outerFeatures = null;
	        try {
	        	outerFeatures = source0.getFeatures( outerGeometry );
	        } catch (IOException e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
	        FeatureIterator<SimpleFeature> iterator = outerFeatures.features();
	        try {
	        	while( iterator.hasNext() ){
	        		SimpleFeature feature = (SimpleFeature) iterator.next();
	        		System.out.println("erstes outer "+feature) ;
	        		try {
	        			Geometry geometry = (Geometry) feature.getDefaultGeometry();
	    				System.out.println("DefGeom " + geometry);
	        			Filter innerFilter= null;
	        			if (this.joinRule.equals(FeatureSpatialJoiner.INTERSECTS))innerFilter= ff.intersects( ff.property(geomName1), ff.literal( geometry ));
	        			else if (this.joinRule.equals(FeatureSpatialJoiner.DWITHIN))innerFilter= ff.dwithin(ff.property(geomName1), ff.literal( geometry ),1.0,"km");
	        			else if (this.joinRule.equals(FeatureSpatialJoiner.BEYOND))innerFilter=  ff.beyond(ff.property(geomName1), ff.literal( geometry ),1.0,"km");
	        			else if (this.joinRule.equals(FeatureSpatialJoiner.NOT))innerFilter=  ff.not( ff.disjoint(ff.property(geomName1), ff.literal( geometry )) );
                    
	        			DefaultQuery innerQuery = new DefaultQuery( typeName1, innerFilter, new String[]{ geomName1 } );
	        			FeatureCollection join = source1.getFeatures( innerQuery );
	        			System.out.println("Join Size " +join.size());
	        			for ( Iterator<Feature> i = join.iterator(); i.hasNext(); )
	        			{
	        				System.out.println("Add result "+ i.next());
	        			  result.add(i.next());
	        			}

	        		}
	        		catch( Exception skipBadData ){
	        		}
	        	}
	        }
	        finally {
	        	iterator.close();            
	        }

        return result;
	}

}





