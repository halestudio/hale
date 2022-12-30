
package eu.esdihumboldt.hale.io.pb;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;

import com.google.protobuf.Struct;
import com.google.protobuf.Struct.Builder;
import com.google.protobuf.util.JsonFormat;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.json.JsonInstanceWriter;
import eu.esdihumboldt.util.io.OutputSupplier;

/**
 * Class to generate instances to Protocol Buffer.
 * 
 * @author Flaminia Catalli
 */

public class ProtocolBufferInstanceWriter extends AbstractInstanceWriter {

	private SchemaSpace targetSchema;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected String getDefaultTypeName() {
		return "ProtocolBuffer";

	}

	@Override
	public boolean isPassthrough() {
		return true;
	}

	/**
	 * @see InstanceWriter#getTargetSchema()
	 */
	@Override
	public SchemaSpace getTargetSchema() {
		return targetSchema;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Generating " + getDefaultTypeName(), ProgressIndicator.UNKNOWN);

		// IDEA:
		// 1. write instances into a JSON file
		// 2. use the structBuilder to convert the content of the JSON file into
		// PB
		// https://stackoverflow.com/questions/38406211/how-to-convert-from-json-to-protobuf/38441368#38441368

		File jsonFile = File.createTempFile("intermediate_json_file", ".json");
		try {
			JsonInstanceWriter writer = new JsonInstanceWriter(true);
			writer.writeInstanceCollectionToJsonFile(getInstances(), jsonFile, reporter);
			try (Reader jsonReader = Files.newBufferedReader(jsonFile.toPath(),
					writer.getCharset())) {
				writeJsonToProtocolBuffer(jsonReader, getTarget());
			}
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(String.format("Error generating %s file", getDefaultTypeName()), e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
			jsonFile.delete();
		}
		return reporter;

	}

	/**
	 * Convert JSON to Protocol Buffer.
	 * 
	 * @param jsonSource reader of the JSON to convert
	 * @param target supplier for the target output stream
	 * @throws IOException if an error occurs reading, converting or writing
	 *             data
	 */
	public static void writeJsonToProtocolBuffer(Reader jsonSource,
			OutputSupplier<? extends OutputStream> target) throws IOException {
		Builder structBuilder = Struct.newBuilder();
		JsonFormat.parser().merge(jsonSource, structBuilder);

		try (OutputStream out = target.getOutput()) {
			structBuilder.build().writeTo(out);
		}
	}

}