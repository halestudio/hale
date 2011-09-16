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

package eu.esdihumboldt.hale.io.xml.validator;

import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.instance.io.InstanceValidatorFactory;

/**
 * Factory for {@link XmlInstanceValidator}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlInstanceValidatorFactory extends AbstractIOProviderFactory<InstanceValidator>
		implements InstanceValidatorFactory {

	private static final String PROVIDER_ID = "eu.esdihumboldt.hale.xml.validate";

	/**
	 * Default constructor
	 */
	public XmlInstanceValidatorFactory() {
		super(PROVIDER_ID);
		
		addSupportedContentType("XML");
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public InstanceValidator createProvider() {
		return new XmlInstanceValidator();
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "XML validator (Java XML API)";
	}

}
