/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.headless.transform.validate.impl;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.headless.transform.validate.TransformedInstanceValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.impl.DefaultInstanceValidationReporter;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Abstract base class for transformed instance validators.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformedInstanceValidator implements TransformedInstanceValidator {

	private static final ALogger log = ALoggerFactory
			.getLogger(AbstractTransformedInstanceValidator.class);

	private final ReportHandler reportHandler;
	private final InstanceValidationReporter reporter;

	/**
	 * Constructor.
	 * 
	 * @param reportHandler the report handler used to communicate validation
	 *            results.
	 */
	public AbstractTransformedInstanceValidator(ReportHandler reportHandler) {
		super();
		this.reportHandler = reportHandler;

		reporter = new DefaultInstanceValidationReporter(false);
	}

	@Override
	public void validateInstance(Instance instance) {
		validateInstance(instance, reporter);
	}

	@Override
	public void validateCompleted() {
		try {
			validateCompleted(reporter);
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.setSuccess(false);
			log.error("Error during completion of transformed instance validation", e);
		}
		reportHandler.publishReport(reporter);
	}

	/**
	 * Validate an instance.
	 * 
	 * @param instance the instance to validate
	 * @param reporter the validation reporter
	 */
	protected abstract void validateInstance(Instance instance,
			InstanceValidationReporter reporter);

	/**
	 * Perform validation of state collected during instance validation.
	 * 
	 * @param reporter the validation reporter
	 */
	protected abstract void validateCompleted(InstanceValidationReporter reporter);

}
