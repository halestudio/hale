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

package eu.esdihumboldt.hale.common.align.extension.transformation.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.AbstractTransformationFactory;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation;

/**
 * Extension for {@link TypeTransformation}s
 * 
 * @author Simon Templer
 */
public class TypeTransformationExtension extends
		AbstractTransformationExtension<TypeTransformation<?>, TypeTransformationFactory> {

	/**
	 * Factory for {@link TypeTransformation}s
	 */
	public static class TypeTransformationConfiguration extends
			AbstractTransformationFactory<TypeTransformation<?>> implements
			TypeTransformationFactory {

		/**
		 * @see AbstractTransformationFactory#AbstractTransformationFactory(IConfigurationElement)
		 */
		protected TypeTransformationConfiguration(IConfigurationElement conf) {
			super(conf);
		}

	}

	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.transformation";

	private static TypeTransformationExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static TypeTransformationExtension getInstance() {
		if (instance == null) {
			instance = new TypeTransformationExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	public TypeTransformationExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected TypeTransformationFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("typeTransformation")) {
			return new TypeTransformationConfiguration(conf);
		}

		return null;
	}

}
