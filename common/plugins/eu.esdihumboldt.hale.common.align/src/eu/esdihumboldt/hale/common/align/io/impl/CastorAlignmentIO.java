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

package eu.esdihumboldt.hale.common.align.io.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentBean;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Save or load an alignment
 * 
 * @author Simon Templer
 */
public class CastorAlignmentIO {

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
	 * @param updater the path updater to use for base alignments
	 * @return the alignment
	 * 
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the alignment could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 * @throws IOException if loading of base alignments failed
	 */
	public static MutableAlignment load(InputStream in, IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes, PathUpdate updater)
			throws MappingException, MarshalException, ValidationException, IOException {
		AlignmentBean bean = AlignmentBean.load(in, reporter);
		return bean.createAlignment(reporter, sourceTypes, targetTypes, updater);
	}

	/**
	 * Adds the given base alignment to the given alignment.
	 * 
	 * @param alignment the alignment to add a base alignment to
	 * @param newBase URI of the new base alignment
	 * @param projectLocation the project location or <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if adding the base alignment fails
	 */
	public static void addBaseAlignment(MutableAlignment alignment, URI newBase,
			URI projectLocation, TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter)
			throws IOException {
		AlignmentBean.addBaseAlignment(alignment, newBase, projectLocation, sourceTypes,
				targetTypes, reporter);
	}

	/**
	 * Save a default alignment to an output stream.
	 * 
	 * @param alignment the alignment to save
	 * @param out the output stream
	 * @param pathUpdate to update relative paths in case of a path change
	 * @throws MappingException if the mapping could not be loaded
	 * @throws ValidationException if the mapping is no valid XML
	 * @throws MarshalException if the alignment could not be marshaled
	 * @throws IOException if the output could not be written
	 */
	public static void save(Alignment alignment, OutputStream out, PathUpdate pathUpdate)
			throws MappingException, MarshalException, ValidationException, IOException {
		AlignmentBean bean = new AlignmentBean(alignment, pathUpdate);

		Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
		mapping.loadMapping(
				new InputSource(AlignmentBean.class.getResourceAsStream("AlignmentBean.xml")));

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
