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

import java.util.Collection;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Transforms the eu.esdihumboldt.cst.functions.groovy.join function to a
 * {@link FeatureTypeMapping}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class GroovyJoinHandler extends JoinHandler {

	GroovyJoinHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void doHandle(final Collection<? extends Entity> sourceTypes, final Entity targetType,
			final Cell typeCell) {
		try {
			super.doHandle(sourceTypes, targetType, typeCell);
		} catch (final Exception e) {
			mappingContext.getReporter().error("Error transforming GroovyJoin for Feature Type {0}."
					+ " Replace the GroovyJoin with a Join before you transform the Alignment.",
					mappingContext.getFeatureTypeName());
			throw e;
		}
		mappingContext.getReporter()
				.warn("A GroovyJoin for Feature Type {0} has been transformed but requires further"
						+ " manual editing in the generated Mapping file.",
						mappingContext.getFeatureTypeName());
	}
}
