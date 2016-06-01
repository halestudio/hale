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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import net.jcip.annotations.Immutable;

/**
 * {@link IConfigurationElement} based function base class
 * 
 * @param
 * 			<P>
 *            the parameter type
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractFunction<P extends AbstractParameter> implements Function {

	private final ALogger log = ALoggerFactory.getLogger(AbstractFunction.class);

	/**
	 * The configuration element
	 */
	protected final IConfigurationElement conf;

	private final Map<String, FunctionParameter> parameters;

	private boolean explanationInitialized = false;
	private CellExplanation explanation;

	/**
	 * Create a function definition based on the given configuration element
	 * 
	 * @param conf the configuration element
	 */
	protected AbstractFunction(IConfigurationElement conf) {
		super();
		this.conf = conf;

		this.parameters = createParameters(conf);
	}

	private static Map<String, FunctionParameter> createParameters(IConfigurationElement conf) {
		Map<String, FunctionParameter> parameters = new LinkedHashMap<String, FunctionParameter>();

		IConfigurationElement[] pars = conf.getChildren("functionParameter");
		if (pars != null) {
			for (IConfigurationElement par : pars) {
				FunctionParameter funPar = new FunctionParameter(par);
				parameters.put(funPar.getName(), funPar);
			}
		}

		return parameters;
	}

	/**
	 * @see Function#getExplanation()
	 */
	@Override
	public CellExplanation getExplanation() {
		if (explanationInitialized) {
			return explanation;
		}

		if (conf.getAttribute("cellExplanation") == null
				|| conf.getAttribute("cellExplanation").isEmpty()) {
			explanationInitialized = true;
			return null;
		}
		try {
			explanation = (CellExplanation) conf.createExecutableExtension("cellExplanation");
		} catch (CoreException e) {
			explanationInitialized = true;
			log.error("Could not create cell explanation for function", e);
		}
		explanationInitialized = true;
		return explanation;
	}

	/**
	 * Get the source entities
	 * 
	 * @return the source entities
	 */
	@Override
	public abstract Set<? extends P> getSource();

	/**
	 * Get the target entities
	 * 
	 * @return the target entities
	 */
	@Override
	public abstract Set<? extends P> getTarget();

	/**
	 * States if the function represents an augmentation of a target instance
	 * instead of a transformation.
	 * 
	 * @return if the function is an augmentation
	 */
	public boolean isAugmentation() {
		return getSource().isEmpty();
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public final String getId() {
		return conf.getAttribute("identifier");
	}

	/**
	 * @see Function#getDefiningBundle()
	 */
	@Override
	public String getDefiningBundle() {
		return conf.getContributor().getName();
	}

	/**
	 * @see Function#getDisplayName()
	 */
	@Override
	public final String getDisplayName() {
		return conf.getAttribute("name");
	}

	/**
	 * @see Function#getDescription()
	 */
	@Override
	public final String getDescription() {
		return conf.getAttribute("description");
	}

	/**
	 * @see Function#getCategoryId()
	 */
	@Override
	public final String getCategoryId() {
		return conf.getAttribute("category");
	}

	/**
	 * @see Function#getDefinedParameters()
	 */
	@Override
	public final Collection<FunctionParameter> getDefinedParameters() {
		return Collections.unmodifiableCollection(parameters.values());
	}

	/**
	 * Get the function parameter with the given name.
	 * 
	 * @param paramName the parameter name
	 * @return the parameter or <code>null</code> if it doesn't exist
	 */
	public FunctionParameter getParameter(String paramName) {
		return parameters.get(paramName);
	}

	/**
	 * @see Function#getIconURL()
	 */
	@Override
	public URL getIconURL() {
		String icon = conf.getAttribute("icon");
		return getURL(icon);
	}

	private URL getURL(String resource) {
		if (resource != null && !resource.isEmpty()) {
			String contributor = conf.getDeclaringExtension().getContributor().getName();
			Bundle bundle = Platform.getBundle(contributor);

			if (bundle != null) {
				return bundle.getResource(resource);
			}
		}

		return null;
	}

	/**
	 * @see Function#getHelpURL
	 */
	@Override
	public URL getHelpURL() {
		String help = conf.getAttribute("help");
		return getURL(help);
	}

//	/**
//	 * @see Function#getHelpFileID()
//	 */
//	@Override
//	public final String getHelpFileID() {
//		return conf.getAttribute("helpID");
//	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractFunction<?> other = (AbstractFunction<?>) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		}
		else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
