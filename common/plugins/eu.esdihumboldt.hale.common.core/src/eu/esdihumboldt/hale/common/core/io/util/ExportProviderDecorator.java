/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.util;

import java.io.OutputStream;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Decorator for {@link ExportProvider}s.
 * 
 * @param <T> the provider type
 * @author Simon Templer
 */
public abstract class ExportProviderDecorator<T extends ExportProvider>
		extends IOProviderDecorator<T>implements ExportProvider {

	/**
	 * @see IOProviderDecorator#IOProviderDecorator(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	public ExportProviderDecorator(T internalProvider) {
		super(internalProvider);
	}

	@Override
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target) {
		internalProvider.setTarget(target);
	}

	@Override
	public LocatableOutputSupplier<? extends OutputStream> getTarget() {
		return internalProvider.getTarget();
	}

}
