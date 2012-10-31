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

package eu.esdihumboldt.hale.common.headless.io;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.google.common.io.LimitInputStream;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Provides input supplier based on a single input stream, that allow to consume
 * it multiple times up to a limit in read bytes (see
 * {@link #getLookupSupplier()}), and once completely (see
 * {@link #getInputSupplier()}). For the underlying input stream to be closed,
 * the input stream provided by {@link #getInputSupplier()} must be closed.
 * 
 * @author Simon Templer
 */
public class LookupStreamResource {

	private final InputStream input;
	private final URI location;
	private final int lookupLimit;

	/**
	 * Constructor.
	 * 
	 * @param input the input stream
	 * @param location the location represented by the input stream, may be
	 *            <code>null</code>
	 * @param lookupLimit the limit of bytes that may be read from a lookup
	 *            input stream
	 */
	public LookupStreamResource(InputStream input, URI location, int lookupLimit) {
		super();
		this.location = location;
		this.lookupLimit = lookupLimit;
		if (input.markSupported()) {
			this.input = input;
		}
		else {
			// add mark support
			this.input = new BufferedInputStream(input);
		}
		this.input.mark(lookupLimit);
	}

	/**
	 * Get an input supplier that supplies streams that may only be read to a
	 * certain amount of bytes. Only one instance of such a stream may be used
	 * at a time (as they are all backed by the same stream) and the instance
	 * should be closed before the stream is retrieved through
	 * {@link #getInputSupplier()}.
	 * 
	 * @return the input supplier
	 */
	public LocatableInputSupplier<? extends InputStream> getLookupSupplier() {
		return new LocatableInputSupplier<InputStream>() {

			@SuppressWarnings("resource")
			@Override
			public InputStream getInput() throws IOException {
				input.reset();
				return new FilterInputStream(new LimitInputStream(input, lookupLimit)) {

					@Override
					public void close() throws IOException {
						// don't close stream, reset instead
						reset();
					}

				};
			}

			@Override
			public URI getLocation() {
				return location;
			}
		};
	}

	/**
	 * Get an input supplier that supplies the underlying stream, which can be
	 * fully consumed only once.
	 * 
	 * @return the input supplier
	 */
	public LocatableInputSupplier<? extends InputStream> getInputSupplier() {
		return new LocatableInputSupplier<InputStream>() {

			boolean first = true;

			@Override
			public InputStream getInput() throws IOException {
				if (first) {
					first = false;
				}
				else {
					throw new IllegalStateException("Input stream can only be consumed once.");
				}

				input.reset();
				return input;
			}

			@Override
			public URI getLocation() {
				return location;
			}
		};
	}

}
