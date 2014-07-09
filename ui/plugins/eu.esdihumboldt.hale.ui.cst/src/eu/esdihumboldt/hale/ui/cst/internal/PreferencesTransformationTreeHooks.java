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

package eu.esdihumboldt.hale.ui.cst.internal;

import org.eclipse.jface.preference.IPreferenceStore;

import de.fhg.igd.eclipse.ui.util.extension.selective.PreferencesSelectiveExtension;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHookExtension;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHookFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHooks;

/**
 * {@link TransformationTreeHooks} implementation based on an
 * {@link IPreferenceStore}.
 * 
 * @author Simon Templer
 */
public class PreferencesTransformationTreeHooks extends
		PreferencesSelectiveExtension<TransformationTreeHook, TransformationTreeHookFactory>
		implements TransformationTreeHooks, CSTPreferencesConstants {

	/**
	 * Default constructor
	 */
	public PreferencesTransformationTreeHooks() {
		super(new TransformationTreeHookExtension(), CSTUIPlugin.getDefault().getPreferenceStore(),
				PREF_ACTIVE_TREE_HOOKS);
	}

}
