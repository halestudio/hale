/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
