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

package eu.esdihumboldt.hale.align.extension.transformation;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import eu.esdihumboldt.hale.align.transformation.function.EvaluationFunction;
import eu.esdihumboldt.hale.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.align.transformation.function.impl.EvaluationPropertyTransformation;

/**
 * Extension for {@link PropertyTransformation}s
 * @author Simon Templer
 */
public class PropertyTransformationExtension
		extends AbstractTransformationExtension<PropertyTransformation<?>, PropertyTransformationFactory> {

	/**
	 * Factory for {@link PropertyTransformation}s that are defined directly
	 */
	public static class PropertyTransformationConfiguration extends
			AbstractTransformationFactory<PropertyTransformation<?>> implements
			PropertyTransformationFactory {

		/**
		 * @see AbstractTransformationFactory#AbstractTransformationFactory(IConfigurationElement)
		 */
		protected PropertyTransformationConfiguration(IConfigurationElement conf) {
			super(conf);
		}

	}
	
	/**
	 * Factory for {@link PropertyTransformation}s that are defined through a
	 * {@link EvaluationFunction}
	 */
	public static class EvaluatorConfiguration extends
			AbstractTransformationFactory<PropertyTransformation<?>> implements
			PropertyTransformationFactory {

		/**
		 * @see AbstractTransformationFactory#AbstractTransformationFactory(IConfigurationElement)
		 */
		protected EvaluatorConfiguration(IConfigurationElement conf) {
			super(conf);
		}

		/**
		 * @see AbstractConfigurationFactory#createExtensionObject()
		 */
		@Override
		public PropertyTransformation<?> createExtensionObject()
				throws Exception {
			EvaluationFunction<?> evaluator = (EvaluationFunction<?>) conf.createExecutableExtension("class");
			
			return EvaluationPropertyTransformation.createTransformation(evaluator);
		}

	}

	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.transformation";

	/**
	 * Default constructor
	 */
	public PropertyTransformationExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected PropertyTransformationFactory createFactory(
			IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("propertyTransformation")) {
			return new PropertyTransformationConfiguration(conf);
		}
		
		if (conf.getName().equals("evaluator")) {
			return new EvaluatorConfiguration(conf);
		}
		
		return null;
	}

}
