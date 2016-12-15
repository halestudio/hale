package eu.esdihumboldt.hale.io.schematron.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.ValidationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.opengis.cite.validation.SchematronValidator;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.ConfigurableInstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator;

/***
 * 
 * Validator for ISO Schematron
 * 
 * @author Florian Esser
 */
public class SchematronInstanceValidator extends AbstractInstanceValidator
		implements ConfigurableInstanceValidator {

	private Locatable schematronRules;

	/**
	 * 
	 */
	public SchematronInstanceValidator() {
		super();
//
//		this.schematronRules = new FileIOSupplier(new File("C:/Temp/ProtectedSites2.sch"));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Performing Schematron validation", ProgressIndicator.UNKNOWN);

		final InputStream sourceInput = this.getSource().getInput();
		if (sourceInput == null) {
			throw new RuntimeException("No input for Schematron validator");
		}
		final Source xmlSource = new StreamSource(sourceInput);

		final DefaultInputSupplier schematronInputSupplier = new DefaultInputSupplier(
				this.schematronRules.getLocation());
		final InputStream schematronInput = schematronInputSupplier.getInput();
		if (schematronInput == null) {
			throw new RuntimeException("No rules input for Schematron validator");
		}
		final Source schematronSource = new StreamSource(schematronInput);

		try {
			final SchematronValidator validator = new SchematronValidator(schematronSource);
			final Result result = validator.validate(xmlSource, /* svrlReport */false);

			final StringWriter reportWriter = new StringWriter();
			SchematronUtils.convertValidatorResult(result, reportWriter);

			reporter.setSuccess(!validator.ruleViolationsDetected());
			if (validator.ruleViolationsDetected()) {
				reporter.error(new IOMessageImpl(reportWriter.toString(),
						new ValidationException(reportWriter.toString())));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			schematronInput.close();
			progress.end();
		}

		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XML file";
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.ConfigurableInstanceValidator#configure()
	 */
	@Override
	public void configure(IOProviderDescriptor factory) {
		SchematronValidatorConfigurationDialog dialog = new SchematronValidatorConfigurationDialog(
				Display.getCurrent().getActiveShell());

		dialog.create();
		if (dialog.open() == Dialog.OK) {
//			factory.factory.setParameter("schematron.rules",
//					Value.of(dialog.getSchematronRulesFile()));
		}
	}
}
