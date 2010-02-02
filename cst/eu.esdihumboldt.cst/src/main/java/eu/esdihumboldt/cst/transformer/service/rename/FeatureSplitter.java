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
import java.util.regex.Pattern;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * The FeatureSplitter is used by the {@link RenameFeatureFunction} to handle
 * rename cells with a defined split condition. The split condition is expressed
 * using the following grammar:
 * 
 * <pre>
 * &lt;SplitRule&gt;                  ::= &lt;Operation&gt;:&lt;Operator&gt;
 * &lt;Operation&gt;                  ::= split
 * &lt;Operator&gt;                   ::= extractSubgeometry(&lt;extractGeometryParameters&gt;) | extractSubstring(&lt;extractSubstringParameters&gt;)
 * &lt;extractGeometryParameters&gt;  ::= Point | LineString | Polygon
 * &lt;extractSubstringParameters&gt; ::= &lt;RegularExpression&gt;
 * </pre>
 * 
 * @author Thorsten Reitz
 */
public class FeatureSplitter {
	
	private String onAttributeName = null;
	
	private Class<? extends Geometry> geometryAtomType = null;
	
	private Pattern pattern = null;

	/**
	 * @param value
	 */
	public FeatureSplitter(String onAttributeName, String value) {
		this.onAttributeName = onAttributeName;
		String[] splitrule = value.split(":");
		if (splitrule[0].equals("split")) {
			if (splitrule[1].startsWith("extractSubgeometry")) {
				String extractGeometryParameter = splitrule[1].substring(
						splitrule[1].indexOf("("), 
						splitrule[1].indexOf(")"));
				if (extractGeometryParameter.equals("Point")) {
					this.geometryAtomType = Point.class;
				}
				else if (extractGeometryParameter.equals("LineString")) {
					this.geometryAtomType = LineString.class;
				}
				else if (extractGeometryParameter.equals("Polygon")) {
					this.geometryAtomType = Polygon.class;
				}
			}
			else if (splitrule[1].startsWith("extractSubstring")) {
				String extractSubstringParameters = splitrule[1].substring(
						splitrule[1].indexOf("("), 
						splitrule[1].indexOf(")"));
				this.pattern = Pattern.compile(extractSubstringParameters);
			}
		}
		else {
			throw new RuntimeException("You can only create a " +
				"FeatureSplitter from a split rule.");
		}
	}
	
	public List<Feature> split(Feature source, FeatureType targetFT) {
		return null;
	}

}
