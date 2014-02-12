/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;
import groovy.lang.Range;
import groovy.lang.Script;

/**
 * {@link GroovyInterceptor} with lots of output.
 * 
 * @author Kai Schwierczek
 */
public class RestrictiveGroovyInterceptor extends GroovyInterceptor {

	/**
	 * Classes, which may be initialized, and all their methods may be used.
	 */
	private static final Set<Class<?>> allowedClasses = new HashSet<>();
	/**
	 * Classes, which may be initialized, and all their methods, and method
	 * missing/getProperty() may be used.
	 */
	private static final Set<Class<?>> allAllowedClasses = new HashSet<>();

	/**
	 * Generally disallowed methods.
	 */
	private static final Set<String> disallowedMethods = new HashSet<>();
	/**
	 * Generally disallowed properties (write access).
	 */
	private static final Set<String> disallowedWriteProperties = new HashSet<>();

	/**
	 * Disallowed Script methods.
	 */
	private static final Set<String> disallowedScriptMethods = new HashSet<>();
	/**
	 * Disallowed Script properties (write access).
	 */
	private static final Set<String> disallowedScriptWriteProperties = new HashSet<>();

	/**
	 * Disallowed Closure methods.
	 */
	private static final Set<String> disallowedClosureMethods = new HashSet<>();
	/**
	 * Disallowed Closure properties (write access).
	 */
	private static final Set<String> disallowedClosureWriteProperties = new HashSet<>();

	static {
		// standard classes
		allowedClasses.add(String.class);
		allowedClasses.add(Byte.class);
		allowedClasses.add(Short.class);
		allowedClasses.add(Integer.class);
		allowedClasses.add(Long.class);
		allowedClasses.add(Float.class);
		allowedClasses.add(Double.class);
		allowedClasses.add(BigInteger.class);
		allowedClasses.add(BigDecimal.class);
		allowedClasses.add(Math.class);
		allowedClasses.add(Date.class);
		allowedClasses.add(GStringImpl.class);

		// Instance classes (what about value bindings?)
		// XXX disallow set*?
		allowedClasses.add(FamilyInstanceImpl.class);
		allowedClasses.add(DefaultInstance.class);
		allowedClasses.add(OInstance.class);

		// Instance manipulation
		allowedClasses.add(InstanceBuilder.class);
		allowedClasses.add(InstanceAccessor.class);
		allAllowedClasses.add(InstanceBuilder.class);
		allAllowedClasses.add(InstanceAccessor.class);

		// Groovy Collections
		allowedClasses.add(LinkedHashMap.class);
		allowedClasses.add(ArrayList.class);
		allowedClasses.add(Range.class);

		// Some more collections
		allowedClasses.add(HashMap.class);
		allowedClasses.add(HashSet.class);

		// Binding classes
		allowedClasses.add(Timestamp.class);

		// general disallow access to specific Groovy methods
		disallowedMethods.add("invokeMethod");
		disallowedMethods.add("setMetaClass");
		disallowedMethods.add("setProperty");
		disallowedWriteProperties.add("metaClass");

		// forbid self-execution of script and overwriting of binding
		disallowedScriptMethods.add("run");
		disallowedScriptMethods.add("evaluate");
		disallowedScriptMethods.add("setBinding");
		disallowedScriptWriteProperties.add("binding");

		// forbid explicit setting of delegate, resolve strategy and directive
		disallowedClosureMethods.add("setDelegate");
		disallowedClosureMethods.add("setResolveStrategy");
		disallowedClosureMethods.add("setDirective");
		// and rehydrate as that results basically in setting the owner,
		// delegate and thisObject
		disallowedClosureMethods.add("rehydrate");
		disallowedClosureWriteProperties.add("delegate");
		disallowedClosureWriteProperties.add("resolveStrategy");
		disallowedClosureWriteProperties.add("directive");
	}

	@Override
	public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args)
			throws Throwable {
		if (allowedClasses.contains(receiver) || isScriptClass(receiver))
			return super.onStaticCall(invoker, receiver, method, args);
		else
			throw new TransformationException("using class " + receiver + " is not allowed!");
	}

	@Override
	public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
		// classes defined in the script would be okay, sadly it is not possible
		// to identify those?
		if (allowedClasses.contains(receiver) || isScriptClass(receiver))
			return super.onNewInstance(invoker, receiver, args);
		else
			throw new TransformationException("using class " + receiver.getSimpleName()
					+ " is not allowed!");
	}

	@Override
	public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args)
			throws Throwable {
		if (disallowedMethods.contains(method))
			throw new TransformationException("using methods named " + method
					+ " is not allowed in Groovy transformations!");
		else if (receiver instanceof Closure && disallowedClosureMethods.contains(method))
			throw new TransformationException("using the closure method " + method
					+ " is not allowed in Groovy transformations!");
		// Return value doesn't matter!
		// true -> allowed delegation found
		// false -> no disallowed delegation found
		checkMethodCall(receiver, method);
		return super.onMethodCall(invoker, receiver, method, args);
	}

	private boolean checkMethodCall(Object receiver, String method) throws TransformationException {
		if (receiver instanceof Closure) {
			// Closure method names were tested before.
			Closure<?> closure = (Closure<?>) receiver;
			Object owner = closure.getOwner();
			Object delegate = closure.getDelegate();
			int rs = closure.getResolveStrategy();
			// Check owner first.
			if (rs == Closure.OWNER_FIRST || rs == Closure.OWNER_ONLY)
				if (checkMethodCall(owner, method))
					return true;
			// Check delegate first/second.
			if (rs == Closure.OWNER_FIRST || rs == Closure.DELEGATE_FIRST
					|| rs == Closure.DELEGATE_ONLY)
				if (delegate != null && delegate != closure)
					if (checkMethodCall(delegate, method))
						return true;
			// Check owner second.
			if (rs == Closure.DELEGATE_FIRST)
				if (checkMethodCall(owner, method))
					return true;

			// Cannot be 100% sure whether the call will be handled by
			// delegation to this closure.
			return false;
		}
		else if (allowedClasses.contains(receiver.getClass()))
			return allAllowedClasses.contains(receiver.getClass())
					|| !InvokerHelper.getMetaClass(receiver).respondsTo(receiver, method).isEmpty();
		else if (isScriptClass(receiver.getClass()) && !disallowedScriptMethods.contains(method))
			return !InvokerHelper.getMetaClass(receiver).respondsTo(receiver, method).isEmpty();
		throw new TransformationException("Possible access of method " + method + " on class "
				+ receiver.getClass().getSimpleName()
				+ " is not allowed in Groovy transformations!");
	}

	private boolean isScriptClass(Class<?> receiver) {
		// while-doesn't really do anything, because Groovy extracts classes
		// defined in scripts as stand-alone classes.
//		while (receiver.getEnclosingClass() != null)
//			receiver = receiver.getEnclosingClass();
		return Script.class.isAssignableFrom(receiver);
	}

	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
		if (receiver instanceof Class<?> && allowedClasses.contains(receiver)
				&& !"class".equals(property))
			return super.onGetProperty(invoker, receiver, property);
		checkPropertyAccess(receiver, property, false);
		return super.onGetProperty(invoker, receiver, property);
	}

	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value)
			throws Throwable {
		if (disallowedWriteProperties.contains(property))
			throw new TransformationException("setting the property " + property
					+ " is not allowed in Groovy transformations!");
		if (receiver instanceof Closure && disallowedClosureWriteProperties.contains(property))
			throw new TransformationException("setting the closure property " + property
					+ " is not allowed in Groovy transformations!");
		checkPropertyAccess(receiver, property, true);
		return super.onSetProperty(invoker, receiver, property, value);
	}

	private boolean checkPropertyAccess(Object receiver, String property, boolean set)
			throws TransformationException {
		if (receiver instanceof Closure) {
			// Closure properties were tested before.
			Closure<?> closure = (Closure<?>) receiver;
			Object owner = closure.getOwner();
			Object delegate = closure.getDelegate();
			int rs = closure.getResolveStrategy();
			// Check owner first.
			if (rs == Closure.OWNER_FIRST || rs == Closure.OWNER_ONLY)
				if (checkPropertyAccess(owner, property, set))
					return true;
			// Check delegate first/second.
			if (rs == Closure.OWNER_FIRST || rs == Closure.DELEGATE_FIRST
					|| rs == Closure.DELEGATE_ONLY)
				if (delegate != null && delegate != closure)
					if (checkPropertyAccess(delegate, property, set))
						return true;
			// Check owner second.
			if (rs == Closure.DELEGATE_FIRST)
				if (checkPropertyAccess(owner, property, set))
					return true;
			// Cannot be 100% sure whether the property will be handled by
			// delegation to this closure.
			return false;
		}
		else if (allAllowedClasses.contains(receiver.getClass()))
			return true;
		else if (allowedClasses.contains(receiver.getClass()))
			return hasProperty(receiver, property);
		else if (isScriptClass(receiver.getClass())
				&& (!set || !disallowedScriptWriteProperties.contains(property)))
			return hasProperty(receiver, property);
		throw new TransformationException("Possible " + (set ? "write " : "")
				+ "access of property " + property + " on class "
				+ receiver.getClass().getSimpleName()
				+ " is not allowed in Groovy transformations!");
	}

	@Override
	public Object onGetAttribute(Invoker invoker, Object receiver, String attribute)
			throws Throwable {
		checkPropertyAccess(receiver, attribute, false);
		return super.onGetAttribute(invoker, receiver, attribute);
	}

	@Override
	public Object onSetAttribute(Invoker invoker, Object receiver, String attribute, Object value)
			throws Throwable {
		if (disallowedWriteProperties.contains(attribute))
			throw new TransformationException("setting the property " + attribute
					+ " is not allowed in Groovy transformations!");
		if (receiver instanceof Closure && disallowedClosureWriteProperties.contains(attribute))
			throw new TransformationException("setting the closure property " + attribute
					+ " is not allowed in Groovy transformations!");
		checkPropertyAccess(receiver, attribute, true);
		return super.onSetAttribute(invoker, receiver, attribute, value);
	}

	@Override
	public Object onGetArray(Invoker invoker, Object receiver, Object index) throws Throwable {
		// generally allow array access for now
		return super.onGetArray(invoker, receiver, index);
	}

	@Override
	public Object onSetArray(Invoker invoker, Object receiver, Object index, Object value)
			throws Throwable {
		// generally allow array access for now
		return super.onSetArray(invoker, receiver, index, value);
	}

	private static boolean hasProperty(Object object, String property) {
		if (InvokerHelper.getMetaClass(object).hasProperty(object, property) != null)
			return true;

		// The only way to be sure whether something is handled as a property in
		// Groovy is to actually get it and catch a MissingPropertyException.
		// But this actually accesses the property (-> side effects?)!
		// Here this is no problem, since we only disallow some write access...

		// The only allowed class with side effects should be InstanceAccessor,
		// which is in "allAllowedClasses" and thus shouldn't reach here

		try {
			InvokerHelper.getProperty(object, property);
			return true;
		} catch (MissingPropertyException e) {
			return false;
		}
	}
}
