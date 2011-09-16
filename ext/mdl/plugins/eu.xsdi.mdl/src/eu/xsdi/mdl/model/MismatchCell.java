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

import eu.esdihumboldt.commons.goml.align.Cell;

/**
 * TODO Add Type comment
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 0.1.0
 */
public class MismatchCell extends Cell {
	
	private List<Mismatch> mismatches;

	/**
	 * @return the {@link List} of {@link Mismatch}s attached to this {@link Cell}.
	 */
	public List<Mismatch> getMismatches() {
		return mismatches;
	}

	/**
	 * 
	 */
	public MismatchCell() {
		super();
		this.mismatches = new ArrayList<Mismatch>();
	}

	/**
	 * @param mismatches
	 */
	public void setMismatches(List<Mismatch> mismatches) {
		this.mismatches = mismatches;
	}
	

}
