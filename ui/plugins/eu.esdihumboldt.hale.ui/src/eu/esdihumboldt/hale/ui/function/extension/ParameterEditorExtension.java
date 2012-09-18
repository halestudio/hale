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

package eu.esdihumboldt.hale.ui.function.extension;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Composite;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.EditorFactory;
import eu.esdihumboldt.hale.ui.common.editors.StringEditor;
import eu.esdihumboldt.hale.ui.function.extension.impl.ParameterEditorFactoryImpl;

/**
 * {@link ParameterEditorFactory} extension.
 * 
 * @author Simon Templer
 */
public class ParameterEditorExtension extends
		AbstractExtension<EditorFactory, ParameterEditorFactory> {

	private static final ALogger log = ALoggerFactory.getLogger(ParameterEditorExtension.class);

	private static ParameterEditorExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the parameter page extension
	 */
	public static ParameterEditorExtension getInstance() {
		if (instance == null) {
			instance = new ParameterEditorExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	public ParameterEditorExtension() {
		super(FunctionWizardExtension.ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected ParameterEditorFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("propertyParameterEditor")
				|| conf.getName().equals("typeParameterEditor"))
			return new ParameterEditorFactoryImpl(conf);

		return null;
	}

	/**
	 * Create an editor for a parameter.
	 * 
	 * @param parent the parent composite
	 * @param functionId the ID of the function the parameter is associated with
	 * @param parameterName the parameter name
	 * @return the editor
	 */
	public Editor<?> createEditor(final Composite parent, final String functionId,
			final String parameterName) {
		List<ParameterEditorFactory> factories = getFactories(new FactoryFilter<EditorFactory, ParameterEditorFactory>() {

			@Override
			public boolean acceptFactory(ParameterEditorFactory factory) {
				return factory.getParameterName().equals(parameterName)
						&& factory.getFunctionId().equals(functionId);
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<EditorFactory, ParameterEditorFactory> collection) {
				return true;
			}
		});

		if (!factories.isEmpty()) {
			ParameterEditorFactory fact = factories.get(0);
			try {
				return fact.createExtensionObject().createEditor(parent);
			} catch (Exception e) {
				// ignore, use default
				log.error("Could not create editor for parameter, using default editor instead.");
			}
		}

		// default editor
		return new StringEditor(parent);
	}

}
