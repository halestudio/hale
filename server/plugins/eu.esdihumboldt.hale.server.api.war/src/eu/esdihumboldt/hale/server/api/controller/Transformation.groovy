/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.controller

import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier
import eu.esdihumboldt.hale.common.headless.EnvironmentService
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment
import eu.esdihumboldt.hale.common.headless.transform.TransformationWorkspace
import eu.esdihumboldt.hale.common.instance.io.InstanceIO
import eu.esdihumboldt.hale.common.instance.io.InstanceReader
import eu.esdihumboldt.hale.server.api.model.IOConfig
import eu.esdihumboldt.hale.server.api.model.SourceConfig
import eu.esdihumboldt.hale.server.api.model.TransformRequest


/**
 * Transformation controller.
 * 
 * @author Simon Templer
 */
@Controller
class Transformation {

	private final EnvironmentService envService;

	/**
	 * Create the transformation controller.
	 * 
	 * @param envService the transformation environment service
	 */
	@Autowired
	public Transformation(EnvironmentService envService) {
		super();
		this.envService = envService;
	}

	/**
	 * Execute a transformation.
	 * 
	 * @param id the identifier of the transformation environment
	 * @param config the transformation configuration
	 * @param files the uploaded files
	 * @return the transformation response
	 */
	@RequestMapping(value = '/transform/{id}', method = RequestMethod.POST,
	consumes = 'multipart/form-data', produces = 'application/json')
	Map transform(@PathVariable('id') String id, @RequestPart('config') TransformRequest config,
			@RequestPart(value = 'file', required = false) List<MultipartFile> files,
			HttpServletResponse response) {
		//TODO check if configuration is valid and transformation can be executed
		// check given id
		TransformationEnvironment env = envService.getEnvironment(id)
		if (env == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Transformation environment $id not found.")
			return null
		}

		/*
		 * XXX First version of transformation API is based on transformation
		 * workspace.
		 */
		final TransformationWorkspace workspace = new TransformationWorkspace()

		// create instance readers
		List<InstanceReader> readers = createReaders(config.source, files, workspace)

		workspace.transform(id, readers, createTargetConfig(config.target, env))
	}

	/**
	 * Create instance readers from the given source configurations and
	 * uploaded files.
	 *  
	 * @param sources the source configurations
	 * @param files the uploaded files
	 * @param workspace the transformation workspace
	 * @return the list of instance readers
	 */
	protected List<InstanceReader> createReaders(List<SourceConfig> sources,
			List<MultipartFile> files, TransformationWorkspace workspace) {
		def result = []
		int nextFile = 0;

		if (sources != null) {
			for (SourceConfig source in sources) {
				if (source.location == SourceConfig.FILE) {
					// source is a file
					if (files == null || nextFile > files.size()) {
						throw new IllegalArgumentException('No matching uploaded file for source configuration.')
					}
					result << createFileReader(files[nextFile], nextFile, source, workspace)

					nextFile++
				}
				else {
					// source is an URI
					//TODO check if valid URI
					//TODO reject local file URIs
					//TODO create reader
					//TODO configure reader
				}
			}
		}

		if (files != null) {
			// all remaining files use automatic configuration
			while (nextFile <= files.size()) {
				result << createFileReader(files[nextFile], nextFile, new SourceConfig(), workspace)

				nextFile++
			}
		}

		result
	}

	/**
	 * Create an instance reader for an uploaded file.
	 * 
	 * @param file the uploaded file
	 * @param index the index of the uploaded file
	 * @param config the I/O configuration
	 * @param workspace the transformation workspace
	 * @return the instance reader
	 */
	protected InstanceReader createFileReader(MultipartFile file, int index, SourceConfig config,
			TransformationWorkspace workspace) {
		/*
		 * Copy uploaded file to source folder, because the
		 * input stream retrieved from the FileUpload is
		 * automatically closed with the end of the request.
		 * XXX is this also true for MultipartFile?
		 */
		File sourceFile = new File(workspace.sourceFolder, index + '_' + file.originalFilename)
		try {
			file.transferTo(file);
		} catch (IOException e) {
			throw new IllegalStateException('Unable to read uploaded source file', e)
		}

		InstanceReader reader = null;
		try {
			LocatableInputSupplier<? extends InputStream> input = new FileIOSupplier(sourceFile)

			//XXX this is automatic configuration
			reader = HaleIO.findIOProvider(InstanceReader, input, file.originalFilename)
			//TODO respect the source configuration

			if (reader != null) {
				reader.setSource(input)
			}
		} catch (Exception e) {
			throw new IllegalStateException('Unable to read uploaded source file', e)
		}
		if (reader == null) {
			throw new IllegalStateException('Could not find I/O provider for source file.')
		}

		reader
	}

	protected IOConfiguration createTargetConfig(IOConfig target, TransformationEnvironment env) {
		IOConfiguration result = new IOConfiguration()
		// set action id
		result.actionId = InstanceIO.ACTION_SAVE_TRANSFORMED_DATA

		if (target.preset) {
			//TODO try to select a preset and return
		}

		//TODO content type?!

		// configure provider
		if (!target.provider || target.provider == IOConfig.AUTO) {
			//TODO how to auto-determine provider?

			//XXX for now just try the first export template
			def templates = env.getExportTemplates()
			if (templates) {
				result.providerId = templates.find().providerId
			}
			else {
				throw new IllegalStateException('No I/O provider for transformation export available')
			}
		}
		else {
			result.providerId = target.provider
		}

		// copy settings
		if (target.settings) {
			target.settings.entrySet().each {
				result.providerConfiguration.put(it.key, Value.simple(it.value))
			}
		}

		result
	}
}
