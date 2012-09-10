/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.io.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentBean;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Save or load an alignment
 * 
 * @author Simon Templer
 */
public class DefaultAlignmentIO {

	/**
	 * Load a default alignment from an input stream.
	 * 
	 * @param in the input stream
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @return the alignment
	 * 
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the alignment could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public static MutableAlignment load(InputStream in, IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes) throws MappingException, MarshalException, ValidationException {
		Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
		mapping.loadMapping(new InputSource(AlignmentBean.class
				.getResourceAsStream("AlignmentBean.xml")));

		XMLContext context = new XMLContext();
		context.addMapping(mapping);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		try {
			AlignmentBean bean = (AlignmentBean) unmarshaller.unmarshal(new InputSource(in));
			return bean.createAlignment(reporter, sourceTypes, targetTypes);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Save a default alignment to an output stream.
	 * 
	 * @param alignment the alignment to save
	 * @param out the output stream
	 * @throws MappingException if the mapping could not be loaded
	 * @throws ValidationException if the mapping is no valid XML
	 * @throws MarshalException if the alignment could not be marshaled
	 * @throws IOException if the output could not be written
	 */
	public static void save(Alignment alignment, OutputStream out) throws MappingException,
			MarshalException, ValidationException, IOException {
		AlignmentBean bean = new AlignmentBean(alignment);

		Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
		mapping.loadMapping(new InputSource(AlignmentBean.class
				.getResourceAsStream("AlignmentBean.xml")));

		XMLContext context = new XMLContext();
		context.setProperty("org.exolab.castor.indent", true); // enable
																// indentation
																// for
																// marshaling as
																// project files
																// should be
																// very small
		context.addMapping(mapping);
		Marshaller marshaller = context.createMarshaller();
//		marshaller.setEncoding("UTF-8"); XXX not possible using the XMLContext but UTF-8 seems to be default, see http://jira.codehaus.org/browse/CASTOR-2846
		Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		try {
			marshaller.setWriter(writer);
			marshaller.marshal(bean);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

}
