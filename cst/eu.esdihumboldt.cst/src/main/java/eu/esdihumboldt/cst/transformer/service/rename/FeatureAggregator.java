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


import java.util.List;
import java.util.UUID;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;


/**
 * The FeatureAggregator is used by the {@link RenameFeatureFunction} to handle
 * rename cells with a defined aggregation condition. The aggrgation condition is expressed
 * using the following grammar:
 * 
 * <pre>
 * &lt;AggregationRule&gt;            ::= &lt;Operation&gt;:&lt;Operator&gt;
 * &lt;Operation&gt;                  ::= aggregate
 * &lt;Operator&gt;                   ::= equal() | inBetween(&lt;Double&gt;; &lt;Double&gt;) | lessThan(&lt;Double&gt;) | greaterThan(&lt;Double&gt;) | touches | overlaps | all
 * </pre>
 * @author Ulrich Schaeffler
 * @author Thorsten Reitz
 */
public class FeatureAggregator {
	
	//Constants for aggregation
	public static final String EQUAL = "equal";
	public static final String INBETWEEN ="inBetween";
	public static final String LESSTHAN ="lessThan";
	public static final String GREATERTHAN ="greaterThan";
	public static final String TOUCHES ="touches";
	public static final String OVERLAPS ="overlaps";
	public static final String ALL ="all";
	
	
	private String onAttributeName = null;
	private String aggregationRule = null;
	private String[] ruleValues = null;
	


	/**
	 * @param value
	 */
	public FeatureAggregator(String onAttributeNames, String aggregationRule) {
		this.onAttributeName = onAttributeNames;
		String[] aggrule = aggregationRule.split(":");
		if (aggrule[0].equals("aggregate")) {
			if (aggrule[1].startsWith("equal")) {
				this.aggregationRule = FeatureAggregator.EQUAL;
			}
			else if (aggrule[1].startsWith("greaterThan")) {
				this.aggregationRule = FeatureAggregator.GREATERTHAN;
			}
			//TODO: Implement all other aggregation rules 
			
			try{
				this.ruleValues =
					(aggrule[1].substring(aggrule[1].indexOf("(") + 1, aggrule[1].indexOf(")"))).split(";");
			}
			catch(Exception e){
				this.ruleValues[0] =
					aggrule[1].substring(aggrule[1].indexOf("(") + 1, aggrule[1].indexOf(")"));
			}
		}
		else {
			throw new RuntimeException("You can only create a " +
				"FeatureAggregator from a aggregate rule.");
		}
	}
	
	
	
	public Feature aggregate(List<Feature> source, FeatureType targetType) {
		List<Feature> filtered = null ;
		SimpleFeature target = null;
		if (this.aggregationRule.equals(FeatureAggregator.GREATERTHAN)) {
			for (Feature f : source){
				//TODO: Problem: All features must have the same attribute name
				PropertyDescriptor pd = f.getProperty(
							this.onAttributeName).getDescriptor();
				if (pd.getType().getBinding().isAssignableFrom(Number.class)) {
					Number attribute_value = (Number)((SimpleFeature)f).getAttribute(this.onAttributeName);
					if (attribute_value.doubleValue() > Double.parseDouble(this.ruleValues[0])){
						filtered.add(f);
					}
					double average= 0;
					for (int i = 0; i < filtered.size(); i++){
						average = average + (Double)((SimpleFeature)filtered.get(i)).getAttribute(this.onAttributeName);
					}
					average = average / filtered.size();
					target = SimpleFeatureBuilder.build(
							(SimpleFeatureType) targetType, new Object[]{}, 
							UUID.randomUUID().toString());
					
					target.setAttribute(this.onAttributeName,average);
				}
			}
			return target;
		}
//		else if (this.aggregationRule.equals(FeatureAggregator.TOUCHES)) {
//			//TODO: Add all other...
//		}
		else{
			return null;
		}

	}
	
	
	
	
	
		

	

}
