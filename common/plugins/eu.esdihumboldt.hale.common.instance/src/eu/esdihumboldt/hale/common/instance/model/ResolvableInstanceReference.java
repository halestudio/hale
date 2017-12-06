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

package eu.esdihumboldt.hale.common.instance.model;

import eu.esdihumboldt.hale.common.instance.model.impl.InstanceReferenceDecorator;

/**
 * Adds the capability to an {@link InstanceReference} to resolve the referenced
 * instance.
 * 
 * @author Florian Esser
 */
public class ResolvableInstanceReference extends InstanceReferenceDecorator
		implements IdentifiableInstance {

	private final InstanceResolver resolver;

	/**
	 * Create a resolvable instance reference
	 * 
	 * @param reference The instance reference
	 * @param resolver The resolver that can resolve the instance reference
	 */
	public ResolvableInstanceReference(InstanceReference reference, InstanceResolver resolver) {
		super(reference);

		this.resolver = resolver;
	}

	/**
	 * Resolve this instance reference
	 * 
	 * @return the reoslved instance or null if the instance reference could not
	 *         be resolved
	 */
	public Instance resolve() {
		if (resolver != null) {
			InstanceReference root = InstanceReferenceDecorator
					.getRootReference(this.getOriginalReference());
			return resolver.getInstance(root);
		}
		else {
			return null;
		}
	}

	/**
	 * Will try to resolve the given {@link InstanceReference}.<br>
	 * <br>
	 * The reference can be resolved if it is an instance of
	 * {@link ResolvableInstanceReference} or is another
	 * {@link InstanceReferenceDecorator} that can eventually be reduced to a
	 * <code>ResolvableInstanceReference</code> via calls to
	 * {@link InstanceReferenceDecorator#getOriginalReference()}
	 * 
	 * @param reference Instance reference to resolve
	 * @return the resolved {@link Instance} or null if it could not be resolved
	 */
	public static Instance tryResolve(final InstanceReference reference) {
		InstanceReference current = reference;
		while (current instanceof InstanceReferenceDecorator) {
			if (current instanceof ResolvableInstanceReference) {
				return ((ResolvableInstanceReference) current).resolve();
			}
			current = ((InstanceReferenceDecorator) current).getOriginalReference();
		}

		return null;
	}

	/**
	 * Looks for an ID in the original reference and, if that fails, for an
	 * {@link InstanceReferenceDecorator}.
	 * 
	 * @see eu.esdihumboldt.hale.common.instance.model.IdentifiableInstance#getId()
	 */
	@Override
	public Object getId() {
		InstanceReference origRef = getOriginalReference();
		if (origRef instanceof IdentifiableInstance) {
			return ((IdentifiableInstance) origRef).getId();
		}

		IdentifiableInstanceReference iir = InstanceReferenceDecorator.findDecoration(origRef,
				IdentifiableInstanceReference.class);
		if (iir != null) {
			return iir.getId();
		}

		return null;
	}
}
