package eu.esdihumboldt.util.groovy.sandbox;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import eu.esdihumboldt.util.groovy.sandbox.internal.RestrictiveGroovyInterceptor;
import eu.esdihumboldt.util.groovy.sandbox.internal.SecureScript;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

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

	private final CopyOnWriteArraySet<GroovyServiceListener> listeners = new CopyOnWriteArraySet<GroovyServiceListener>();

	private boolean restrictionActive = true;
	private final RestrictiveGroovyInterceptor interceptor;

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
		interceptor = new RestrictiveGroovyInterceptor(additionalAllowedClasses,
				additionalAllAllowedClasses);
	}

	@Override
	public GroovyShell createShell(Binding binding) {
		// TODO use a specific classloader?
		CompilerConfiguration cc = new CompilerConfiguration();
		if (isRestrictionActive()) {
			cc.addCompilationCustomizers(new SandboxTransformer());
			cc.setScriptBaseClass(SecureScript.class.getName());
		}
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
		boolean registered = false;
		if (isRestrictionActive()) {
			if (!(script instanceof SecureScript)) {
				throw new GroovyRestrictionException(
						"Supplied script was not parsed with active restriction.");
			}
			interceptor.register();
			registered = true;
		}
		try {
			return script.run();
		} finally {
			if (registered)
				interceptor.unregister();
		}
	}

	@Override
	public boolean isRestrictionActive() {
		return restrictionActive;
	}

	@Override
	public void setRestrictionActive(boolean active) {
		if (active != restrictionActive) {
			restrictionActive = active;
			notifyRestrictionChanged(active);
		}
	}

	@Override
	public void addListener(GroovyServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(GroovyServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Call when restriction active changes.
	 * 
	 * @param restrictionActive the new value
	 */
	protected void notifyRestrictionChanged(boolean restrictionActive) {
		for (GroovyServiceListener listener : listeners) {
			listener.restrictionChanged(restrictionActive);
		}
	}
}
