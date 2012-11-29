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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.GZIPOutputStream;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.core.io.util.GZipContentDescriber;

/**
 * Export provider that supports GZiped output based on the content type.
 * 
 * @author Simon Templer
 */
public abstract class GZipEnabledExport extends AbstractExportProvider {

	/**
	 * Output supplier wrapping the output stream in a {@link GZIPOutputStream}
	 */
	public static class GZipOutputSupplier implements LocatableOutputSupplier<OutputStream> {

		private final LocatableOutputSupplier<? extends OutputStream> target;

		/**
		 * Create a GZiped output supplier.
		 * 
		 * @param target the original target
		 */
		public GZipOutputSupplier(LocatableOutputSupplier<? extends OutputStream> target) {
			this.target = target;
		}

		@Override
		public OutputStream getOutput() throws IOException {
			return new BufferedOutputStream(new GZIPOutputStream(target.getOutput()), 64 * 1024);
		}

		@Override
		public URI getLocation() {
			return target.getLocation();
		}

	}

	/**
	 * @see AbstractExportProvider#getTarget()
	 */
	@Override
	public LocatableOutputSupplier<? extends OutputStream> getTarget() {
		IContentType contentType = getContentType();
		if (GZipContentDescriber.isGZipContentType(contentType)) {
			return new GZipOutputSupplier(super.getTarget());
		}

		return super.getTarget();
	}

}
