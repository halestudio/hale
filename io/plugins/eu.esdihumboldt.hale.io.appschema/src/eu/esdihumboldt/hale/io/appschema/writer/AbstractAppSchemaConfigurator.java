package eu.esdihumboldt.hale.io.appschema.writer;

import java.io.IOException;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;

/**
 * Base class for HALE alignment to app-schema mapping translators.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class AbstractAppSchemaConfigurator extends AbstractAlignmentWriter {

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Translate HALE alignment to App-Schema Configuration",
				ProgressIndicator.UNKNOWN);
		if (getAlignment() == null) {
			throw new IOProviderConfigurationException("No alignment was provided.");
		}
		if (getTargetSchema() == null) {
			throw new IOProviderConfigurationException("No target schema was provided.");
		}
		if (getTarget() == null) {
			throw new IOProviderConfigurationException("No target was provided.");
		}

		DataStore dataStoreParam = getDataStoreParameter();
		AppSchemaMappingGenerator generator = new AppSchemaMappingGenerator(getAlignment(),
				getTargetSchema(), dataStoreParam);
		try {
			generator.generateMapping(reporter);

			handleMapping(generator, progress, reporter);
		} catch (IOProviderConfigurationException pce) {
			throw pce;
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
			return reporter;
		}

		progress.end();
		reporter.setSuccess(true);
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "GeoTools App-Schema Configurator";
	}

	/**
	 * Retrieves the DataStore configuration.
	 * 
	 * @return the configured DataStore
	 */
	protected DataStore getDataStoreParameter() {
		return getParameter(AppSchemaIO.PARAM_DATASTORE).as(DataStore.class);
	}

	/**
	 * Template method to be implemented by subclasses. Implementations should
	 * write the generated app-schema mapping to the specified target in the
	 * format specified by the content type parameter.
	 * 
	 * @param generator holds the generated app-schema mapping
	 * @param progress progress indicator
	 * @param reporter status reporter
	 * @throws IOProviderConfigurationException if an unsupported content type
	 *             has been specified
	 * @throws IOException if an error occurs writing to target
	 */
	protected abstract void handleMapping(AppSchemaMappingGenerator generator,
			ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException;

}
