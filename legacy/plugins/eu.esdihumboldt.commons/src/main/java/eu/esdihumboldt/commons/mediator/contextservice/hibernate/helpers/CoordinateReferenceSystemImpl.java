/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers;

import java.util.Collection;
import java.util.Set;

import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.util.InternationalString;

/**
 * 
 * @author mgone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class CoordinateReferenceSystemImpl implements CoordinateReferenceSystem {

	public CoordinateSystem getCoordinateSystem() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Extent getDomainOfValidity() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Extent getValidArea() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public InternationalString getScope() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public ReferenceIdentifier getName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Collection getAlias() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set getIdentifiers() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public InternationalString getRemarks() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String toWKT() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
