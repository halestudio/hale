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
package eu.xsdi.mdl.model;

import java.util.ArrayList;
import java.util.List;

import eu.xsdi.mdl.model.consequence.DataQualityElement;
import eu.xsdi.mdl.model.consequence.MismatchContext;

/**
 * A {@link Consequence} object describes the impact a mismatch has in terms of 
 * {@link DataQualityElement}s and a defined {@link MismatchContext}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class Consequence {
	
	private MismatchContext context;
	
	private List<DataQualityElement> impact;
	
	/**
	 * build a {@link Consequence} with an empty context and an empty List of
	 * impacts.
	 */
	public Consequence() {
		this.context = null;
		this.impact = new ArrayList<DataQualityElement>();
	}

	/**
	 * @return the {@link MismatchContext} for this {@link Consequence}.
	 */
	public MismatchContext getContext() {
		return context;
	}

	/**
	 * Sets this {@link Consequence}s {@link MismatchContext}.
	 * @param context the {@link MismatchContext} for this {@link Consequence}
	 */
	public void setContext(MismatchContext context) {
		this.context = context;
	}

	
	/**
	 * @return 
	 */
	public List<DataQualityElement> getImpact() {
		return impact;
	}

	/**
	 * @param impact
	 */
	public void setImpact(List<DataQualityElement> impact) {
		this.impact = impact;
	}
	
	public String toString() {
		return "Consequence: ";
	}

}
