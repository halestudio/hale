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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationLocation;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.impl.DefaultInstanceValidationMessage;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.XLinkReference;

/**
 * Context for {@link XLinkReference} validation.
 * 
 * @author Simon Templer
 */
public class XLinkReferenceContext {

	private final Set<String> identifiers = new HashSet<>();
	private final Map<String, ValidationLocation> localRefs = new HashMap<>();

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
	 * @param location the validation location
	 */
	public void addLocalReference(String id, ValidationLocation location) {
		localRefs.put(id, location);
	}

	/**
	 * Validate references.
	 * 
	 * @param reporter the instance validation reporter
	 */
	public void validate(InstanceValidationReporter reporter) {
		Map<String, ValidationLocation> localRefs = new HashMap<>(this.localRefs);
		for (String id : identifiers) {
			localRefs.remove(id);
		}

		for (Entry<String, ValidationLocation> entry : localRefs.entrySet()) {
			reporter.warn(new DefaultInstanceValidationMessage(entry.getValue(),
					XLinkReference.class.getSimpleName(), MessageFormat
							.format("Local reference #{0} cannot be resolved", entry.getKey())));
		}
	}

}
