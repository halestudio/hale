/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.config;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Abstract base class for {@link IOProvider} configuration dialogs.
 * 
 * Provides a default no-arg constructor that fetches the currently active
 * {@link Shell} via {@link PlatformUI}.
 * 
 * @author Florian Esser
 * @param
 * 			<P>
 *            {@link IOProvider} class for which the dialog provides
 *            configuration
 */
public abstract class AbstractConfigurationDialog<P extends IOProvider> extends Dialog {

	private P provider;

	/**
	 * No-arg constructor for this {@link Dialog} that fetches the currently
	 * active {@link Shell} via {@link PlatformUI}.
	 */
	public AbstractConfigurationDialog() {
		super(PlatformUI.getWorkbench().getDisplay().getActiveShell());
	}

	/**
	 * @param parentShell object that returns the current parent shell
	 */
	protected AbstractConfigurationDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	/**
	 * @param parentShell the parent shell, or <code>null</code> to create a
	 *            top-level shell
	 */
	protected AbstractConfigurationDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (this.configureProvider(provider)) {
			super.okPressed();
		}
	}

	/**
	 * Override in implementations to set configuration in provider from the
	 * data in the dialog.
	 * 
	 * @param provider Provider that needs configuring
	 * @return Return true if it is ok to close the configuration dialog
	 */
	protected abstract boolean configureProvider(P provider);

	/**
	 * Set the provider to be configured by this dialog instance.
	 * 
	 * @param provider Provider to be configured
	 */
	public void setProvider(P provider) {
		this.provider = provider;
	}

	/**
	 * @return the provider configured by this dialog or null if none has been
	 *         set yet
	 */
	public P getProvider() {
		return this.provider;
	}

}
