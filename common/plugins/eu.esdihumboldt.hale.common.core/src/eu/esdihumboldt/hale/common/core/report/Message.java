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

package eu.esdihumboldt.hale.common.core.report;

/**
 * Report message. For a concrete message implementation there must be a
 * corresponding {@link MessageDefinition}. The {@link MessageDefinition} must
 * be published as OSGi service.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public interface Message {

	/**
	 * Get the message string
	 * 
	 * @return the message string
	 */
	public String getMessage();

	/**
	 * Get the formatted message string with additional informations.
	 * 
	 * @return the formatted message
	 */
	public String getFormattedMessage();

	/**
	 * Get the associated stack trace if any
	 * 
	 * @return the associated stack trace or <code>null</code>
	 */
	public String getStackTrace();

	/**
	 * Get the associated throwable. It may be not available even if there is a
	 * stack trace, so use {@link #getStackTrace()} instead if possible.
	 * 
	 * @return the associated throwable or <code>null</code>
	 */
	public Throwable getThrowable();

}
