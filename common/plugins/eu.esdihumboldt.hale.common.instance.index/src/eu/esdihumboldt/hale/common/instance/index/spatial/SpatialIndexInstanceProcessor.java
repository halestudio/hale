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

package eu.esdihumboldt.hale.common.instance.index.spatial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vividsolutions.jts.geom.Geometry;

import de.fhg.igd.geom.BoundingBox;
import de.fhg.igd.geom.Localizable;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.index.LocalizableInstanceReference;
import eu.esdihumboldt.hale.common.instance.index.TypedInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.processing.AbstractInstanceProcessor;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Instance processor to populate the spatial index provided by the
 * {@link SpatialIndexService}.
 * 
 * @author Florian Esser
 */
public class SpatialIndexInstanceProcessor extends AbstractInstanceProcessor {

	/**
	 * @see eu.esdihumboldt.hale.common.instance.processing.InstanceProcessor#process(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      eu.esdihumboldt.hale.common.instance.model.InstanceReference)
	 */
	@Override
	public void process(Instance instance, InstanceReference reference) {
		SpatialIndexService<Localizable, Localizable> index = getSpatialIndexService();

		final GeometryFinder finder = new GeometryFinder(null);
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		traverser.traverse(instance, finder);

		final List<Geometry> geometries = new ArrayList<>();
		for (GeometryProperty<?> property : finder.getGeometries()) {
			Geometry g = property.getGeometry();
			for (int i = 0; i < g.getNumGeometries(); i++) {
				geometries.add(g.getGeometryN(i));
			}
		}

		final BoundingBox boundingBox = new BoundingBox();
		for (Geometry geometry : geometries) {
			boundingBox.add(BoundingBox.compute(geometry));
		}

		if (boundingBox.checkIntegrity()) {
			TypedInstanceReference typedRef = new TypedInstanceReference(reference,
					instance.getDefinition());
			index.insert(new LocalizableInstanceReference(typedRef, boundingBox));
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.processing.AbstractInstanceProcessor#close()
	 */
	@Override
	public void close() throws IOException {
		getSpatialIndexService().flush();
	}

	/**
	 * @return the spatial index service
	 * @throws IllegalStateException thrown if there is no
	 *             {@link ServiceProvider} or no {@link SpatialIndexService}
	 *             provided
	 */
	private SpatialIndexService<Localizable, Localizable> getSpatialIndexService() {
		final ServiceProvider serviceProvider = Optional.ofNullable(this.getServiceProvider())
				.orElseThrow(() -> new IllegalStateException("No service provider available"));

		@SuppressWarnings("unchecked")
		SpatialIndexService<Localizable, Localizable> index = Optional
				.ofNullable(serviceProvider.getService(SpatialIndexService.class)).orElseThrow(
						() -> new IllegalStateException("No SpatialIndexService was provided."));
		return index;
	}

}
