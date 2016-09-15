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

package eu.esdihumboldt.hale.ui.io.align;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.io.BaseAlignmentReader;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader;

/**
 * Wizard for importing base alignments.
 * 
 * @author Kai Schwierczek
 */
public class BaseAlignmentImportWizard extends ImportWizard<BaseAlignmentReader> {

	/**
	 * Create a base alignment import wizard
	 */
	public BaseAlignmentImportWizard() {
		super(BaseAlignmentReader.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		return as.addBaseAlignment(new BaseAlignmentLoader() {

			@Override
			public boolean load(MutableAlignment alignment) {
				BaseAlignmentReader provider = getProvider();
				if (provider == null)
					return false;
				provider.setAlignment(alignment);
				return BaseAlignmentImportWizard.super.performFinish();
			}
		});
	}
}
