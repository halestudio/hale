/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.style.editors;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface for an {@link Editor} factory
 * 
 * @param <T> the type that is edited with the {@link Editor}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface EditorFactory<T> {

	/**
	 * Create an editor
	 * 
	 * @param parent the parent composite
	 * @param value the initial value of the editor
	 * 
	 * @return the created editor
	 */
	public Editor<T> createEditor(Composite parent, T value);

}
