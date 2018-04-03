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

package eu.esdihumboldt.hale.common.instance.io.impl;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.GZipEnabledImport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;

/**
 * Abstract {@link InstanceValidator} base implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class AbstractInstanceValidator extends GZipEnabledImport
		implements InstanceValidator {

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
	 * Fails if there are no schemas set
	 * 
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		// Don't call super.validate() here because InstanceValidators
		// may be called in a context where the source property is unset.

		if (schemas == null || schemas.length == 0) {
			fail("No schemas provided for validation");
		}
	}

	/**
	 * @see IOProvider#createReporter()
	 */
	@Override
	public IOReporter createReporter() {
		return new DefaultIOReporter(getSource(), getReportLabel(), getReportType(), false) {

			@Override
			protected String getFailSummary() {
				return getDefaultFailSummary();
			}

			@Override
			protected String getSuccessSummary() {
				return getDefaultSuccessSummary();
			}

		};
	}

	/**
	 * @return the label of the validation report
	 */
	protected abstract String getReportLabel();

	/**
	 * @return the identifier of the validation report type, by convention the
	 *         same as the I/O provider id
	 */
	protected abstract String getReportType();

	/**
	 * @return the name default failure summary for the validation report
	 */
	protected abstract String getDefaultFailSummary();

	/**
	 * @return the name default success summary for the validation report
	 */
	protected abstract String getDefaultSuccessSummary();

}
