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

package eu.esdihumboldt.hale.common.instance.io.impl;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;

/**
 * Abstract {@link InstanceValidator} base implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class AbstractInstanceValidator extends AbstractImportProvider implements
		InstanceValidator {

	private Locatable[] schemas;

	/**
	 * @see InstanceValidator#setSchemas(Locatable[])
	 */
	@Override
	public void setSchemas(Locatable... schemas) {
		this.schemas = schemas;
	}

	/**
	 * @return the schemas
	 */
	protected Locatable[] getSchemas() {
		return schemas;
	}

	/**
	 * Additionally fails if there are no schemas set
	 * 
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (schemas == null || schemas.length == 0) {
			fail("No schemas provided for validation");
		}
	}

}
