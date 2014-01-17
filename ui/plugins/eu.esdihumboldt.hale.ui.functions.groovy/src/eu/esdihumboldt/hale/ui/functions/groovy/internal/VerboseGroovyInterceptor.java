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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.codehaus.groovy.runtime.GStringImpl;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import groovy.lang.Closure;
import groovy.lang.Script;

/**
 * {@link GroovyInterceptor} with lots of output.
 * 
 * @author Kai Schwierczek
 */
public class VerboseGroovyInterceptor extends GroovyInterceptor {

	private static final Set<Class<?>> allowedClasses = new HashSet<>();

	private static final Set<String> disallowedScriptMethods = new HashSet<>();
	private static final Set<String> disallowedScriptWriteProperties = new HashSet<>();

	private static final Set<String> disallowedClosureMethods = new HashSet<>();
	private static final Set<String> disallowedClosureWriteProperties = new HashSet<>();

	private static final Set<String> disallowedMethods = new HashSet<>();

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
		allowedClasses.add(OInstance.class);

		// Instance manipulation
		allowedClasses.add(InstanceBuilder.class);
		allowedClasses.add(InstanceAccessor.class);

		// Groovy Collections
		allowedClasses.add(LinkedHashMap.class);
		allowedClasses.add(ArrayList.class);

		disallowedMethods.add("invokeMethod");
		disallowedMethods.add("setMetaClass");
		disallowedMethods.add("setProperty");

		disallowedScriptMethods.add("run");
		disallowedScriptMethods.add("evaluate");
		disallowedScriptMethods.add("setBinding");
		disallowedScriptWriteProperties.add("binding");

		disallowedClosureMethods.add("setDelegate");
		disallowedClosureMethods.add("setResolveStrategy");
		disallowedClosureMethods.add("setDirective");
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
		if (allowedClasses.contains(receiver) || isScriptClass(receiver))
			return super.onNewInstance(invoker, receiver, args);
		else
			throw new TransformationException("using class " + receiver + " is not allowed!");
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
		checkMethodCall(receiver, method);
		return super.onMethodCall(invoker, receiver, method, args);
	}

	private void checkMethodCall(Object receiver, String method) throws TransformationException {
		if (receiver instanceof Closure) {
			// Closure method names were tested before.
			Closure<?> closure = (Closure<?>) receiver;
			Object owner = closure.getOwner();
			Object delegate = closure.getDelegate();
			int rs = closure.getResolveStrategy();
			// Check owner if necessary.
			if (rs == Closure.OWNER_FIRST || rs == Closure.DELEGATE_FIRST
					|| rs == Closure.OWNER_ONLY)
				checkMethodCall(owner, method);
			// Check delegate if necessary.
			if (rs == Closure.OWNER_FIRST || rs == Closure.DELEGATE_FIRST
					|| rs == Closure.DELEGATE_ONLY)
				if (delegate != null && delegate != closure)
					checkMethodCall(delegate, method);
			// Finally, check thisObject.
			// TODO needed?
			if (closure.getThisObject() != null)
				checkMethodCall(closure.getThisObject(), method);
			return;
		}
		else if (allowedClasses.contains(receiver.getClass()))
			return;
		else if (isScriptClass(receiver.getClass()) && !disallowedScriptMethods.contains(method))
			return;
		throw new TransformationException("Possible access of method " + method + " on class "
				+ receiver.getClass().getSimpleName()
				+ " is not allowed in Groovy transformations!");
	}

	private boolean isScriptClass(Class<?> receiver) {
		while (receiver.getEnclosingClass() != null)
			receiver = receiver.getEnclosingClass();
		return receiver == Script.class;
	}

	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
		checkPropertyAccess(receiver, property, false);
		return super.onGetProperty(invoker, receiver, property);
	}

	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value)
			throws Throwable {
		if (receiver instanceof Closure && disallowedClosureWriteProperties.contains(property))
			throw new TransformationException("setting the closure property " + property
					+ " is not allowed in Groovy transformations!");
		checkPropertyAccess(receiver, property, true);
		return super.onSetProperty(invoker, receiver, property, value);
	}

	private void checkPropertyAccess(Object receiver, String property, boolean set)
			throws TransformationException {
		if (receiver instanceof Closure) {
			// Closure properties were tested before.
			Closure<?> closure = (Closure<?>) receiver;
			Object owner = closure.getOwner();
			Object delegate = closure.getDelegate();
			int rs = closure.getResolveStrategy();
			// Check owner if necessary.
			if (rs == Closure.OWNER_FIRST || rs == Closure.DELEGATE_FIRST
					|| rs == Closure.OWNER_ONLY)
				checkPropertyAccess(owner, property, set);
			// Check delegate if necessary.
			if (rs == Closure.OWNER_FIRST || rs == Closure.DELEGATE_FIRST
					|| rs == Closure.DELEGATE_ONLY)
				if (delegate != null && delegate != closure)
					checkPropertyAccess(delegate, property, set);
			// Finally, check thisObject.
			// TODO needed?
			if (closure.getThisObject() != null)
				checkPropertyAccess(closure.getThisObject(), property, set);
			return;
		}
		else if (allowedClasses.contains(receiver.getClass()))
			return;
		else if (isScriptClass(receiver.getClass()) && set
				&& !disallowedScriptWriteProperties.contains(property))
			return;
		throw new TransformationException("Possible " + (set ? "write " : "")
				+ "access of property " + property + " on class "
				+ receiver.getClass().getSimpleName()
				+ " is not allowed in Groovy transformations!");
	}

	@Override
	public Object onGetAttribute(Invoker invoker, Object receiver, String attribute)
			throws Throwable {
		// forwarding to property
		return onGetProperty(invoker, receiver, attribute);
	}

	@Override
	public Object onSetAttribute(Invoker invoker, Object receiver, String attribute, Object value)
			throws Throwable {
		// forwarding to property
		return onSetProperty(invoker, receiver, attribute, value);
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

}
