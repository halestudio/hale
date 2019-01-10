package eu.esdihumboldt.util.groovy.sandbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Nullable;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import eu.esdihumboldt.util.groovy.sandbox.internal.RestrictiveGroovyInterceptor;
import eu.esdihumboldt.util.groovy.sandbox.internal.RestrictiveGroovyInterceptor.AllowedPrefix;
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
		interceptor = createInterceptorFromExtensions();
	}

	/**
	 * @return the Groovy interceptor configured with the allowed classes from
	 *         the extension point.
	 */
	public static RestrictiveGroovyInterceptor createInterceptorFromExtensions() {
		Set<Class<?>> additionalAllowedClasses = new HashSet<>();
		Set<Class<?>> additionalAllAllowedClasses = new HashSet<>();
		List<AllowedPrefix> additionalAllowedPackages = new ArrayList<>();

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
			if (conf.getName().equals("allowPackage")) {
				boolean allowChildren = Boolean.parseBoolean(conf.getAttribute("allowChildren"));
				String packageName = conf.getAttribute("name");
				additionalAllowedPackages.add(new AllowedPrefix(packageName, allowChildren));
			}
		}
		return new RestrictiveGroovyInterceptor(additionalAllowedClasses,
				additionalAllAllowedClasses, additionalAllowedPackages);
	}

	@Override
	public GroovyShell createShell(Binding binding) {
		// TODO use a specific classloader?
		CompilerConfiguration cc = new CompilerConfiguration();

		// enable invoke dynamic support
		cc.getOptimizationOptions().put(CompilerConfiguration.INVOKEDYNAMIC, true);

		// add pre-defined imports
		ImportCustomizer importCustomizer = new ImportCustomizer();

		// add extension-defined imports
		configureImportsFromExtensions(importCustomizer);

		cc.addCompilationCustomizers(importCustomizer);

		/*
		 * Disable handling Groovy Grape annotations.
		 * 
		 * This mainly serves the purpose to allow external Groovy snippets to
		 * use Grapes, but have them disabled when imported into hale.
		 * 
		 * If at some point we support Grapes within hale studio, we will want
		 * to change this behavior. Then we will need to think about how we can
		 * deal with conflicts on the classpath.
		 */
		if (cc.getDisabledGlobalASTTransformations() == null) {
			cc.setDisabledGlobalASTTransformations(new HashSet<String>());
		}
		cc.getDisabledGlobalASTTransformations().add("groovy.grape.GrabAnnotationTransformation");

		if (isRestrictionActive()) {
			// configure restriction
			cc.addCompilationCustomizers(new SandboxTransformer());
			cc.setScriptBaseClass(SecureScript.class.getName());
		}

		if (binding != null)
			return new GroovyShell(binding, cc);
		else
			return new GroovyShell(cc);
	}

	/**
	 * Add imports defined as extensions.
	 * 
	 * @param importCustomizer the import customizer
	 */
	private static void configureImportsFromExtensions(ImportCustomizer importCustomizer) {
		for (IConfigurationElement conf : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ID)) {
			if (conf.getName().equals("import")) {
				String className = conf.getAttribute("class");
				String alias = conf.getAttribute("alias");

				if (className != null && !className.isEmpty()) {
					if (alias == null || alias.isEmpty()) {
						int lastDotIndex = className.lastIndexOf('.');
						if (lastDotIndex >= 0) {
							if (lastDotIndex < className.length() - 1) {
								alias = className.substring(lastDotIndex + 1);
							}
							else {
								alias = null;
							}
						}
						else {
							alias = className;
						}
					}

					if (alias != null) {
						importCustomizer.addImport(alias, className);
					}
				}
			}

			// TODO support also other kind of imports?
			// e.g. star imports, static imports...
		}
	}

	@Override
	public Script parseScript(String script, Binding binding) {
		return createShell(binding).parse(script);
	}

	@Override
	public <T> T evaluate(Script script, @Nullable ResultProcessor<T> processor) throws Exception {
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
			Object returnValue = script.run();
			if (processor != null) {
				return processor.process(script, returnValue);
			}
			else {
				return null;
			}
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
