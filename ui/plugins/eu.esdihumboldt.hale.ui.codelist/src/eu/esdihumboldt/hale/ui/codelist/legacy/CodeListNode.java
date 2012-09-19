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

package eu.esdihumboldt.hale.ui.codelist.legacy;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;

/**
 * Tree node representing a code list
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CodeListNode extends DefaultTreeNode {

	/**
	 * Create a new tree node with a given code list
	 * 
	 * @param codes the code list
	 */
	public CodeListNode(CodeList codes) {
		super(codes.getIdentifier());

		for (CodeEntry entry : codes.getEntries()) {
			addChild(new DefaultTreeNode(entry.getName()));
		}
	}

}
