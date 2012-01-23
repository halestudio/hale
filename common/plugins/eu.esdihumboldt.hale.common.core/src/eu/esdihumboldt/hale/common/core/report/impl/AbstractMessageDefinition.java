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

package eu.esdihumboldt.hale.common.core.report.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.MessageDefinition;
import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * Abstract message definition
 * @param <T> the message type
 * @author Simon Templer
 */
public abstract class AbstractMessageDefinition<T extends Message> implements MessageDefinition<T> {
	
	private static final ALogger log = ALoggerFactory.getLogger(AbstractMessageDefinition.class);
	
	private final Class<T> messageClass;
	
	private final String identifier;
	
	/**
	 * Create message definition
	 * @param messageClass the message class
	 * @param id the identifier for the definition (without prefix)
	 */
	public AbstractMessageDefinition(Class<T> messageClass, String id) {
		super();
		
		this.messageClass = messageClass;
		this.identifier = ID_PREFIX + id.toUpperCase();
	}

	/**
	 * @see ObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @see ObjectDefinition#getObjectClass()
	 */
	@Override
	public Class<T> getObjectClass() {
		return messageClass;
	}

	/**
	 * @see ObjectDefinition#parse(String)
	 */
	@Override
	public T parse(String value) {
		Properties props = new Properties();
		StringReader reader = new StringReader(value.trim());
		try {
			props.load(reader);
		} catch (IOException e) {
			log.error("Error loading message properties", e);
			return null;
		} finally {
			reader.close();
		}
		
		return createMessage(props);
	}

	/**
	 * Create a message from a set of properties
	 * @param props the properties
	 * @return the message
	 */
	protected abstract T createMessage(Properties props);

	/**
	 * @see ObjectDefinition#asString(Object)
	 */
	@Override
	public String asString(T message) {
		String nl = System.getProperty("line.separator");
		Properties props = asProperties(message);
		
		StringWriter writer = new StringWriter();
		try {
			props.store(writer, null);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		return nl + writer.toString() + nl+nl;
	}

	/**
	 * Get a {@link Properties} representation of the given message that can be
	 * used to create a new message instance using 
	 * {@link #createMessage(Properties)}.
	 * @param message the message
	 * @return the properties representing the message
	 */
	protected abstract Properties asProperties(T message);

}
