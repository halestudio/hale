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

package eu.esdihumboldt.hale.gmlvalidate;

import java.io.IOException;
import java.io.InputStream;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.instance.io.impl.AbstractInstanceValidator;

/**
 * Validates XML
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlInstanceValidator extends AbstractInstanceValidator {

	/**
	 * @see IOProvider#execute(ProgressIndicator)
	 */
	@Override
	public void execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Validating XML", true);
		Validator val = ValidatorFactory.getInstance().createValidator(getSchemas());
		InputStream in = getSource().getInput();
		try {
			Report report = val.validate(in);
			//FIXME use the report/what to do with the report?
		} finally {
			in.close();
			progress.end();
		}
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

}
