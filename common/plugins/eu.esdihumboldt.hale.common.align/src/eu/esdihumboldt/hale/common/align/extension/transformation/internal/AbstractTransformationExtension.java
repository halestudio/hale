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

import java.util.List;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import eu.esdihumboldt.hale.common.align.extension.transformation.TransformationFactory;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;

/**
 * Abstract extension for transformation functions
 * 
 * @param <T> the transformation type
 * @param <F> the transformation factory type
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationExtension<T extends TransformationFunction<?>, F extends TransformationFactory<T>>
		extends AbstractExtension<T, F> {

	/**
	 * @see AbstractExtension#AbstractExtension(String)
	 */
	public AbstractTransformationExtension(String extensionPointID) {
		super(extensionPointID);
	}

	/**
	 * Get all transformations for a function
	 * 
	 * @param functionId the function ID
	 * @return the transformations matching the function
	 */
	public List<F> getTransformations(final String functionId) {
		return getFactories(new FactoryFilter<T, F>() {

			@Override
			public boolean acceptFactory(F factory) {
				return functionId.equals(factory.getFunctionId());
			}

			@Override
			public boolean acceptCollection(ExtensionObjectFactoryCollection<T, F> collection) {
				return true;
			}

		});
	}

}
