/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;

/**
 * Factory for creating Type Transformation Handlers
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TypeTransformationHandlerFactory
		extends AbstractTransformationHandlerFactory<TypeTransformationHandler> {

	private final static String[] supportedTypes = { RetypeFunction.ID, JoinFunction.ID };
	final static Set<String> SUPPORTED_TYPES = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(supportedTypes)));

	@SuppressWarnings("serial")
	TypeTransformationHandlerFactory(final MappingContext mappingContext) {
		super(new HashMap<String, TypeTransformationHandler>() {

			{
				int i = 0;
				put(supportedTypes[i++], new RetypeHandler(mappingContext));
				put(supportedTypes[i++], new JoinHandler(mappingContext));
				// GroovyJoins are partially transformed with warnings
				put("eu.esdihumboldt.cst.functions.groovy.join",
						new GroovyJoinHandler(mappingContext));
			}
		});
	}

}
