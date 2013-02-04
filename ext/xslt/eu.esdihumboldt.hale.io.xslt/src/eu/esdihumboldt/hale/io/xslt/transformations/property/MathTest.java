/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.property;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Type description
 * 
 * @author hydrologis
 */
public class MathTest {

	public static void main(String[] args) {

		String exp = "[(a + b*6/c^4- d)asd]";
		String pattern = "\\[|\\(|\\)|\\]|\\+|\\-|\\*|\\^|\\/";

		String[] splitAndKeep = splitAndKeep(exp, pattern);
		for (String s : splitAndKeep) {
			System.out.println(s);
		}

	}

	static String[] splitAndKeep(String input, String regex) {
		ArrayList<String> res = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		int pos = 0;
		while (m.find()) {
			String string = input.substring(pos, m.end() - 1).trim();
			if (string.length() != 0)
				res.add(string);
			string = input.substring(m.end() - 1, m.end()).trim();
			if (string.length() != 0)
				res.add(string);
			pos = m.end();
		}
		if (pos < input.length())
			res.add(input.substring(pos));
		return res.toArray(new String[res.size()]);
	}

}
