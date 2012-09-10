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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.AbstractObjectDefinition;
import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterLayoutFactory;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * {@link PainterLayout} extension.
 * 
 * @author Simon Templer
 */
public class PainterLayoutExtension extends AbstractExtension<PainterLayout, PainterLayoutFactory> {

	/**
	 * {@link PainterLayout} factory based on an {@link IConfigurationElement}.
	 */
	public static class ConfigurationPainterLayoutFactory extends
			AbstractConfigurationFactory<PainterLayout> implements PainterLayoutFactory {

		/**
		 * Create a {@link PainterLayout} factory based on the given
		 * configuration element.
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationPainterLayoutFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(PainterLayout instance) {
			// TODO ?
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		/**
		 * @see AbstractObjectFactory#getIconURL()
		 */
		@Override
		public URL getIconURL() {
			return getIconURL("icon");
		}

		/**
		 * @see PainterLayoutFactory#getPaintersToLayout()
		 */
		@Override
		public List<PainterProxy> getPaintersToLayout() {
			List<PainterProxy> proxies = new ArrayList<PainterProxy>();
			for (IConfigurationElement child : conf.getChildren()) {
				if (child.getName().equals("tileoverlay")) {
					proxies.add(new TileOverlayProxy(child.getAttribute("ref")));
				}
				else {
					// not supported
				}
			}
			return proxies;
		}

		/**
		 * @see AbstractObjectDefinition#getPriority()
		 */
		@Override
		public int getPriority() {
			String order = conf.getAttribute("order");

			if (order != null) {
				try {
					return Integer.parseInt(order);
				} catch (NumberFormatException e) {
					// ignore, use default
				}
			}

			return super.getPriority();
		}

	}

	/**
	 * The extension point ID
	 */
	private static final String ID = "eu.esdihumboldt.hale.ui.views.styledmap.layout";

	/**
	 * Default constructor
	 */
	public PainterLayoutExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected PainterLayoutFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConfigurationPainterLayoutFactory(conf);
	}

}
