<<<<<<< HEAD
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
=======
// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2011 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
>>>>>>> Work on #282.

package eu.esdihumboldt.hale.rcp.utils.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This class is used for proxy authentication.
 * (Sometimes just setting System properties does not work)
 *  
 * @author <a href="mailto:andreas.burchert@igd.fhg.de">Andreas Burchert</a>
 */
public class HttpAuth extends Authenticator {
	private String user = "";
	private String password = "";
	
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
