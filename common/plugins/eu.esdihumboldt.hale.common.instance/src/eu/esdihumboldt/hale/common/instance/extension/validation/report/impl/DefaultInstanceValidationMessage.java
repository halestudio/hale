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

package eu.esdihumboldt.hale.common.instance.extension.validation.report.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationLocation;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Default implementation of {@link InstanceValidationMessage}.
 * 
 * @author Kai Schwierczek
 */
public class DefaultInstanceValidationMessage extends MessageImpl
		implements InstanceValidationMessage {

	private final InstanceReference instanceReference;
	private final QName type;
	private final List<QName> path;
	private final String category;

	/**
	 * Create a new instance validation message.
	 * 
	 * @param instanceReference the instance reference this message is
	 *            associated to, may be null
	 * @param type the type's name
	 * @param path the path within the type
	 * @param category the message's category
	 * @param message the message string
	 */
	public DefaultInstanceValidationMessage(InstanceReference instanceReference, QName type,
			List<QName> path, String category, String message) {
		super(message, null);
		this.instanceReference = instanceReference;
		this.type = type;
		this.path = path;
		this.category = category;
	}

	/**
	 * Create a new instance validation message.
	 * 
	 * @param location the validation location
	 * @param category the message's category
	 * @param message the message string
	 */
	public DefaultInstanceValidationMessage(ValidationLocation location, String category,
			String message) {
		this(location.getReference(), location.getType(), location.getPath(), category, message);
	}

	/**
	 * @see InstanceValidationMessage#getInstanceReference()
	 */
	@Override
	public InstanceReference getInstanceReference() {
		return instanceReference;
	}

	/**
	 * @see InstanceValidationMessage#getType()
	 */
	@Override
	public QName getType() {
		return type;
	}

	/**
	 * @see InstanceValidationMessage#getPath()
	 */
	@Override
	public List<QName> getPath() {
		return path;
	}

	/**
	 * @see InstanceValidationMessage#getCategory()
	 */
	@Override
	public String getCategory() {
		return category;
	}

	/**
	 * @see MessageImpl#getFormattedMessage()
	 */
	@Override
	public String getFormattedMessage() {
		// build path string
		StringBuilder pathBuilder = new StringBuilder();
		// add type if available
		if (type != null)
			pathBuilder.append(type.getLocalPart());
		// separator between type and path
		if (!path.isEmpty())
			pathBuilder.append('#');
		// the path
		for (QName pathPart : path)
			pathBuilder.append(pathPart.getLocalPart()).append('.');
		// remove last dot
		if (!path.isEmpty())
			pathBuilder.setLength(pathBuilder.length() - 1);
		// add separator to message
		if (pathBuilder.length() > 0)
			pathBuilder.append(": ");
		String pathString = pathBuilder.toString();

		// return string containing all information
		return MessageFormat.format("{0}{1}: {2}", pathString, category, getMessage());
	}
}
