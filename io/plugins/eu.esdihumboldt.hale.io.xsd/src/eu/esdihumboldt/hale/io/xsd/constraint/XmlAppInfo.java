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

package eu.esdihumboldt.hale.io.xsd.constraint;

import java.util.Collections;
import java.util.List;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint that holds an {@link XmlSchemaAppInfo}s for a schema element.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
public class XmlAppInfo implements TypeConstraint, PropertyConstraint, GroupPropertyConstraint {

	private final List<XmlSchemaAppInfo> appInfos;

	/**
	 * Create a default constraint w/o any app infos.
	 */
	public XmlAppInfo() {
		super();
		this.appInfos = null;
	}

	/**
	 * Create a constraint w/ the given app infos.
	 * 
	 * @param appInfos the app infos
	 */
	public XmlAppInfo(List<XmlSchemaAppInfo> appInfos) {
		super();
		this.appInfos = appInfos;
	}

	/**
	 * Get the elements associated with the type
	 * 
	 * @return the XML elements
	 */
	public List<? extends XmlSchemaAppInfo> getAppInfos() {
		if (appInfos == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableList(appInfos);
		}
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

}
