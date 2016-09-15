/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.extension;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.io.xslt.XslTransformation;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;

/**
 * Extension for {@link XslTypeTransformation}s.
 * 
 * @author Simon Templer
 * @param <T> the concrete type of XSLT transformation
 * @param <X> the concrete function type associated to the XSLT transformation
 *            type
 */
public abstract class AbstractXslTransformationExtension<T extends XslTransformation, X extends FunctionDefinition<?>>
		extends AbstractExtension<T, XslTransformationFactory<T, X>> implements
		XslExtensionConstants {

	private static final ALogger log = ALoggerFactory
			.getLogger(AbstractXslTransformationExtension.class);

	/**
	 * Default factory based on a configuration element.
	 */
	public class DefaultFactory extends AbstractConfigurationFactory<T> implements
			XslTransformationFactory<T, X> {

		/**
		 * Create a factory for a {@link XslTypeTransformation} based on the
		 * given configuration element.
		 * 
		 * @param conf the configuration element
		 */
		public DefaultFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public String getFunctionId() {
			return conf.getAttribute("function");
		}

		@Override
		public void dispose(T instance) {
			// do nothing
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getFunction().getDisplayName() + " XSLT template";
		}

		@Override
		public X getFunction() {
			return AbstractXslTransformationExtension.this.getFunction(getFunctionId());
		}

	}

	/**
	 * Default constructor
	 */
	public AbstractXslTransformationExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * Retrieve the function associated to a function ID in the context of a
	 * XSLT transformation provided by this extension.
	 * 
	 * @param functionId the function identifier
	 * @return the function
	 */
	protected abstract X getFunction(String functionId);

	/**
	 * Get the name of the supported configuration element.
	 * 
	 * @return the configuration element name
	 */
	protected abstract String getConfigurationElementName();

	@Override
	protected XslTransformationFactory<T, X> createFactory(IConfigurationElement conf)
			throws Exception {
		if (getConfigurationElementName().equals(conf.getName())) {
			return new DefaultFactory(conf);
		}
		return null;
	}

	/**
	 * Get the XSLT transformation for the function identified by the given
	 * function identifier.
	 * 
	 * @param functionId the function identifier
	 * @return the associated XSLT transformation, for each call a new instance
	 * @throws Exception if no XSLT transformation is available for the function
	 *             or the instantiation fails
	 */
	public T getTransformation(final String functionId) throws Exception {
		List<XslTransformationFactory<T, X>> factories = getFactories(new FactoryFilter<T, XslTransformationFactory<T, X>>() {

			@Override
			public boolean acceptFactory(XslTransformationFactory<T, X> factory) {
				return factory.getFunctionId().equals(functionId);
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<T, XslTransformationFactory<T, X>> collection) {
				return true;
			}
		});

		if (factories.isEmpty()) {
			throw new IllegalStateException("No XSLT transformation for function " + functionId
					+ "found");
		}

		if (factories.size() > 1) {
			log.warn("Multiple XSLT transformations for the transformation function " + functionId
					+ " found, using XSLT transformation with ID "
					+ factories.get(0).getIdentifier());
		}

		return factories.get(0).createExtensionObject();
	}

}
