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
package eu.esdihumboldt.specification.mediator.constraints;

/**
 * A ServiceConstraint describes the most important parameters which define the
 * OGC service which provides the geoinformation to be used. These parameters
 * are the serviceType (e.g."WMS") and the serviceVersion (e.g."1.1.1").
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */
public interface ServiceConstraint extends Constraint {

	/**
	 * This operation is used to return the alphanumeric value identifying the
	 * OGC service (e.g."WMS").
	 */
	public String getServiceType();

	/**
	 * This operation is used to return the alphanumeric value identifying the
	 * OGC service version (e.g."1.1.0").
	 */
	public String getServiceVersion();

}
