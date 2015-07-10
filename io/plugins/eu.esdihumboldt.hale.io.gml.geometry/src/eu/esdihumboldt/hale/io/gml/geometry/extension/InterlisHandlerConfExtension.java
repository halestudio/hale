package eu.esdihumboldt.hale.io.gml.geometry.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * @author Sameer Sheikh
 * 
 */
public class InterlisHandlerConfExtension extends IdentifiableExtension<InterlisHandlerConf> {

	private static final String extensionId = "eu.esdihumboldt.hale.io.handlers";

	/**
	 * instance of the interlis handler configuration extension
	 */
	public static InterlisHandlerConfExtension instance;

	/**
	 * constructor
	 */
	public InterlisHandlerConfExtension() {
		super(extensionId);
	}

	@Override
	protected String getIdAttributeName() {

		return "id";
	}

	@Override
	protected InterlisHandlerConf create(String elementId, IConfigurationElement element) {
		if (elementId != null && element != null) {
			return new InterlisHandlerConf(elementId, element);
		}
		return null;

	}

	/**
	 * @return all the geometry handlers whose handlertype is customized as
	 *         "interlis"
	 */
	public List<Class<?>> getInterlisHandlers() {

		List<Class<?>> handlers = new ArrayList<Class<?>>();
		for (InterlisHandlerConf handler : getElements()) {
			if (handler.getHandlerType().equals("interlis")) {
				handlers.add(handler.getHandlerClass());
			}
		}
		return handlers;

	}

	/**
	 * @return instance of the interlis handler configuration extension
	 *         (InterlisHandlerConfExtension)
	 */
	public static InterlisHandlerConfExtension getInstance() {

		if (instance == null) {
			instance = new InterlisHandlerConfExtension();
		}
		return instance;
	}
}
