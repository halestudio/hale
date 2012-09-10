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
package eu.esdihumboldt.specification.mediator;

import java.util.UUID;

import eu.esdihumboldt.specification.dataaccess.DataAccessService;
import eu.esdihumboldt.specification.dataaccess.abstractionmodel.AbstractedDataSet;

/**
 * The MediatorResponse is the container- and interface-neutral structure
 * representing the result of a MediatorComplexRequest.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface MediatorResponse {

	/**
	 * @return the {@link UUID} uniquely identifying this
	 *         {@link MediatorResponse}.
	 */
	public UUID getIdentifier();

	/**
	 * @return the UUID that identified the {@link MediatorComplexRequest} that
	 *         led to the creation of this {@link MediatorResponse}.
	 */
	public UUID getRequestIdentifier();

	/**
	 * @return the {@link AbstractedDataSet} that has been returned from the
	 *         {@link DataAccessService}.
	 */
	public AbstractedDataSet getResponseDataset();

}
