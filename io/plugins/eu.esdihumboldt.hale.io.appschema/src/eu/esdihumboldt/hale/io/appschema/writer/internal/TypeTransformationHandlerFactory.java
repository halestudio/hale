package eu.esdihumboldt.hale.io.appschema.writer.internal;

import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;

/**
 * Instantiates the type transformation handler capable of handling the
 * specified transformation function.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class TypeTransformationHandlerFactory {

	private static TypeTransformationHandlerFactory instance;

	private TypeTransformationHandlerFactory() {

	}

	/**
	 * Return the singleton factory instance.
	 * 
	 * @return the factory instance
	 */
	public static TypeTransformationHandlerFactory getInstance() {
		if (instance == null) {
			instance = new TypeTransformationHandlerFactory();
		}

		return instance;
	}

	/**
	 * Creates a new type transformation handler instance to handle the
	 * transformation function specified by the provided identifier.
	 * 
	 * @param typeTransformationIdentifier the type transformation function
	 *            identifier
	 * @return the type transformation handler instance
	 * @throws UnsupportedTransformationException if the specified
	 *             transformation is not supported
	 */
	public TypeTransformationHandler createTypeTransformationHandler(
			String typeTransformationIdentifier) throws UnsupportedTransformationException {
		if (typeTransformationIdentifier == null || typeTransformationIdentifier.trim().isEmpty()) {
			throw new IllegalArgumentException("typeTransformationIdentifier must be set");
		}

		if (typeTransformationIdentifier.equals(RetypeFunction.ID)) {
			return new RetypeHandler();
		}
		else if (typeTransformationIdentifier.equals(MergeFunction.ID)) {
			return new MergeHandler();
		}
		else if (typeTransformationIdentifier.equals(JoinFunction.ID)) {
			return new JoinHandler();
		}
		else {
			String errMsg = String.format("Unsupported type transformation %s",
					typeTransformationIdentifier);
			throw new UnsupportedTransformationException(errMsg, typeTransformationIdentifier);
		}
	}

}
