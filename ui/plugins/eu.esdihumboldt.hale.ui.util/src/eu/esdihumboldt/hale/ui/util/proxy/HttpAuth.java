/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.util.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This class is used for proxy authentication. (Sometimes just setting System
 * properties does not work)
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class HttpAuth extends Authenticator {

	private String user = ""; //$NON-NLS-1$
	private String password = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param user proxyUser
	 * @param password proxyPassword
	 */
	public HttpAuth(String user, String password) {
		this.user = user;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.password.toCharArray());
	}
}
