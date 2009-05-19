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

//package eu.esdihumboldt.goml.omwg;
package eu.esdihumboldt.goml.oml.ext;

import java.util.List;

import eu.esdihumboldt.goml.omwg.Function;
import eu.esdihumboldt.goml.omwg.Service;
import eu.esdihumboldt.goml.omwg.Transformation;


/**
 * This class represents oml:TransfPipeType, which is an extension to omwg's OML
 *
 * FIXME not ready, will change
 * FIXME @MdV explain relation to {@link Transformation}.
 * 
 * @author Marian de Vries 
 * @partner 08 / Delft University of Technology
 * @version $Id$ 
 */
public class TransfPipe {

    private List<Function> transf;
    private List<Service> service;
    private TransfPipe transfPipe;
    
    // getters / setters .......................................................
    
	/**
	 * @return the transf
	 */
	public List<Function> getTransf() {
		return transf;
	}
	/**
	 * @param transf the transf to set
	 */
	public void setTransf(List<Function> transf) {
		this.transf = transf;
	}
	/**
	 * @return the service
	 */
	public List<Service> getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(List<Service> service) {
		this.service = service;
	}
	/**
	 * @return the transfPipe
	 */
	public TransfPipe getTransfPipe() {
		return transfPipe;
	}
	/**
	 * @param transfPipe the transfPipe to set
	 */
	public void setTransfPipe(TransfPipe transfPipe) {
		this.transfPipe = transfPipe;
	}
    
}
