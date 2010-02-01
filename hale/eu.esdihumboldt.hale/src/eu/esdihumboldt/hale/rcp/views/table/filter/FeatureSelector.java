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

package eu.esdihumboldt.hale.rcp.views.table.filter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * Feature selector interface.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface FeatureSelector {

	/**
	 * Add a listener. A listener that was added should be informed of the
	 * currently selected features by calling
	 * {@link FeatureSelectionListener#selectionChanged(org.opengis.feature.type.FeatureType, Iterable)}
	 * 
	 * @param listener the listener to add
	 */
	public abstract void addSelectionListener(FeatureSelectionListener listener);

	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove
	 */
	public abstract void removeSelectionListener(
			FeatureSelectionListener listener);
	
	/**
	 * Create the selector control. The control must be disposed before creating
	 * another one. When the control is disposed, the listeners will be reset
	 * 
	 * @param parent the parent composite
	 * 
	 * @return the feature selector control 
	 */
	public Control createControl(Composite parent);

}