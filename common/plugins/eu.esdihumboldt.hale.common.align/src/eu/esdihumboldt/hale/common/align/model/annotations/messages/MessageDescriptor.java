/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.model.annotations.messages;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder;

/**
 * Annotation descriptor for generic messages attached to cells.
 * 
 * @author Simon Templer
 */
public class MessageDescriptor implements AnnotationDescriptor<Message> {

	/**
	 * Annotation type identifier.
	 */
	public static final String ID = "message";

	@Override
	public Message fromDOM(Element fragment, Void context) {
		return new Message().applyFromValue(DOMValueUtil.fromTag(fragment));
	}

	@Override
	public Element toDOM(Message value) {
		Map<String, String> prefixes = new HashMap<>();
		prefixes.put("core", HaleIO.NS_HALE_CORE);
		NSDOMBuilder builder;
		try {
			builder = NSDOMBuilder.newBuilder(prefixes);
			return DOMValueUtil.valueTag(builder, "core:message", value.toProperties().toValue());
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Message create() {
		return new Message();
	}

}
