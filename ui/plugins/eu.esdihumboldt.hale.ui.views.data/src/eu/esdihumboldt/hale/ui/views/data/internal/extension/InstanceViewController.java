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

package eu.esdihumboldt.hale.ui.views.data.internal.extension;

import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferenceStore;

import de.cs3d.ui.util.eclipse.extension.exclusive.PreferencesExclusiveExtension;
import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ObjectExtension;
import eu.esdihumboldt.hale.ui.views.data.InstanceViewer;
import eu.esdihumboldt.hale.ui.views.data.internal.compare.DefinitionInstanceTreeViewer;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class InstanceViewController extends PreferencesExclusiveExtension<InstanceViewer, InstanceViewFactory> {

	/**
	 * Factory for default instance view
	 */
	private static class DefaultViewFactory extends
			AbstractObjectFactory<InstanceViewer> implements
			InstanceViewFactory {

		/**
		 * @see ExtensionObjectFactory#createExtensionObject()
		 */
		@Override
		public InstanceViewer createExtensionObject() throws Exception {
			return new DefinitionInstanceTreeViewer();
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(InstanceViewer instance) {
			// dispose is handled externally
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return "default";
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		/**
		 * @see ExtensionObjectDefinition#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return DefinitionInstanceTreeViewer.class.getName();
		}

	}

	/**
	 * Default factory based on a configuration element.
	 */
	private static class ConfigurationViewFactory extends AbstractConfigurationFactory<InstanceViewer>
			implements InstanceViewFactory {

		/**
		 * Create an instance view factory based on a configuration element. 
		 * @param conf the configuration element
		 */
		protected ConfigurationViewFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(InstanceViewer instance) {
			// dispose is handled externally
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

	}

	/**
	 * The extension ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.ui.views.data";
	
	/**
	 * {@link InstanceViewer} extension
	 */
	public static class InstanceViewExtension extends AbstractExtension<InstanceViewer, InstanceViewFactory>
			implements ObjectExtension<InstanceViewer, InstanceViewFactory> {
		
		/**
		 * Default constructor
		 */
		public InstanceViewExtension() {
			super(EXTENSION_ID);
		}

		/**
		 * @see AbstractExtension#createFactory(IConfigurationElement)
		 */
		@Override
		protected InstanceViewFactory createFactory(IConfigurationElement conf)
				throws Exception {
			if (conf.getName().equals("instanceView")) {
				return new ConfigurationViewFactory(conf);
			}
			
			return null;
		}

	}

	/**
	 * Create an instance view controller.
	 * @param preferences the preference store
	 * @param preferenceKey the preference key to use for storing the setting
	 */
	public InstanceViewController(
			IPreferenceStore preferences, String preferenceKey) {
		super(new InstanceViewExtension(), preferences, preferenceKey);
	}

	/**
	 * @see PreferencesExclusiveExtension#getFallbackFactory()
	 */
	@Override
	protected InstanceViewFactory getFallbackFactory() {
		return new DefaultViewFactory();
	}

}
