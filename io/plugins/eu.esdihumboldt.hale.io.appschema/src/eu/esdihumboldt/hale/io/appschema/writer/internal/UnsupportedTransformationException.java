package eu.esdihumboldt.hale.io.appschema.writer.internal;

/**
 * Exception thrown by {@link PropertyTransformationHandlerFactory} or
 * {@link TypeTransformationHandlerFactory} when client code attempts to create
 * a handler for an unsupported transformation function.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class UnsupportedTransformationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1989826026638856227L;

	private String transformationIdentifier;

	/**
	 * @see Exception#Exception(String)
	 */
	public UnsupportedTransformationException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(String)
	 * @param message the error message
	 * @param transformationIdentifier the identifier of the unsupported
	 *            transformation
	 */
	public UnsupportedTransformationException(String message, String transformationIdentifier) {
		super(message);
		this.transformationIdentifier = transformationIdentifier;
	}

	/**
	 * @return the unsupported transformation identifier
	 */
	public String getTransformationIdentifier() {
		return transformationIdentifier;
	}

}
