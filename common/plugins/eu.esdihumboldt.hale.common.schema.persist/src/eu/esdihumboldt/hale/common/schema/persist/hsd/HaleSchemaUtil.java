/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.persist.hsd;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;

/**
 * HALE Schema Definition related utilities.
 * 
 * @author Simon Templer
 */
public abstract class HaleSchemaUtil {

	/**
	 * Get the input stream to the HALE Schema XML Schema Definition.
	 * 
	 * @return the input stream, the caller has to close it
	 */
	public static InputStream getHaleSchemaXSD() {
		return HaleSchemaUtil.class.getResourceAsStream("hsd.xsd");
	}

	/**
	 * Combine the given schemas to a single schema.
	 * 
	 * @param schemas the schemas to combine
	 * @param reporter the reporter if available
	 * @return the combined schema
	 */
	public static Schema combineSchema(List<Schema> schemas, IOReporter reporter) {
		if (schemas == null || schemas.isEmpty()) {
			// empty schema
			return new DefaultSchema(null, null);
		}
		else if (schemas.size() == 1) {
			return schemas.get(0);
		}
		else {
			DefaultSchema result = new DefaultSchema(null, null);

			schemas.forEach(schema -> {
				schema.getTypes().forEach(type -> {
					if (reporter != null) {
						// check if type is already there
						QName name = type.getName();
						if (result.getType(name) != null) {
							reporter.warn(
									MessageFormat.format("Multiple definitions of type {0}", name));
						}
					}
					result.addType(type);
				});
			});

			return result;
		}
	}

}
