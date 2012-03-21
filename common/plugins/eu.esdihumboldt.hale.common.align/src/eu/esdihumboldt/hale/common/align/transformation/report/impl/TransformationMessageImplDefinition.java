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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.impl.internal.AlignmentBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractMessageDefinition;

/**
 * Object definition for {@link TransformationMessageImpl}
 * @author Simon Templer
 */
public class TransformationMessageImplDefinition extends AbstractMessageDefinition<TransformationMessage> {

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
		mapping.loadMapping(new InputSource(
				AlignmentBean.class.getResourceAsStream("AlignmentBean.xml")));
		        
		XMLContext context = new XMLContext();
		CellBean bean;
		try {
			context.addMapping(mapping);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			bean = (CellBean) unmarshaller.unmarshal(new StringReader(props.getProperty(KEY_CELL)));
			
			TransformationMessageImpl message =  new TransformationMessageImpl(bean.getCell(),
					props.getProperty(KEY_MESSAGE),
					null,
					props.getProperty(KEY_STACK_TRACE));
			
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
			CellBean bean = new CellBean(message.getCell());
			
			Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
			mapping.loadMapping(new InputSource(
					AlignmentBean.class.getResourceAsStream("AlignmentBean.xml")));
			        
			XMLContext context = new XMLContext();
			context.setProperty("org.exolab.castor.indent", true); // enable indentation for marshaling as project files should be very small
			
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
