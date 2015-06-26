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

package eu.esdihumboldt.hale.io.gml.geometry;

import javax.xml.namespace.QName;

import org.geotools.gml2.SrsSyntax;

import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;

/**
 * Finds a CRS definition in a GML instance. The first valid definition found
 * will be stored, traversal in this case is aborted.
 * 
 * @author Simon Templer
 */
public class CRSFinder implements InstanceTraversalCallback {

	/**
	 * A CRS definition if found
	 */
	private CRSDefinition definition;

	@Override
	public boolean visit(Instance instance, QName name, DefinitionGroup parent) {
		return true;
	}

	@Override
	public boolean visit(Group group, QName name, DefinitionGroup parent) {
		return true;
	}

	@Override
	public boolean visit(Object value, QName name, DefinitionGroup parent) {
		if (value != null && name != null && name.getLocalPart().equals("srsName")) {
			String candidate = value.toString();

			for (SrsSyntax srsSyntax : SrsSyntax.values()) {
				if (checkCode(candidate, srsSyntax.getPrefix())) {
					// if definition is set, abort the traversal
					return false;
				}
			}

			// urn:ogc:def:crs:EPSG:(:)xxx style code
			if (checkCode(candidate, "urn:ogc:def:crs:EPSG:")) {
				// if definition is set, abort the traversal
				return false;
			}

		}

		return true;
	}

	/**
	 * Check a candidate for a CRS code. Set {@link #definition} to the
	 * corresponding {@link CRSDefinition} if it represents a CRS.
	 * 
	 * @param candidate the CRS code candidate
	 * @param prefix the expected code prefix
	 * @return if {@link #definition} was set
	 */
	private boolean checkCode(String candidate, String prefix) {
		if (candidate.length() > prefix.length()) {
			String authPart = candidate.substring(0, prefix.length());
			String codePart = candidate.substring(prefix.length());

			try {
				// ignore anything before the last colon
				int colonIndex = codePart.lastIndexOf(':');
				if (colonIndex >= 0) {
					codePart = codePart.substring(colonIndex + 1);
				}

				// check if codePart represents an integer
				Integer.parseInt(codePart);

				if (authPart.equalsIgnoreCase(prefix)) {
					definition = new CodeDefinition(candidate, null);
					// check if valid
					try {
						definition.getCRS();
					} catch (Exception e) {
						// code seems to be not valid

						// fall back to only using code part
						definition = new CodeDefinition("EPSG:" + codePart, null);
						// XXX check as well? (and return false on failure?)
					}

					return true;
				}
			} catch (NumberFormatException e) {
				// invalid
			}
		}

		return false;
	}

	/**
	 * Get the CRS definition found during traversal.
	 * 
	 * @return the definition the CRS definition or <code>null</code>
	 */
	public CRSDefinition getDefinition() {
		return definition;
	}

	/**
	 * Reset for reuse
	 */
	public void reset() {
		definition = null;
	}

}
