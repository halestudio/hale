/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Decorator that adds the {@link PrefixAwareStreamWriter} capability to a
 * {@link XMLStreamWriter}.
 * 
 * @author Simon Templer
 */
public class PrefixAwareStreamWriterDecorator extends XMLStreamWriterDecorator
		implements PrefixAwareStreamWriter {

	private final PrefixAwareStreamWriter prefixAware;

	private final Map<String, String> prefixes;

	/**
	 * Create a new decorator.
	 * 
	 * @param decoratee the decoratee
	 */
	public PrefixAwareStreamWriterDecorator(XMLStreamWriter decoratee) {
		super(decoratee);

		if (decoratee instanceof PrefixAwareStreamWriter) {
			prefixAware = (PrefixAwareStreamWriter) decoratee;
			prefixes = null;
		}
		else {
			prefixes = new HashMap<>();
			prefixAware = null;
		}
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		super.setPrefix(prefix, uri);
		// add prefix to map
		if (prefixes != null) {
			prefixes.put(prefix, uri);
		}
	}

	@Override
	public String getNamespace(String prefix) {
		if (prefixAware != null) {
			return prefixAware.getNamespace(prefix);
		}
		else {
			return prefixes.get(prefix);
		}
	}

}
