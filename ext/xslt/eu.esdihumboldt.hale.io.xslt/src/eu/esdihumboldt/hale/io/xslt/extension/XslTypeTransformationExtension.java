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

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;

/**
 * Extension for {@link XslTypeTransformation}s.
 * 
 * @author Simon Templer
 */
public class XslTypeTransformationExtension extends
		AbstractExtension<XslTypeTransformation, XslTypeTransformationFactory> implements
		XslExtensionConstants {

	private static XslTypeTransformationExtension instance;

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static XslTypeTransformationExtension getInstance() {
		synchronized (XslTypeTransformationExtension.class) {
			if (instance == null) {
				instance = new XslTypeTransformationExtension();
			}
		}
		return instance;
	}

	/**
	 * Default factory based on a configuration element.
	 */
	public class DefaultFactory extends AbstractConfigurationFactory<XslTypeTransformation>
			implements XslTypeTransformationFactory {

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
		public void dispose(XslTypeTransformation instance) {
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
		public TypeFunction getFunction() {
			return TypeFunctionExtension.getInstance().get(getFunctionId());
		}

	}

	/**
	 * Default constructor
	 */
	public XslTypeTransformationExtension() {
		super(EXTENSION_ID);
	}

	@Override
	protected XslTypeTransformationFactory createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("typeTransformation")) {
			return new DefaultFactory(conf);
		}
		return null;
	}

}
