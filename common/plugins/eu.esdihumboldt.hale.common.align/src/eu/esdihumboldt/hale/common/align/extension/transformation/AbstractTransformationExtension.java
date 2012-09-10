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

package eu.esdihumboldt.hale.common.align.extension.transformation;

import java.util.List;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
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
