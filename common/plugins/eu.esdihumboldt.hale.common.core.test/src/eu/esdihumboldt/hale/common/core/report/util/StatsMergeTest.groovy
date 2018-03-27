/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.report.util

import org.junit.Test

/**
 * Tests for merging statistics.
 * 
 * @author Simon Templer
 */
class StatsMergeTest {

	@Test
	void testMerge() {
		def a = [report:
			[summary: 'Test 1', completed: true, duration: 1, count: 10]
		]

		def b = [report:
			[summary: 'Test 2', completed: false, count: 5]
		]

		def expected = [report:
			[summary: ['Test 1', 'Test 2'], completed: false, duration: 1, count: 15]
		]

		def result = new StatsMerge(true).mergeConfigs(a, b)

		assert result == expected
	}

	@Test
	void testMerge2() {
		def a = [report:
			[summary: 'Test 1', completed: true, duration: 1, count: 10]
		]

		def b = [('la.la'):
			[summary: 'Test 2', completed: true, count: 1]
		]

		def c = [report:
			[summary: 'Test 3', completed: false, count: 5]
		]

		def expected = [
			report: [summary: ['Test 1', 'Test 3'], completed: false, duration: 1, count: 15],
			la_la: [summary: 'Test 2', completed: true, count: 1],
		]

		def result = new StatsMerge(true).mergeConfigs(a, b)
		result = new StatsMerge(true).mergeConfigs(result, c)

		assert result == expected
	}

	@Test
	void testMerge3() {
		def a = ['report.1':
			[summary: 'Test 1', completed: true, duration: 1, count: 10]
		]

		def b = ['report.2':
			[summary: 'Test 2', completed: false, count: 5]
		]

		def expected = [
			'report_1': [summary: 'Test 1', completed: true, duration: 1, count: 10],
			'report_2': [summary: 'Test 2', completed: false, count: 5]
		]

		def result = new StatsMerge(true).mergeConfigs(a, b)

		assert result == expected
	}

	@Test
	void testMerge4() {
		def a = [report:
			[summary: 'Test 1', completed: true, duration: 1, 'c.ount': 10]
		]

		def b = [report:
			[summary: 'Test 2', completed: true, 'c.ount': 1]
		]

		def c = [report:
			[summary: 'Test 3', completed: false, 'c.ount': 5]
		]

		def d = [report:
			[summary: 'Test 4', completed: false, 'c.ount': 7]
		]

		def expected = [
			report: [summary: [
					'Test 1',
					'Test 2',
					'Test 3',
					'Test 4'
				], completed: false, duration: 1, c_ount: 23]
		]

		def result = new StatsMerge(true).mergeConfigs(a, b)
		result = new StatsMerge(true).mergeConfigs(result, c)
		result = new StatsMerge(true).mergeConfigs(result, d)

		assert result == expected
	}

	@Test
	void testMerge5() {
		def a = ['report.1':
			[summary: 'Test 1', completed: true, duration: 1, 'c.ount': 10, types: ['www.example.com': 12]]
		]

		def expected = ['report_1':
			[summary: 'Test 1', completed: true, duration: 1, 'c_ount': 10, types: ['www_example_com': 12]]
		]

		def result = new StatsMerge(true).mergeConfigs([:], a)

		assert result == expected
	}

	@Test
	void testMerge6() {
		def a = ['report.1':
			[summary: 'Test 1', completed: true, duration: 1, 'c.ount': 10, types: ['www.example.com': 12]]
		]

		def expected = ['report.1':
			[summary: 'Test 1', completed: true, duration: 1, 'c.ount': 10, types: ['www.example.com': 12]]
		]

		def result = new StatsMerge(false).mergeConfigs([:], a)

		assert result == expected
	}
}
