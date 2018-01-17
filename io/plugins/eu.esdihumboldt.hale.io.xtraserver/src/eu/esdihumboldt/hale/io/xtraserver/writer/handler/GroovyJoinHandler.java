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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Transforms the eu.esdihumboldt.cst.functions.groovy.join function to a
 * {@link FeatureTypeMapping}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class GroovyJoinHandler extends JoinHandler {

	private static final ALogger logger = ALoggerFactory.getLogger(GroovyJoinHandler.class);

	GroovyJoinHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void doHandle(final Entity sourceType, final Entity targetType,
			final FeatureTypeMapping featureTypeMapping, final Cell typeCell) {
		try {
			super.doHandle(sourceType, targetType, featureTypeMapping, typeCell);
		} catch (final Exception e) {
			logger.error(
					"Error transforming GroovyJoin. Replace the GroovyJoin with a Join before you transform the Alignment.");
			throw e;
		}
		logger.warn(
				"A GroovyJoin has been added but requires further manual editing in Feature Type Mapping: {} {}",
				System.getProperty("line.separator"), featureTypeMapping.toString());
	}
}
