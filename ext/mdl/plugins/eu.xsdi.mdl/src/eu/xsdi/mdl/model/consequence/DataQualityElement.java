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

import eu.xsdi.mdl.model.Mismatch;

/**
 * A {@link DataQualityElement} is used to describe the relative change in a
 * given category of data quality that related from a given {@link Mismatch}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public abstract class DataQualityElement {

	private final boolean isRelative;
	
	private final String type;
	
	private Measurement measurement;

	public DataQualityElement(boolean isRelative, String type) {
		super();
		this.isRelative = isRelative;
		this.type = type;
	}

	public Measurement getMeasurement() {
		return measurement;
	}

	public void setMeasurement(Measurement measurement) {
		this.measurement = measurement;
	}

	public boolean isRelative() {
		return isRelative;
	}

	public String getType() {
		return type;
	}

}
