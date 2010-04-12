
package eu.esdihumboldt.cst.transformer.service.rename;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.visitor.CalcResult;
import org.geotools.feature.visitor.MaxVisitor;
import org.geotools.feature.visitor.SumVisitor;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;





public class FeatureAggregator2 {
	
	//Aggregation Functions from Geotools
	
	public static final String SUM ="Collection_Sum";
	public static final String AVERAGE ="Collection_Average";
	public static final String BOUNDS = "Collection_Bounds";
	public static final String COUNT = "Collection_Count";
	public static final String MAX = "Collection_Max";	
	public static final String MEDIAN = "Collection_Median";	
	public static final String MIN = "Collection_Min";
	public static final String UNIQUE = "Collection_Unique";	


	private String onAttributeName = null;
	private String aggregationRule = null;



/**
 * 
 * @param onAttributeName
 * @param aggregationRule
 */
	public FeatureAggregator2(String onAttributeName, String aggregationRule) {
		this.onAttributeName = onAttributeName;
		String[] aggrule = aggregationRule.split(":");
		if (aggrule[0].equals("aggregate")) {
			if (aggrule[1].startsWith("Collection_Sum")) {
				this.aggregationRule = FeatureAggregator2.SUM;
			}
			else if (aggrule[1].startsWith("Collection_Average")) {
				this.aggregationRule = FeatureAggregator2.AVERAGE;
			}
			else if (aggrule[1].startsWith("Collection_Bounds")) {
				this.aggregationRule = FeatureAggregator2.BOUNDS;
			}
			else if (aggrule[1].startsWith("Collection_Count")) {
				this.aggregationRule = FeatureAggregator2.COUNT;
			}
			else if (aggrule[1].startsWith("Collection_Max")) {
				this.aggregationRule = FeatureAggregator2.MAX;
			}
			else if (aggrule[1].startsWith("Collection_Median")) {
				this.aggregationRule = FeatureAggregator2.MEDIAN;
			}
			else if (aggrule[1].startsWith("Collection_Min")) {
				this.aggregationRule = FeatureAggregator2.MIN;
			}
			else if (aggrule[1].startsWith("Collection_Unique")) {
				this.aggregationRule = FeatureAggregator2.UNIQUE;
			}
			else{
				throw new RuntimeException (aggrule[1] + " is not a valid aggregation rule.");
			}

		}
		else {
			throw new RuntimeException("You can only create a " +
				"FeatureAggregator from a aggregate rule.");
		}
		System.out.println("AggRule " + this.aggregationRule);
	}
	
	
	
	public List<Feature> aggregate(Collection<? extends Feature> source, FeatureType targetType) {
		List<Feature> result = new ArrayList<Feature>();
		SimpleFeature target = null;
		
//		if (this.aggregationRule.equals("Collection_Sum")){
//			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
//			Expression exp = ff.property(this.onAttributeName);
//			SumVisitor sumVisitor = new SumVisitor(exp);
//			FeatureCollection sourceFeatures = FeatureCollections.newCollection(); 
//			for (Feature f : source){
//				sourceFeatures.add(f);
//			}
//			try {
//				sourceFeatures.accepts(sumVisitor, null);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			CalcResult res = sumVisitor.getResult();
//
//			Object sum = res.getValue();
//			result.add(target);
//			return result;
//
//		}
		
		
		
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
		Function sum = ff.function(this.aggregationRule, ff.property(this.onAttributeName));
		FeatureCollection sourceFeatures = FeatureCollections.newCollection(); 
		for (Feature f : source){
			sourceFeatures.add(f);
		}
		Object value = sum.evaluate(sourceFeatures);
		target = SimpleFeatureBuilder.build(
					(SimpleFeatureType) targetType, new Object[]{}, 
					UUID.randomUUID().toString());
			
		target.setAttribute(this.onAttributeName,value);
		
		result.add(target);
		return result;

	}

}
