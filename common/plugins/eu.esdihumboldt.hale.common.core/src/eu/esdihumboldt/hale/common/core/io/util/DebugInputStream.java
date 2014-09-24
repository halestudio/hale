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

package eu.esdihumboldt.hale.common.core.io.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.io.InputStreamDecorator;

/**
 * Input stream for debugging purposes. Writes read bytes to a file so it can be
 * reproduced what was read.
 * 
 * @author Simon Templer
 */
public class DebugInputStream extends InputStreamDecorator {

	private static final ALogger log = ALoggerFactory.getLogger(DebugInputStream.class);

	private static int COUNT = 0;

	private OutputStream out;

	private final int id;

	private final String logPrefix;

	/**
	 * @see InputStreamDecorator#InputStreamDecorator(InputStream)
	 */
	public DebugInputStream(InputStream in) {
		super(in);

		synchronized (DebugInputStream.class) {
			id = COUNT++;
		}
		logPrefix = "[dbgin" + id + "] ";
	}

	/**
	 * Get the output stream to write to.
	 * 
	 * @return the output stream, may be <code>null</code> if it could not be
	 *         created
	 */
	protected OutputStream getOut() {
		if (out == null) {

			try {
				File tmp = File.createTempFile("debugin" + id, "read");
				out = new FileOutputStream(tmp);

				log.info(logPrefix + "DebugStream file: " + tmp.getAbsolutePath());

				tmp.deleteOnExit(); // TODO configurable
			} catch (IOException e) {
				log.error(logPrefix + "Failed to create outfile", e);
			}
		}
		return out;
	}

	@Override
	public void close() throws IOException {
		super.close();

		if (out != null) {
			out.close();
		}
	}

	@Override
	public void mark(int arg0) {
		// ignore
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		int r = super.read();
		if (r != -1) {
			OutputStream out = getOut();
			if (out != null) {
				out.write(r);
			}
		}
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int num = super.read(b, off, len);
		if (num > 0) {
			OutputStream out = getOut();
			if (out != null) {
				out.write(b, off, num);
			}
		}
		return num;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int num = super.read(b);
		if (num > 0) {
			OutputStream out = getOut();
			if (out != null) {
				out.write(b, 0, num);
			}
		}
		return num;
	}

	@Override
	public void reset() throws IOException {
		throw new IOException("mark not supported");
	}

	@Override
	public long skip(long n) throws IOException {
		long skipped = 0;
		byte[] buff = new byte[1024];
		while (n > 0) {
			int len;
			if (n >= buff.length) {
				n -= buff.length;
				len = buff.length;
			}
			else {
				len = (int) n;
				n = 0;
			}
			read(buff, 0, len);
		}
		return skipped;
	}

}
