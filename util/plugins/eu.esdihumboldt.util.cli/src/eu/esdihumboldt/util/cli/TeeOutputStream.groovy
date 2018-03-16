/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.util.cli


/**
 * Output stream that delegates to two output streams.
 * @author Simon Templer
 */
class TeeOutputStream extends OutputStream {

	private final OutputStream out
	private final OutputStream tee

	TeeOutputStream(OutputStream out, OutputStream tee) {
		if (out == null)
			throw new NullPointerException()
		else if (tee == null)
			throw new NullPointerException()

		this.out = out
		this.tee = tee
	}

	@Override
	void write(int b) throws IOException {
		out.write(b);
		tee.write(b);
	}

	@Override
	void write(byte[] b) throws IOException {
		out.write(b);
		tee.write(b);
	}

	@Override
	void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		tee.write(b, off, len);
	}

	@Override
	void flush() throws IOException {
		out.flush();
		tee.flush();
	}

	@Override
	void close() throws IOException {
		out.close();
		tee.close();
	}
}
