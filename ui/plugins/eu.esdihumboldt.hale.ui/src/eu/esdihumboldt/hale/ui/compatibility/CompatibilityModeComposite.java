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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.compatibility;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityServiceListener;

/**
 * UI Composite containing elements displaying the current compatibility mode
 * and its status
 * 
 * @author Sebastian Reinhardt
 */
public class CompatibilityModeComposite extends WorkbenchWindowControlContribution {

	CompatibilityService cs;
	ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory> modeListener;
	CompatibilityServiceListener compListener;

	/**
	 * standard constructor, retrieves the current compatibility service
	 */
	public CompatibilityModeComposite() {
		super();
		this.cs = PlatformUI.getWorkbench().getService(CompatibilityService.class);
	}

	/**
	 * standard constructor with id
	 * 
	 * @param id the id
	 */
	public CompatibilityModeComposite(String id) {
		super(id);
	}

	/**
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {

		// initiate the composite for the compatibility elements
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new RowLayout(SWT.HORIZONTAL));

		// label for displaying the status of the mode
		final Label statusLabel = new Label(comp, SWT.NONE);
		statusLabel.setImage(
				CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_SIGNED_YES));

		// label for displaying the mode itself
		final Label modeLabel = new Label(comp, SWT.NONE);

		// Menu for mode selection on left click
		IContributionItem popupMenu = new CompatibilityMenu();
		final MenuManager mmanager = new MenuManager();
		mmanager.add(popupMenu);
		modeLabel.setMenu(mmanager.createContextMenu(modeLabel));

		modeLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseUp(MouseEvent e) {
				modeLabel.getMenu().setVisible(true);

			}

		});

		// listener to update the mode label
		modeListener = new ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory>() {

			@Override
			public void currentObjectChanged(final CompatibilityMode arg0,
					final CompatibilityModeFactory arg1) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						modeLabel.setText(cs.getCurrentDefinition().getDisplayName());
					}
				});
			}

		};

		cs.addListener(modeListener);

		// listener for updating the mode status label
		compListener = new CompatibilityServiceListener() {

			@Override
			public void compatibilityChanged(final boolean isCompatible,
					List<Cell> incompatibleCells) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						if (isCompatible) {
							statusLabel.setImage(CommonSharedImages.getImageRegistry()
									.get(CommonSharedImages.IMG_SIGNED_YES));
							statusLabel.setToolTipText("No incompatibility detected!");
						}
						if (!isCompatible) {
							statusLabel.setImage(CommonSharedImages.getImageRegistry()
									.get(CommonSharedImages.IMG_SIGNED_NO));
							statusLabel.setToolTipText("Incompatibility detected!");
						}
					}
				});
			}
		};
		cs.addCompatibilityListener(compListener);
		modeLabel.setText(cs.getCurrentDefinition().getDisplayName());
		statusLabel.setToolTipText("No incompatibility detected!");
		return comp;
	}

	/**
	 * @see org.eclipse.jface.action.ContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		cs.removeListener(modeListener);
		cs.removeCompatibilityListener(compListener);
		super.dispose();
	}
}