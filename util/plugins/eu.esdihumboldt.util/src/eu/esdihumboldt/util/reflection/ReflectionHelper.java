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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.util.reflection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

/**
 * Provides several utility methods which use Java Reflections to access hidden
 * types, fields and methods.
 * 
 * @author Michel Kraemer
 */
public class ReflectionHelper {

	/**
	 * The package resolver used to retrieve URLs to packages
	 */
	private static PackageResolver _packageResolver = new DefaultPackageResolver();

	/**
	 * Sets the package resolver used to retrieve URLs to packages
	 * 
	 * @see #getFilesFromPackage(String)
	 * @param res the package resolver
	 */
	public static synchronized void setPackageResolver(PackageResolver res) {
		_packageResolver = res;
	}

	/**
	 * Returns a public setter method for a given property
	 * 
	 * @param c the class which would contain the setter method
	 * @param property the property
	 * @param propertyType the property's type
	 * @return the public setter or null if there is no setter for this property
	 */
	public static Method findSetter(Class<?> c, String property, Class<?> propertyType) {
		char[] propertyNameArr = property.toCharArray();
		propertyNameArr[0] = Character.toUpperCase(propertyNameArr[0]);
		String setterName = "set" + new String(propertyNameArr); //$NON-NLS-1$

		Method result = null;
		Method[] methods = c.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(setterName) && m.getParameterTypes().length == 1
					&& (propertyType == null
							|| m.getParameterTypes()[0].isAssignableFrom(propertyType))) {
				result = m;
				break;
			}
		}

		return result;
	}

	/**
	 * Returns a setter method for a given property, no matter if the method is
	 * visible or hidden or if it is declared in the given class or in a
	 * superclass or implemented interface.
	 * 
	 * @param c the class which would contain the setter method
	 * @param property the property
	 * @param propertyType the property's type
	 * @return the setter or null if there is no setter for this property
	 */
	public static Method findDeepSetter(Class<?> c, String property, Class<?> propertyType) {
		if (c == Object.class || c == null) {
			return null;
		}

		char[] propertyNameArr = property.toCharArray();
		propertyNameArr[0] = Character.toUpperCase(propertyNameArr[0]);
		String setterName = "set" + new String(propertyNameArr); //$NON-NLS-1$

		Method result = null;
		// search visible and hidden methods in this class
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals(setterName) && m.getParameterTypes().length == 1
					&& (propertyType == null
							|| m.getParameterTypes()[0].isAssignableFrom(propertyType))) {
				result = m;
				break;
			}
		}

		// search superclass and interfaces
		if (result == null) {
			result = findDeepSetter(c.getSuperclass(), property, propertyType);
			if (result == null) {
				Class<?>[] interfaces = c.getInterfaces();
				for (Class<?> inter : interfaces) {
					result = findDeepSetter(inter, property, propertyType);
					if (result != null) {
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Returns a public getter method for a given property
	 * 
	 * @param c the class which would contain the getter method
	 * @param property the property
	 * @param propertyType the property's type
	 * @return the public getter or null if there is no getter for this property
	 */
	public static Method findGetter(Class<?> c, String property, Class<?> propertyType) {
		char[] propertyNameArr = property.toCharArray();
		propertyNameArr[0] = Character.toUpperCase(propertyNameArr[0]);
		String getterName = "get" + new String(propertyNameArr); //$NON-NLS-1$

		Method result = null;
		Method[] methods = c.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(getterName) && m.getParameterTypes().length == 0
					&& (propertyType == null || propertyType.isAssignableFrom(m.getReturnType()))) {
				result = m;
				break;
			}
		}

		return result;
	}

	/**
	 * Returns a getter method for a given property, no matter if the method is
	 * visible or hidden or if it is declared in the given class or in a
	 * superclass or implemented interface.
	 * 
	 * @param c the class which would contain the getter method
	 * @param property the property
	 * @param propertyType the property's type (can be null if the type does not
	 *            matter)
	 * @return the getter or null if there is no getter for this property
	 */
	public static Method findDeepGetter(Class<?> c, String property, Class<?> propertyType) {
		if (c == Object.class || c == null) {
			return null;
		}

		char[] propertyNameArr = property.toCharArray();
		propertyNameArr[0] = Character.toUpperCase(propertyNameArr[0]);
		String getterName = "get" + new String(propertyNameArr); //$NON-NLS-1$

		Method result = null;
		// search visible and hidden methods in this class
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals(getterName) && m.getParameterTypes().length == 0
					&& (propertyType == null || propertyType.isAssignableFrom(m.getReturnType()))) {
				result = m;
				break;
			}
		}

		// search superclass and interfaces
		if (result == null) {
			result = findDeepGetter(c.getSuperclass(), property, propertyType);
			if (result == null) {
				Class<?>[] interfaces = c.getInterfaces();
				for (Class<?> inter : interfaces) {
					result = findDeepGetter(inter, property, propertyType);
					if (result != null) {
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Returns a public field for a given property
	 * 
	 * @param c the class which would contain the field
	 * @param property the property
	 * @param propertyType the property's type
	 * @return the field or null if there is no field for this property
	 */
	public static Field findField(Class<?> c, String property, Class<?> propertyType) {
		Field result = null;
		Field[] fields = c.getFields();
		for (Field f : fields) {
			String fn = f.getName();
			if (fn.charAt(0) == '_') {
				// handle code style
				fn = fn.substring(1);
			}
			if (fn.equals(property)
					&& (propertyType == null || f.getType().isAssignableFrom(propertyType))) {
				result = f;
				break;
			}
		}

		return result;
	}

	/**
	 * Returns a field for a given property, no matter if the field is visible
	 * or hidden or if it is declared in the given class or in a superclass.
	 * 
	 * @param c the class which would contain the field
	 * @param property the property
	 * @param propertyType the property's type
	 * @return the field or null if there is no field for this property
	 */
	public static Field findDeepField(Class<?> c, String property, Class<?> propertyType) {
		if (c == Object.class || c == null) {
			return null;
		}

		Field result = null;
		// search visible and hidden fields in this class
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			String fn = f.getName();
			if (fn.charAt(0) == '_') {
				// handle code style
				fn = fn.substring(1);
			}
			if (fn.equals(property)
					&& (propertyType == null || f.getType().isAssignableFrom(propertyType))) {
				result = f;
				break;
			}
		}

		// search superclass
		if (result == null) {
			result = findDeepField(c.getSuperclass(), property, propertyType);
		}

		return result;
	}

	/**
	 * Invokes a setter method on a given bean
	 * 
	 * @param bean the object to invoke the getter on
	 * @param setter the setter method
	 * @param value the value to set
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws IllegalAccessException if the setter is not accessible
	 * @throws InvocationTargetException if the setter throws an exception
	 */
	private static void invokeSetter(Object bean, Method setter, Object value)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		boolean accessible = setter.isAccessible();
		setter.setAccessible(true);
		try {
			setter.invoke(bean, value);
		} finally {
			setter.setAccessible(accessible);
		}
	}

	/**
	 * Invokes a public setter for a property
	 * 
	 * @param bean the object to invoke the public setter on
	 * @param propertyName the name of the property that shall be updated
	 * @param value the value passed to the setter
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws IllegalAccessException if the setter is not accessible
	 * @throws InvocationTargetException if the setter throws an exception
	 * @throws NoSuchMethodException if there is no setter for this property
	 */
	public static void setProperty(Object bean, String propertyName, Object value)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Method setter = findSetter(bean.getClass(), propertyName, value.getClass());
		if (setter == null) {
			throw new NoSuchMethodException("There is " + //$NON-NLS-1$
					"no setter for property " + propertyName); //$NON-NLS-1$
		}

		invokeSetter(bean, setter, value);
	}

	/**
	 * Invokes a setter for a property, no matter if the setter is visible or
	 * hidden or if it is declared in the given class or in a superclass or
	 * implemented interface.
	 * 
	 * @param bean the object to invoke the public setter on
	 * @param propertyName the name of the property that shall be updated
	 * @param value the value passed to the setter
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws IllegalAccessException if the setter is not accessible
	 * @throws InvocationTargetException if the setter throws an exception
	 * @throws NoSuchMethodException if there is no setter for this property
	 */
	public static void setDeepProperty(Object bean, String propertyName, Object value)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Method setter = findDeepSetter(bean.getClass(), propertyName, value.getClass());
		if (setter == null) {
			throw new NoSuchMethodException("There is " + //$NON-NLS-1$
					"no setter for property " + propertyName); //$NON-NLS-1$
		}

		invokeSetter(bean, setter, value);
	}

	/**
	 * Invokes a getter method on a given bean
	 * 
	 * @param <T> the result type
	 * @param bean the object to invoke the getter on
	 * @param getter the getter method
	 * @return the property's value
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws IllegalAccessException if the getter is not accessible
	 * @throws InvocationTargetException if the getter throws an exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> T invokeGetter(Object bean, Method getter)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		boolean accessible = getter.isAccessible();
		getter.setAccessible(true);
		T result = null;
		try {
			result = (T) getter.invoke(bean);
		} finally {
			getter.setAccessible(accessible);
		}

		return result;
	}

	/**
	 * Invokes a public getter for a property
	 * 
	 * @param <T> the result type
	 * @param bean the object to invoke the public getter on
	 * @param propertyName the name of the property to retrieve
	 * @return the property's value
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws IllegalAccessException if the getter is not accessible
	 * @throws InvocationTargetException if the getter throws an exception
	 * @throws NoSuchMethodException if there is no getter for this property
	 */
	public static <T> T getProperty(Object bean, String propertyName)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Method getter = findGetter(bean.getClass(), propertyName, Object.class);
		if (getter == null) {
			throw new NoSuchMethodException("There is " + //$NON-NLS-1$
					"no getter for property " + propertyName); //$NON-NLS-1$
		}

		return ReflectionHelper.<T> invokeGetter(bean, getter);
	}

	/**
	 * Invokes a getter for a property, no matter if the getter is visible or
	 * hidden or if it is declared in the given class or in a superclass or
	 * implemented interface.
	 * 
	 * @param <T> the result type
	 * @param bean the object to invoke the getter on
	 * @param propertyName the name of the property to retrieve
	 * @return the property's value
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws IllegalAccessException if the setter is not accessible
	 * @throws InvocationTargetException if the setter throws an exception
	 * @throws NoSuchMethodException if there is no setter for this property
	 */
	public static <T> T getDeepProperty(Object bean, String propertyName)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Method getter = findDeepGetter(bean.getClass(), propertyName, null);
		if (getter == null) {
			throw new NoSuchMethodException("There is " + //$NON-NLS-1$
					"no getter for property " + propertyName); //$NON-NLS-1$
		}

		return ReflectionHelper.<T> invokeGetter(bean, getter);
	}

	/**
	 * Invokes a getter for a property, or gets the matching field, no matter
	 * what.
	 * 
	 * @param bean the object to invoke the getter on
	 * @param propertyName the name of the property to retrieve
	 * @param valueClass the class of the property or field
	 * @return the property's value
	 * @throws IllegalArgumentException if the argument's type is invalid
	 * @throws InvocationTargetException if the getter throws an exception
	 * @throws IllegalStateException is the field could be found or accessed
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDeepPropertyOrField(Object bean, String propertyName,
			Class<T> valueClass) throws IllegalArgumentException, InvocationTargetException {
		try {
			return ReflectionHelper.getDeepProperty(bean, propertyName);
		} catch (NoSuchMethodException e) {/* ignore */
		} catch (IllegalAccessException e) {/* ignore */
		}

		// there is no getter for the property. try to get the field directly
		Field f = findDeepField(bean.getClass(), propertyName, valueClass);
		if (f == null) {
			throw new IllegalStateException("Could not find " + "field for property " + propertyName
					+ " in class " + bean.getClass().getCanonicalName());
		}

		boolean access = f.isAccessible();
		f.setAccessible(true);
		try {
			return (T) f.get(bean);
		} catch (Exception e) {
			throw new IllegalStateException("Could not get " + "field for property " + propertyName
					+ "in class " + bean.getClass().getCanonicalName(), e);
		} finally {
			f.setAccessible(access);
		}
	}

	/**
	 * @return the URL to the JAR file this class is in or null
	 * @throws MalformedURLException if the URL to the jar file could not be
	 *             created
	 */
	public static URL getCurrentJarURL() throws MalformedURLException {
		String name = ReflectionHelper.class.getCanonicalName();
		name = name.replaceAll("\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name + ".class"; //$NON-NLS-1$
		URL url = ReflectionHelper.class.getClassLoader().getResource(name);
		String str = url.toString();
		int to = str.indexOf("!/"); //$NON-NLS-1$
		if (to == -1) {
			url = ClassLoader.getSystemResource(name);
			if (url != null) {
				str = url.toString();
				to = str.indexOf("!/"); //$NON-NLS-1$
			}
			else {
				return null;
			}
		}
		return new URL(str.substring(0, to + 2));
	}

	/**
	 * Returns an array of all files contained by a given package
	 * 
	 * @param pkg the package (e.g. "de.igd.fhg.CityServer3D")
	 * @return an array of files
	 * @throws IOException if the package could not be found
	 */
	public static synchronized File[] getFilesFromPackage(String pkg) throws IOException {

		File[] files;
		JarFile jarFile = null;
		try {
			URL u = _packageResolver.resolve(pkg);
			if (u != null && !u.toString().startsWith("jar:")) { //$NON-NLS-1$
				// we got the package as an URL. Simply create a file
				// from this URL
				File dir;
				try {
					dir = new File(u.toURI());
				} catch (URISyntaxException e) {
					// if the URL contains spaces and they have not been
					// replaced by %20 then we'll have to use the following line
					dir = new File(u.getFile());
				}
				if (!dir.isDirectory()) {
					// try another method
					dir = new File(u.getFile());
				}
				files = null;
				if (dir.isDirectory()) {
					files = dir.listFiles();
				}
			}
			else {
				// the package may be in a jar file
				// get the current jar file and search it
				if (u != null && u.toString().startsWith("jar:file:")) { //$NON-NLS-1$
					// first try using URL and File
					try {
						String p = u.toString().substring(4);
						p = p.substring(0, p.indexOf("!/")); //$NON-NLS-1$
						File file = new File(URI.create(p));
						p = file.getAbsolutePath();
						try {
							jarFile = new JarFile(p);
						} catch (ZipException e) {
							throw new IllegalArgumentException("No zip file: " + p, e); //$NON-NLS-1$
						}
					} catch (Throwable e1) {
						// second try directly using path
						String p = u.toString().substring(9);
						p = p.substring(0, p.indexOf("!/")); //$NON-NLS-1$
						try {
							jarFile = new JarFile(p);
						} catch (ZipException e2) {
							throw new IllegalArgumentException("No zip file: " + p, e2); //$NON-NLS-1$
						}
					}
				}
				else {
					u = getCurrentJarURL();

					// open jar file
					JarURLConnection juc = (JarURLConnection) u.openConnection();
					jarFile = juc.getJarFile();
				}

				// enumerate entries and add those that match the package path
				Enumeration<JarEntry> entries = jarFile.entries();
				ArrayList<String> file_names = new ArrayList<String>();
				String package_path = pkg.replaceAll("\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
				boolean slashed = false;
				if (package_path.charAt(0) == '/') {
					package_path = package_path.substring(1);
					slashed = true;
				}
				while (entries.hasMoreElements()) {
					JarEntry j = entries.nextElement();
					String entryName = j.getName();

					// Ensure the entry name is properly normalized and checked
					File targetFile = new File(package_path, entryName);
					String canonicalTargetPath = targetFile.getCanonicalPath();
					String canonicalBasePath = new File(package_path).getCanonicalPath();

					if (!canonicalTargetPath.startsWith(canonicalBasePath + File.separator)) {
						throw new IOException(
								"Entry is outside of the target directory: " + entryName);
					}

					if (entryName.matches("^" + package_path + ".+\\..+")) { //$NON-NLS-1$ //$NON-NLS-2$
						if (slashed) {
							file_names.add("/" + entryName); //$NON-NLS-1$
						}
						else {
							file_names.add(entryName);
						}
					}
				}

				// convert list to array
				files = new File[file_names.size()];
				Iterator<String> i = file_names.iterator();
				int n = 0;
				while (i.hasNext()) {
					files[n++] = new File(i.next());
				}
			}
		} catch (Throwable e) {
			throw new IOException("Could not find package: " + pkg, e); //$NON-NLS-1$
		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}

		if (files != null && files.length == 0)
			return null; // let's not require paranoid callers

		return files;
	}

	/**
	 * Gets a list of all classes in the given package and all subpackages
	 * recursively.
	 * 
	 * @param pkg the package
	 * @param classLoader the class loader to use
	 * @return the list of classes
	 * @throws IOException if a subpackage or a class could not be loaded
	 */
	public static List<Class<?>> getClassesFromPackage(String pkg, ClassLoader classLoader)
			throws IOException {
		return getClassesFromPackage(pkg, classLoader, true);
	}

	/**
	 * Gets a list of all classes in the given package
	 * 
	 * @param pkg the package
	 * @param classLoader the class loader to use
	 * @param recursive true if all subpackages shall be traversed too
	 * @return the list of classes
	 * @throws IOException if a subpackage or a class could not be loaded
	 */
	public static List<Class<?>> getClassesFromPackage(String pkg, ClassLoader classLoader,
			boolean recursive) throws IOException {
		List<Class<?>> result = new ArrayList<Class<?>>();
		getClassesFromPackage(pkg, result, classLoader, recursive);
		return result;
	}

	/**
	 * Gets a list of all subpackages in the given package
	 * 
	 * @param pkg the package
	 * @return the list of classes
	 * @throws IOException if a subpackage or a class could not be loaded
	 */
	public static List<String> getSubPackagesFromPackage(String pkg) throws IOException {
		return getSubPackagesFromPackage(pkg, true);
	}

	/**
	 * Gets a list of all subpackages in the given package
	 * 
	 * @param pkg the package
	 * @param recursive true if all subpackages shall be traversed too
	 * @return the list of classes
	 * @throws IOException if a subpackage or a class could not be loaded
	 */
	public static List<String> getSubPackagesFromPackage(String pkg, boolean recursive)
			throws IOException {
		List<String> result = new ArrayList<String>();
		getSubPackagesFromPackage(pkg, result, recursive);
		return result;
	}

	private static void getSubPackagesFromPackage(String pkg, List<String> l, boolean recursive)
			throws IOException {
		File[] files = getFilesFromPackage(pkg);
		for (File f : files) {
			String name = f.getName();
			if (f.isDirectory() && !name.startsWith(".")) { //$NON-NLS-1$
				l.add(pkg + "." + name); //$NON-NLS-1$
				if (recursive) {
					getSubPackagesFromPackage(pkg + "." + name, l, true); //$NON-NLS-1$
				}
			}
		}
	}

	private static void getClassesFromPackage(String pkg, List<Class<?>> l, ClassLoader classLoader,
			boolean recursive) throws IOException {
		File[] files = getFilesFromPackage(pkg);
		for (File f : files) {
			String name = f.getName();
			if (f.isDirectory() && recursive) {
				if (!name.startsWith(".")) { //$NON-NLS-1$
					getClassesFromPackage(pkg + "." + name, l, classLoader, true); //$NON-NLS-1$
				}
			}
			else if (name.toLowerCase().endsWith(".class")) { //$NON-NLS-1$
				// the following lines make sure we also handle classes
				// in subpackages. These subpackages may be returned by
				// ApplicationContext.getFilesFromPackage() when we are
				// in a jar file
				String classPath = f.toURI().toString().replace('/', '.').replace('\\', '.');
				String className = classPath.substring(classPath.lastIndexOf(pkg),
						classPath.lastIndexOf('.'));
				Class<?> c;
				try {
					c = Class.forName(className, true, classLoader);
				} catch (ClassNotFoundException e) {
					throw new IOException("Could not load class: " + //$NON-NLS-1$
							e.getMessage());
				}

				l.add(c);
			}
		}
	}

	/**
	 * <p>
	 * Find the most specialised class from group compatible with clazz. A
	 * direct superclass match is searched and returned if found.
	 * </p>
	 * <p>
	 * If not and checkAssignability is true, the most derived assignable class
	 * is being searched.
	 * </p>
	 * <p>
	 * See The Java Language Specification, sections 5.1.1 and 5.1.4 , for
	 * details.
	 * </p>
	 * 
	 * @param clazz a class
	 * @param group a collection of classes to match against
	 * @param checkAssignability whether to use assignability when no direct
	 *            match is found
	 * @return null or the most specialised match from group
	 */
	public static Class<?> findMostSpecificMatch(Class<?> clazz, Collection<Class<?>> group,
			boolean checkAssignability) {
		if (clazz == null || group == null)
			throw new IllegalArgumentException(""); //$NON-NLS-1$

		// scale up the type hierarchy until we have found a matching class
		for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
			if (group.contains(c))
				return c;
		}

		if (checkAssignability) {
			// in lieu of a direct match, check assignability (likely clazz is
			// an interface)
			Class<?> result = null;
			for (Class<?> c : group) {
				if (clazz.isAssignableFrom(c)) {
					// result null or less specialized -> overwrite
					if (result == null || result.isAssignableFrom(c))
						result = c;
				}
			}
			return result;
		}
		else {
			return null;
		}
	}

	/**
	 * Performs a shallow copy of all fields defined by the class of src and all
	 * superclasses.
	 * 
	 * @param <T> the type of the source and destination object
	 * @param src the source object
	 * @param dst the destination object
	 * @throws IllegalArgumentException if a field is unaccessible
	 * @throws IllegalAccessException if a field is not accessible
	 */
	public static <T> void shallowEnforceDeepProperties(T src, T dst)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> cls = src.getClass();
		shallowEnforceDeepProperties(cls, src, dst);
	}

	private static <T> void shallowEnforceDeepProperties(Class<?> cls, T src, T dst)
			throws IllegalArgumentException, IllegalAccessException {
		if (cls == Object.class || cls == null) {
			return;
		}

		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {
				continue;
			}

			boolean accessible = f.isAccessible();
			f.setAccessible(true);
			try {
				Object val = f.get(src);
				f.set(dst, val);
			} finally {
				f.setAccessible(accessible);
			}
		}

		shallowEnforceDeepProperties(cls.getSuperclass(), src, dst);
	}
}
