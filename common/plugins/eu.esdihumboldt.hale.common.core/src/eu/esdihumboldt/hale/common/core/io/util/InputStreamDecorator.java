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

package eu.esdihumboldt.hale.common.core.io.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decorator on an {@link InputStream}
 * 
 * @author Simon Templer
 */
public class InputStreamDecorator extends InputStream {

	private final InputStream in;

	/**
	 * Creates a decorator for the given input stream
	 * 
	 * @param in the input stream to decorate
	 */
	public InputStreamDecorator(InputStream in) {
		super();
		this.in = in;
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return in.available();
	}

	/**
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/**
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public void mark(int arg0) {
		in.mark(arg0);
	}

	/**
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return in.read();
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return in.read(arg0, arg1, arg2);
	}

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] arg0) throws IOException {
		return in.read(arg0);
	}

	/**
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		in.reset();
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long arg0) throws IOException {
		return in.skip(arg0);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return in.toString();
	}

}
