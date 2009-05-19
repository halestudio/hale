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
 * This class represents <xs:complexType name="ServiceType" >.
 * 
 * @authors Marian de Vries
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class Service {

	/**
	 * MdV changed the original OML structure here - now a list of parameters
	 */
	private List<Param> param;

	/**
	 * The rdf resource identifier of the service. <xs:attribute
	 * ref="rdf:resource" use="optional"/>
	 */
	private URI resource;

	/**
	 * The id identifier of the service (see thesis 9.2 Alignment language
	 * grammar) <xs:attribute ref="id" use="optional"/>
	 */
	private URI id;
	
	// constructors ............................................................
	
	/**
	 * @param resource
	 * @param id
	 */
	public Service(URI resource, URI id) {
		super();
		this.resource = resource;
		this.id = id;
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

	/**
	 * @return the id
	 */
	public URI getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(URI id) {
		this.id = id;
	}
	
	

}
