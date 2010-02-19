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

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link Mismatch} object provides detailed information on non-perfect 
 * mappings, ie.e ones that contain irreconcilable differences between the 
 * mapped entities.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class Mismatch {
	
	private final MismatchType type;
	
	private Reason reason = null;
	
	private Set<Consequence> consequences = null;
	
	public Mismatch(MismatchType type) {
		this.type = type;
		this.consequences = new HashSet<Consequence>();
	}
	
	// standard methods ........................................................
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Mismatch.type: " + this.type + ", reason: " + this.reason.toString());
		int i = 0;
		for (Consequence c : this.consequences) {
			sb.append("Consequence " + i++ + ": " + c.toString());
		}
		return sb.toString();
	}
	
	// getters / setters .......................................................
	
	/**
	 * @return the reason
	 */
	public Reason getReason() {
		return this.reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(Reason reason) {
		this.reason = reason;
	}

	/**
	 * @return the type
	 */
	public MismatchType getType() {
		return this.type;
	}

	/**
	 * @return the consequences
	 */
	public Set<Consequence> getConsequences() {
		return this.consequences;
	}

	/**
	 * TODO: Enter Type comment.
	 * 
	 * @author Thorsten Reitz
	 */
	public enum MismatchType {
		SubsumptionMismatch,
		OverlappingScopeMismatch,
		CategorizationMismatch,
		AggregationLevelMismatch,
		StructureMismatch,
		ConstraintMismatch,
		AttributeTypeMismatch
	}
	
}
