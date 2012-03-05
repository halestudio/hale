/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.extension.function;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;

/**
 * {@link IConfigurationElement} based function base class
 * @param <P> the parameter type
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractFunction<P extends AbstractParameter> implements Function {
	
	private final ALogger log = ALoggerFactory.getLogger(AbstractFunction.class);

	/**
	 * The configuration element
	 */
	protected final IConfigurationElement conf;
	
	private final Set<FunctionParameter> parameters;
	
	/**
	 * Create a function definition based on the given configuration element
	 * @param conf the configuration element
	 */
	protected AbstractFunction(IConfigurationElement conf) {
		super();
		this.conf = conf;
		
		this.parameters = createParameters(conf);
	}

	private static Set<FunctionParameter> createParameters(IConfigurationElement conf) {
		Set<FunctionParameter> parameters = new LinkedHashSet<FunctionParameter>();
		
		IConfigurationElement[] pars = conf.getChildren("functionParameter");
		if (pars != null) {
			for (IConfigurationElement par : pars) {
				parameters.add(new FunctionParameter(par));
			}
		}
		
		return parameters;
	}
	
	/**
	 * @see Function#getExplanation()
	 */
	@Override
	public CellExplanation getExplanation() {
		if (conf.getAttribute("cellExplanation") == null
				|| conf.getAttribute("cellExplanation").isEmpty()) {
			return null;
		}
		try {
			return (CellExplanation) conf.createExecutableExtension("cellExplanation");
		} catch (CoreException e) {
			log.error("Could not create cell explanation for function", e);
			return null;
		}
	}

	/**
	 * Get the source entities
	 * @return the source entities
	 */
	@Override
	public abstract Set<? extends P> getSource();
	
	/**
	 * Get the target entities
	 * @return the target entities
	 */
	@Override
	public abstract Set<? extends P> getTarget();
	
	/**
	 * States if the function represents an augmentation of a target instance
	 * instead of a transformation.
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
	public final Set<FunctionParameter> getDefinedParameters() {
		return Collections.unmodifiableSet(parameters);
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
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
