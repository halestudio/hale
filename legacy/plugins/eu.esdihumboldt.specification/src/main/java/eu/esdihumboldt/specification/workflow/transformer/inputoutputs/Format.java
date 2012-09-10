package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import java.io.Serializable;
import java.net.URI;

public interface Format extends Serializable {

	/**
	 * Identicication of format supported by the process input or output. The
	 * process, shall expect input or priduce output in this Mimetype unless the
	 * execute request specifies another MimeType.
	 * 
	 * @return Character String type, not empty
	 */
	public MimeType getMimeType();

	/**
	 * Reference to default encoding supported by the process input or output
	 * The process, shall expect input or produce output using this Encoding,
	 * unless another Encoding is required. This element shall be included when
	 * the default encoding is other than the encoding of the xml response
	 * document (UTF-8). This parameter shall be omitted when there is no
	 * encoding required
	 * 
	 * @return URI type
	 */
	public URI getEncoding();

	/**
	 * Reference to XML Schema Document supported by process input or output
	 * This element shall be omitted when there is no XML schema associated with
	 * this input/output.This parameter shall be included only when this
	 * output/input XML is encoded using XML schema. When Inluded, the
	 * input/output shall validate against a referenced schema.
	 * 
	 * @return URI type
	 */
	public URI getSchema();

	/**
	 * Checks whether this Format is the default format or not
	 * 
	 * @return True if default format
	 */
	public boolean isDefault();

	public enum MimeType {

		/**
		 * A Text or XML type e.g GML
		 */
		TextXml,
		/**
		 * Image type e.g a raster data
		 */
		ImageTiff;

	}

}
