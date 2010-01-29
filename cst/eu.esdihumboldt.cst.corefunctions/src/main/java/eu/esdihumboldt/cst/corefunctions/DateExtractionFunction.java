/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.corefunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.PropertyImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * This function extracts the date/time from a source string and puts it
 * reformatted to the target, based on a format parameter for the date/time
 * pattern of the source and the target. For date/time pattern:
 * <a href=
 * "http://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html">http
 * ://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html</a>
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 * @version $Id$
 */

public class DateExtractionFunction extends AbstractCstFunction {
	
	
//	public static final String DATE_STRING = "dateString";
	public static final String DATE_FORMAT_SOURCE = "dateFormatSource";
	public static final String DATE_FORMAT_TARGET = "dateFormatTarget";
	


	private String dateFormatSource = null;
	private String dateFormatTarget = null;
	private Property targetProperty = null;
	private Property sourceProperty = null;
	
	

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parameterTypes.put(DateExtractionFunction.DATE_FORMAT_SOURCE, String.class);
		parameterTypes.put(DateExtractionFunction.DATE_FORMAT_TARGET, String.class);
		
	}

	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(DateExtractionFunction.DATE_FORMAT_SOURCE)) {
				this.dateFormatSource = ip.getValue();
			} else if (ip.getName().equals(DateExtractionFunction.DATE_FORMAT_TARGET)) {
				// if dateFormatTarget is not set use the format of the source
				if (ip.getValue() != null
						|| !ip.getValue().toString().equals("")) {
					this.dateFormatTarget = ip.getValue();
				}
			}
		}
		if (this.dateFormatTarget == null) {
			this.dateFormatTarget = this.dateFormatSource;

		}
		this.sourceProperty = (Property) cell.getEntity1();
		this.targetProperty = (Property) cell.getEntity2();
		return true;
	}

	public Cell getParameters() {
		Cell parameterCell = new Cell();	
		Property entity1 = new Property(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add("java.lang.String");
		entityTypes.add("java.util.Date");
		entity1.setTypeCondition(entityTypes);

		Property entity2 = new Property(new About(""));
	
		// Setting of type condition for entity2
		// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
			
		Parameter p_source   = 
			new Parameter(DateExtractionFunction.DATE_FORMAT_SOURCE,"");
		Parameter p_targert = 
			new Parameter(DateExtractionFunction.DATE_FORMAT_TARGET,"");
		
		params.add(p_source);
		params.add(p_targert);
		t.setParameters(params);
		entity1.setTransformation(t);
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		return null;
	}

	public Feature transform(Feature source, Feature target) {
		//transform date string
		SimpleDateFormat sdf = new SimpleDateFormat(); 
		sdf.applyPattern(this.dateFormatSource);
		
		//get the date string from the source
		String dateString = (String) source.getProperty(
				this.sourceProperty.getLocalname()).getValue();
		Date sourceDate = null;
		try {
			sourceDate = sdf.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Parsing the given date string " 
					+ dateString + " using the supplied format " 
					+ this.dateFormatSource + " failed.", e);
		}
		sdf.applyPattern(this.dateFormatTarget);

		PropertyDescriptor pd = target.getProperty(
				this.targetProperty.getLocalname()).getDescriptor();

		if (pd.getType().getBinding().equals(String.class)) {
			((SimpleFeature) target).setAttribute(this.targetProperty
					.getLocalname(), sdf.format(sourceDate));
		}
		
		if (pd.getType().getBinding().equals(Date.class) || Date.class.isAssignableFrom(
				target.getProperty(
						this.targetProperty.getLocalname()).getType().getBinding())) {
				((SimpleFeature) target).setAttribute(this.targetProperty
						.getLocalname(), sourceDate);
			
		}
		
		return target;
	}
}
