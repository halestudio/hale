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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;

/**
 * Abstract base class for implementing {@link IOProvider}s 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractIOProvider implements IOProvider {
	
	/**
	 * The configuration parameter name for the content type 
	 */
	public static final String PARAM_CONTENT_TYPE = "contentType";
	
	/**
	 * The configuration parameters
	 */
	private final Map<String, String> parameters = new HashMap<String, String>();
	
	/**
	 * The supported configuration parameter names
	 */
	private final Set<String> supported = new HashSet<String>();
	
	/**
	 * The content type
	 */
	private ContentType contentType = null;

	/**
	 * Default constructor 
	 */
	protected AbstractIOProvider() {
		super();
		
		addSupportedParameter(PARAM_CONTENT_TYPE);
	}

	/**
	 * @see IOProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		//TODO check parameters?
	}
	
	/**
	 * Uses {@link #setParameter(String, String)} to load the configuration. For
	 * changing the behavior please override {@link #setParameter(String, String)}
	 * 
	 * @see IOProvider#loadConfiguration(Map)
	 */
	@Override
	public final void loadConfiguration(Map<String, String> configuration) {
		for (Entry<String, String> entry : configuration.entrySet()) {
			setParameter(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Stores all parameters that were set using {@link #setParameter(String, String)}
	 * in the configuration. For changing the behavior please override this
	 * method.
	 * 
	 * @see IOProvider#storeConfiguration(Map)
	 */
	@Override
	public void storeConfiguration(Map<String, String> configuration) {
		// store content type (if set)
		if (contentType != null) {
			configuration.put(PARAM_CONTENT_TYPE, contentType.getIdentifier());
		}
		
		// store generic parameters
		configuration.putAll(parameters);
	}

	/**
	 * Fail validation or execution if the configuration is not valid
	 * 
	 * @param message the error message
	 * 
	 * @throws IOProviderConfigurationException always
	 */
	protected void fail(String message) throws IOProviderConfigurationException {
		throw new IOProviderConfigurationException(message);
	}

	/**
	 * Add a supported parameter name, should be called in the constructor
	 * 
	 * @param name the supported parameter name to add
	 */
	protected void addSupportedParameter(String name) {
		supported.add(name);
	}
	
	/**
	 * @see IOProvider#getParameter(String)
	 */
	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * @see IOProvider#getSupportedParameters()
	 */
	@Override
	public Set<String> getSupportedParameters() {
		return Collections.unmodifiableSet(supported);
	}

	/**
	 * @see IOProvider#setParameter(String, String)
	 */
	@Override
	public void setParameter(String name, String value) {
		if (name.equals(PARAM_CONTENT_TYPE)) {
			// configure content type
			setContentType(ContentType.getContentType(value));
		}
		else {
			// load generic parameter
			parameters.put(name, value);
		}
	}

	/**
	 * @see IOProvider#getContentType()
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * @see IOProvider#setContentType(ContentType)
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

}
