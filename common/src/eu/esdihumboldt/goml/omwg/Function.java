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

package eu.esdihumboldt.goml.omwg;

import java.net.URI;
import java.util.List;

/**
 * This class represents <xs:complexType name="TransformationType" >. FIXME not correct anymore?
 * 
 * @authors Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft Universtiy of Technology
 * @version $Id$
 */
public class Function {

	/**
	 * A {@link List} of {@link Param} objects used for the {@link Function}.
	 * Note: Changed compared to the original OML,, where just one parameter was
	 * accepted.
	 */
	private List<Param> param;

	/**
	 * The identifier of the {@link Function}. 
	 * <xs:attribute ref="rdf:resource" use="optional"/>
	 */
	private URI resource;
	
	// constructors ............................................................
	
	/**
	 * @param param
	 * @param resource
	 */
	public Function(List<Param> param, URI resource) {
		super();
		this.param = param;
		this.resource = resource;
	}

	// getters / setters .......................................................
	
	/**
	 * @return the param
	 */
	public List<Param> getParam() {
		return param;
	}

	/**
	 * @param param the param to set
	 */
	public void setParam(List<Param> param) {
		this.param = param;
	}

	/**
	 * @return the resource
	 */
	public URI getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(URI resource) {
		this.resource = resource;
	}
	
	

}
