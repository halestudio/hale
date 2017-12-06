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
@SuppressWarnings('javadoc')
abstract class TransformationExamples {

	public static final String SIMPLE_RENAME = 'simplerename'
	public static final String SIMPLE_ATTRIBUTE = 'simpleatt'
	public static final String SIMPLE_ASSIGN = 'simpleassign'
	public static final String CARD_RENAME = 'cardrename'
	public static final String DUPE_ASSIGN = 'dupeassign'
	public static final String PROPERTY_JOIN = 'propjoin'
	public static final String JOIN_MULTI_COND_1 = 'join_multi_cond_1'
	public static final String MERGE = 'merge'
	public static final String MERGE2 = 'merge2'
	public static final String MERGE3 = 'merge3'
	public static final String MERGE4 = 'merge4'
	public static final String MERGE5 = 'merge5'
	public static final String MERGE6 = 'merge6'
	public static final String SIMPLE_MERGE = 'simplemerge'
	public static final String CARDINALITY_MERGE_1 = 'cardmerge_1'
	public static final String CARDINALITY_MERGE_2 = 'cardmerge_2'
	public static final String SIMPLE_COMPLEX = 'simplecomplex'
	public static final String CARDINALITY_MOVE = 'cardmove'
	public static final String STRUCTURAL_RENAME_1 = 'structuralrename_1'
	public static final String STRUCTURAL_RENAME_2 = 'structuralrename_2'
	public static final String STRUCTURAL_RENAME_3 = 'structuralrename_3'
	public static final String STRUCTURAL_RETYPE_1 = 'structuralretype_1'
	public static final String INLINE_2 = 'inline_2'
	public static final String INLINE_3 = 'inline_3'
	public static final String MATH_EXPRESSION = 'mathexpression'
	public static final String REGEX_ANALYSIS = 'regexstringanalysis'
	public static final String GENERATEUID = 'generateuid'
	public static final String CLASSIFICATION_1 = 'classification1'
	public static final String CLASSIFICATION_2 = 'classification2'
	public static final String CLASSIFICATION_3 = 'classification3'
	public static final String CLASSIFICATION_4 = 'classification4'
	public static final String FORMATSTRING = 'formatstring'
	public static final String PRIORITY = 'priority'
	public static final String NULLVALUE = 'nullvalue'

	public static final String GROOVY1 = 'groovy1'
	public static final String GROOVY2 = 'groovy2'
	public static final String GROOVY3 = 'groovy3'

	public static final String MULTI_RESULT_1 = 'multiresult1'
	public static final String MULTI_RESULT_2 = 'multiresult2'

	public static final String PROPCONDITION1 = 'propcondition1'
	public static final String PROPCONDITION2 = 'propcondition2'
	public static final String TYPEFILTER = 'typefilter'
	public static final String PROPERTYFILTER = 'propertyfilter'
	public static final String IMPASSIGN = 'impassign'

	public static final String CM_UNION_1 = 'cm_union_1'
	public static final String CM_UNION_2 = 'cm_union_2'
	public static final String CM_UNION_3 = 'cm_union_3'
	public static final String CM_UNION_4 = 'cm_union_4'
	public static final String CM_UNION_5 = 'cm_union_5'
	public static final String CM_UNION_6 = 'cm_union_6'

	public static final String CM_CCROSSOVER_1 = 'cm_ccrossover_1'

	public static final String CM_PCROSSOVER_1 = 'cm_pcrossover_1'
	public static final String CM_PCROSSOVER_1B = 'cm_pcrossover_1b'
	public static final String CM_PCROSSOVER_2 = 'cm_pcrossover_2'
	public static final String CM_PCROSSOVER_3 = 'cm_pcrossover_3'
	public static final String CM_PCROSSOVER_4 = 'cm_pcrossover_4'
	public static final String CM_PCROSSOVER_4_EX_1 = 'cm_pcrossover_4_ex_1'
	public static final String CM_PCROSSOVER_4_EX_2 = 'cm_pcrossover_4_ex_2'
	public static final String CM_PCROSSOVER_5 = 'cm_pcrossover_5'
	public static final String CM_PCROSSOVER_6 = 'cm_pcrossover_6'
	public static final String CM_PCROSSOVER_6B = 'cm_pcrossover_6b'

	public static final String CM_MULTI_1 = 'cm_multi_1'
	public static final String CM_MULTI_1B = 'cm_multi_1b'
	public static final String CM_MULTI_2 = 'cm_multi_2'
	public static final String CM_MULTI_3 = 'cm_multi_3'
	public static final String CM_MULTI_4 = 'cm_multi_4'

	public static final String CM_NESTED_1 = 'cm_nested_1'
	public static final String CM_NESTED_2 = 'cm_nested_2'
	public static final String CM_NESTED_3 = 'cm_nested_3'
	public static final String CM_NESTED_3B = 'cm_nested_3b'
	public static final String CM_NESTED_4 = 'cm_nested_4'
	public static final String CM_NESTED_5 = 'cm_nested_5'
	public static final String CM_NESTED_6 = 'cm_nested_6'

	public static final String XSL_XPATH_1 = 'xpath1'

	/**
	 * Internal example map.
	 */
	private static final def internalExamples = [
		(SIMPLE_RENAME): defaultExample(SIMPLE_RENAME),
		(SIMPLE_ATTRIBUTE): defaultExample(SIMPLE_ATTRIBUTE),
		(SIMPLE_ASSIGN): defaultExample(SIMPLE_ASSIGN),
		(CARD_RENAME): defaultExample(CARD_RENAME),
		(DUPE_ASSIGN): defaultExample(DUPE_ASSIGN),
		(PROPERTY_JOIN): defaultExample(PROPERTY_JOIN),
		(JOIN_MULTI_COND_1): defaultExample(JOIN_MULTI_COND_1),
		(SIMPLE_MERGE): defaultExample(SIMPLE_MERGE),
		(MERGE): defaultExample(MERGE),
		(MERGE2): defaultExample(MERGE2),
		(MERGE3): defaultExample(MERGE3),
		(MERGE4): defaultExample(MERGE4),
		(MERGE5): defaultExample(MERGE5),
		(MERGE6): defaultExample(MERGE6),
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
		(STRUCTURAL_RENAME_1): defaultExample(STRUCTURAL_RENAME_1),
		(STRUCTURAL_RENAME_2): defaultExample(STRUCTURAL_RENAME_2),
		(STRUCTURAL_RENAME_3): defaultExample(STRUCTURAL_RENAME_3),
		(STRUCTURAL_RETYPE_1): defaultExample(STRUCTURAL_RETYPE_1),
		(INLINE_2): defaultExample(INLINE_2),
		(INLINE_3): defaultExample(INLINE_3),
		(MATH_EXPRESSION): defaultExample(MATH_EXPRESSION),
		(REGEX_ANALYSIS): defaultExample(REGEX_ANALYSIS),
		(GENERATEUID): defaultExample(GENERATEUID),
		(CLASSIFICATION_1): defaultExample(CLASSIFICATION_1),
		(CLASSIFICATION_2): defaultExample(CLASSIFICATION_2),
		(CLASSIFICATION_3): defaultExample(CLASSIFICATION_3),
		(CLASSIFICATION_4): defaultExample(CLASSIFICATION_4),
		(FORMATSTRING): defaultExample(FORMATSTRING),
		(PRIORITY): defaultExample(PRIORITY),
		(NULLVALUE): defaultExample(NULLVALUE),
		(PROPCONDITION1): defaultExample(PROPCONDITION1),
		(PROPCONDITION2): defaultExample(PROPCONDITION2),
		(TYPEFILTER): defaultExample(TYPEFILTER),
		(PROPERTYFILTER): defaultExample(PROPERTYFILTER),
		(IMPASSIGN): defaultExample(IMPASSIGN),

		(GROOVY1): defaultExample(GROOVY1),
		(GROOVY2): defaultExample(GROOVY2),
		(GROOVY3): defaultExample(GROOVY3),

		(MULTI_RESULT_1): defaultExample(MULTI_RESULT_1),
		(MULTI_RESULT_2): defaultExample(MULTI_RESULT_2),

		// context matching examples
		(CM_UNION_1): defaultExample(CM_UNION_1),
		(CM_UNION_2): defaultExample(CM_UNION_2),
		(CM_UNION_3): defaultExample(CM_UNION_3),
		(CM_UNION_4): defaultExample(CM_UNION_4),
		(CM_UNION_5): defaultExample(CM_UNION_5),
		(CM_UNION_6): defaultExample(CM_UNION_6),

		(CM_CCROSSOVER_1): defaultExample(CM_CCROSSOVER_1),

		(CM_PCROSSOVER_1): defaultExample(CM_PCROSSOVER_1),
		(CM_PCROSSOVER_1B): defaultExample(CM_PCROSSOVER_1B),
		(CM_PCROSSOVER_2): defaultExample(CM_PCROSSOVER_2),
		(CM_PCROSSOVER_3): defaultExample(CM_PCROSSOVER_3),
		(CM_PCROSSOVER_4): defaultExample(CM_PCROSSOVER_4),
		(CM_PCROSSOVER_4_EX_1): defaultExample(CM_PCROSSOVER_4_EX_1),
		(CM_PCROSSOVER_4_EX_2): defaultExample(CM_PCROSSOVER_4_EX_2),
		(CM_PCROSSOVER_5): defaultExample(CM_PCROSSOVER_5),
		(CM_PCROSSOVER_6): defaultExample(CM_PCROSSOVER_6),
		(CM_PCROSSOVER_6B): defaultExample(CM_PCROSSOVER_6B),

		(CM_MULTI_1): defaultExample(CM_MULTI_1),
		(CM_MULTI_1B): defaultExample(CM_MULTI_1B),
		(CM_MULTI_2): defaultExample(CM_MULTI_2),
		(CM_MULTI_3): defaultExample(CM_MULTI_3),
		(CM_MULTI_4): defaultExample(CM_MULTI_4),

		(CM_NESTED_1): defaultExample(CM_NESTED_1),
		(CM_NESTED_2): defaultExample(CM_NESTED_2),
		(CM_NESTED_3): defaultExample(CM_NESTED_3),
		(CM_NESTED_3B): defaultExample(CM_NESTED_3B),
		(CM_NESTED_4): defaultExample(CM_NESTED_4),
		(CM_NESTED_5): defaultExample(CM_NESTED_5),
		(CM_NESTED_6): defaultExample(CM_NESTED_6),

		// XSL only examples
		(XSL_XPATH_1): defaultExample(XSL_XPATH_1)
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
