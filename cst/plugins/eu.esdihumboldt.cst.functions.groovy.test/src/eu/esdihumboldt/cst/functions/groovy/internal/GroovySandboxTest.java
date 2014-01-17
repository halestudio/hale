package eu.esdihumboldt.cst.functions.groovy.internal;

import static junit.framework.Assert.fail;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

/**
 * Test for Groovy sandboxing.
 * 
 * @author Kai Schwierczek
 */
public class GroovySandboxTest {

	private RestrictiveGroovyInterceptor interceptor;
	private GroovyShell shell;

	/**
	 * Sets up the Groovy shell and interceptor.
	 */
	@Before
	public void setUp() {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(new SandboxTransformer());
		shell = new GroovyShell(cc);
		interceptor = new RestrictiveGroovyInterceptor();
		interceptor.register();
	}

	/**
	 * Unregisters the interceptor.
	 */
	@After
	public void tearDown() {
		interceptor.unregister();
	}

	/**
	 * Tests that the basic classes are allowed with their defined methods.
	 */
	@Test
	public void basicClassesAllowed() {
		shell.evaluate("new String().toString()");
		shell.evaluate("new Integer(5).intValue(); Integer.MAX_VALUE");
		shell.evaluate("Math.random()");
		shell.evaluate("new Date().getTime()");
		shell.evaluate("\"${new Date()}.toString()\"");
		shell.evaluate("['foo', 'bar', 'list'].size()");
		shell.evaluate("['foo':'bar', 'map':'value'].size()");
		shell.evaluate("[1..3].size()");
	}

	private void assertDisallowed(String script) {
		boolean caught = false;
		try {
			shell.evaluate(script);
		} catch (Exception e) {
			caught = true;
		} finally {
			if (!caught)
				fail("script was not intercepted correctly! " + script);
		}
	}

	/**
	 * Tests, that access to System.* is disallowed.
	 */
	@Test
	public void notAllowedClassesDisallowed() {
		// some exemplary tests
		assertDisallowed("System.exit(0)");
		assertDisallowed("''.getClass().forName('java.lang.System')");
	}

	/**
	 * Tests, that certain Groovy methods, properties cannot be accessed.
	 */
	@Test
	public void groovyStuffDisallowed() {
		assertDisallowed("\"${new Date()}\".invokeMethod('blafoo', null)");
		assertDisallowed("metaClass = null");
		assertDisallowed("setMetaClass(null)");
		assertDisallowed("setProperty(null, null)");
	}

	/**
	 * Tests, that certain Groovy methods, properties cannot be accessed.
	 */
	@Test
	public void closureStuffDisallowed() {
		assertDisallowed("{->}.delegate = null");
		assertDisallowed("{->}.setDelegate(null)");
		assertDisallowed("{->}.directive = 0");
		assertDisallowed("{->}.setDirective(0)");
		assertDisallowed("{->}.resolveStrategy = 0");
		assertDisallowed("{->}.setResolveStrategy(0)");

		// test one pair from within closure
		assertDisallowed("{-> resolveStrategy = 0}()");
		assertDisallowed("{-> setResolveStrategy(0)}()");
	}

	/**
	 * Tests, that expressions inside GStrings are intercepted, too.
	 */
	@Test
	public void gstringIntercepted() {
		assertDisallowed("\"${->System.exit(0)}\".toString()");
		assertDisallowed("\"${out -> out << 'foo'}\".toString()");
		assertDisallowed("\"${new File('foo')}\".toString()");
	}
}
