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

import java.io.InputStream;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Decorator for {@link ImportProvider}s.
 * 
 * @param <T> the provider type
 * @author Stefano Costa, GeoSolutions
 */
public abstract class ImportProviderDecorator<T extends ImportProvider> extends
		IOProviderDecorator<T> implements ImportProvider {

	/**
	 * @see IOProviderDecorator#IOProviderDecorator(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	public ImportProviderDecorator(T internalProvider) {
		super(internalProvider);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ImportProvider#setSource(eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier)
	 */
	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		internalProvider.setSource(source);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ImportProvider#getSource()
	 */
	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
		return internalProvider.getSource();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ImportProvider#getResourceIdentifier()
	 */
	@Override
	public String getResourceIdentifier() {
		return internalProvider.getResourceIdentifier();
	}

}
