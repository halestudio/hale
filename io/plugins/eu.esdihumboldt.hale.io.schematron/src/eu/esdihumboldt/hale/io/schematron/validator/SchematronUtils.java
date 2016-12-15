/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.schematron.validator;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opengis.cite.validation.SchematronValidator;

/**
 * Utilities for Schematron validation
 * 
 * @author Florian Esser
 */
public abstract class SchematronUtils {

	/**
	 * Writes the content of the given {@link Result} into a
	 * {@link StringWriter}
	 * 
	 * @param result {@link Result} from {@link SchematronValidator} validation
	 * @param writer {@link StringWriter} to write report to
	 */
	public static void convertValidatorResult(Result result, StringWriter writer) {
		if (result instanceof DOMResult) {
			convertResult((DOMResult) result, writer);
		}
		else if (result instanceof StreamResult) {
			convertResult((StreamResult) result, writer);
		}
		else {
			throw new RuntimeException(
					String.format("Could not evaluate Schematron validation result of type '%s'",
							result.getClass().getCanonicalName()));
		}
	}

	/**
	 * @param result
	 * @param writer
	 */
	private static void convertResult(StreamResult result, StringWriter writer) {
		ByteArrayOutputStream baos = (ByteArrayOutputStream) result.getOutputStream();
		writer.write(baos.toString());
	}

	/**
	 * @param result
	 * @param writer
	 * @throws TransformerFactoryConfigurationError
	 */
	private static void convertResult(DOMResult result, StringWriter writer)
			throws TransformerFactoryConfigurationError {
		TransformerFactory txfFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = txfFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource resultSource = new DOMSource(result.getNode(), result.getSystemId());
			StreamResult printer = new StreamResult(writer);
			transformer.transform(resultSource, printer);
		} catch (TransformerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
