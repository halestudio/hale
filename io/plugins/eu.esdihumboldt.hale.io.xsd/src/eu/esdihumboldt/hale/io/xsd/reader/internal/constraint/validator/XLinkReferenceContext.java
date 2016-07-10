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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.validator;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.XLinkReference;

/**
 * Context for {@link XLinkReference} validation.
 * 
 * @author Simon Templer
 */
public class XLinkReferenceContext {

	private final Set<String> identifiers = new HashSet<>();
	private final Set<String> localRefs = new HashSet<>();

	/**
	 * Add an identifier used in an XML ID.
	 * 
	 * @param id the identifier to add
	 */
	public void addIdentifier(String id) {
		identifiers.add(id);
	}

	/**
	 * Add a local XLink reference.
	 * 
	 * @param id the identifier to add
	 */
	public void addLocalReference(String id) {
		localRefs.add(id);
	}

	/**
	 * Validate references.
	 * 
	 * @throws ValidationException if local references cannot be resolved
	 */
	public void validate() throws ValidationException {
		Set<String> localRefs = new HashSet<>(this.localRefs);
		localRefs.removeAll(identifiers);

		if (!localRefs.isEmpty()) {
			throw new ValidationException(
					MessageFormat.format("{0} local references cannot be resolved (e.g. #{1})",
							localRefs.size(), localRefs.iterator().next()));
		}
	}

}
