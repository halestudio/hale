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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider;

/**
 * Content provider for a groovy join page
 * 
 * @author Sameer Sheikh
 */
public class GroovyJoinContentProvider extends TypePropertyContentProvider {

	private final ParameterValue param;

	/**
	 * @param tree a tree viewer
	 * @param param a cell for getting join condition values from cell util
	 * 
	 */
	public GroovyJoinContentProvider(TreeViewer tree, ParameterValue param) {
		super(tree);
		this.param = param;

	}

	/**
	 * 
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		List<Object> elements = new ArrayList<Object>();

		JoinParameter joinParameter = param.as(JoinParameter.class);

		for (JoinCondition j : joinParameter.getConditions()) {
			if (j.baseProperty.getType().equals(inputElement)) {

				elements.add(j.joinProperty.getType());
			}
		}

		elements.addAll(Arrays.asList(super.getElements(inputElement)));

		return elements.toArray(new Object[elements.size()]);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {

		List<Object> elements = new ArrayList<Object>();

		JoinParameter joinParameter = param.as(JoinParameter.class);

		for (JoinCondition j : joinParameter.getConditions()) {
			if (j.baseProperty.getType().equals(parentElement)) {

				elements.add(j.joinProperty.getType());
			}
		}

		elements.addAll(Arrays.asList(super.getChildren(parentElement)));

		return elements.toArray(new Object[elements.size()]);
	}

}
