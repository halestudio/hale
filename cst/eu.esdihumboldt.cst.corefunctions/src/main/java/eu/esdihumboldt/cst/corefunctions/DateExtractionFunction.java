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
import java.util.Date;
import java.util.Map;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * This function extracts the date/time from a source string and puts it
 * reformatted to the target, based on a format parameter for the date/time
 * pattern of the source and the target. For date/time pattern:
 * @see <a href=
 * "http://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html">http
 * ://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html</a>
 * 
 * @author Ulrich Schaeffler
 * @partner 01 / TUM
 * @version $Id$
 */

public class DateExtractionFunction extends AbstractCstFunction {
	
	
	public static final String DATE_STRING = "dateString";
	public static final String DATE_FORMAT_SOURCE = "dateFormatSource";
	public static final String DATE_FORMAT_TARGET = "dateFormatTarget";
	
	private String dateStringSource = null;
	private String dateStringTarget = null;
	private String dateFormatSource = null;
	private String dateFormatTarget = null;
	private Property targetProperty = null;
	
	

	@Override
	protected void setParametersTypes(Map<String, Class<?>> parametersTypes) {
		parameterTypes.put(DateExtractionFunction.DATE_STRING, String.class);
		parameterTypes.put(DateExtractionFunction.DATE_FORMAT_SOURCE, String.class);
		parameterTypes.put(DateExtractionFunction.DATE_FORMAT_TARGET, String.class);
		
	}

	@Override
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(DateExtractionFunction.DATE_STRING)) {
				this.dateStringSource = ip.getValue();
			}
			else{
				if (ip.getName().equals(DateExtractionFunction.DATE_FORMAT_SOURCE)) {
					this.dateFormatSource = ip.getValue();
				}	
				else{
					if (ip.getName().equals(DateExtractionFunction.DATE_FORMAT_TARGET)) {
						//if dateFormatTarget is not set use the format of the source
						if (ip.getValue() == null || ip.getValue().toString().equals("")){
							this.dateFormatTarget = this.dateFormatSource;
						}
						else {
							this.dateFormatTarget = ip.getValue();
						}
						
					}
				}
			}
		}
		
		this.targetProperty = (Property) cell.getEntity2();
		
		//transform date string
		SimpleDateFormat sdf = new SimpleDateFormat(); 
		sdf.applyPattern(this.dateFormatSource);
		Date sourceDate = null;
		try {
			sourceDate = sdf.parse( this.dateStringSource );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(this.dateFormatTarget);
		this.dateStringTarget = sdf.format(sourceDate);
		
		return true;
	}

	@Override
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		return null;
	}

	@Override
	public Feature transform(Feature source, Feature target) {
		((SimpleFeature)target).setAttribute(this.targetProperty.getLocalname(),this.dateStringTarget);
		return target;
	}

}
