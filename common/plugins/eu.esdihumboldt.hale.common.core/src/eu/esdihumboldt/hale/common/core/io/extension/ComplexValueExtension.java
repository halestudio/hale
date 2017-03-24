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

package eu.esdihumboldt.hale.common.core.io.extension;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ComplexValueJson;
import eu.esdihumboldt.hale.common.core.io.ComplexValueType;

/**
 * Extension for complex parameter types.
 * 
 * @author Simon Templer
 */
public class ComplexValueExtension extends IdentifiableExtension<ComplexValueDefinition> {

	private static final ALogger log = ALoggerFactory.getLogger(ComplexValueExtension.class);

	/**
	 * The extension identifier.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.io.complexvalue";

	private static ComplexValueExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension singleton
	 */
	public static ComplexValueExtension getInstance() {
		synchronized (ComplexValueExtension.class) {
			if (instance == null)
				instance = new ComplexValueExtension();
		}
		return instance;
	}

	private final Map<QName, ComplexValueDefinition> definitions = new HashMap<QName, ComplexValueDefinition>();

	private final Map<Class<?>, ComplexValueDefinition> definitionsPerType = new HashMap<Class<?>, ComplexValueDefinition>();

	/**
	 * Default constructor
	 */
	protected ComplexValueExtension() {
		super(EXTENSION_ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ComplexValueDefinition create(String id, IConfigurationElement conf) {
		if (!conf.getName().equals("complexValue")) {
			return null;
		}

		try {
			String localPart = conf.getAttribute("name");
			String ns = conf.getAttribute("namespace");
			QName name;
			if (ns == null || ns.isEmpty()) {
				name = new QName(localPart);
			}
			else {
				name = new QName(ns, localPart);
			}

			Class<ComplexValueJson<?, ?>> converterClass = null;
			ComplexValueJsonDescriptor cvs = ComplexValueJsonExtension.getInstance().get(id);
			if (cvs != null) {
				converterClass = cvs.getConverterClass();
			}

			ComplexValueDefinition cvd = new ComplexValueDefinition(id, name,
					(Class<ComplexValueType<?, ?>>) ExtensionUtil.loadClass(conf, "descriptor"),
					converterClass, (Class<?>) ExtensionUtil.loadClass(conf, "type"));
			synchronized (definitions) {
				definitions.put(name, cvd);
			}
			synchronized (definitionsPerType) {
				definitionsPerType.put(cvd.getValueType(), cvd);
			}
			return cvd;
		} catch (Exception e) {
			log.error("Could not load descriptor for complex parameter type with ID " + id, e);
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * Get the complex value definition associated to the given element name.
	 * 
	 * @param name the element name
	 * @return the complex value definition or <code>null</code> if none is
	 *         registered for the element name
	 */
	public ComplexValueDefinition getDefinition(QName name) {
		synchronized (definitions) {
			ComplexValueDefinition cvd = definitions.get(name);
			if (cvd != null)
				return cvd;
		}

		for (ComplexValueDefinition def : getElements()) {
			if (name.equals(def.getElementName())) {
				return def;
			}
		}

		return null;
	}

	/**
	 * Get the complex value definition associated to the object type.
	 * 
	 * @param valueType the complex value type
	 * @return the complex value definition or <code>null</code> if none is
	 *         registered for the type or a super type
	 */
	public ComplexValueDefinition getDefinition(Class<?> valueType) {
		synchronized (definitionsPerType) {
			ComplexValueDefinition cvd = definitionsPerType.get(valueType);
			if (cvd != null)
				return cvd;
		}

		// look for eventually not yet loaded definitions with the type
		for (ComplexValueDefinition def : getElements()) {
			if (valueType.equals(def.getValueType())) {
				return def;
			}
		}

		// look for any definition for a super type
		for (ComplexValueDefinition def : getElements()) {
			if (def.getValueType().isAssignableFrom(valueType)) {
				return def;
			}
		}

		return null;
	}

	@Override
	public ComplexValueDefinition get(String id) {
		ComplexValueDefinition result = super.get(id);
		if (result == null) {
			// try to lookup alias
			ComplexValueAlias alias = ComplexValueAliasExtension.INSTANCE.get(id);
			if (alias != null) {
				result = super.get(alias.getRef());
			}
		}
		return result;
	}

}
