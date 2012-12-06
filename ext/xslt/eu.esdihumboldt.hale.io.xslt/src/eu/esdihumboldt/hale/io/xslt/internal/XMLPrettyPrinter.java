/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XML pretty printer based on XSLT. Write to the {@link XMLPrettyPrinter} and
 * your XML output will be pretty printed on the delegated output stream
 * specified in {@link #XMLPrettyPrinter(OutputStream)}
 * 
 * @author Simon Templer
 */
public class XMLPrettyPrinter extends PipedOutputStream {

	private final StreamSource source;
	private final StreamResult out;
	private final Transformer transformer;

	/**
	 * Create a pretty printer for XML output.
	 * 
	 * @param delegatedOut where the pretty XML should be written to
	 * @throws IOException if the XSLT transformation for pretty printing cannot
	 *             be loaded
	 * @throws TransformerException if the XSLT transformation for pretty
	 *             printing cannot be loaded
	 */
	public XMLPrettyPrinter(OutputStream delegatedOut) throws IOException, TransformerException {
		super();

		PipedInputStream pin = new PipedInputStream(this);

		source = new StreamSource(pin);
		StreamSource transformation = new StreamSource(
				XMLPrettyPrinter.class.getResourceAsStream("prettyprint.xsl"));
		out = new StreamResult(delegatedOut);

		TransformerFactory factory = TransformerFactory.newInstance();
		transformer = factory.newTransformer(transformation);

		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	}

	/**
	 * Start the transformation in a new thread.
	 * 
	 * @return the future representing the transformation process, call
	 *         <code>get()</code> to wait for its completion
	 */
	public Future<?> start() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> f = start(executor);
		executor.shutdown();
		return f;
	}

	/**
	 * Start the transformation in the given executor service.
	 * 
	 * @param executor the executor service
	 * @return the future representing the transformation process, call
	 *         <code>get()</code> to wait for its completion
	 */
	public Future<?> start(ExecutorService executor) {
		return executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					transformer.transform(source, out);
				} catch (TransformerException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

}
