/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.constraints;

import org.opengis.metadata.identification.Resolution;

/**
 * A ResolutionConstraint allows definition of the Ground Sample distance to use
 * and the definition of the size in which an actual map product is to be put
 * out. FIXME: maybe refactor the latter to another class in portrayal.*
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id$
 */
public interface ResolutionConstraint extends Constraint {

	/**
	 * @return the Resolution Object, that is defined by ISO 19115 (Ground
	 *         Sample distance)
	 */
	public Resolution getResolution();

	/**
	 * @return the height in pixels of the map.
	 */
	public int getWindowHeight();

	/**
	 * @return the width in pixels of the map.
	 */
	public int getWindowWidth();

}
