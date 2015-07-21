/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.codelist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Response;
import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.impl.DefaultResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.codelist.inspire.reader.INSPIRECodeListReader;

/**
 * Advisor for the inspire code list content type
 * 
 * @author Sameer Sheikh
 */
public class InspireCodeListAdvisor extends DefaultResourceAdvisor {

	@Override
	public void copyResource(LocatableInputSupplier<? extends InputStream> resource,
			final Path target, IContentType resourceType, boolean includeRemote, IOReporter reporter)
			throws IOException {

		URI uri = resource.getLocation();
		String uriScheme = uri.getScheme();
		if (uriScheme.equals("http")) {
			// Get the response for the given uri
			Response response = INSPIRECodeListReader.getResponse(uri);

			// Handle the fluent response
			response.handleResponse(new ResponseHandler<Boolean>() {

				@Override
				public Boolean handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					StatusLine status = response.getStatusLine();
					HttpEntity entity = response.getEntity();

					if (status.getStatusCode() >= 300) {
						throw new HttpResponseException(status.getStatusCode(), status
								.getReasonPhrase());
					}
					if (entity == null) {
						throw new ClientProtocolException();
					}
					// Copy the resource file to the target path
					Files.copy(entity.getContent(), target);
					return true;
				}
			});
		}
		else {
			super.copyResource(resource, target, resourceType, includeRemote, reporter);
		}
	}
}
