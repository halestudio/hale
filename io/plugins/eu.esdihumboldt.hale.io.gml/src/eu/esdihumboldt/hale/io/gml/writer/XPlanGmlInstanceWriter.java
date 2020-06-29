/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.gml.writer;

import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Writes instances to a XPlanGML XPlanAuszug
 * 
 * @author Florian Esser
 */
public class XPlanGmlInstanceWriter extends StreamGmlWriter {

	/**
	 * The identifier of the writer as registered to the I/O provider extension.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.gml.xplan.writer";

	/**
	 * Default constructor
	 */
	public XPlanGmlInstanceWriter() {
		super(true);
	}

	/**
	 * @see StreamGmlWriter#requiresDefaultContainer()
	 */
	@Override
	protected boolean requiresDefaultContainer() {
		return true; // requires an XPlanAuszug element being present
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter#isFeatureCollection(eu.esdihumboldt.hale.io.xsd.model.XmlElement)
	 */
	@Override
	protected boolean isFeatureCollection(XmlElement el) {
		return (el.getName().getLocalPart().contains("XPlanAuszug"))
				&& !el.getType().getConstraint(AbstractFlag.class).isEnabled()
				&& hasChild(el.getType(), "featureMember"); //$NON-NLS-1$
	}
}
