/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.controller;

import java.io.IOException;
import java.io.Writer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.esdihumboldt.hale.server.api.RestAPI;

/**
 * Basic controller for static information.
 * 
 * @author Simon Templer
 */
@Controller
public class Main implements RestAPI {

	/**
	 * Get the REST API version.
	 * 
	 * @param writer the response writer
	 * @throws IOException if writing to the response fails
	 */
	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public void getVersion(Writer writer) throws IOException {
		writer.write(String.valueOf(VERSION));
	}

}
