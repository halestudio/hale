/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : cst
 * 	 
 * Classname    : eu.esdihumboldt.cst.transformer/ClipByRectangleFunction.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Dec, 2009
 *
 */

package eu.esdihumboldt.cst.corefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

public class ClipByRectangleFunction extends AbstractCstFunction{

	public static final String YMAX = "YMAX";
	public static final String YMIN = "YMIN";
	public static final String XMAX = "XMAX";
	public static final String XMIN = "XMIN";	
	
	private double YmaxCoord = Double.POSITIVE_INFINITY;
	private double YminCoord = Double.NEGATIVE_INFINITY;
	private double XmaxCoord = Double.POSITIVE_INFINITY;
	private double XminCoord = Double.NEGATIVE_INFINITY;
	private Envelope clipEnvelope;
	private Geometry clipGeometry;
	
	public void setYmaxCoord (double yMax){
		YmaxCoord = yMax;
	}
	public void setYminCoord (double yMin){
		YminCoord = yMin;
	}
	public void setXmaxCoord (double xMax){
		XmaxCoord = xMax;
	}
	public void setXminCoord (double xMin){
		XminCoord = xMin;
	}
	
	public void buildClip (double xMax, double xMin, double yMax, double yMin){
		GeometryFactory gf = new GeometryFactory();
		Coordinate[] coords = new Coordinate[5];
		coords[0] = new Coordinate(xMin, yMin);
		coords[1] = new Coordinate(xMax, yMin);
		coords[2] = new Coordinate(xMax, yMax);
		coords[3] = new Coordinate(xMin, yMax);
		coords[4] = new Coordinate(xMin, yMin);			
		clipGeometry = gf.createLinearRing(coords);
		clipEnvelope = clipGeometry.getEnvelopeInternal();
	}

	public Feature transform(Feature source, Feature target) {
		Collection<org.opengis.feature.Property> c = new HashSet<org.opengis.feature.Property>();
		PropertyDescriptor pd = target.getDefaultGeometryProperty().getDescriptor();
		
		Geometry geom = (Geometry)source.getDefaultGeometryProperty().getValue();
		if (clipEnvelope.intersects(geom.getEnvelopeInternal())){
			Object newGeometry = clipGeometry.intersection(geom);
			PropertyImpl p = new AttributeImpl(newGeometry, (AttributeDescriptor) pd, null);	
			c.add(p);
			target.setValue(c);
			return target;
		}
		return null;
	}

	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(ClipByRectangleFunction.XMAX)) {
				this.setXmaxCoord(Double.parseDouble(ip.getValue()));
			}
			else{
				if (ip.getName().equals(ClipByRectangleFunction.XMIN)) {
					this.setXminCoord(Double.parseDouble(ip.getValue()));
				}	
				else{
					if (ip.getName().equals(ClipByRectangleFunction.YMAX)) {
						this.setYmaxCoord(Double.parseDouble(ip.getValue()));
					}
					else{
						if (ip.getName().equals(ClipByRectangleFunction.YMIN)) {
							this.setYminCoord(Double.parseDouble(ip.getValue()));
						}	
					}
				}
			}
		}
		buildClip(XmaxCoord, XminCoord, YmaxCoord, YminCoord);
		return true;
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();	
				
		Property entity1 = new Property(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entityTypes.add(org.opengis.geometry.Geometry.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About(""));
		 
		// Setting of type condition for entity2
			// 	entity2 has same conditions as entity1
		entity2.setTypeCondition(entityTypes);
			
	
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
			
		Parameter xmax = new Parameter(XMAX,"0");
		Parameter ymax = new Parameter(YMAX,"0");
		Parameter xmin = new Parameter(XMIN,"0");
		Parameter ymin = new Parameter(YMIN,"0");					
		params.add(xmax);
		params.add(ymax);
		params.add(xmin);
		params.add(ymin);	
		t.setParameters(params);
		entity1.setTransformation(t);	
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
