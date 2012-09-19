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

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.codelist.internal.CodeListUIPlugin;
import eu.esdihumboldt.hale.ui.util.tree.MultiColumnTreeNodeLabelProvider;

/**
 * Label provider for {@link SearchPathNode}s and {@link CodeListNode}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SearchPathLabelProvider extends MultiColumnTreeNodeLabelProvider {

	private final Image searchpath_img;
	private final Image codelist_img;
	private final Image value_img;

	/**
	 * Default constructor
	 */
	public SearchPathLabelProvider() {
		super(0);

		searchpath_img = CodeListUIPlugin.getImageDescriptor("/icons/open.gif").createImage(); //$NON-NLS-1$
		codelist_img = CodeListUIPlugin.getImageDescriptor("/icons/attribute.gif").createImage(); //$NON-NLS-1$
		value_img = CodeListUIPlugin.getImageDescriptor("/icons/ok.gif").createImage(); //$NON-NLS-1$
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getDefaultImage()
	 */
	@Override
	protected Image getDefaultImage() {
		return value_img;
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueImage(Object, TreeNode)
	 */
	@Override
	protected Image getValueImage(Object value, TreeNode node) {
		if (node instanceof SearchPathNode) {
			return searchpath_img;
		}
		else if (node instanceof CodeListNode) {
			return codelist_img;
		}
		else {
			return value_img;
		}
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		searchpath_img.dispose();
		codelist_img.dispose();
		value_img.dispose();

		super.dispose();
	}

}
