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

import eu.esdihumboldt.cst.test.internal.InternalExample


/**
 * Holds a set of common available transformation examples for use in tests.
 * 
 * @author Simon Templer
 */
abstract class TransformationExamples {

	public static final String SIMPLE_RENAME = 'simplerename'
	public static final String CARD_RENAME = 'cardrename'
	public static final String DUPE_ASSIGN = 'dupeassign'
	public static final String PROPERTY_JOIN = 'propjoin'
	public static final String SIMPLE_MERGE = 'simplemerge'
	public static final String CARDINALITY_MERGE_1 = 'cardmerge_1'
	public static final String CARDINALITY_MERGE_2 = 'cardmerge_2'
	public static final String SIMPLE_COMPLEX = 'simplecomplex'
	public static final String CARDINALITY_MOVE = 'cardmove'

	public static final String CM_UNION_1 = 'cm_union_1'
	public static final String CM_UNION_2 = 'cm_union_2'

	public static final String CM_MULTI_2 = 'cm_multi_2'
	public static final String CM_MULTI_4 = 'cm_multi_4'

	public static final String CM_NESTED_1 = 'cm_nested_1'

	/**
	 * Internal example map.
	 */
	private static final def internalExamples = [
		(SIMPLE_RENAME): defaultExample(SIMPLE_RENAME),
		(CARD_RENAME): defaultExample(CARD_RENAME),
		(DUPE_ASSIGN): defaultExample(DUPE_ASSIGN),
		(PROPERTY_JOIN): defaultExample(PROPERTY_JOIN),
		(SIMPLE_MERGE): defaultExample(SIMPLE_MERGE),
		(CARDINALITY_MERGE_1): [
			sourceSchema: "/testdata/cardmerge/t1.xsd",
			targetSchema: "/testdata/cardmerge/t2.xsd",
			alignment: "/testdata/cardmerge/t1t2.halex.alignment.xml",
			sourceData: "/testdata/cardmerge/instance1_1.xml",
			transformedData: "/testdata/cardmerge/instance2_1.xml",
			containerNamespace: null,
			containerName: 'collection'
		],
		(CARDINALITY_MERGE_2): [
			sourceSchema: "/testdata/cardmerge/t1.xsd",
			targetSchema: "/testdata/cardmerge/t2.xsd",
			alignment: "/testdata/cardmerge/t1t2.halex.alignment.xml",
			sourceData: "/testdata/cardmerge/instance1_2.xml",
			transformedData: "/testdata/cardmerge/instance2_2.xml",
			containerNamespace: null,
			containerName: 'collection'
		],
		(SIMPLE_COMPLEX): [
			sourceSchema: "/testdata/simplecomplex/t2.xsd",
			targetSchema: "/testdata/simplecomplex/t2.xsd",
			alignment: "/testdata/simplecomplex/t2t2.halex.alignment.xml",
			sourceData: "/testdata/simplecomplex/instance2.xml",
			transformedData: "/testdata/simplecomplex/instance2_result.xml",
			containerNamespace: null,
			containerName: 'collection'
		],
		(CARDINALITY_MOVE): defaultExample(CARDINALITY_MOVE),
		// context matching examples
		(CM_UNION_1): defaultExample(CM_UNION_1),
		(CM_UNION_2): defaultExample(CM_UNION_2),
		(CM_MULTI_2): defaultExample(CM_MULTI_2),
		(CM_MULTI_4): defaultExample(CM_MULTI_4),
		(CM_NESTED_1): defaultExample(CM_NESTED_1)
	];

	static def defaultExample(String folder) {
		[
					sourceSchema: "/testdata/${folder}/t1.xsd",
					targetSchema: "/testdata/${folder}/t2.xsd",
					alignment: "/testdata/${folder}/t1t2.halex.alignment.xml",
					sourceData: "/testdata/${folder}/instance1.xml",
					transformedData: "/testdata/${folder}/instance2.xml",
					containerNamespace: null,
					containerName: 'collection'
				]
	}

	/**
	 * Get the transformation example with the given identifier.
	 * 
	 * @param id the example identifier
	 * @return the transformation example
	 * @throws Exception if the example does not exist or loading it failed
	 */
	static TransformationExample getExample(String id) throws Exception {
		def exampleLocs = internalExamples[(id)]
		if (exampleLocs) {
			new InternalExample(
					exampleLocs.sourceSchema,
					exampleLocs.targetSchema,
					exampleLocs.alignment,
					exampleLocs.sourceData,
					exampleLocs.transformedData,
					exampleLocs.containerNamespace,
					exampleLocs.containerName);
		}
		else
			throw new IllegalArgumentException("No example with ID $id found.");
	}
}
