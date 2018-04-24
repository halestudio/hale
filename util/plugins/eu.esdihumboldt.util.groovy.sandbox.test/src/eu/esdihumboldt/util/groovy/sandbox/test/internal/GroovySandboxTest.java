package eu.esdihumboldt.util.groovy.sandbox.test.internal;

import static org.junit.Assert.fail;

import java.util.Collections;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import eu.esdihumboldt.util.groovy.sandbox.internal.RestrictiveGroovyInterceptor;
import eu.esdihumboldt.util.groovy.sandbox.internal.RestrictiveGroovyInterceptor.AllowedPrefix;
import groovy.lang.GroovyShell;

/**
 * Test for Groovy sandboxing.
 * 
 * @author Kai Schwierczek
 */
@SuppressWarnings("restriction")
public class GroovySandboxTest {

	private RestrictiveGroovyInterceptor interceptor;
	private GroovyShell shell;

	/**
	 * Sets up the Groovy shell and interceptor.
	 */
	@Before
	public void setUp() {
		CompilerConfiguration cc = new CompilerConfiguration();

		// enable invoke dynamic support (simiar to in when scripts are created)
		cc.getOptimizationOptions().put(CompilerConfiguration.INVOKEDYNAMIC, true);

		cc.addCompilationCustomizers(new SandboxTransformer());
		shell = new GroovyShell(cc);
		interceptor = new RestrictiveGroovyInterceptor(Collections.<Class<?>> emptySet(),
				Collections.<Class<?>> emptySet(), Collections.<AllowedPrefix> emptyList());
	}

	/**
	 * Tests that the basic classes are allowed with their defined methods.
	 */
	@Test
	public void basicClassesAllowed() {
		interceptor.register();
		shell.evaluate("new String().toString()");
		shell.evaluate("new Integer(5).intValue(); Integer.MAX_VALUE");
		shell.evaluate("Math.random()");
		shell.evaluate("new Date().getTime()");
		shell.evaluate("\"${new Date()}.toString()\"");
		shell.evaluate("['foo', 'bar', 'list'].size()");
		shell.evaluate("['foo':'bar', 'map':'value'].size()");
		shell.evaluate("[1..3].size()");
		interceptor.unregister();
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
	 * Tests, that access to not allowed classes is disallowed.
	 */
	@Test
	public void notAllowedClassesDisallowed() {
		interceptor.register();
		// some exemplary tests
		assertDisallowed("System.exit(0)");
		assertDisallowed("''.getClass().forName('java.lang.System')");
		interceptor.unregister();
	}

	/**
	 * Tests, that certain Groovy methods, properties cannot be accessed.
	 */
	@Test
	public void groovyStuffDisallowed() {
		interceptor.register();
		assertDisallowed("\"${new Date()}\".invokeMethod('blafoo', null)");
		assertDisallowed("metaClass = null");
		assertDisallowed("setMetaClass(null)");
		assertDisallowed("setProperty(null, null)");
		interceptor.unregister();
	}

	/**
	 * Tests, that certain Closure methods, properties cannot be accessed.
	 */
	@Test
	public void closureStuffDisallowed() {
		interceptor.register();
		assertDisallowed("{->}.delegate = null");
		assertDisallowed("{->}.setDelegate(null)");
		assertDisallowed("{->}.directive = 0");
		assertDisallowed("{->}.setDirective(0)");
		assertDisallowed("{->}.resolveStrategy = 0");
		assertDisallowed("{->}.setResolveStrategy(0)");
		assertDisallowed("{->}.rehydrate(0, 1, 2)");

		// test one pair from within closure
		assertDisallowed("{-> resolveStrategy = 0}()");
		assertDisallowed("{-> setResolveStrategy(0)}()");
		interceptor.unregister();
	}

	/**
	 * Tests, that expressions inside GStrings are intercepted, too.
	 */
	@Test
	public void gstringIntercepted() {
		interceptor.register();
		assertDisallowed("\"${->System.exit(0)}\".toString()");
		assertDisallowed("\"${out -> out << 'foo'}\".toString()");
		assertDisallowed("\"${new File('foo')}\".toString()");
		interceptor.unregister();
	}
}
