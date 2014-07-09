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

package eu.esdihumboldt.hale.common.core.report;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * Factory for messages
 * 
 * @author Simon Templer
 * @since 2.5
 * @see MessageDefinition
 * @see Message
 */
public class MessageFactory extends AbstractObjectFactory<Message, MessageDefinition<?>> {

	/**
	 * Logger
	 */
	private static final ALogger _log = ALoggerFactory.getLogger(MessageFactory.class);

	private final MessageDefinitions definitionExtension = new MessageDefinitions();

	/**
	 * Contains all {@link MessageDefinition} for
	 * {@link MessageFactory#getDefinitions()}.
	 */
	private ArrayList<MessageDefinition<?>> messageDefinitions = new ArrayList<MessageDefinition<?>>();

	/**
	 * Instance
	 */
	private static MessageFactory _instance;

	/**
	 * Constructor
	 */
	private MessageFactory() {
		/* nothing */
		super();
	}

	/**
	 * Get the instance of this factory.
	 * 
	 * @return the instance
	 */
	public static MessageFactory getInstance() {
		if (_instance == null) {
			_instance = new MessageFactory();
		}

		return _instance;
	}

	/**
	 * 
	 * @author Andreas Burchert
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 */
	public static class MessageDefinitions extends
			AbstractExtension<MessageDefinition<?>, ExtensionObjectFactory<MessageDefinition<?>>> {

		/**
		 * Default constructor
		 */
		public MessageDefinitions() {
			super(ReportFactory.EXTENSION_ID);
		}

		/**
		 * @see AbstractExtension#createFactory(IConfigurationElement)
		 */
		@Override
		protected ExtensionObjectFactory<MessageDefinition<?>> createFactory(
				IConfigurationElement conf) throws Exception {
			if (conf.getName().equals("messageDefinition")) {
				return new AbstractConfigurationFactory<MessageDefinition<?>>(conf, "class") {

					@Override
					public void dispose(MessageDefinition<?> md) {
						// do nothing
					}

					@Override
					public String getDisplayName() {
						return getIdentifier();
					}

					@Override
					public String getIdentifier() {
						return conf.getAttribute("id");
					}

				};
			}

			return null;
		}

	}

	/**
	 * @see AbstractObjectFactory#getDefinitions()
	 */
	@Override
	protected List<MessageDefinition<?>> getDefinitions() {
		// check if definitions are available
		if (this.messageDefinitions.size() > 0) {
			return this.messageDefinitions;
		}

		// get all factories
		List<ExtensionObjectFactory<MessageDefinition<?>>> factories = definitionExtension
				.getFactories();

		// create arraylist
		List<MessageDefinition<?>> result = new ArrayList<MessageDefinition<?>>();

		// iterate through factories and create MessageDefinition
		for (ExtensionObjectFactory<MessageDefinition<?>> m : factories) {
			try {
				MessageDefinition<?> md = m.createExtensionObject();
				result.add(md);
			} catch (Exception e) {
				_log.error("Error during object creation", e);
			}
		}

		this.messageDefinitions.addAll(result);
		return result;
	}

}
