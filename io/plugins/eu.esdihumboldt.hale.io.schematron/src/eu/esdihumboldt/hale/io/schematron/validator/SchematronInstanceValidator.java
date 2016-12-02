package eu.esdihumboldt.hale.io.schematron.validator;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.opengis.cite.validation.SchematronValidator;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator;

/***
 * 
 * Validator for ISO Schematron
 * 
 * @author Florian Esser
 */
public class SchematronInstanceValidator extends AbstractInstanceValidator {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Performing Schematron validation", ProgressIndicator.UNKNOWN);

		final Source xmlSource = new StreamSource(this.getSource().getInput());
		for (Locatable schema : this.getSchemas()) {
			try {
				final DefaultInputSupplier inputSupplier = new DefaultInputSupplier(
						schema.getLocation());
				final Source schematronSource = new StreamSource(inputSupplier.getInput());
				final SchematronValidator validator = new SchematronValidator(schematronSource);

				Result result = validator.validate(xmlSource, /* svrlReport */true);
			} catch (Exception e) {

			}
		}

		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}
}
