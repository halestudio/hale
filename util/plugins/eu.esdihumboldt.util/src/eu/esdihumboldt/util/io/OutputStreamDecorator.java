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

package eu.esdihumboldt.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Decorator for an {@link OutputStream}. Alternative to
 * {@link FilterOutputStream} that calls the same methods on the decoratee that
 * were actually called.
 * 
 * @author Simon Templer
 */
public class OutputStreamDecorator extends OutputStream {

	private final OutputStream out;

	/**
	 * Creates a decorator for an output stream
	 * 
	 * @param out the output stream
	 */
	public OutputStreamDecorator(OutputStream out) {
		super();
		this.out = out;
	}

	/**
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		out.close();
	}

	/**
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return out.toString();
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

}
