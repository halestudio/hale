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

package eu.esdihumboldt.hale.io.haleconnect.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.text.MessageFormat;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUrnBuilder;
import eu.esdihumboldt.hale.io.haleconnect.Owner;
import eu.esdihumboldt.hale.io.haleconnect.OwnerType;

/**
 * Saves a project (optonally including all related resources) as a ZIP archive
 * and uploads it to hale connect.
 * 
 * @author Florian Esser
 */
public class HaleConnectProjectWriter extends ArchiveProjectWriter {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectProjectWriter.class);

	/**
	 * Owner of the uploaded project
	 */
	public static final String OWNER_TYPE = "ownerType";

	/**
	 * Share uploaded project publicly
	 */
	public static final String SHARING_PUBLIC = "sharingOptions.public";

	/**
	 * Enable versioning for uploaded project
	 */
	public static final String ENABLE_VERSIONING = "enableVersioning";

	private final HaleConnectService haleConnect;
	private URI projectUri;
	private URI clientAccessUrl;

	/**
	 * Creates a hale connect project writer
	 */
	public HaleConnectProjectWriter() {
		super();
		this.haleConnect = HalePlatform.getService(HaleConnectService.class);
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		if (!haleConnect.isLoggedIn()) {
			throw new IOProviderConfigurationException("Must be logged in to hale connect");
		}

		boolean enableVersioning = getParameter(ENABLE_VERSIONING).as(Boolean.class);
		boolean publicAccess = getParameter(SHARING_PUBLIC).as(Boolean.class);
		String ownerTypeParameter = getParameter(OWNER_TYPE).as(String.class);
		OwnerType ownerType;
		try {
			ownerType = OwnerType.fromJsonValue(ownerTypeParameter);
		} catch (IllegalArgumentException e) {
			throw new IOProviderConfigurationException(
					MessageFormat.format("Invalid owner type: {0}", ownerTypeParameter), e);
		}

		// redirect project archive to temporary local file
		File projectArchive = Files.createTempFile("hc-arc", ".zip").toFile();
		IOReport report;
		try (final FileOutputStream archiveStream = new FileOutputStream(projectArchive)) {
			report = createProjectArchive(archiveStream, reporter, progress);
		}

		URI location = getTarget().getLocation();
		Project project = getProject();

		String projectId;
		Owner owner;
		if (location == null) {
			// was not shared before
			String ownerId;
			switch (ownerType) {
			case USER:
				ownerId = haleConnect.getSession().getUserId();
				break;
			case ORGANISATION:
				if (haleConnect.getSession().getOrganisationIds().isEmpty()) {
					throw new IOProviderConfigurationException(MessageFormat.format(
							"Owner type is set to ORGANISATION but user \"{0}\" is not associated with any organisation",
							haleConnect.getSession().getUsername()));
				}
				ownerId = haleConnect.getSession().getOrganisationIds().iterator().next();
				break;
			default:
				throw new IOProviderConfigurationException(
						MessageFormat.format("Unknown owner type: {0}", ownerType));
			}
			owner = new Owner(ownerType, ownerId);
			try {
				projectId = haleConnect.createProject(project.getName(), project.getAuthor(), owner,
						enableVersioning);
				haleConnect.setProjectSharingOptions(projectId, owner,
						new SharingOptions(publicAccess));
			} catch (HaleConnectException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		else if (!HaleConnectUrnBuilder.isValidProjectUrn(location)) {
			throw new IOProviderConfigurationException(
					MessageFormat.format("Cannot write to location: {0}", location.toString()));
		}
		else {
			projectId = HaleConnectUrnBuilder.extractProjectId(location);
			owner = HaleConnectUrnBuilder.extractProjectOwner(location);
		}

		boolean result;
		try {
			result = haleConnect.uploadProjectFile(projectId, owner, projectArchive, progress);
		} catch (HaleConnectException e) {
			throw new IOException(e.getMessage(), e);
		}

		// Make sure the bucket name corresponds to possibly updated project
		// name
		try {
			haleConnect.setProjectName(projectId, owner, project.getName());
		} catch (HaleConnectException e) {
			// This is non-fatal
			log.warn(MessageFormat.format(
					"Unable to update project bucket name for project {0}: {1}",
					HaleConnectUrnBuilder.buildProjectUrn(owner, projectId).toString(),
					e.getMessage()), e);
		}

		this.clientAccessUrl = HaleConnectUrnBuilder.buildClientAccessUrl(
				haleConnect.getBasePathManager().getBasePath(HaleConnectServices.WEB_CLIENT), owner,
				projectId);
		this.projectUri = HaleConnectUrnBuilder.buildProjectUrn(owner, projectId);
		reporter.setSuccess(result);

		return reporter;
	}

	/**
	 * @return the URI of the created project
	 */
	public URI getProjectUri() {
		return this.projectUri;
	}

	/**
	 * @return the URL required to access the transformation project via the
	 *         hale connect web client
	 */
	public URI getClientAccessUrl() {
		return this.clientAccessUrl;
	}

}
