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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;

/**
 * This class represents <xs:group name="transformation">.
 * 
 * @author Thorsten Reitz, Marian de Vries 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$ 
 */
public class Transformation 
	implements ITransformation {



	private URI service;
	private String label;
	private List<IParameter> parameters;

	// getters / setters .......................................................

	
	public Transformation() {
		this.parameters = new ArrayList<IParameter>();
	}
	
	/**
	 * @return the service
	 */
	public URI getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(URI service) {
		this.service = service;
	}

	public String getLabel() {
		return this.label;
	}

	public List<IParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<IParameter> parameters) {
		this.parameters = parameters;
	}

}
