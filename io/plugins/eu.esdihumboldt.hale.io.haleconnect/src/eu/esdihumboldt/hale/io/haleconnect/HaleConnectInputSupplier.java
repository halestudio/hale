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

import com.haleconnect.api.projectstore.v1.ApiException;
import com.haleconnect.api.projectstore.v1.ApiResponse;
import com.haleconnect.api.projectstore.v1.api.BucketsApi;
import com.haleconnect.api.projectstore.v1.api.FilesApi;
import com.haleconnect.api.projectstore.v1.model.BucketDetail;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.internal.ProjectStoreHelper;

/**
 * I/O supplier for projects imported from hale connect
 * 
 * @author Florian Esser
 */
public class HaleConnectInputSupplier extends DefaultInputSupplier {

	private Long lastModified;
	private final String apiKey;
	private final BasePathResolver basePathResolver;

	/**
	 * Create the hale connect input supplier
	 * 
	 * @param location the location URI
	 * @param apiKey API key to access the hale connect
	 * @param resolver {@link BasePathResolver} for building hale connect URLs
	 */
	public HaleConnectInputSupplier(URI location, String apiKey, BasePathResolver resolver) {
		super(location);

		this.apiKey = apiKey;
		this.basePathResolver = resolver;

	}

	@Override
	public InputStream getInput() throws IOException {
		Owner owner = HaleConnectUrnBuilder.extractProjectOwner(getLocation());
		String projectId = HaleConnectUrnBuilder.extractProjectId(getLocation());

		FilesApi api = ProjectStoreHelper.getFilesApi(basePathResolver, apiKey);
		final ApiResponse<File> response;
		try {
			response = api.getProjectFilesAsZipWithHttpInfo(owner.getType().getJsonValue(),
					owner.getId(), projectId);
		} catch (com.haleconnect.api.projectstore.v1.ApiException e) {
			throw new IOException(e.getMessage(), e);
		}

		// Cache lastModified timestamp at the time of import
		getLastModified();

		return new BufferedInputStream(new FileInputStream(response.getData()));
	}

	/**
	 * @return details on the hale connect project
	 */
	public Long getLastModified() {
		if (lastModified == null) {
			Owner owner = HaleConnectUrnBuilder.extractProjectOwner(getLocation());
			String projectId = HaleConnectUrnBuilder.extractProjectId(getLocation());
			final BucketsApi api = ProjectStoreHelper.getBucketsApi(basePathResolver, apiKey);
			final ApiResponse<BucketDetail> response;
			try {
				response = api.getBucketInfoWithHttpInfo(owner.getType().getJsonValue(),
						owner.getId(), projectId);
				lastModified = response.getData().getLastModified();
			} catch (ApiException e) {
				// Not fatal
			}
		}

		return lastModified;
	}

	/**
	 * @return The {@link BasePathResolver} used by this input supplier
	 */
	public BasePathResolver getBasePathResolver() {
		return this.basePathResolver;
	}

}
