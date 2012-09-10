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
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Abstract alignment reader implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractAlignmentReader extends AbstractImportProvider implements
		AlignmentReader {

	private TypeIndex sourceSchema;
	private TypeIndex targetSchema;

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
