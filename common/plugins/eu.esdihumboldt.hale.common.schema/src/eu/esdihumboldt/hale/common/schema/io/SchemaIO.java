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

package eu.esdihumboldt.hale.common.schema.io;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.HaleIO;

/**
 * Schema I/O utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class SchemaIO {

	/**
	 * ID of the action to load a source schema. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_SOURCE_SCHEMA = "eu.esdihumboldt.hale.io.schema.read.source";

	/**
	 * ID of the action to load a target schema. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_TARGET_SCHEMA = "eu.esdihumboldt.hale.io.schema.read.target";

	/**
	 * Creates a schema reader instance
	 * 
	 * @param contentType the content type the provider must match, may be
	 *            <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *            if contentType is set
	 * @return the I/O provider preconfigured with the content type if it was
	 *         given or <code>null</code> if no matching I/O provider is found
	 */
	public static SchemaReader createSchemaReader(IContentType contentType, String providerId) {
		return HaleIO.createIOProvider(SchemaReader.class, contentType, providerId);
	}

}
