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

package eu.esdihumboldt.hale.io.oml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * This class reads the OML Document into Java Object.
 * 
 * @author Kevin Mais
 */
public class OmlReader implements AlignmentReader {
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(OmlReader.class);

	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.generated.oml";

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.AlignmentReader#setSourceSchema(eu.esdihumboldt.hale.common.align.io.TypeIndex)
	 */
	@Override
	public void setSourceSchema(TypeIndex sourceSchema) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.AlignmentReader#setTargetSchema(eu.esdihumboldt.hale.common.align.io.TypeIndex)
	 */
	@Override
	public void setTargetSchema(TypeIndex targetSchema) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.AlignmentReader#getAlignment()
	 */
	@Override
	public MutableAlignment getAlignment() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ImportProvider#setSource(eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier)
	 */
	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ImportProvider#getSource()
	 */
	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator)
	 */
	@Override
	public IOReport execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#createReporter()
	 */
	@Override
	public IOReporter createReporter() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#setContentType(eu.esdihumboldt.hale.common.core.io.IContentType)
	 */
	@Override
	public void setContentType(IContentType contentType) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#getContentType()
	 */
	@Override
	public IContentType getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#getSupportedParameters()
	 */
	@Override
	public Set<String> getSupportedParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#setParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public void setParameter(String name, String value) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#loadConfiguration(java.util.Map)
	 */
	@Override
	public void loadConfiguration(Map<String, String> configuration) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#storeConfiguration(java.util.Map)
	 */
	@Override
	public void storeConfiguration(Map<String, String> configuration) {
		// TODO Auto-generated method stub
		
	}

}
