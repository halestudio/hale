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

package eu.esdihumboldt.hale.common.align.io.impl;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.io.PathUpdate;

/**
 * Abstract alignment reader implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractAlignmentReader extends AbstractImportProvider implements
		AlignmentReader {

	private TypeIndex sourceSchema;
	private TypeIndex targetSchema;
	private PathUpdate updater;

	/**
	 * @see AlignmentReader#setSourceSchema(TypeIndex)
	 */
	@Override
	public void setSourceSchema(TypeIndex sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * Get the source schema
	 * 
	 * @return the source schema
	 */
	public TypeIndex getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @see AlignmentReader#setTargetSchema(TypeIndex)
	 */
	@Override
	public void setTargetSchema(TypeIndex targetSchema) {
		this.targetSchema = targetSchema;
	}

	/**
	 * Get the target schema
	 * 
	 * @return the target schema
	 */
	public TypeIndex getTargetSchema() {
		return targetSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.AlignmentReader#setPathUpdater(eu.esdihumboldt.util.io.PathUpdate)
	 */
	@Override
	public void setPathUpdater(PathUpdate updater) {
		this.updater = updater;
	}

	/**
	 * Get the location updater
	 * 
	 * @return the location updater
	 */
	public PathUpdate getPathUpdater() {
		return updater;
	}

	/**
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (sourceSchema == null) {
			fail("Source schema not set");
		}

		if (targetSchema == null) {
			fail("Target schema not set");
		}
	}

}
