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

import java.util.function.Consumer

import javax.xml.parsers.ParserConfigurationException

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.Text
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic

/**
 * Annotation descriptor for generic messages attached to cells.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class MessageDescriptor implements AnnotationDescriptor<Message> {

	/**
	 * Annotation type identifier.
	 */
	public static final String ID = "message";

	public Message fromDOM(Element fragment, Void context) {
		def properties = NSDOMCategory.firstChild(fragment, HaleIO.NS_HALE_CORE, 'properties')
		if (properties) {
			// load from properties (old way)
			return new Message().applyFromValue(DOMValueUtil.fromTag(fragment));
		}
		else {
			Message msg = new Message()

			// attributes
			if (fragment.hasAttribute('author')) {
				msg.author = fragment.getAttribute('author')
			}
			if (fragment.hasAttribute('format')) {
				msg.format = fragment.getAttribute('format')
			}
			if (fragment.hasAttribute('category')) {
				msg.category = fragment.getAttribute('category')
			}
			if (fragment.hasAttribute('dismissed')) {
				msg.dismissed = Boolean.parseBoolean(fragment.getAttribute('dismissed'))
			}

			// text
			def content = NSDOMCategory.firstChild(fragment, HaleIO.NS_HALE_CORE, 'content')
			if (content) {
				Value textValue = DOMValueUtil.fromTag(content)
				String message
				Text txt = textValue.as(Text)
				if (txt != null) {
					message = txt.text
				}
				else {
					message = textValue.as(String)
				}
				msg.text = message
			}

			// payload
			def payload = NSDOMCategory.firstChild(fragment, HaleIO.NS_HALE_CORE, 'payload')
			if (payload) {
				msg.customPayload = DOMValueUtil.fromTag(payload)
			}

			// tags
			def tags = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'tag')
			for (Element tag in tags) {
				msg.addTag(tag.textContent)
			}

			// comments
			def comments = NSDOMCategory.children(fragment, HaleIO.NS_HALE_CORE, 'comment')
			for (Element comment in comments) {
				Value commentValue = DOMValueUtil.fromTag(comment)
				if (commentValue != null) {
					Comment.fromValue(commentValue).ifPresent({ c ->
						msg.addComment((Comment) c)
					} as Consumer)
				}
			}


			msg
		}
	}

	public Element toDOM(Message msg) {
		Map<String, String> prefixes = new HashMap<>();
		prefixes.put("core", HaleIO.NS_HALE_CORE);
		NSDOMBuilder b;
		try {
			b = NSDOMBuilder.newBuilder(prefixes);

			//XXX old way using properties (verbose)
			//			return DOMValueUtil.valueTag(b, "core:message", value.toProperties().toValue());

			// attributes
			def attributes = [:]
			if (msg.author) {
				attributes['author'] = msg.author
			}
			if (msg.format) {
				attributes['format'] = msg.format
			}
			if (msg.category) {
				attributes['category'] = msg.category
			}
			if (msg.dismissed) {
				attributes['dismissed'] = msg.dismissed
			}

			def fragment = b('core:message', attributes) {
				// text
				if (msg.text) {
					DOMValueUtil.valueTag(b, 'core:content', Value.complex(new Text(msg.text)))
				}

				// payload
				if (msg.customPayload != null) {
					DOMValueUtil.valueTag(b, 'core:payload', msg.customPayload)
				}

				// tags
				if (!msg.tags.isEmpty()) {
					msg.tags.each { tag ->
						b('core:tag', tag)
					}
				}

				// comments
				if (!msg.comments.isEmpty()) {
					msg.comments.each { comment ->
						DOMValueUtil.valueTag(b, 'core:comment', comment.toValue())
					}
				}
			}

			(Element) fragment
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Message create() {
		return new Message();
	}
}
