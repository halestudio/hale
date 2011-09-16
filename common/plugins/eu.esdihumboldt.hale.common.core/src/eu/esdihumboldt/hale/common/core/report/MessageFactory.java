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

package eu.esdihumboldt.hale.common.core.report;

import java.util.ArrayList;
import java.util.List;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * Factory for messages
 * @author Simon Templer
 * @since 2.5
 * @see MessageDefinition
 * @see Message
 */
public class MessageFactory extends AbstractObjectFactory<Message, MessageDefinition<?>> {

	/**
	 * @see AbstractObjectFactory#getDefinitions()
	 */
	@Override
	protected Iterable<MessageDefinition<?>> getDefinitions() {
		List<MessageDefinition<?>> result = new ArrayList<MessageDefinition<?>>();
		for (MessageDefinition<?> def : OsgiUtils.getServices(MessageDefinition.class)) {
			result.add(def);
		}
		return result;
	}

}
