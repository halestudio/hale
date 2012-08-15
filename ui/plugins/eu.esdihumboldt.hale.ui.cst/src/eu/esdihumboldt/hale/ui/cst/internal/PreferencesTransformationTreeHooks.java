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

package eu.esdihumboldt.hale.ui.cst.internal;

import org.eclipse.jface.preference.IPreferenceStore;

import de.cs3d.ui.util.eclipse.extension.selective.PreferencesSelectiveExtension;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHookExtension;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHookFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHooks;

/**
 * {@link TransformationTreeHooks} implementation based on an
 * {@link IPreferenceStore}.
 * @author Simon Templer
 */
public class PreferencesTransformationTreeHooks
		extends
		PreferencesSelectiveExtension<TransformationTreeHook, TransformationTreeHookFactory>
		implements TransformationTreeHooks, CSTPreferencesConstants {

	/**
	 * Default constructor
	 */
	public PreferencesTransformationTreeHooks() {
		super(new TransformationTreeHookExtension(), 
				CSTUIPlugin.getDefault().getPreferenceStore(), 
				PREF_ACTIVE_TREE_HOOKS);
	}
	
}
