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

import java.util.ArrayList;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceValidator;

/**
 * Validator for transformed instances based on {@link InstanceValidator}.
 * 
 * @author Simon Templer
 */
public class DefaultTransformedInstanceValidator extends AbstractTransformedInstanceValidator {

	/**
	 * The instance validation context.
	 */
	protected final InstanceValidationContext context;

	private final InstanceValidator validator;

	/**
	 * Constructor.
	 * 
	 * @param reportHandler the handler for the validation report
	 * @param services the service provider, if available
	 * @see AbstractTransformedInstanceValidator#AbstractTransformedInstanceValidator(ReportHandler)
	 */
	public DefaultTransformedInstanceValidator(ReportHandler reportHandler,
			@Nullable ServiceProvider services) {
		super(reportHandler);

		this.context = new InstanceValidationContext();
		this.validator = InstanceValidator.createDefaultValidator(services);
	}

	@Override
	protected void validateInstance(Instance instance, InstanceValidationReporter reporter) {
		validator.validateInstance(instance, reporter, instance.getDefinition().getName(),
				new ArrayList<QName>(), false, null, context, null, null);
	}

	@Override
	protected void validateCompleted(InstanceValidationReporter reporter) {
		validator.validateContext(context, reporter);
	}

}
