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
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder;

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
		Map<String, String> prefixes = new HashMap<>();
		prefixes.put("core", HaleIO.NS_HALE_CORE);
		NSDOMBuilder builder;
		try {
			builder = NSDOMBuilder.newBuilder(prefixes);
			return DOMValueUtil.valueTag(builder, "core:messages", value.toValue());
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Messages create() {
		return new Messages();
	}

	/**
	 * Create a log that adds messages to a cell.
	 * 
	 * @param cell the cell to add messages to
	 * @return the cell log
	 */
	public static SimpleLog cellLog(final Cell cell) {
		return new SimpleLog() {

			private SimpleLog getDelegate() {
				List<?> an = cell.getAnnotations(ID);
				Messages msgs = an.stream() //
						.filter(x -> x instanceof Messages) //
						.map(x -> (Messages) x) //
						.findAny() //
						.orElseGet(() -> {
					return (Messages) cell.addAnnotation(ID);
				});

				// XXX would it be better to add individual messages as single
				// annotations?

				return msgs;
			}

			@Override
			public void warn(String message, Throwable e) {
				getDelegate().warn(message, e);
			}

			@Override
			public void error(String message, Throwable e) {
				getDelegate().error(message, e);
			}

			@Override
			public void info(String message, Throwable e) {
				getDelegate().info(message, e);
			}
		};
	}
}
