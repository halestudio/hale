/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.io.impl;

import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.schema.io.SchemaWriter;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Base implementation of a {@link SchemaWriter}.
 * 
 * @author Simon Templer
 */
public abstract class AbstractSchemaWriter extends AbstractExportProvider implements SchemaWriter {

	private SchemaSpace schemas;

	@Override
	public void setSchemas(SchemaSpace schemas) {
		this.schemas = schemas;
	}

	/**
	 * @return the schemas to write
	 */
	public SchemaSpace getSchemas() {
		return schemas;
	}

}
