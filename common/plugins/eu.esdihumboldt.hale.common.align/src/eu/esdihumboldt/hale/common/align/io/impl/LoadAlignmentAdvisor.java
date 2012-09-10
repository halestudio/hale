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

package eu.esdihumboldt.hale.common.align.io.impl;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
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
	 */
	public LoadAlignmentAdvisor(IOConfiguration conf, TypeIndex sourceSchema, TypeIndex targetSchema) {
		super(conf);

		this.sourceSchema = sourceSchema;
		this.targetSchema = targetSchema;
	}

	/**
	 * @see ConfigurationIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(AlignmentReader provider) {
		super.prepareProvider(provider);

		provider.setSourceSchema(sourceSchema);
		provider.setTargetSchema(targetSchema);
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
