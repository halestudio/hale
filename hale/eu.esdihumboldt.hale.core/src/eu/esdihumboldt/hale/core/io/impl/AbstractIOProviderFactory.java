/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.core.io.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;

/**
 * Abstract base class for implementing factories for I/O providers
 * @param <T> the concrete provider type 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractIOProviderFactory<T extends IOProvider> implements IOProviderFactory<T> {

	private final Set<ContentType> supportedTypes = new HashSet<ContentType>();
	
	private final String identifier;
	
	/**
	 * Create an I/O provider factory
	 * 
	 * @param identifier the factory identifier
	 */
	protected AbstractIOProviderFactory(String identifier) {
		super();
		this.identifier = identifier;
	}

	/**
	 * Add a supported content type, should be called in the constructor
	 * 
	 * @param identifier the content type identifier
	 */
	protected void addSupportedContentType(String identifier) {
		supportedTypes.add(ContentType.getContentType(identifier));
	}
	
	/**
	 * @see IOProviderFactory#getSupportedTypes()
	 */
	@Override
	public Set<ContentType> getSupportedTypes() {
		return Collections.unmodifiableSet(supportedTypes);
	}

	/**
	 * @see IOProviderFactory#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

}
