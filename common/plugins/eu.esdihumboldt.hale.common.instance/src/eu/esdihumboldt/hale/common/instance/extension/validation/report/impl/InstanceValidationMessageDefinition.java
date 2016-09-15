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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Definition for {@link InstanceValidationMessage}s.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationMessageDefinition extends
		AbstractMessageDefinition<InstanceValidationMessage> {

	/**
	 * Key for category string.
	 */
	public static final String KEY_CATEGORY = "category";
	/**
	 * Key for type name.
	 */
	public static final String KEY_TYPE = "type";
	/**
	 * Key for count of path members.
	 */
	public static final String KEY_PATH_COUNT = "path_count";
	/**
	 * Key prefix for path member.
	 */
	public static final String KEY_PATH_PREFIX = "path_";

	/**
	 * Constructor.
	 */
	public InstanceValidationMessageDefinition() {
		super(InstanceValidationMessage.class, "instance_validation_message");
	}

	/**
	 * @see AbstractMessageDefinition#createMessage(Properties)
	 */
	@Override
	protected InstanceValidationMessage createMessage(Properties props) {
		// instance reference isn't valid anymore either way... simply use null
		InstanceReference ref = null;

		QName type = QName.valueOf(props.getProperty(KEY_TYPE));

		int pathCount = Integer.valueOf(props.getProperty(KEY_PATH_COUNT));
		List<QName> path = new ArrayList<QName>(pathCount);
		for (int i = 0; i < pathCount; i++)
			path.add(QName.valueOf(props.getProperty(KEY_PATH_PREFIX + i)));

		String category = props.getProperty(KEY_CATEGORY);
		String message = props.getProperty(KEY_MESSAGE);

		return new DefaultInstanceValidationMessage(ref, type, path, category, message);
	}

	/**
	 * @see AbstractMessageDefinition#asProperties(eu.esdihumboldt.hale.common.core.report.Message)
	 */
	@Override
	protected Properties asProperties(InstanceValidationMessage message) {
		Properties props = super.asProperties(message);

		props.setProperty(KEY_TYPE, message.getType().toString());

		List<QName> path = message.getPath();
		props.setProperty(KEY_PATH_COUNT, String.valueOf(path.size()));
		for (int i = 0; i < path.size(); i++)
			props.setProperty(KEY_PATH_PREFIX + i, path.get(i).toString());

		props.setProperty(KEY_CATEGORY, message.getCategory());
		// message is stored in super.asProperties

		return props;
	}
}
