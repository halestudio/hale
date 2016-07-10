/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Validation location.
 * 
 * @author Simon Templer
 */
public class ValidationLocation {

	private final InstanceReference reference;
	private final QName type;
	private final List<QName> path;

	/**
	 * Constructor.
	 * 
	 * @param reference the validated instance
	 * @param type the parent type name
	 * @param path the property path
	 */
	public ValidationLocation(InstanceReference reference, QName type, List<QName> path) {
		super();
		this.reference = reference;
		this.type = type;
		this.path = path;
	}

	/**
	 * @return the reference
	 */
	public InstanceReference getReference() {
		return reference;
	}

	/**
	 * @return the type
	 */
	public QName getType() {
		return type;
	}

	/**
	 * @return the path
	 */
	public List<QName> getPath() {
		return path;
	}

}
