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
package eu.esdihumboldt.hale.quality.mdl;

import javax.measure.unit.Unit;

/**
 * A {@link Measurement} is used to describe the change in a
 * given category of data quality that related from a given {@link Mismatch}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class Measurement {
	
	private String value;
	
	private Class<?> valueType;
	
	private Unit<?> unit;

	public Measurement(String value, Class<?> valueType, Unit<?> unit) {
		super();
		this.value = value;
		this.valueType = valueType;
		this.unit = unit;
	}
	
	public Measurement(double value, Unit<?> unit) {
		this.value = value + "";
		this.valueType = Double.class;
		this.unit = unit;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public Unit<?> getUnit() {
		return unit;
	}

	public void setUnit(Unit<?> unit) {
		this.unit = unit;
	}
	

}
