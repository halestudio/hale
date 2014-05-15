/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.io.instance.exportconfig;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.ui.io.instance.InstanceSelectTargetPage;

/**
 * Wizard page that allows selecting a target instance file with the saved
 * content type and a corresponding validator
 * 
 * @author Patrick Lieb
 */
public class SelectTargetExportConfigurationPage extends InstanceSelectTargetPage {

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// set the correct content type in the save field editor
		Set<IContentType> contentType = new HashSet<IContentType>();
		contentType.add(getWizard().getProvider().getContentType());
		getSaveFieldEditor().setContentTypes(contentType);
	}

}
