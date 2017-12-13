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

package eu.esdihumboldt.hale.server.console.war.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.osgi.framework.console.ConsoleSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.fhg.igd.osgi.util.OsgiUtils;

/**
 * A REST service offering access to the OSGI console. The service accepts GET
 * requests containing the command and returns the command's result.
 * 
 * @author Michel Kraemer
 */
@Controller
@RequestMapping
public class ConsoleController {

	/**
	 * The OSGi console's prompt
	 */
	private static final String PROMPT = "osgi> ";

	/**
	 * Can be used to send commands to the console session
	 */
	private PrintWriter _output;

	/**
	 * Can be used to read command results from the console session
	 */
	private InputStream _input;

	/**
	 * The OSGi console session
	 */
	private WebConsole _console;

	/**
	 * An OSGi console
	 */
	private static class WebConsole extends ConsoleSession {

		private final InputStream _in;
		private final OutputStream _out;

		public WebConsole(InputStream in, OutputStream out) {
			_in = in;
			_out = out;
		}

		@Override
		protected void doClose() {
			// nothing to do here
		}

		@Override
		public InputStream getInput() {
			return _in;
		}

		@Override
		public OutputStream getOutput() {
			return _out;
		}
	}

	/**
	 * Initializes this bean. Creates the console session
	 * 
	 * @throws IOException if the initial prompt could not be read
	 */
	public void init() throws IOException {
		InputStream in = makeConsoleInput();
		OutputStream out = makeConsoleOutput();

		_console = new WebConsole(in, out);
		OsgiUtils.registerService(ConsoleSession.class, _console);

		// read initial prompt
		readLines();
	}

	/**
	 * Destroys this bean. Closes the console session
	 */
	public void destroy() {
		if (_console != null) {
			_console.close();
		}
	}

	/**
	 * Creates an InputStream that can be used in the console service to read the
	 * commands. The input stream will be connected via a pipe with
	 * {@link #_output}, so commands can be written to the output stream and will be
	 * read by the console service.
	 * 
	 * @return the input stream
	 */
	private InputStream makeConsoleInput() {
		PipedInputStream result = new PipedInputStream(1024 * 1024);
		try {
			OutputStream output = new PipedOutputStream(result);
			_output = new PrintWriter(new OutputStreamWriter(output));
		} catch (IOException e) {
			throw new IllegalStateException("Could not create console pipe");
		}
		return result;
	}

	/**
	 * Creates an InputStream that can be used by the console service to write
	 * command results. The output stream will be connected via a pipe with
	 * {@link #_input}, so results can be read from that input stream.
	 * 
	 * @return the output stream
	 */
	private OutputStream makeConsoleOutput() {
		PipedInputStream input = new PipedInputStream(1024 * 1024);
		_input = input;
		try {
			return new PipedOutputStream(input);
		} catch (IOException e) {
			throw new IllegalStateException("Could not create console pipe");
		}
	}

	/**
	 * Reads bytes from {@link #_input} until it finds the OSGi {@link #PROMPT}.
	 * Truncates the prompt and returns the string read.
	 * 
	 * @return the string or null if the console input stream ended
	 * @throws IOException if the string could not be read
	 */
	private String readLines() throws IOException {
		int b = _input.read();
		if (b == -1) {
			return null;
		}
		else {
			String result = new String(new byte[] { (byte) b });
			byte[] buf = new byte[1024 * 64];
			int len;
			while ((len = _input.read(buf, 0, buf.length)) > 0) {
				result += new String(buf, 0, len);
				if (result.endsWith(PROMPT)) {
					break;
				}
			}

			if (result.endsWith(PROMPT)) {
				result = result.substring(0, result.length() - 6);
			}

			return result;
		}
	}

	/**
	 * Handles OSGi console commands
	 * 
	 * @param command the command
	 * @param response used to return the command's results
	 * @throws IOException if the command could not be executed
	 */
	@RequestMapping(value = "/{command}")
	public void get(@PathVariable String command, HttpServletResponse response) throws IOException {
		// send command
		_output.write(command + "\r\n");
		_output.flush();

		response.setContentType("text/plain");

		String lines = readLines();
		if (lines == null) {
			response.getWriter().println("End of console stream");
		}
		else {
			response.getOutputStream().write(lines.getBytes());
		}
	}
}
