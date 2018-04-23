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

package eu.esdihumboldt.hale.io.groovy.snippets

import org.junit.After
import org.junit.Before
import org.junit.Test

import eu.esdihumboldt.hale.common.core.service.ServiceProvider
import eu.esdihumboldt.hale.io.groovy.snippets.impl.SnippetServiceImpl
import eu.esdihumboldt.hale.io.groovy.snippets.impl.StringSnippet
import eu.esdihumboldt.util.groovy.sandbox.DefaultGroovyService
import eu.esdihumboldt.util.groovy.sandbox.GroovyService

/**
 * Tests for accessing snippets via GroovySnippets.
 * 
 * @author Simon Templer
 */
class GroovySnippetsTest {

	private ServiceProvider services

	@Test
	void testRun() {
		addSnippet('test', '6 * 7')

		def res = get().test()
		assert res == 42
	}

	@Test
	void testParentBindung() {
		addSnippet('test', 'x * y')

		def res = get(x: '*', y: 5).test()
		assert res == '*****'
	}

	@Test
	void testCallBindung() {
		addSnippet('test', 'x * y')

		def res = get().test(x: '+', y: 3)
		assert res == '+++'
	}

	@Test
	void testOverrideBindung() {
		addSnippet('test', 'x * y')

		def res = get(x: '*', y: 5).test(y: 3)
		assert res == '***'
	}

	@Test
	void testCallMethod() {
		addSnippet('myclass', '''
def myfunction(x, y) {
  x * y
}

42
'''.stripMargin())

		def res = get().myclass.myfunction('+', 3)
		assert res == '+++'
	}

	@Test
	void testCallClosure() {
		addSnippet('myclass', '''
def myfunction(x, y) {
  x * y
}

42
'''.stripMargin())
		def res = get().myclass { myfunction('+', 3) }
		assert res == '+++'
	}

	@Test
	void testCustomClass() {
		addSnippet('myclass', '''
class MyClass {
  def myfunction(x, y) {
    x * y
  }
}

new MyClass()
'''.stripMargin())

		def my = get().myclass()
		def res = my.myfunction('+', 3)
		assert res == '+++'
	}

	@Test
	void testRunsInHale() {
		addSnippet('test', "binding.hasVariable('runs_in_hale')")

		def res = get().test()
		assert res == true
	}

	// util

	void addSnippet(String id, String script) {
		def sn = new StringSnippet(script, id)
		services.getService(SnippetService).addSnippet(UUID.randomUUID().toString(), sn)
	}

	GroovySnippets get(Map binding = null) {
		Map vars = binding ?: [:]
		new GroovySnippets(services, new Binding(vars))
	}

	// setup

	@Before
	void setup() {
		GroovyService gs = new DefaultGroovyService()
		SnippetService ss = new SnippetServiceImpl(gs)

		services = new ServiceProvider() {
					public <T> T getService(Class<T> serviceInterface) {
						if (serviceInterface == GroovyService) {
							gs
						}
						else if (serviceInterface == SnippetService) {
							ss
						}
						else {
							throw new IllegalStateException()
						}
					}
				}
	}

	@After
	void reset() {
		services = null
	}
}
