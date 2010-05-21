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
package eu.esdihumboldt.hale.models;

/**
 * This class is used by the {@link HaleServiceListener} and can be used to 
 * provide details on a given update to the Listener.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 *  
 * @param <T> the message object type 
 */
public class UpdateMessage<T> {
	
	private Class<?> sourceClass;
	private T messageObject;

	/**
	 * Constructor
	 * 
	 * @param sourceClass the source type
	 * @param messageObject the message object
	 */
	public UpdateMessage(Class<?> sourceClass, T messageObject) {
		this.sourceClass = sourceClass;
		this.messageObject = messageObject;
	}
	
	/**
	 * @return the interface class of the service that sent the message.
	 */
	public Class<?> getSource(){
		return this.sourceClass;
	}
	
	/**
	 * @return the message object providing details on the updating event.
	 */
	public T getMessage() {
		return this.messageObject;
	}
	
}
