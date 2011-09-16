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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opengis.feature.Feature;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.specification.cst.AbstractCstFunction;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

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
	
	/**
	 * Source date format parameter name
	 */
	public static final String DATE_FORMAT_SOURCE = "dateFormatSource"; //$NON-NLS-1$
	/**
	 * Target date format parameter name
	 */
	public static final String DATE_FORMAT_TARGET = "dateFormatTarget"; //$NON-NLS-1$
	
	private String dateFormatSource = null;
	private String dateFormatTarget = null;
	private Property targetProperty = null;
	private Property sourceProperty = null;

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		for (IParameter ip : cell.getEntity1().getTransformation().getParameters()) {
			if (ip.getName().equals(DateExtractionFunction.DATE_FORMAT_SOURCE)) {
				this.dateFormatSource = ip.getValue();
			} else if (ip.getName().equals(DateExtractionFunction.DATE_FORMAT_TARGET)) {
				// if dateFormatTarget is not set use the format of the source
				if (ip.getValue() != null
						|| !ip.getValue().toString().equals("")) { //$NON-NLS-1$
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

	/**
	 * @see CstFunction#getParameters()
	 */
	public Cell getParameters() {
		Cell parameterCell = new Cell();	
		Property entity1 = new Property(new About("")); //$NON-NLS-1$
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add("java.lang.String"); //$NON-NLS-1$
		entityTypes.add("java.util.Date"); //$NON-NLS-1$
		entity1.setTypeCondition(entityTypes);

		Property entity2 = new Property(new About("")); //$NON-NLS-1$
	
		// Setting of type condition for entity2
		// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
			
		Parameter p_source   = 
			new Parameter(DateExtractionFunction.DATE_FORMAT_SOURCE,""); //$NON-NLS-1$
		Parameter p_targert = 
			new Parameter(DateExtractionFunction.DATE_FORMAT_TARGET,""); //$NON-NLS-1$
		
		params.add(p_source);
		params.add(p_targert);
		t.setParameters(params);
		entity1.setTransformation(t);
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}
	
	/**
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		//transform date string
		SimpleDateFormat sdf = new SimpleDateFormat(); 
		sdf.applyPattern(this.dateFormatSource);
		
		//get the date string from the source
		String dateString = (String) FeatureInspector.getPropertyValue(source, sourceProperty.getAbout(), null); 
		
		Date sourceDate = null;
		try {
			sourceDate = sdf.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Parsing the given date string "  //$NON-NLS-1$
					+ dateString + " using the supplied format "  //$NON-NLS-1$
					+ this.dateFormatSource + " failed.", e); //$NON-NLS-1$
		}

		org.opengis.feature.Property targetProperty = FeatureInspector.getProperty(target, this.targetProperty.getAbout(), true);
		
		Class<?> targetBinding = targetProperty.getType().getBinding();
		if (targetBinding.equals(String.class)) {
			// string target property
			sdf.applyPattern(this.dateFormatTarget);
			targetProperty.setValue(sdf.format(sourceDate));
		}
		if (Date.class.isAssignableFrom(targetBinding)) {
			// default to the java.util.Date value
			Date value = sourceDate;
			
			// date target property
			if (targetBinding.equals(Timestamp.class)) {
				// java.sql.Timestamp binding
				value = new Timestamp(value.getTime());
			}
			else if (targetBinding.equals(java.sql.Date.class)) {
				// java.sql.Date binding
				value = new java.sql.Date(value.getTime());
			}
			
			targetProperty.setValue(value);
		}
		
		return target;
	}

	@Override
	public String getDescription() {
		return Messages.getString("DateExtractionFunction.1"); //$NON-NLS-1$
	}
}
