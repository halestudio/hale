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

package eu.esdihumboldt.hale.gmlparser.gml3_2;

import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.bindings.AbstractRingPropertyTypeBinding;
import org.geotools.gml3.bindings.GML3ParsingUtils;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.Binding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.geometry.DirectPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

/**
 * Adds support for converting {@link LineString}s to {@link LinearRing}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleAbstractRingPropertyTypeBinding extends AbstractRingPropertyTypeBinding {
	
	private final GeometryFactory gf;
	private final CoordinateSequenceFactory csFactory;
	
	/**
	 * Create a AbstractRingPropertyTypeBinding binding
	 * 
	 * @param gFactory the geometry factory
	 */
    public HaleAbstractRingPropertyTypeBinding(GeometryFactory gFactory, CoordinateSequenceFactory csFactory) {
        this.gf = gFactory;
        this.csFactory = csFactory;
    }

	/**
	 * @see Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return new QName("http://www.opengis.net/gml/3.2","AbstractRingPropertyType"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see AbstractComplexBinding#parse(ElementInstance, Node, Object)
	 */
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		Object result = super.parse(instance, node, value);
		
		if (result == null) {
			LineString line = (LineString) node.getChildValue(LineString.class);
			if (line != null) {
				// try to create a LinearRing
				Coordinate[] coordinates = line.getCoordinates(); 
				
				if (coordinates != null && coordinates.length >= 2) {
					if (coordinates[0].equals(coordinates[coordinates.length - 1])) {
						// is closed, create ring
						result = gf.createLinearRing(coordinates);
					}
					else {
						Coordinate[] tmp = new Coordinate[coordinates.length + 1];
						System.arraycopy(coordinates, 0, tmp, 0, coordinates.length);
						tmp[coordinates.length] = coordinates[0];
						
						result = gf.createLinearRing(tmp);
					}
				}
			}
			else if (node.hasChild(DirectPosition.class) || node.hasChild(DirectPosition[].class)) {
				result = line(node, gf, csFactory, true);
			}
		}
		
		return result;
	}
	
	/**
	 * Copied from {@link GML3ParsingUtils}, not visible
	 * @param node
	 * @param gf
	 * @param csf
	 * @param ring
	 * @return
	 */
	static LineString line(Node node, GeometryFactory gf, CoordinateSequenceFactory csf,
	        boolean ring) {
	        if (node.hasChild(DirectPosition.class)) {
	            List dps = node.getChildValues(DirectPosition.class);
	            DirectPosition dp = (DirectPosition) dps.get(0);

	            CoordinateSequence seq = csf.create(dps.size(), dp.getDimension());

	            for (int i = 0; i < dps.size(); i++) {
	                dp = (DirectPosition) dps.get(i);

	                for (int j = 0; j < dp.getDimension(); j++) {
	                    seq.setOrdinate(i, j, dp.getOrdinate(j));
	                }
	            }

	            return ring ? gf.createLinearRing(seq) : gf.createLineString(seq);
	        }

	        if (node.hasChild(Point.class)) {
	            List points = node.getChildValues(Point.class);
	            Coordinate[] coordinates = new Coordinate[points.size()];

	            for (int i = 0; i < points.size(); i++) {
	                coordinates[i] = ((Point) points.get(0)).getCoordinate();
	            }

	            return ring ? gf.createLinearRing(coordinates) : gf.createLineString(coordinates);
	        }

	        if (node.hasChild(Coordinate.class)) {
	            List list = node.getChildValues(Coordinate.class);
	            Coordinate[] coordinates = (Coordinate[]) list.toArray(new Coordinate[list.size()]);

	            return ring ? gf.createLinearRing(coordinates) : gf.createLineString(coordinates);
	        }

	        if (node.hasChild(DirectPosition[].class)) {
	            DirectPosition[] dps = (DirectPosition[]) node.getChildValue(DirectPosition[].class);

	            CoordinateSequence seq = null;

	            if (dps.length == 0) {
	                seq = csf.create(0, 0);
	            } else {
	                seq = csf.create(dps.length, dps[0].getDimension());

	                for (int i = 0; i < dps.length; i++) {
	                    DirectPosition dp = dps[i];

	                    for (int j = 0; j < dp.getDimension(); j++) {
	                        seq.setOrdinate(i, j, dp.getOrdinate(j));
	                    }
	                }
	            }

	            return ring ? gf.createLinearRing(seq) : gf.createLineString(seq);
	        }

	        if (node.hasChild(CoordinateSequence.class)) {
	            CoordinateSequence seq = (CoordinateSequence) node.getChildValue(CoordinateSequence.class);

	            return ring ? gf.createLinearRing(seq) : gf.createLineString(seq);
	        }

	        return null;
	    }

}
