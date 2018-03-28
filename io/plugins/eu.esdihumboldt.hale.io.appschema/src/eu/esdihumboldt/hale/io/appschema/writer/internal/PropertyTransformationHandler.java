/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingContext;

/**
 * Interface defining the API for property transformation handlers.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public interface PropertyTransformationHandler {

	/**
	 * Translates a property cell to an app-schema attribute mapping.
	 * 
	 * @param typeCell the type cell owning the property cell
	 * @param propertyCell the property cell
	 * @param context the app-schema mapping context
	 * @return the attribute mapping
	 */
	public AttributeMappingType handlePropertyTransformation(Cell typeCell, Cell propertyCell,
			AppSchemaMappingContext context);

}
