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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.common.base.Splitter;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionSpecification;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;

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
				if (method.getName().startsWith("_") && !Modifier.isAbstract(modifiers)
						&& !method.getName().endsWith(SPEC_END)) {
					// a candidate -> check parameters
					Class<?>[] params = method.getParameterTypes();
					if (params != null && params.length == 1) {
						// has a single parameter
//						final boolean paramIsMap = Map.class.isAssignableFrom(params[0]);
						final boolean isStatic = Modifier.isStatic(modifiers);
						final Method callMethod = method;

						// Get the specification from field
						String fieldOrMethodName = callMethod.getName() + SPEC_END;

						Object fieldV = null;
						try {
							Field field = helperClass.getField(fieldOrMethodName);
							int fieldModifiers = field.getModifiers();
							if (Modifier.isStatic(fieldModifiers)
									&& Modifier.isFinal(fieldModifiers)) {
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
							meth = helperClass.getMethod(fieldOrMethodName,
									new Class[] { String.class });
							int specModifier = meth.getModifiers();
							isSpecStatic = Modifier.isStatic(specModifier);

						} catch (Exception e) {
							// do nothing
						}
						final Method specMethod = meth;
						final boolean isSpecMethodStatic = isSpecStatic;

						HelperFunction<Object> function = new HelperFunction<Object>() {

							@Override
							public Object call(Object arg) throws Exception {
								if (isStatic) {
									return callMethod.invoke(null, arg);
								}
								else {
									Object helper = helperClass.newInstance();
									return callMethod.invoke(helper, arg);
								}
							}

							@Override
							public HelperFunctionSpecification getSpec(String name)
									throws Exception {
								if (fieldValue != null
										&& fieldValue instanceof HelperFunctionSpecification) {

									return ((HelperFunctionSpecification) fieldValue);
								}
								else if (specMethod != null) {
									if (isSpecMethodStatic) {
										return (HelperFunctionSpecification) specMethod.invoke(
												null, name);
									}
									else {
										Object helper = helperClass.newInstance();
										return (HelperFunctionSpecification) specMethod.invoke(
												helper, name);
									}

								}
								return null;
							}
						};

						// method name
						String name = method.getName().substring(1);

						functions.add(new HelperFunctionWrapper(function, name));
					}
				}
			}
			return functions;
		}
	}

	@Override
	public Iterable<HelperFunctionOrCategory> getChildren(Category cat) {
		init();

		synchronized (children) {
			Map<String, HelperFunctionOrCategory> catMap = children.get(cat);
			if (catMap == null) {
				return Collections.emptyList();
			}
			else {
				return Collections.unmodifiableCollection(catMap.values());
			}
		}
	}

	@Override
	public HelperFunctionOrCategory get(Category cat, String name) {
		init();

		synchronized (children) {
			Map<String, HelperFunctionOrCategory> catMap = children.get(cat);
			if (catMap == null) {
				return null;
			}
			else {
				return catMap.get(name);
			}
		}
	}

}
