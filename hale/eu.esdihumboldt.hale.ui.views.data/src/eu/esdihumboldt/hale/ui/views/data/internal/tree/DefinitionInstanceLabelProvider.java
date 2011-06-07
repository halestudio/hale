/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.data.internal.tree;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;

import eu.esdihumboldt.hale.instance.model.Instance;

/**
 * TODO Type description
 * @author sitemple
 */
public class DefinitionInstanceLabelProvider extends BaseLabelProvider implements ITreePathLabelProvider {

	private final Instance instance;
	
	/**
	 * @param instance
	 */
	public DefinitionInstanceLabelProvider(Instance instance) {
		super();
		
		this.instance = instance;
	}

	/**
	 * @see ITreePathLabelProvider#updateLabel(ViewerLabel, TreePath)
	 */
	@Override
	public void updateLabel(ViewerLabel label, TreePath elementPath) {
		// TODO Auto-generated method stub
		label.setText("Test");
	}

}
