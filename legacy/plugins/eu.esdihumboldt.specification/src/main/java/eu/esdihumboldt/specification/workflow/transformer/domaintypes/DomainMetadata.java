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
package eu.esdihumboldt.specification.workflow.transformer.domaintypes;

import java.io.Serializable;
import java.net.URI;

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * This interface specifies the metadata for domaintype data structures
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r6:Annex D.1")
public interface DomainMetadata extends Serializable {

	/**
	 * Human readable name of domain metadata described by associated referenced
	 * document
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Reference to metadat about this domain
	 * 
	 * @return
	 */
	public URI getReference();

}
