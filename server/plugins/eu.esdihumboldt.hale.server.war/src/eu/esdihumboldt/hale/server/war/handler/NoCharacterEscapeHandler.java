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

package eu.esdihumboldt.hale.server.war.handler;

import java.io.IOException;
import java.io.Writer;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

/**
 * This implements a Character Escape Handler which does nothing.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class NoCharacterEscapeHandler implements CharacterEscapeHandler {

	/**
	 * @see com.sun.xml.bind.marshaller.CharacterEscapeHandler#escape(char[], int, int, boolean, java.io.Writer)
	 */
	@Override
	public void escape(char[] ch, int start, int length, boolean isAttVal,
			Writer out) throws IOException {
		// do nothing
		out.write(ch, start, length);
	}

}
