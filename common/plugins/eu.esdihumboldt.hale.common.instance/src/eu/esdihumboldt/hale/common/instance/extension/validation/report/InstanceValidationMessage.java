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

package eu.esdihumboldt.hale.common.instance.extension.validation.report;

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Instance validation message.
 * 
 * @author Kai Schwierczek
 */
public interface InstanceValidationMessage extends Message {

	/**
	 * Returns the reference to the instance this message is about. The
	 * reference is only valid as long as the data set didn't change.
	 * 
	 * @return the reference to the instance this message is about, may be null
	 */
	public InstanceReference getInstanceReference();

	/**
	 * Returns the type name this message belongs to. If the validation wasn't
	 * started at an instance this may be <code>null</code>.
	 * 
	 * @return the type name this message belongs to
	 */
	public QName getType();

	/**
	 * Returns the path within the type this message is about.
	 * 
	 * @return the path within the type this message is about
	 */
	public List<QName> getPath();

	/**
	 * Returns the category of this message (i. e. which validator created the
	 * message).
	 * 
	 * @return the category of this message
	 */
	public String getCategory();
}
