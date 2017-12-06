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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.Objects;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * InstanceReference decorator class.
 * 
 * @author Florian Esser
 */
public class InstanceReferenceDecorator implements InstanceReference {

	private final InstanceReference reference;

	/**
	 * Constructs the decorator with the given reference.
	 * 
	 * @param reference the reference to decorate
	 */
	public InstanceReferenceDecorator(InstanceReference reference) {
		this.reference = Objects.requireNonNull(reference);
	}

	/**
	 * Returns the original reference.
	 * 
	 * @return the original reference
	 */
	public InstanceReference getOriginalReference() {
		return reference;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceReference#getDataSet()
	 */
	@Override
	public DataSet getDataSet() {
		return reference.getDataSet();
	}

	/**
	 * Determines if a given object is an InstanceReferenceDecorator and itself
	 * or its original reference is decorated with the given type. If the
	 * original reference is an InstanceReferenceDecorator itself, the method
	 * will that object's original reference recursively.
	 * 
	 * @param decorator Object to check
	 * @param decoratorType Decorator type to search
	 * @return the object or one of its original references that implements the
	 *         given <code>decoratorType</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findDecoration(final Object decorator, Class<T> decoratorType) {
		if (!(decorator instanceof InstanceReferenceDecorator)) {
			return null;
		}

		InstanceReference current = (InstanceReference) decorator;
		while (current instanceof InstanceReferenceDecorator) {
			if (decoratorType.isAssignableFrom(current.getClass())) {
				return (T) current;
			}
			else {
				current = ((InstanceReferenceDecorator) current).getOriginalReference();
			}
		}

		return null;
	}

	/**
	 * Find the root {@link InstanceReference} in a layer of
	 * {@link InstanceReferenceDecorator}s
	 * 
	 * @param reference <code>InstanceReference</code> to find the root of
	 * @return Root reference or <code>reference</code> itself if it is not an
	 *         <code>InstanceReferenceDecorator</code>
	 */
	public static InstanceReference getRootReference(final InstanceReference reference) {
		InstanceReference current = reference;
		while (current instanceof InstanceReferenceDecorator) {
			current = ((InstanceReferenceDecorator) current).getOriginalReference();
		}

		return current;
	}
}
