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

package eu.esdihumboldt.hale.ui.common.editors;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.EditorFactory;

/**
 * Creates a {@link StringEditor}.
 * @author Simon Templer
 */
public class StringEditorFactory implements EditorFactory {

	/**
	 * @see EditorFactory#createEditor(Composite)
	 */
	@Override
	public Editor<?> createEditor(Composite parent) {
		return new StringEditor(parent);
	}

}
