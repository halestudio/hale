/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdl.model.consequence;

import javax.measure.unit.Unit;

import eu.xsdi.mdl.model.Mismatch;

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
