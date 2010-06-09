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
	
	
	private String onAttributeName = null;
	private boolean spatial;
	private String joinRule = null;
	
	
	public FeatureSpatialJoiner(String onAttributeName, boolean spatial, String joinRule) {
		this.onAttributeName = onAttributeName;
		this.spatial = spatial;
		this.joinRule = joinRule;
		
		
		String[] jRule = joinRule.split(":");
		if (jRule[0].equals("join")) {
			if (jRule[1].startsWith("intersects")) {
				this.joinRule = FeatureSpatialJoiner.INTERSECTS;
			}
			else if (jRule[1].startsWith("dWithin")) {
				this.joinRule = FeatureSpatialJoiner.DWITHIN;
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
	
	
	public List<Feature> join (List<Collection<? extends Feature>> sources, FeatureType targetType){
		if (spatial) return spatialJoin (sources, targetType);
		if (!spatial) return join (sources, targetType);
		else throw new RuntimeException("Error specifying spatial or non spatial join");
	}
	
	
	public List<Feature> spatialJoin(List<Collection<? extends Feature>> sources, FeatureType targetType) {
		
			List<Feature> result = new ArrayList<Feature>();
		
	     
			FeatureCollection sourceFeatures0 = FeatureCollections.newCollection(); 
			for (Feature f : sources.get(0)){
				sourceFeatures0.add(f);
			}
			FeatureSource source0 = DataUtilities.source( sourceFeatures0 );
			

		 	FeatureType schema0 = sourceFeatures0.getSchema();
	        String typeName0 = schema0.getName().getLocalPart();
	        String geomName0 = schema0.getGeometryDescriptor().getLocalName();
	        
	
	        
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
	        		try {
	        			Geometry geometry = (Geometry) feature.getDefaultGeometry();
	        			if( !geometry.isValid()) {
	        				// skip bad data
	        				continue;
	        			}
	        			Filter innerFilter = ff.intersects( ff.property(geomName1), ff.literal( geometry ));
	        			//Filter innerFilter = ff.dwithin(ff.property(geomName2), ff.literal( geometry ),1.0,"km");
	        			//Filter innerFilter = ff.beyond(ff.property(geomName2), ff.literal( geometry ),1.0,"km");
	        			//Filter innerFilter = ff.not( ff.disjoint(ff.property(geomName2), ff.literal( geometry )) );
                    
	        			DefaultQuery innerQuery = new DefaultQuery( typeName1, innerFilter, DefaultQuery.NO_NAMES );
	        			FeatureCollection<FeatureType, Feature> join = source1.getFeatures( innerQuery );
	        			
	        		}
	        		catch( Exception skipBadData ){
	        		}
	        	}
	        }
	        finally {
	        	iterator.close();            
	        }

        return null;
	}

}





