/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.helper.extension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.common.base.Splitter;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.ContextAwareHelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperContext;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Groovy script helper functions extension point.
 * 
 * @author Simon Templer
 */
public class HelperFunctionsExtension implements HelperFunctionsService {

	private static final String EXTENSION_ID = "eu.esdihumboldt.cst.functions.groovy.helper";

	private static final ALogger log = ALoggerFactory.getLogger(HelperFunctionsExtension.class);

	private final Map<Category, Map<String, HelperFunctionOrCategory>> children = new HashMap<>();

	private final AtomicBoolean initialized = new AtomicBoolean();
	private static final String SPEC_END = "_spec";

	private final ServiceProvider serviceProvider;

	private final HelperContext defaultContext = new HelperContext() {

		@Override
		public Cell getTypeCell() {
			return null;
		}

		@Override
		public ServiceProvider getServiceProvider() {
			return serviceProvider;
		}

		@Override
		public ExecutionContext getExecutionContext() {
			return null;
		}

		@Override
		public Cell getContextCell() {
			return null;
		}
	};

	/**
	 * Create a helper function extension instance.
	 * 
	 * @param serviceProvider the service provider if available
	 */
	public HelperFunctionsExtension(@Nullable ServiceProvider serviceProvider) {
		super();
		this.serviceProvider = serviceProvider;
	}

	/**
	 * Initialize the extension point from the registered extensions (if not
	 * already done).
	 */
	protected void init() {
		if (initialized.compareAndSet(false, true)) {
			synchronized (children) {
				IConfigurationElement[] elements = Platform.getExtensionRegistry()
						.getConfigurationElementsFor(EXTENSION_ID);

				for (IConfigurationElement element : elements) {
					if ("helper".equals(element.getName())) {
						String category = element.getAttribute("category");
						if ("ROOT".equals(category)) {
							category = "";
						}
						String customName = element.getAttribute("name");
						Class<?> helperClass = ExtensionUtil.loadClass(element, "class");

						try {
							Iterable<HelperFunctionWrapper> functions = loadFunctions(helperClass,
									customName);

							addToCategory(category, functions);
						} catch (Exception e) {
							log.error("Failed loading Groovy helper functions", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Add the given functions to the given category.
	 * 
	 * @param category the category path
	 * @param functions the functions to add to the category
	 */
	private void addToCategory(String category, Iterable<HelperFunctionWrapper> functions) {
		Iterable<String> path = Splitter.on('.').omitEmptyStrings().split(category);

		Category cat = new Category(path);
		Map<String, HelperFunctionOrCategory> catMap = children.get(cat);
		if (catMap == null) {
			catMap = new HashMap<>();
			children.put(cat, catMap);
		}

		for (HelperFunctionWrapper function : functions) {
			Object previous = catMap.put(function.getName(), function);
			if (previous != null) {
				log.error(MessageFormat.format("Duplicate helper function {0}.{1}", cat,
						function.getName()));
			}
		}

		// make sure category is listed
		while (cat != null) {
			Category parent = cat.getParent();
			Map<String, HelperFunctionOrCategory> parentMap = children.get(parent);
			if (parentMap == null) {
				parentMap = new HashMap<>();
				children.put(parent, parentMap);
			}
			parentMap.put(cat.getName(), cat);

			// check parent category
			cat = parent;
		}
	}

	/**
	 * Load helper functions from a class that defines them.
	 * 
	 * @param helperClass the helper class, either a {@link HelperFunction} or a
	 *            class that defines helper functions by convention
	 * @param customName the custom name for a helper function, only applicable
	 *            for {@link HelperFunction} classes
	 * @return the functions that were loaded from the class
	 * @throws Exception if loading the functions failed
	 */
	private Iterable<HelperFunctionWrapper> loadFunctions(final Class<?> helperClass,
			String customName) throws Exception {
		if (HelperFunction.class.isAssignableFrom(helperClass)) {
			// name must be defined
			if (customName == null) {
				throw new IllegalStateException(
						"Function name must be specified for HelperFunction implementations");
			}

			// is already a helper function
			HelperFunction<?> function = (HelperFunction<?>) helperClass.newInstance();
			return Collections.singleton(new HelperFunctionWrapper(function, customName));
		}
		else {
			// determine functions via reflection

			List<HelperFunctionWrapper> functions = new ArrayList<>();
			for (Method method : helperClass.getMethods()) {
				int modifiers = method.getModifiers();
				if (method.getName().startsWith("_")
						&& !method.getName().startsWith(
								"__") /* exclude __$swapInit and the like */
						&& !Modifier.isAbstract(modifiers)
						&& !method.getName().endsWith(SPEC_END)) {
					HelperFunctionWrapper function = loadFunction(method, helperClass);
					if (function != null) {
						functions.add(function);
					}
				}
			}
			return functions;
		}
	}

	/**
	 * Load helper function via reflection from a method.
	 * 
	 * @param callMethod the method (probably) defining a helper function
	 * @param helperClass the class defining the method
	 * @return the loaded helper function or <code>null</code>
	 */
	@Nullable
	protected HelperFunctionWrapper loadFunction(final Method callMethod, Class<?> helperClass) {
		int modifiers = callMethod.getModifiers();

		// a candidate -> check parameters
		Class<?>[] params = callMethod.getParameterTypes();
		if (params != null && params.length <= 2) {
			// has maximum two parameters

			// last parameter may be context parameter
			final boolean hasContextParam = params.length >= 1
					&& params[params.length - 1].equals(HelperContext.class);
			// check if there is an actual main parameter
			final boolean hasMainParam = (hasContextParam && params.length == 2)
					|| (!hasContextParam && params.length == 1);

			final boolean isStatic = Modifier.isStatic(modifiers);

			// Get the specification from field
			String specFieldOrMethodName = callMethod.getName() + SPEC_END;

			Object fieldV = null;
			try {
				Field field = helperClass.getField(specFieldOrMethodName);
				int fieldModifiers = field.getModifiers();
				if (Modifier.isStatic(fieldModifiers) && Modifier.isFinal(fieldModifiers)) {
					fieldV = field.get(null);
				}
			} catch (Exception e) {
				// do nothing
			}
			final Object fieldValue = fieldV;

			// Get spec from method
			Method meth = null;
			boolean isSpecStatic = false;
			try {
				meth = helperClass.getMethod(specFieldOrMethodName, new Class[] { String.class });
				int specModifier = meth.getModifiers();
				isSpecStatic = Modifier.isStatic(specModifier);

			} catch (Exception e) {
				// do nothing
			}
			final Method specMethod = meth;
			final boolean isSpecMethodStatic = isSpecStatic;

			HelperFunction<Object> function = new ContextAwareHelperFunction<Object>() {

				@Override
				public Object call(Object arg, HelperContext context) throws Exception {
					Object helper = null;
					if (!isStatic) {
						helper = helperClass.newInstance();
					}
					if (hasMainParam) {
						if (hasContextParam) {
							return callMethod.invoke(helper, arg, context);
						}
						else {
							return callMethod.invoke(helper, arg);
						}
					}
					else {
						if (hasContextParam) {
							return callMethod.invoke(helper, context);
						}
						else {
							return callMethod.invoke(helper);
						}
					}
				}

				@Override
				public Specification getSpec(String name) throws Exception {
					if (fieldValue != null && fieldValue instanceof Specification) {

						return ((Specification) fieldValue);
					}
					else if (specMethod != null) {
						if (isSpecMethodStatic) {
							return (Specification) specMethod.invoke(null, name);
						}
						else {
							Object helper = helperClass.newInstance();
							return (Specification) specMethod.invoke(helper, name);
						}

					}
					return null;
				}
			};

			// method name
			String name = callMethod.getName().substring(1);
			return new HelperFunctionWrapper(function, name);
		}

		return null;
	}

	@Override
	public Iterable<HelperFunctionOrCategory> getChildren(Category cat, HelperContext context) {
		init();

		final HelperContext theContext = extendContext(context);

		synchronized (children) {
			final Map<String, HelperFunctionOrCategory> catMap = children.get(cat);
			if (catMap == null) {
				return Collections.emptyList();
			}
			else {
				return () -> catMap.values().stream().map(fc -> injectContext(fc, theContext))
						.iterator();
			}
		}
	}

	@Override
	public HelperFunctionOrCategory get(Category cat, String name, HelperContext context) {
		init();

		context = extendContext(context);

		synchronized (children) {
			Map<String, HelperFunctionOrCategory> catMap = children.get(cat);
			if (catMap == null) {
				return null;
			}
			else {
				HelperFunctionOrCategory res = catMap.get(name);
				if (res != null) {
					return injectContext(res, context);
				}
				else {
					return null;
				}
			}
		}
	}

	/**
	 * Inject the helper context if applicable.
	 * 
	 * @param helperFunctionOrCategory the helper function or category
	 * @param context the helper context to inject
	 * @return the adapted helper function or the unchanged category
	 */
	protected HelperFunctionOrCategory injectContext(
			HelperFunctionOrCategory helperFunctionOrCategory, HelperContext context) {
		if (context != null) {
			HelperFunction<?> function = helperFunctionOrCategory.asFunction();
			if (function != null && function instanceof ContextAwareHelperFunction<?>) {
				return new HelperFunctionContextWrapper<>((ContextAwareHelperFunction<?>) function,
						helperFunctionOrCategory.getName(), context);
			}
		}
		return helperFunctionOrCategory;
	}

	/**
	 * Extend the given helper context w/ additional information if possible.
	 * 
	 * @param context the context to extend
	 * @return the extended context information
	 */
	protected HelperContext extendContext(final HelperContext context) {
		if (context == null) {
			return defaultContext;
		}
		else if (serviceProvider != null && context.getServiceProvider() == null) {
			// extend w/ service provider
			return new HelperContext() {

				@Override
				public Cell getTypeCell() {
					return context.getTypeCell();
				}

				@Override
				public ServiceProvider getServiceProvider() {
					return serviceProvider;
				}

				@Override
				public ExecutionContext getExecutionContext() {
					return context.getExecutionContext();
				}

				@Override
				public Cell getContextCell() {
					return context.getContextCell();
				}
			};
		}
		else {
			return context;
		}
	}

}
