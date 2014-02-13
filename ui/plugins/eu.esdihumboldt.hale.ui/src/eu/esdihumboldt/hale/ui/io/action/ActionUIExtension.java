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

package eu.esdihumboldt.hale.ui.io.action;

import java.net.URL;
import java.util.List;

import org.eclipse.core.expressions.ElementHandler;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.AbstractObjectDefinition;
import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * {@link IOAdvisor} extension
 * 
 * @author Simon Templer
 */
public class ActionUIExtension extends AbstractExtension<IOWizard<?>, ActionUI> {

	private static final ALogger log = ALoggerFactory.getLogger(ActionUIExtension.class);

	/**
	 * Factory for {@link IOAdvisor}s based on a {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<IOWizard<?>>
			implements ActionUI {

		private boolean advisorInitialized = false;
		private ActionUIAdvisor<?> actionAdvisor;

		/**
		 * Create a factory based on the given configuration element
		 * 
		 * @param conf the configuration
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "wizard");
		}

		/**
		 * @see ActionUI#getEnabledWhen()
		 */
		@Override
		public Expression getEnabledWhen() {
			IConfigurationElement[] children = conf.getChildren("enabledWhen");
			if (children != null && children.length > 0) {
				// get child of enabled when
				children = children[0].getChildren();

				if (children != null && children.length > 0) {
					try {
						return ElementHandler.getDefault().create(ExpressionConverter.getDefault(),
								children[0]);
					} catch (CoreException e) {
						log.error("Could not evaluate expression for action enablement.", e);
					}
				}
			}

			return null;
		}

		@Override
		public ActionUIAdvisor<?> getUIAdvisor() {
			if (!advisorInitialized) {
				try {
					if (conf.getAttribute("ui-advisor") != null) {
						Class<?> advisorClass = ExtensionUtil.loadClass(conf, "ui-advisor");
						actionAdvisor = (ActionUIAdvisor<?>) advisorClass.newInstance();
					}
				} catch (Exception e) {
					log.error("Failed to created action UI advisor instance", e);
				}

				advisorInitialized = true;
			}
			return actionAdvisor;
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(IOWizard<?> wizard) {
			// do nothing
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("label");
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		/**
		 * @see ActionUI#getDisabledReason()
		 */
		@Override
		public String getDisabledReason() {
			IConfigurationElement[] children = conf.getChildren("enabledWhen");
			if (children != null && children.length > 0) {
				// get child of enabled when
				return children[0].getAttribute("disabledReason");
			}

			return null;
		}

		/**
		 * @see AbstractConfigurationFactory#createExtensionObject()
		 */
		@Override
		public IOWizard<?> createExtensionObject() throws Exception {
			IOWizard<?> wizard = super.createExtensionObject();

			String customTitle = getCustomTitle();
			if (customTitle != null && !customTitle.isEmpty()) {
				wizard.setWindowTitle(customTitle);
			}

			return wizard;
		}

		/**
		 * @see AbstractObjectDefinition#getPriority()
		 */
		@Override
		public int getPriority() {
			try {
				return Integer.parseInt(conf.getAttribute("priority"));
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		/**
		 * @see AbstractObjectFactory#getIconURL()
		 */
		@Override
		public URL getIconURL() {
			return getIconURL("icon");
		}

		/**
		 * @see ActionUI#isProjectResource()
		 */
		@Override
		public boolean isProjectResource() {
			return Boolean.parseBoolean(conf.getAttribute("projectResource"));
		}

		/**
		 * @see ActionUI#getActionID()
		 */
		@Override
		public String getActionID() {
			return conf.getAttribute("action");
		}

		/**
		 * @see ActionUI#getCustomTitle()
		 */
		@Override
		public String getCustomTitle() {
			return conf.getAttribute("customTitle");
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.io.action";

	private static ActionUIExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the instance
	 */
	public static ActionUIExtension getInstance() {
		if (instance == null) {
			instance = new ActionUIExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private ActionUIExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected ActionUI createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("action-ui")) {
			return new ConfigurationFactory(conf);
		}

		return null;
	}

	/**
	 * Find the {@link ActionUI} associated with a certain action
	 * 
	 * @param actionId the action identifier
	 * @return the action UI or <code>null</code>
	 */
	public ActionUI findActionUI(final String actionId) {
		List<ActionUI> factories = getFactories(new FactoryFilter<IOWizard<?>, ActionUI>() {

			@Override
			public boolean acceptFactory(ActionUI factory) {
				return factory.getActionID().equals(actionId);
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<IOWizard<?>, ActionUI> collection) {
				return true;
			}
		});
		if (factories == null || factories.isEmpty()) {
			return null;
		}
		// XXX what if there are multiple ActionUIs for an action?
		return factories.get(0);
	}

}
