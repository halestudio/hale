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

package eu.esdihumboldt.hale.common.core.io.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.io.util.GZipContentDescriber;

/**
 * Import provider that supports GZiped input based on the content type.
 * 
 * @author Simon Templer
 */
public abstract class GZipEnabledImport extends AbstractImportProvider {

	/**
	 * Input supplier wrapping the input stream in a {@link GZIPInputStream}.
	 */
	public static class GZipInputSupplier implements LocatableInputSupplier<InputStream> {

		private final LocatableInputSupplier<? extends InputStream> source;

		/**
		 * Create a GZiped input supplier.
		 * 
		 * @param source the original source
		 */
		public GZipInputSupplier(LocatableInputSupplier<? extends InputStream> source) {
			this.source = source;
		}

		@Override
		public InputStream getInput() throws IOException {
			return new BufferedInputStream(new GZIPInputStream(source.getInput()));
		}

		@Override
		public URI getLocation() {
			return source.getLocation();
		}

		@Override
		public URI getUsedLocation() {
			return getLocation();
		}

		/**
		 * @return the wrapped LocatableInputSupplier
		 */
		public LocatableInputSupplier<? extends InputStream> getSource() {
			return source;
		}
	}

	/**
	 * @see AbstractImportProvider#getSource()
	 */
	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
		IContentType contentType = getContentType();
		if (GZipContentDescriber.isGZipContentType(contentType)) {
			return new GZipInputSupplier(super.getSource());
		}

		return super.getSource();
	}

}
