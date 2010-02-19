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

import eu.esdihumboldt.goml.align.Entity;

/**
 * The {@link Reason} object provides information on why a certain mismatch 
 * occurs.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class Reason {
	
	private Entity entity1;

	private Entity entity2;

	private String description;
	
	public Reason(Entity e1, Entity e2, String description) {
		this.entity1 = e1;
		this.entity2 = e2;
		this.description = description;
	}
	
	public String getDescription() {
		return "";
	}
	
	public enum ReasonType {
		CardinalityDifference,
		BindingDifference
	}
	
}
