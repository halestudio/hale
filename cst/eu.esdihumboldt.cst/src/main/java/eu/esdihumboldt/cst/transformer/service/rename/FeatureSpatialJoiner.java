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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.cst.transformer.Messages;
/**
* @author Ulrich Schaeffler 
* @partner 14 / TUM
* @version $Id$ 
**/

public class FeatureSpatialJoiner {
	public static final String INTERSECTS ="intersects"; //$NON-NLS-1$
	public static final String DWITHIN ="dWithin"; //$NON-NLS-1$
	public static final String BEYOND ="beyond"; //$NON-NLS-1$
	public static final String NOT ="not";	 //$NON-NLS-1$
	
	private String onAttributeName0 = null;
	private String onAttributeName1 = null;
	private boolean spatial;
	private String joinRule = null;
	private double value;
	private String UOM = null;
	
	
	public FeatureSpatialJoiner(String onAttributename0, String onAttributename1, boolean spatial, String joinRule) {
		this.onAttributeName0 = onAttributename0;
		this.onAttributeName1 = onAttributename1;
		this.spatial = spatial;
		this.joinRule = joinRule;
		
		//TODO:Repalca join with enum
		if (joinRule.startsWith("join")){ //$NON-NLS-1$
			if (joinRule.contains(":")){ //$NON-NLS-1$
				String[] jRule = joinRule.split(":"); //$NON-NLS-1$
				if (jRule[1].startsWith("intersects")) { //$NON-NLS-1$
					this.joinRule = FeatureSpatialJoiner.INTERSECTS;
				}
				else if (jRule[1].startsWith("dWithin")) { //$NON-NLS-1$
					this.joinRule = FeatureSpatialJoiner.DWITHIN;
					this.value = Double.valueOf(jRule[1].substring(
							jRule[1].indexOf("(") + 1,  //$NON-NLS-1$
							jRule[1].indexOf(";"))); //$NON-NLS-1$
					this.UOM = jRule[1].substring(
							jRule[1].indexOf(";") + 1,  //$NON-NLS-1$
							jRule[1].indexOf(")")); //$NON-NLS-1$
				}
				else if (jRule[1].startsWith("beyond")) { //$NON-NLS-1$
					this.joinRule = FeatureSpatialJoiner.BEYOND;
					this.value = Double.valueOf(jRule[1].substring(
							jRule[1].indexOf("(") + 1,  //$NON-NLS-1$
							jRule[1].indexOf(";"))); //$NON-NLS-1$
					this.UOM = jRule[1].substring(
							jRule[1].indexOf(";") + 1,  //$NON-NLS-1$
							jRule[1].indexOf(")")); //$NON-NLS-1$
				}
				else if (jRule[1].startsWith("not")) { //$NON-NLS-1$
					this.joinRule = FeatureSpatialJoiner.NOT;
					this.value = Double.valueOf(jRule[1].substring(
							jRule[1].indexOf("(") + 1,  //$NON-NLS-1$
							jRule[1].indexOf(";"))); //$NON-NLS-1$
					this.UOM = jRule[1].substring(
							jRule[1].indexOf(";") + 1,  //$NON-NLS-1$
							jRule[1].indexOf(")")); //$NON-NLS-1$
				}
				else{
					throw new RuntimeException (jRule[1] + " is not a valid join rule."); //$NON-NLS-1$
				}
			}
			else{
				this.joinRule = "alpha"; //$NON-NLS-1$
			}
		}

		else {
			throw new RuntimeException("You can only create a " + //$NON-NLS-1$
				"Feature Joiner from a valid join rule."); //$NON-NLS-1$
		}

	}
	
	
	public List<Feature> join (List<Collection<Feature>> sources){
		if (spatial){
			return spatialJoin (sources);
		}
		if (!spatial) return alphaJoin (sources);
		else throw new RuntimeException("Error specifying spatial or non spatial join"); //$NON-NLS-1$
	}
	
	private List<Feature> alphaJoin (List<Collection<Feature>> sources){

		List<Feature> result = new ArrayList<Feature>();
		
		FeatureCollection sourceFeatures0 = FeatureCollections.newCollection(); 
		for (Feature f : sources.get(0)){
			sourceFeatures0.add(f);
		}
		
		FeatureCollection sourceFeatures1 = FeatureCollections.newCollection(); 
		for (Feature f : sources.get(1)){
			sourceFeatures1.add(f);
		}

		FeatureType schema0 = sourceFeatures0.getSchema();
        String typeName0 = schema0.getName().getLocalPart();
		FeatureType schema1 = sourceFeatures1.getSchema();
        String typeName1 = schema1.getName().getLocalPart();
        
        //Build synthetic FT from source0 and source 1
        SimpleFeatureType joinedFt = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(MessageFormat.format("Joined_{0}_{1}", typeName0, typeName1)); //$NON-NLS-1$
		//TODO: Which namespace for synthetic FT?
		ftbuilder.setNamespaceURI(schema0.getName().getNamespaceURI());
		for (PropertyDescriptor p :schema0.getDescriptors()){
			ftbuilder.add(p.getName().getLocalPart(), p.getType().getBinding());
		}
		for (PropertyDescriptor p :schema1.getDescriptors()){
			ftbuilder.add(p.getName().getLocalPart(), p.getType().getBinding());
		}
		joinedFt = ftbuilder.buildFeatureType();
		
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null); 
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(joinedFt);
		
        FeatureIterator iterator = sourceFeatures0.features();
        try{
        	while( iterator.hasNext() ){
        		Feature feature = (Feature) iterator.next();
        		Filter	innerFilter = ff.equals( ff.property(this.onAttributeName1), ff.literal( feature.getProperty(this.onAttributeName0).getValue() ));

        		FeatureCollection join = sourceFeatures1.subCollection(innerFilter);
        		if (join.size()>0){
        			FeatureIterator iteratorJoin = join.features();
        			try{
        		        	while( iteratorJoin.hasNext() ){
        		        		Feature featureJoin = (Feature) iteratorJoin.next();
        		        		for (Property p :feature.getProperties()){
        		        			builder.add(p.getValue());
        		        		}
        		        		for (Property p :featureJoin.getProperties()){
        		        			builder.add(p.getValue());
        		        		}
        		        		
        		        		Feature resultJoinedFeature = builder.buildFeature(UUID.randomUUID().toString());
        		        		result.add(resultJoinedFeature);
        		        	}
        		        }
        		        finally {
        		        	iteratorJoin.close();
        		        }
        		}
        	}
        }
        finally {
            iterator.close();
       }
    return result;
        
	}
	
	
	private List<Feature> spatialJoin (List<Collection<Feature>> sources){
		
		List<Feature> result = new ArrayList<Feature>();
		
		FeatureCollection sourceFeatures0 = FeatureCollections.newCollection(); 
		for (Feature f : sources.get(0)){
			sourceFeatures0.add(f);
		}
		
		FeatureCollection sourceFeatures1 = FeatureCollections.newCollection(); 
		for (Feature f : sources.get(1)){
			sourceFeatures1.add(f);
		}

		FeatureType schema0 = sourceFeatures0.getSchema();
        String typeName0 = schema0.getName().getLocalPart();
		FeatureType schema1 = sourceFeatures1.getSchema();
        String typeName1 = schema1.getName().getLocalPart();
        
        //Build synthetic FT from source0 and source 1
        SimpleFeatureType joinedFt = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(MessageFormat.format("Joined_{0}_{1}", typeName0, typeName1)); //$NON-NLS-1$
		//TODO: Which namespace for synthetic FT?
		ftbuilder.setNamespaceURI(schema0.getName().getNamespaceURI());
		for (PropertyDescriptor p :schema0.getDescriptors()){
			ftbuilder.add(p.getName().getLocalPart(), p.getType().getBinding());
		}
		for (PropertyDescriptor p :schema1.getDescriptors()){
			ftbuilder.add(p.getName().getLocalPart(), p.getType().getBinding());
		}
		joinedFt = ftbuilder.buildFeatureType();
        
		
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null); 
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(joinedFt);
		
        FeatureIterator iterator = sourceFeatures0.features();
        try{
        	while( iterator.hasNext() ){
        		Feature feature = (Feature) iterator.next();
        		Filter	innerFilter = ff.equals( ff.property(this.onAttributeName1), ff.literal( feature.getProperty(this.onAttributeName0).getValue() ));
        		if (this.joinRule.equals(FeatureSpatialJoiner.INTERSECTS))innerFilter= ff.intersects( ff.property(this.onAttributeName1), ff.literal( (Geometry)feature.getProperty(this.onAttributeName0).getValue() ));
    			else if (this.joinRule.equals(FeatureSpatialJoiner.DWITHIN))innerFilter= ff.dwithin(ff.property(this.onAttributeName1), ff.literal( (Geometry)feature.getProperty(this.onAttributeName0).getValue() ),1.0,""); //$NON-NLS-1$
    			else if (this.joinRule.equals(FeatureSpatialJoiner.BEYOND))innerFilter=  ff.beyond(ff.property(this.onAttributeName1), ff.literal( (Geometry)feature.getProperty(this.onAttributeName0).getValue() ),1.0,""); //$NON-NLS-1$
    			else if (this.joinRule.equals(FeatureSpatialJoiner.NOT))innerFilter=  ff.not( ff.disjoint(ff.property(this.onAttributeName1), ff.literal( (Geometry)feature.getProperty(this.onAttributeName0).getValue() )) );
        		FeatureCollection join = sourceFeatures1.subCollection(innerFilter);
        		if (join.size()>0){
        			FeatureIterator iteratorJoin = join.features();
        			try{
        		        	while( iteratorJoin.hasNext() ){
        		        		Feature featureJoin = (Feature) iteratorJoin.next();
        		        		for (Property p :feature.getProperties()){
        		        			builder.add(p.getValue());
        		        		}
        		        		for (Property p :featureJoin.getProperties()){
        		        			builder.add(p.getValue());
        		        		}
        		        		
        		        		Feature resultJoinedFeature = builder.buildFeature(UUID.randomUUID().toString());
        		        		result.add(resultJoinedFeature);
        		        	}
        		        }
        		        finally {
        		        	iteratorJoin.close();
        		        }
        		}
        	}
        }
        finally {
            iterator.close();
       }
    return result;
        
	}
	

}





