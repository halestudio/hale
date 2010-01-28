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
 * Classname    : eu.esdihumboldt.cst.transformer/BufferGeometryTransformer.java 
 * 
 * Author       : schneidersb
 * 
 * Created on   : Aug 13, 2009 -- 9:34:39 AM
 *
 */
package eu.esdihumboldt.cst.corefunctions;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * CstFunction to apply a geometric buffer to a Feature.
 */
public class NetworkExpansionFunction extends AbstractCstFunction {

	private double bufferWidth = 10.0;
	private int capStyle = BufferParameters.CAP_ROUND;
	
	public static final String BUFFERWIDTH = "BUFFERWIDTH";
	public static final String CAPSTYLE = "CAPSSTYLE";
	
	protected Map<String, Class<?>>  parameters = new HashMap<String, Class<?>>();
	private Property targetProperty = null;
	
	
	
	public void setBufferWidth(double bufferWidth) {
		this.bufferWidth = bufferWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		return null;
	}

	public Feature transform(Feature source, Feature target) {
		Geometry old_geometry = (Geometry)source.getDefaultGeometryProperty().getValue();
		if (old_geometry != null) {
			Geometry new_geometry = null;
			try {
				BufferParameters bufferParameters = new BufferParameters();
				bufferParameters.setEndCapStyle(capStyle);
				BufferBuilder bb = new BufferBuilder(new BufferParameters());
				new_geometry = bb.buffer(old_geometry, bufferWidth);
				((SimpleFeatureImpl)target).setDefaultGeometry(new_geometry);
			} catch (Exception ex) {
				if (!ex.getClass().equals(TopologyException.class)) {
					throw new RuntimeException(ex);
				}
			}
		}
		return target;
	}

	
	public boolean configure(Map<String, String> parametersValues) {		
		this.setBufferWidth(Double.parseDouble(parametersValues.get(NetworkExpansionFunction.BUFFERWIDTH)));	
		this.capStyle = Integer.parseInt(parametersValues.get(NetworkExpansionFunction.CAPSTYLE));
		return true;
    }

	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
				if (ip.getName().equals(NetworkExpansionFunction.BUFFERWIDTH)) {
					this.setBufferWidth(Double.parseDouble(ip.getValue()));
				}
				else if(ip.getName().equals(NetworkExpansionFunction.CAPSTYLE)) {
					this.capStyle = Integer.parseInt(ip.getValue());
				}
		}
		// if no bufferWidth or capStyle then default values will be used

		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parametersTypes.put(NetworkExpansionFunction.BUFFERWIDTH, Double.class);
		parametersTypes.put(NetworkExpansionFunction.CAPSTYLE, Integer.class);
	}
	
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));
		Property entity2 = new Property(new About(""));
	
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
}
