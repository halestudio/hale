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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.test;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.cst.test.internal.InternalExample;

/**
 * Holds a set of common available transformation examples for use in tests.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public abstract class TransformationExamples {

	public static final String SIMPLE_RENAME = "simplerename";
	public static final String CARD_RENAME = "cardrename";
	public static final String DUPE_ASSIGN = "dupeassign";
	public static final String PROPERTY_JOIN = "propjoin";
	public static final String SIMPLE_MERGE = "simplemerge";
	public static final String CARDINALITY_MERGE_1 = "cardmerge_1";
	public static final String CARDINALITY_MERGE_2 = "cardmerge_2";
	public static final String SIMPLE_COMPLEX = "simplecomplex";
	public static final String CARDINALITY_MOVE = "cardmove";

	private static final Map<String, String[]> internalExamples = new HashMap<String, String[]>();

	static {
		internalExamples.put(SIMPLE_RENAME, new String[] { //
				"/testdata/simplerename/t1.xsd", // source schema
						"/testdata/simplerename/t2.xsd", // target schema
						"/testdata/simplerename/t1t2.halex.alignment.xml", // alignment
						"/testdata/simplerename/instance1.xml", // source data
						"/testdata/simplerename/instance2.xml" }); // target

		internalExamples.put(CARD_RENAME, new String[] { "/testdata/cardrename/t1.xsd",
				"/testdata/cardrename/t2.xsd", "/testdata/cardrename/t1t2.halex.alignment.xml",
				"/testdata/cardrename/instance1.xml", "/testdata/cardrename/instance2.xml" });

		internalExamples.put(DUPE_ASSIGN, new String[] { "/testdata/dupeassign/t1.xsd",
				"/testdata/dupeassign/t2.xsd", "/testdata/dupeassign/t1t2.halex.alignment.xml",
				"/testdata/dupeassign/instance1.xml", "/testdata/dupeassign/instance2.xml" });

		internalExamples.put(PROPERTY_JOIN, new String[] { "/testdata/propjoin/t1.xsd",
				"/testdata/propjoin/t2.xsd", "/testdata/propjoin/t1t2.halex.alignment.xml",
				"/testdata/propjoin/instance1.xml", "/testdata/propjoin/instance2.xml" });

		internalExamples.put(SIMPLE_MERGE, new String[] { "/testdata/simplemerge/t1.xsd",
				"/testdata/simplemerge/t2.xsd", "/testdata/simplemerge/t1t2.halex.alignment.xml",
				"/testdata/simplemerge/instance1.xml", "/testdata/simplemerge/instance2.xml" });

		internalExamples.put(CARDINALITY_MERGE_1, new String[] { "/testdata/cardmerge/t1.xsd",
				"/testdata/cardmerge/t2.xsd", "/testdata/cardmerge/t1t2.halex.alignment.xml",
				"/testdata/cardmerge/instance1_1.xml", "/testdata/cardmerge/instance2_1.xml" });

		internalExamples.put(CARDINALITY_MERGE_2, new String[] { "/testdata/cardmerge/t1.xsd",
				"/testdata/cardmerge/t2.xsd", "/testdata/cardmerge/t1t2.halex.alignment.xml",
				"/testdata/cardmerge/instance1_2.xml", "/testdata/cardmerge/instance2_2.xml" });

		internalExamples.put(SIMPLE_COMPLEX, new String[] { "/testdata/simplecomplex/t2.xsd",
				"/testdata/simplecomplex/t2.xsd",
				"/testdata/simplecomplex/t2t2.halex.alignment.xml",
				"/testdata/simplecomplex/instance2.xml",
				"/testdata/simplecomplex/instance2_result.xml" });

		internalExamples.put(CARDINALITY_MOVE, new String[] { "/testdata/cardmove/t1.xsd",
				"/testdata/cardmove/t2.xsd", "/testdata/cardmove/t1t2.halex.alignment.xml",
				"/testdata/cardmove/instance1.xml", "/testdata/cardmove/instance2.xml" });
	}

	/**
	 * Get the transformation example with the given identifier.
	 * 
	 * @param id the example identifier
	 * @return the transformation example
	 * @throws Exception if the example does not exist or loading it failed
	 */
	public static TransformationExample getExample(String id) throws Exception {
		String[] exampleLocs = internalExamples.get(id);
		if (exampleLocs != null) {
			return new InternalExample(exampleLocs[0], exampleLocs[1], exampleLocs[2],
					exampleLocs[3], exampleLocs[4]);
		}
		throw new IllegalArgumentException("No example with ID " + id + " found.");
	}

}
