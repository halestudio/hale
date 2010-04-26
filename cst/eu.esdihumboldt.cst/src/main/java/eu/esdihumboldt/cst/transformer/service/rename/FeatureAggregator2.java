
package eu.esdihumboldt.cst.transformer.service.rename;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;





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
	public static final String MULTI = "Multi";


	private String onAttributeName = null;
	private String aggregationRule = null;
	//private String groupingAttribute = null;
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
			else if (aggrule[1].startsWith("Multi")) {
				this.aggregationRule = FeatureAggregator2.MULTI;
			}
			else{
				throw new RuntimeException (aggrule[1] + " is not a valid aggregation rule.");
			}

		}
		else {
			throw new RuntimeException("You can only create a " +
				"FeatureAggregator from a aggregate rule.");
		}
//		System.out.println("AggRule " + this.aggregationRule);
	}
	
	
	
	public List<Feature> aggregate(Collection<? extends Feature> source, FeatureType targetType) {
		
		List<Feature> result = new ArrayList<Feature>();
		SimpleFeature target = null;
		if (this.aggregationRule.equals(FeatureAggregator2.AVERAGE)||
				this.aggregationRule.equals(FeatureAggregator2.BOUNDS)||
				this.aggregationRule.equals(FeatureAggregator2.COUNT)||
				this.aggregationRule.equals(FeatureAggregator2.MAX)||
				this.aggregationRule.equals(FeatureAggregator2.MEDIAN)||
				this.aggregationRule.equals(FeatureAggregator2.MIN)||
				this.aggregationRule.equals(FeatureAggregator2.SUM)||
				this.aggregationRule.equals(FeatureAggregator2.UNIQUE)){
			
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
		}
		
		else if (this.aggregationRule.equals(FeatureAggregator2.MULTI)){
			GeometryFactory geomFactory = new GeometryFactory();
			Object geom = ((SimpleFeature)source.iterator().next()).getAttribute(this.onAttributeName);;
			Geometry newGeometry = null;
			System.out.println("targetGEOM " + targetType.getGeometryDescriptor().getType().getBinding());
			if (geom.getClass().equals(Point.class) && targetType.getGeometryDescriptor().getType().getBinding().equals(MultiPoint.class)) {
				//Aggregation from multiple point Features to a MultiPoint
				List<Point> points = new ArrayList<Point>();
				for (Feature f : source){
					Point p = (Point)((SimpleFeature)f).getAttribute(this.onAttributeName);
					points.add(p);
				}
				Point[] pointsArray = new Point[points.size()];
				System.arraycopy(points.toArray(), 0, pointsArray, 0, points.size());
				newGeometry= geomFactory.createMultiPoint(pointsArray);
				target = SimpleFeatureBuilder.build(
						(SimpleFeatureType) targetType, new Object[]{}, 
						UUID.randomUUID().toString());
				
				target.setAttribute(this.onAttributeName,newGeometry);
				result.add(target);			
				
			}
			else if (geom.getClass().equals(LineString.class) && targetType.getGeometryDescriptor().getType().getBinding().equals(MultiLineString.class)) {
				//Aggregation from multiple LineString Features to a MultiLineString
				List<LineString> lines = new ArrayList<LineString>();
				for (Feature f : source){
					LineString l = (LineString)((SimpleFeature)f).getAttribute(this.onAttributeName);
					lines.add(l);
				}
				LineString[] linesArray = new LineString[lines.size()];
				System.arraycopy(lines.toArray(), 0, linesArray, 0, lines.size());
				newGeometry= geomFactory.createMultiLineString(linesArray);
				target = SimpleFeatureBuilder.build(
						(SimpleFeatureType) targetType, new Object[]{}, 
						UUID.randomUUID().toString());
				
				target.setAttribute(this.onAttributeName,newGeometry);
				result.add(target);
			}
			else if (geom.getClass().equals(Polygon.class) && targetType.getGeometryDescriptor().getType().getBinding().equals(MultiPolygon.class)) {
				//Aggregation from multiple Polygon Features to a MultiPolygon
				List<Polygon> polys = new ArrayList<Polygon>();
				for (Feature f : source){
					Polygon p = (Polygon)((SimpleFeature)f).getAttribute(this.onAttributeName);
					polys.add(p);
				}
				Polygon[] polysArray = new Polygon[polys.size()];
				System.arraycopy(polys.toArray(), 0, polysArray, 0, polys.size());
				newGeometry= geomFactory.createMultiPolygon(polysArray);
				target = SimpleFeatureBuilder.build(
						(SimpleFeatureType) targetType, new Object[]{}, 
						UUID.randomUUID().toString());
				
				target.setAttribute(this.onAttributeName,newGeometry);
				result.add(target);
			}
			else{
				throw new RuntimeException("An aggregate:multi rule was " +
				"defined on a non-Geometry property.");
			}
			
			
		
		}
		return result;
	
	
	}
	
	
	



}
