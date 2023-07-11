/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Input supplier that provides an input stream from a string.
 * 
 * @author Simon Templer
 */
public class StringInputSupplier implements InputSupplier<InputStream> {

	private final byte[] data;

	/**
	 * Create a new input supplier from the given text
	 * 
	 * @param text the text, may not be <code>null</code>
	 * @param charset the charset to use, may not be <code>null</code>
	 */
	public StringInputSupplier(String text, Charset charset) {
		super();
		this.data = text.getBytes(charset);
	}

	@Override
	public InputStream getInput() throws IOException {
		return new ByteArrayInputStream(data);
	}

}
