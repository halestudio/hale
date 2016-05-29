/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.geometry;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryUtil;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;

/**
 * Definition/Instance related geometry utilities.
 * 
 * @author Simon Templer
 */
public abstract class DefaultGeometryUtil {

	private static final ALogger log = ALoggerFactory.getLogger(DefaultGeometryUtil.class);

	/**
	 * Get the default geometry of an instance.
	 * 
	 * @param instance the instance
	 * @return the default geometries or an empty collection if there is none
	 */
	public static Collection<GeometryProperty<?>> getDefaultGeometries(Instance instance) {
		GeometrySchemaService gss = PlatformUI.getWorkbench()
				.getService(GeometrySchemaService.class);

		if (gss == null) {
			throw new IllegalStateException("No geometry schema service available");
		}

		List<QName> path = gss.getDefaultGeometry(instance.getDefinition());

		return GeometryUtil.getGeometries(instance, path);
	}

	/**
	 * Determines if the given entity definition is a default geometry property.
	 * 
	 * @param entityDef the entity definition
	 * @return if the entity definition represents a default geometry property
	 */
	public static boolean isDefaultGeometry(EntityDefinition entityDef) {
		GeometrySchemaService gss = PlatformUI.getWorkbench()
				.getService(GeometrySchemaService.class);

		if (gss == null) {
			log.error("No geometry schema service available");
			return false;
		}

		List<QName> defPath = gss.getDefaultGeometry(entityDef.getType());
		if (defPath != null) {
			// match path against entity definition path
			List<ChildContext> entPath = entityDef.getPropertyPath();
			if (defPath.size() == entPath.size()) {
				// match only possible if path length is equal

				// compare path elements
				for (int i = 0; i < defPath.size(); i++) {
					if (!Objects.equal(defPath.get(i), entPath.get(i).getChild().getName())) {
						// each path entry must be equal
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

}
