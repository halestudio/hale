package eu.esdihumboldt.hale.io.gml.geometry.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.io.gml.geometry.FixedConstraintsGeometryHandler;

/**
 * The configuration for interlis geometry handlers
 * 
 * @author Sameer Sheikh
 */
public class InterlisHandlerConf implements Identifiable {

	private final String id;
	private final String handlerType;
	private final Class<? extends FixedConstraintsGeometryHandler> clasz;

	/**
	 * @param elementId element id
	 * @param element configuration element
	 */
	@SuppressWarnings("unchecked")
	public InterlisHandlerConf(String elementId, IConfigurationElement element) {
		id = elementId;
		handlerType = element.getAttribute("handlertype");
		clasz = (Class<? extends FixedConstraintsGeometryHandler>) ExtensionUtil.loadClass(element,
				"geometryhandler");
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the handler type customized,eg. it may be interlis or gml
	 */
	public String getHandlerType() {
		return handlerType;
	}

	/**
	 * @return the configured geometry handler class
	 */
	public Class<? extends FixedConstraintsGeometryHandler> getHandlerClass() {
		return clasz;
	}
}
