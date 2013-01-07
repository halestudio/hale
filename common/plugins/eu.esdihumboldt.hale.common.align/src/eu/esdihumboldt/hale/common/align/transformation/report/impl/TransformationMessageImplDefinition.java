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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition;

/**
 * Object definition for {@link TransformationMessageImpl}
 * 
 * @author Simon Templer
 */
public class TransformationMessageImplDefinition extends
		AbstractMessageDefinition<TransformationMessage> {

	private static final ALogger _log = ALoggerFactory.getLogger(TransformationMessage.class);

	/**
	 * Key for the cell
	 */
	public static final String KEY_CELL = "cell";

	/**
	 * Default constructor
	 */
	public TransformationMessageImplDefinition() {
		super(TransformationMessage.class, "transformation");
	}

	/**
	 * @see AbstractMessageDefinition#createMessage(Properties)
	 */
	@Override
	protected TransformationMessageImpl createMessage(Properties props) {
		Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
		mapping.loadMapping(new InputSource(AlignmentBean.class
				.getResourceAsStream("AlignmentBean.xml")));

		XMLContext context = new XMLContext();
		CellBean bean;
		try {
			context.addMapping(mapping);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			bean = (CellBean) unmarshaller.unmarshal(new StringReader(props.getProperty(KEY_CELL)));

			TransformationMessageImpl message = new TransformationMessageImpl(bean,
					props.getProperty(KEY_MESSAGE), null, props.getProperty(KEY_STACK_TRACE));

			return message;
		} catch (Exception e) {
			_log.error("Could not recover saved cell.", e.getCause());
		}

		// if the message could not be recovered
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition#asProperties(eu.esdihumboldt.hale.common.core.report.Message)
	 */
	@Override
	protected Properties asProperties(TransformationMessage message) {
		Properties props = super.asProperties(message);

		if (message.getCell() != null) {
			CellBean bean = message.getCell();

			Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
			mapping.loadMapping(new InputSource(AlignmentBean.class
					.getResourceAsStream("AlignmentBean.xml")));

			XMLContext context = new XMLContext();
			context.setProperty("org.exolab.castor.indent", true); // enable
																	// indentation
																	// for
																	// marshaling
																	// as
																	// project
																	// files
																	// should be
																	// very
																	// small

			StringWriter writer = new StringWriter();
			try {
				context.addMapping(mapping);
				Marshaller marshaller = context.createMarshaller();

				marshaller.setWriter(writer);
				marshaller.marshal(bean);

				props.setProperty(KEY_CELL, writer.toString());
			} catch (Exception e) {
				_log.error("Could not save cell.", e.getCause());
			}
		}

		return props;

	}
}
