/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.custom.pages.internal;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class CustomPropertyFunctionEntityList extends
		AbstractValueList<DefaultCustomPropertyFunctionEntity, CustomPropertyFunctionEntityEditor> {

	/**
	 * @see AbstractValueList#AbstractValueList(String, String, Composite, List)
	 */
	public CustomPropertyFunctionEntityList(String caption, String description, Composite parent,
			List<DefaultCustomPropertyFunctionEntity> params) {
		super(caption, description, parent, params);
	}

	@Override
	protected CustomPropertyFunctionEntityEditor createEditor(Composite parent) {
		return new CustomPropertyFunctionEntityEditor(parent);
	}

}
