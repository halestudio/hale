/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.goml.oml.ext;

import java.util.List;

import eu.esdihumboldt.goml.omwg.Function;
import eu.esdihumboldt.goml.omwg.Service;

/**
 * This class represents <xs:group name="transformation">.
 * 
 * @author Thorsten Reitz, Marian de Vries 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$ 
 */
public class Transformation {
	
	private Function transf;
	
	private Service service;
	
	/**
	 * <xs:element name="transfPipe" type="goml:TransfPipeType" minOccurs="0" />
	 */
	private List<Transformation> pipe;


	// getters / setters .......................................................

	/**
	 * @return the transf
	 */
	public Function getTransf() {
		return transf;
	}

	/**
	 * @return the pipe
	 */
	public List<Transformation> getPipe() {
		return pipe;
	}

	/**
	 * @param pipe the pipe to set
	 */
	public void setPipe(List<Transformation> pipe) {
		this.pipe = pipe;
	}

	/**
	 * @param transf
	 *            the transf to set
	 */
	public void setTransf(Function transf) {
		this.transf = transf;
	}

	/**
	 * @return the service
	 */
	public Service getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(Service service) {
		this.service = service;
	}


}
