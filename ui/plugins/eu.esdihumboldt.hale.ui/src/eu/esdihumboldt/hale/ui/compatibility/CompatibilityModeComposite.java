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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.compatibility.extension.CompatibilityService;

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
		this.cs = (CompatibilityService) PlatformUI.getWorkbench().getService(
				CompatibilityService.class);
	}

	/**
	 * standart constructor with id
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
		statusLabel.setImage(CommonSharedImages.getImageRegistry().get(
				CommonSharedImages.IMG_SIGNED_YES));

		// combo for selecting/changing compatibility modes
		final CCombo combo = new CCombo(comp, SWT.READ_ONLY);
		for (CompatibilityModeFactory fac : cs.getFactories()) {
			combo.add(fac.getDisplayName());
		}

		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (CompatibilityModeFactory fac : cs.getFactories()) {
					if (fac.getDisplayName().equals(combo.getItem(combo.getSelectionIndex()))) {
						cs.setCurrent(fac);
					}
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		modeListener = new ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory>() {

			@Override
			public void currentObjectChanged(final CompatibilityMode arg0,
					final CompatibilityModeFactory arg1) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < combo.getItems().length; i++) {
							if (combo.getItems()[i].equals(arg1.getDisplayName())) {
								combo.select(i);
							}
						}
						cs.compatibilityModeChanged();
					}
				});
			}

		};
		cs.addListener(modeListener);

		compListener = new CompatibilityServiceListener() {

			@Override
			public void compatibilityChanged(final boolean isCompatible, final String notification) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						if (isCompatible) {
							statusLabel.setImage(CommonSharedImages.getImageRegistry().get(
									CommonSharedImages.IMG_SIGNED_YES));
							statusLabel.setToolTipText(notification);
						}
						if (!isCompatible) {
							statusLabel.setImage(CommonSharedImages.getImageRegistry().get(
									CommonSharedImages.IMG_SIGNED_NO));
							statusLabel.setToolTipText(notification);
						}
					}
				});
			}
		};
		cs.addCompatibilityListener(compListener);
		if (combo.getItems().length != 0) {
			combo.select(0);
		}
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