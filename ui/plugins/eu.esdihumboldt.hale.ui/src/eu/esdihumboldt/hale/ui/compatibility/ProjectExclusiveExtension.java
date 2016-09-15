/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.compatibility;

import java.util.List;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.eclipse.util.extension.ObjectExtension;
import de.fhg.igd.eclipse.util.extension.exclusive.AbstractExclusiveExtension;
import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * {@link ExclusiveExtension} that saves/loads the current extension object in a
 * Project from the {@link ProjectService}.
 * 
 * @param <T> the extension object type
 * @param <F> the extension object factory type
 * 
 * @author Sebastian Reinhardt
 */
public abstract class ProjectExclusiveExtension<T, F extends ExtensionObjectFactory<T>>
		extends AbstractExclusiveExtension<T, F> {

	/**
	 * The preference key
	 */
	private final String preferenceKey;

	/**
	 * Constructor
	 * 
	 * @param extension the internal extension
	 * @param preferenceKey the preference key
	 */
	public ProjectExclusiveExtension(ObjectExtension<T, F> extension, final String preferenceKey) {
		super(extension);

		this.preferenceKey = preferenceKey;

		final ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

		addListener(new ExclusiveExtensionListener<T, F>() {

			@Override
			public void currentObjectChanged(T current, F definition) {
				if (isSaveAllowed(current, definition)) {
					IConfigurationService conf = ps.getConfigurationService();

					// get the current setting
					String currentValue = conf.get(preferenceKey);
					if (currentValue == null && definition.getIdentifier().equals(getDefaultId())) {
						/*
						 * No setting present and the default is used. Then
						 * don't save the setting, as it may otherwise mark a
						 * project changed that doesn't have any real changes.
						 */
						return;
					}
					if (currentValue != null && currentValue.equals(definition.getIdentifier())) {
						// not a change
						return;
					}

					conf.set(preferenceKey, definition.getIdentifier());
				}
			}
		});

		ps.addListener(new ProjectServiceAdapter() {

			@Override
			public void afterLoad(ProjectService projectService) {
				String key = projectService.getConfigurationService().get(preferenceKey);
				ProjectExclusiveExtension.this.setCurrent(key);
			}
		});
	}

	/**
	 * Determines if saving the state to the project is allowed
	 * 
	 * @param current the extension object
	 * @param definition the extension object definition
	 * 
	 * @return if saving the state is allowed
	 */
	protected boolean isSaveAllowed(T current, F definition) {
		return true;
	}

	/**
	 * Determines if loading the given factory from the project is allowed
	 * 
	 * @param definition the extension object definition
	 * 
	 * @return if loading the factory is allowed
	 */
	protected boolean isLoadAllowed(F definition) {
		return true;
	}

	/**
	 * @see AbstractExclusiveExtension#getInitialFactory()
	 */
	@Override
	protected F getInitialFactory() {
		String identifier = PlatformUI.getWorkbench().getService(ProjectService.class)
				.getConfigurationService().get(preferenceKey);

		List<F> factories = getFactories();

		if (identifier != null) {
			// find factory to load
			for (F factory : factories) {
				if (isLoadAllowed(factory) && factory.getIdentifier().equals(identifier)) {
					return factory;
				}
			}
		}

		identifier = getDefaultId();
		// find default factory
		for (F factory : factories) {
			if (isLoadAllowed(factory) && factory.getIdentifier().equals(identifier)) {
				return factory;
			}
		}

		// return default factory found
		if (!factories.isEmpty()) {
			return getDefaultFactory(factories);
		}
		else {
			return getFallbackFactory();
		}
	}

	/**
	 * Get the identifier of the default factory to select, if no configuration
	 * is present.
	 * 
	 * @return the identifier of the default factory
	 */
	protected abstract String getDefaultId();

	/**
	 * Get the default factory to use
	 * 
	 * @param factories the available factories (non-empty list)
	 * 
	 * @return the factory to set as initial factory
	 */
	protected F getDefaultFactory(List<F> factories) {
		for (F factory : factories) {
			if (isLoadAllowed(factory)) {
				return factory;
			}
		}

		return getFallbackFactory();
	}

	/**
	 * Get the factory to use when no extensions are configured
	 * 
	 * @return the factory to use when no extensions are configured
	 */
	protected abstract F getFallbackFactory();

}
