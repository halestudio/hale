package eu.esdihumboldt.util.groovy.sandbox;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import eu.esdihumboldt.util.groovy.sandbox.internal.RestrictiveGroovyInterceptor;

/**
 * Default implementation of the {@link GroovyService} interface.
 * 
 * @author Kai Schwierczek
 */
public class DefaultGroovyService implements GroovyService {

	/**
	 * Extension point ID.
	 */
	private static final String ID = "eu.esdihumboldt.util.groovy.sandbox";

	private boolean restrictionActive = true;
	private final Set<Class<?>> additionalAllowedClasses;
	private final Set<Class<?>> additionalAllAllowedClasses;

	/**
	 * Constructs a new groovy sandbox service. Use
	 * eu.esdihumboldt.util.groovy.sandbox extension point for configuration
	 * options.
	 */
	public DefaultGroovyService() {
		Set<Class<?>> additionalAllowedClasses = new HashSet<>();
		Set<Class<?>> additionalAllAllowedClasses = new HashSet<>();

		for (IConfigurationElement conf : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ID)) {
			if (conf.getName().equals("allow")) {
				boolean allowAll = Boolean.parseBoolean(conf.getAttribute("allowAll"));
				Class<?> allowedClass = ExtensionUtil.loadClass(conf, "class");
				if (allowAll)
					additionalAllAllowedClasses.add(allowedClass);
				else
					additionalAllowedClasses.add(allowedClass);
			}
		}
		this.additionalAllowedClasses = Collections.unmodifiableSet(additionalAllowedClasses);
		this.additionalAllAllowedClasses = Collections.unmodifiableSet(additionalAllAllowedClasses);
	}

	@Override
	public GroovyShell createShell(Binding binding) {
		// TODO use a specific classloader?
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(new SandboxTransformer());
		if (binding != null)
			return new GroovyShell(binding, cc);
		else
			return new GroovyShell(cc);
	}

	@Override
	public Script parseScript(String script, Binding binding) {
		return createShell(binding).parse(script);
	}

	@Override
	public Object evaluate(Script script) {
		RestrictiveGroovyInterceptor interceptor = new RestrictiveGroovyInterceptor(
				additionalAllowedClasses, additionalAllAllowedClasses);
		interceptor.register();
		try {
			return script.run();
		} finally {
			interceptor.unregister();
		}
	}

	@Override
	public boolean isRestrictionActive() {
		return restrictionActive;
	}

	@Override
	public void setRestrictionActive(boolean active) {
		restrictionActive = active;
	}
}
