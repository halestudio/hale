/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;

import com.haleconnect.api.projectstore.v1.ApiException;
import com.haleconnect.api.projectstore.v1.ApiResponse;
import com.haleconnect.api.projectstore.v1.api.FilesApi;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.internal.ProjectStoreHelper;

/**
 * Input supplier for hale connect projects
 * 
 * @author Florian Esser
 */
public class HaleConnectInputSupplier implements LocatableInputSupplier<InputStream> {

	/**
	 * URI schema for hale connect locations
	 */
	public static final String SCHEME_HALECONNECT = "hc";

	private final String projectId;
	private final String basePath;
	private final Owner owner;
	private final String apiKey;

	/**
	 * Create an I/O supplier based on the given project bucket ID
	 * 
	 * @param projectId Project bucket ID
	 * @param owner Project owner
	 * @param basePath Project store base path
	 * @param apiKey JWT authenticating the current user
	 */
	public HaleConnectInputSupplier(String projectId, Owner owner, String basePath, String apiKey) {
		this.projectId = projectId;
		this.basePath = basePath;
		this.owner = owner;
		this.apiKey = apiKey;
	}

	@Override
	public InputStream getInput() throws IOException {
		FilesApi api = ProjectStoreHelper.getFilesApi(basePath, apiKey);
		ApiResponse<File> response;
		try {
			response = api.getProjectFilesAsZipWithHttpInfo(owner.getType().getJsonValue(),
					owner.getId(), projectId);
		} catch (ApiException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return new BufferedInputStream(new FileInputStream(response.getData()));
	}

	@Override
	public URI getLocation() {
		return URI.create(MessageFormat.format("{0}:project:{1}:{2}:{3}", SCHEME_HALECONNECT,
				owner.getType().getJsonValue(), owner.getId(), projectId));
	}

	@Override
	public URI getUsedLocation() {
		return getLocation();
	}
}
