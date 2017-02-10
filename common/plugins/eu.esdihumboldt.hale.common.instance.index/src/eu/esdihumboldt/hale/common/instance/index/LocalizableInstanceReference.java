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

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Objects;

import de.fhg.igd.geom.BoundingBox;
import de.fhg.igd.geom.Localizable;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceReferenceDecorator;

/**
 * A decorator for {@link InstanceReference}s adding the {@link Localizable}
 * interface
 * 
 * @author Florian Esser
 */
public class LocalizableInstanceReference extends InstanceReferenceDecorator
		implements Localizable {

	private final BoundingBox box;

	/**
	 * Constructs a decorator for the given {@link InstanceReference} and
	 * associates it the given {@link BoundingBox}.
	 *
	 * @param reference decoratee
	 * @param boundingBox bounding box
	 */
	public LocalizableInstanceReference(InstanceReference reference, BoundingBox boundingBox) {
		super(reference);
		this.box = Objects.requireNonNull(boundingBox);
	}

	/**
	 * @see de.fhg.igd.geom.Localizable#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return this.box;
	}
}
