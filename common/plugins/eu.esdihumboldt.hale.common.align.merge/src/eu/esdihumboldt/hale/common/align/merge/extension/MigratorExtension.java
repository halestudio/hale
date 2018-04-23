/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.merge.extension;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.merge.MergeCellMigrator;

/**
 * Extension point for merge migrators.
 * 
 * @author Simon Templer
 */
public class MigratorExtension extends AbstractExtension<MergeCellMigrator, MigratorFactory> {

	/**
	 * Default migrator factory.
	 */
	public class DefaultMigratorFactory extends AbstractConfigurationFactory<MergeCellMigrator>
			implements MigratorFactory {

		private final Set<String> supportedFunctions = new HashSet<>();

		@SuppressWarnings("javadoc")
		protected DefaultMigratorFactory(IConfigurationElement conf) {
			super(conf, "class");

			collectFunctions(conf.getChildren("typeFunction"));
			collectFunctions(conf.getChildren("propertyFunction"));
		}

		private void collectFunctions(IConfigurationElement[] children) {
			for (IConfigurationElement child : children) {
				String id = child.getAttribute("ref");
				if (id != null) {
					supportedFunctions.add(id);
				}
			}
		}

		@Override
		public void dispose(MergeCellMigrator instance) {
			// nothing to do
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		@Override
		public boolean supportsFunction(String functionId) {
			return supportedFunctions.contains(functionId);
		}

	}

	private static final String ID = "eu.esdihumboldt.hale.align.merge.migrator";

	private static final ALogger log = ALoggerFactory.getLogger(MigratorExtension.class);

	private static volatile MigratorExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static MigratorExtension getInstance() {
		if (instance == null) {
			instance = new MigratorExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	protected MigratorExtension() {
		super(ID);
	}

	@Override
	protected MigratorFactory createFactory(IConfigurationElement conf) throws Exception {
		if ("migrator".equals(conf.getName())) {
			return new DefaultMigratorFactory(conf);
		}
		return null;
	}

	/**
	 * Get the migrator for the function with the given identifier.
	 * 
	 * @param functionId the function identifier
	 * @return the merge migrator if there is one available
	 * @throws Exception if creating the migrator fails
	 */
	public Optional<MergeCellMigrator> getMigrator(final String functionId) throws Exception {
		List<MigratorFactory> factories = getFactories(
				new FactoryFilter<MergeCellMigrator, MigratorFactory>() {

					@Override
					public boolean acceptFactory(MigratorFactory factory) {
						return factory.supportsFunction(functionId);
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<MergeCellMigrator, MigratorFactory> collection) {
						return false;
					}
				});

		if (factories.isEmpty()) {
			return Optional.empty();
		}
		else {
			if (factories.size() > 1) {
				log.warn(MessageFormat.format(
						"Multiple factories for merge migrator for function {0} found, using first one",
						functionId));
			}
			return Optional.ofNullable(factories.get(0).createExtensionObject());
		}
	}

}
