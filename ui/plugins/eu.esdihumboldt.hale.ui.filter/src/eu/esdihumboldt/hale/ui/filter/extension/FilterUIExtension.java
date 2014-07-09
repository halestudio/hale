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

package eu.esdihumboldt.hale.ui.filter.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.filter.FilterDialogFactory;

/**
 * Filter UI extension.
 * 
 * @author Simon Templer
 */
public class FilterUIExtension extends
		AbstractExtension<FilterDialogFactory, FilterDialogDefinition> {

	/**
	 * {@link FilterDialogDefinition} created from a configuration element.
	 */
	public class DefaultFactory extends AbstractConfigurationFactory<FilterDialogFactory> implements
			FilterDialogDefinition {

		/**
		 * Create a {@link FilterDialogDefinition} from the given configuration
		 * element.
		 * 
		 * @param conf the configuration element
		 */
		protected DefaultFactory(IConfigurationElement conf) {
			super(conf, "dialogFactory");
		}

		@Override
		public void dispose(FilterDialogFactory instance) {
			// ignore
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("filter");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

	}

	private static FilterUIExtension instance;

	/**
	 * @return the singleton extension instance
	 */
	public static FilterUIExtension getInstance() {
		synchronized (FilterUIExtension.class) {
			if (instance == null) {
				instance = new FilterUIExtension();
			}
		}
		return instance;
	}

	/**
	 * The extension ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.filter";

	/**
	 * Default constructor.
	 */
	public FilterUIExtension() {
		super(ID);
	}

	@Override
	protected FilterDialogDefinition createFactory(IConfigurationElement conf) throws Exception {
		return new DefaultFactory(conf);
	}

}
