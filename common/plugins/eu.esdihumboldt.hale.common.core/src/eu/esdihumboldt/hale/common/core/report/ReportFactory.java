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

package eu.esdihumboldt.hale.common.core.report;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * Factory for Reports.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("rawtypes")
public class ReportFactory extends AbstractObjectFactory<Report, ReportDefinition<?>> {

	/**
	 * Extension point ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.report";

	/**
	 * Logger
	 */
	private static final ALogger _log = ALoggerFactory.getLogger(ReportFactory.class);

	private final ReportDefintions reportExtension = new ReportDefintions();

	/**
	 * Contains all {@link ReportDefinition} for
	 * {@link MessageFactory#getDefinitions()}.
	 */
	private ArrayList<ReportDefinition<?>> reportDefinitions = new ArrayList<ReportDefinition<?>>();

	/**
	 * Instance
	 */
	private static ReportFactory _instance;

	/**
	 * Constructor
	 */
	private ReportFactory() {
		/* nothing */
		super();
	}

	/**
	 * Get the instance of this factory.
	 * 
	 * @return the instance
	 */
	public static ReportFactory getInstance() {
		if (_instance == null) {
			_instance = new ReportFactory();
		}

		return _instance;
	}

	/**
	 * 
	 * @author Andreas Burchert
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 */
	public static class ReportDefintions extends
			AbstractExtension<ReportDefinition<?>, ExtensionObjectFactory<ReportDefinition<?>>> {

		/**
		 * Default constructor
		 */
		public ReportDefintions() {
			super(ReportFactory.EXTENSION_ID);
		}

		/**
		 * @see de.cs3d.util.eclipse.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
		 */
		@Override
		protected ExtensionObjectFactory<ReportDefinition<?>> createFactory(
				IConfigurationElement conf) throws Exception {
			if (conf.getName().equals("reportDefinition")) {
				return new AbstractConfigurationFactory<ReportDefinition<?>>(conf, "class") {

					@Override
					public void dispose(ReportDefinition<?> rd) {
						// do nothing
					}

					@Override
					public String getDisplayName() {
						return getIdentifier();
					}

					@Override
					public String getIdentifier() {
						return conf.getAttribute("id");
					}

				};
			}

			return null;
		}
	}

	/**
	 * @see eu.esdihumboldt.util.definition.AbstractObjectFactory#getDefinitions()
	 */
	@Override
	public List<ReportDefinition<?>> getDefinitions() {
		// check if definitions are available
		if (this.reportDefinitions.size() > 0) {
			return this.reportDefinitions;
		}

		// get all factories
		List<ExtensionObjectFactory<ReportDefinition<?>>> factories = reportExtension
				.getFactories();

		// create arrayliet
		List<ReportDefinition<?>> result = new ArrayList<ReportDefinition<?>>();

		// iterate through factories and create ReportDefinition
		for (ExtensionObjectFactory<ReportDefinition<?>> r : factories) {
			try {
				ReportDefinition<?> rd = r.createExtensionObject();
				result.add(rd);
			} catch (Exception e) {
				_log.error("Error during object creation", e);
			}
		}

		this.reportDefinitions.addAll(result);
		return result;
	}
}
