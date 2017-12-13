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

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder

/**
 * Annotation descriptor for generic messages attached to cells.
 * 
 * @author Simon Templer
 */
public class MessagesDescriptor implements AnnotationDescriptor<Messages> {

	/**
	 * Annotation type identifier.
	 */
	public static final String ID = "messages";

	@Override
	public Messages fromDOM(Element fragment, Void context) {
		return Messages.fromValue(DOMValueUtil.fromTag(fragment));
	}

	@Override
	public Element toDOM(Messages value) {
		def builder = NSDOMBuilder.newBuilder(core: HaleIO.NS_HALE_CORE)
		return DOMValueUtil.valueTag(builder, 'core:messages', value.toValue())
	}

	@Override
	public Messages create() {
		return new Messages();
	}
}
