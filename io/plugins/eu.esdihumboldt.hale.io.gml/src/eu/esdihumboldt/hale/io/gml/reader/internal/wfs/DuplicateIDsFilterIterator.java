/*
 * Copyright (c) 2024 wetransform GmbH
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

package eu.esdihumboldt.hale.io.gml.reader.internal.wfs;

import java.util.HashSet;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants;

/**
 * Filter the instances by storing only those with unique IDs.
 */
public class DuplicateIDsFilterIterator implements InstanceIterator {

	// HashSet to store unique instance IDs, ensuring no duplicates are
	// processed.
	private final HashSet<String> uniqueIDInstances = new HashSet<String>();

	// Iterator for traversing through instances.
	private final InstanceIterator iterator;

	// Holds the next instance to be processed, which should be filled if the
	// next() or skip() have already been called for the current instance
	private Instance nextInstance;

	// Flag to indicate if the current instance has already been returned by the
	// iterator.
	private boolean instanceAlreadyReturned = false;

	/**
	 * @param instanceIterator InstanceIterator
	 */
	public DuplicateIDsFilterIterator(InstanceIterator instanceIterator) {
		this.iterator = instanceIterator;
	}

	/**
	 * 
	 * @see eu.esdihumboldt.hale.common.instance.model.ResourceIterator#close()
	 */
	@Override
	public void close() {
		iterator.close();
		nextInstance = null;
		instanceAlreadyReturned = true;
	}

	/**
	 * @return TypeDefinition
	 * @see eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator#typePeek()
	 */
	@Override
	public TypeDefinition typePeek() {
		hasNext();
		if (nextInstance == null) {
			return null;
		}
		return nextInstance.getDefinition();
	}

	/**
	 * @return boolean
	 * @see eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator#supportsTypePeek()
	 */
	@Override
	public boolean supportsTypePeek() {
		return true;
	}

	/**
	 * 
	 * @see eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator#skip()
	 */
	@Override
	public void skip() {
		next();
	}

	/**
	 * Determines if there is a next instance available for iteration.
	 * 
	 * @return boolean - true if there is another instance to process, false if
	 *         the iteration is complete.
	 * 
	 *         This method checks if the current instance has already been
	 *         returned. If it has, or if the next instance has not been
	 *         initialized, it attempts to fetch the next unique instance.
	 * 
	 *         The iteration continues until a non-duplicate instance is found,
	 *         which is then stored in `nextInstance`. If no more instances are
	 *         available, the method returns false.
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (nextInstance == null || instanceAlreadyReturned) {

			while (true) {
				if (iterator != null && !iterator.hasNext()) {
					return false;
				}
				else {
					Instance instance = iterator.next();
					if (!isTheInstancePresent(instance)) {
						nextInstance = instance;
						instanceAlreadyReturned = false;
						return true;
					}
				}
			}
		}
		else {
			return true;
		}
	}

	/**
	 * @return Instance
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Instance next() {
		hasNext();
		if (nextInstance == null) {
			throw new NoSuchElementException();
		}
		instanceAlreadyReturned = true;
		return nextInstance;
	}

	/**
	 * @param instance Instance to be checked if it has been already given or
	 *            should be returned for further investigation
	 * @return true if the instance to be checked has been returned already,
	 *         false in contrary case
	 */
	private boolean isTheInstancePresent(Instance instance) {
		for (QName propertyName : instance.getPropertyNames()) {
			if (isGmlIdProperty(propertyName)) {
				Object[] gmlID = instance.getProperty(propertyName);
				if (gmlID[0] != null) {
					String gmlIDToCheck = (String) gmlID[0];

					if (!uniqueIDInstances.contains(gmlIDToCheck)) {
						uniqueIDInstances.add(gmlIDToCheck);
						return false;
					}
					else {
						return true;
					}

				}
			}
		}
		return true;
	}

	private boolean isGmlIdProperty(QName propertyName) {
		return (propertyName.getNamespaceURI().startsWith(GMLConstants.NS_WFS)
				|| propertyName.getNamespaceURI().startsWith(GMLConstants.GML_NAMESPACE_CORE))
				&& "id".equals(propertyName.getLocalPart());
	}

}