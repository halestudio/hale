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
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.ConfigurationIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Advisor for loading an alignment using an existing {@link IOConfiguration}.
 * 
 * @author Simon Templer
 */
public class LoadAlignmentAdvisor extends ConfigurationIOAdvisor<AlignmentReader> {

	private final TypeIndex sourceSchema;
	private final TypeIndex targetSchema;
	private final PathUpdate updater;

	/**
	 * The resulting alignment
	 */
	private Alignment alignment;

	/**
	 * Create an advisor for loading an alignment based on the given I/O
	 * configuration.
	 * 
	 * @param conf the I/O configuration
	 * @param sourceSchema the source schema
	 * @param targetSchema the target schema
	 * @param updater the path updater
	 */
	public LoadAlignmentAdvisor(IOConfiguration conf, TypeIndex sourceSchema,
			TypeIndex targetSchema, PathUpdate updater) {
		super(conf);

		this.sourceSchema = sourceSchema;
		this.targetSchema = targetSchema;
		this.updater = updater;
	}

	/**
	 * @see ConfigurationIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(AlignmentReader provider) {
		super.prepareProvider(provider);

		provider.setSourceSchema(sourceSchema);
		provider.setTargetSchema(targetSchema);
		provider.setPathUpdater(updater);
	}

	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(AlignmentReader provider) {
		alignment = provider.getAlignment();
	}

	/**
	 * Get the loaded alignment.
	 * 
	 * @return the alignment or <code>null</code> if it was not loaded or
	 *         loading failed
	 */
	public Alignment getAlignment() {
		return alignment;
	}

}
