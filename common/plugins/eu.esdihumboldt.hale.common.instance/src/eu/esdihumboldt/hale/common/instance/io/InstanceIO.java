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

package eu.esdihumboldt.hale.common.instance.io;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.HaleIO;

/**
 * Instance I/O utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class InstanceIO {

	/**
	 * ID of the action to load source data. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_SOURCE_DATA = "eu.esdihumboldt.hale.io.instance.read.source";

	/**
	 * ID of the action to save transformed data. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_SAVE_TRANSFORMED_DATA = "eu.esdihumboldt.hale.io.instance.write.transformed";

	/**
	 * ID of the action to save source data. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_SAVE_SOURCE_DATA = "eu.esdihumboldt.hale.io.instance.write.source";

	/**
	 * Creates an instance writer instance
	 * 
	 * @param contentType the content type the provider must match, may be
	 *            <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *            if contentType is set
	 * @return the I/O provider preconfigured with the content type if it was
	 *         given or <code>null</code> if no matching I/O provider is found
	 */
	public static InstanceWriter createInstanceWriter(IContentType contentType, String providerId) {
		return HaleIO.createIOProvider(InstanceWriter.class, contentType, providerId);
	}

}
