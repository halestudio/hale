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

package eu.esdihumboldt.hale.ui.io;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;

/**
 * Listener interface for {@link IOWizard}s
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public interface IOWizardListener<P extends IOProvider, W extends IOWizard<P>> {

	/**
	 * Called when the I/O provider descriptor assigned to the wizard has
	 * changed
	 * 
	 * @param providerDescriptor the provider descriptor, may be
	 *            <code>null</code>
	 */
	public void providerDescriptorChanged(IOProviderDescriptor providerDescriptor);

	/**
	 * Called when the content type assigned to the wizard has changed
	 * 
	 * @param contentType the content type, may be <code>null</code>
	 */
	public void contentTypeChanged(IContentType contentType);

}
