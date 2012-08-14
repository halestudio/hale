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

package eu.esdihumboldt.hale.common.instancevalidator.report.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;

/**
 * Default implementation of {@link InstanceValidationMessage}.
 *
 * @author Kai Schwierczek
 */
public class DefaultInstanceValidationMessage extends MessageImpl implements InstanceValidationMessage {
	private final InstanceReference instanceReference;
	private final QName type;
	private final List<QName> path;
	private final String category;

	/**
	 * Create a new instance validation message.
	 *
	 * @param instanceReference the instance reference this message is associated to, may be null
	 * @param type the type's name
	 * @param path the path within the type
	 * @param category the message's category
	 * @param message the message string
	 */
	public DefaultInstanceValidationMessage(InstanceReference instanceReference, QName type, List<QName> path, String category, String message) {
		super(message, null);
		this.instanceReference = instanceReference;
		this.type = type;
		this.path = path;
		this.category = category;
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
