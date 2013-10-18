/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.projects.war.components;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.upload.FileUploadBase.SizeLimitExceededException;
import org.apache.wicket.util.upload.FileUploadException;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;
import eu.esdihumboldt.hale.server.projects.war.pages.ProjectsPage;
import eu.esdihumboldt.hale.server.webapp.components.FieldMessage;
import eu.esdihumboldt.hale.server.webapp.components.FieldValidatingBehavior;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.scavenger.ScavengerException;

/**
 * Upload form for new projects.
 * 
 * @author Simon Templer
 */
public class UploadForm extends Form<Void> {

	private static final long serialVersionUID = -8077630706189091706L;

	private static final ALogger log = ALoggerFactory.getLogger(UploadForm.class);

	private final FileUploadField file;

	private final TextField<String> identifier;

	private Set<String> allowedContentTypes = null;

	private String customTypeErrorMessage = null;

	@SpringBean
	private ProjectScavenger projects;

	/**
	 * @see Form#Form(String)
	 */
	public UploadForm(String id) {
		super(id);

		// multipart always needed for uploads
		setMultiPart(true);

		// input field for identifier text
		identifier = new TextField<String>("identifier", new Model<String>());
		// pattern validator
		identifier.add(new PatternValidator("[A-Za-z0-9 _\\-]+"));
		// validator that checks if the project name is already taken
		identifier.add(new IValidator<String>() {

			private static final long serialVersionUID = 275885544279441469L;

			@Override
			public void validate(IValidatable<String> validatable) {
				String name = validatable.getValue();
				for (String project : projects.getResources()) {
					// ignore case to avoid confusion
					if (name.equalsIgnoreCase(project)) {
						validatable.error(new ValidationError()
								.setMessage("Identifier already in use"));
						break;
					}
				}
			}
		});
		FieldMessage identifierMessage = new FieldMessage("identifierMessage", new Model<String>(
				"Unique identifier for the project"), identifier);
		identifier.add(new FieldValidatingBehavior("onblur", identifierMessage));
		identifier.setOutputMarkupId(true);
		identifier.setRequired(true);
//		identifier.add(new DefaultFocus()); XXX not working well with ajax
		add(identifier);
		add(identifierMessage);

		// Add one file input field
		add(file = new FileUploadField("file"));
		add(new FeedbackPanel("feedback"));

		addAllowedContentType("application/zip");
		addAllowedContentType("application/x-zip");
		addAllowedContentType("application/x-zip-compressed");
		addAllowedContentType("application/octet-stream");

//		setCustomTypeErrorMessage("Only ZIP archives are supported for upload");

		setMaxSize(Bytes.megabytes(20));
	}

	/**
	 * Add an allowed content type. If none is added, any content type is
	 * allowed.
	 * 
	 * @param contentType the content type to add
	 */
	public void addAllowedContentType(String contentType) {
		if (allowedContentTypes == null) {
			allowedContentTypes = new HashSet<String>();
		}

		allowedContentTypes.add(contentType);
	}

	/**
	 * Check if the given content type is allowed for upload
	 * 
	 * @param contentType the content type
	 * 
	 * @return if the upload shall be allowed
	 */
	protected boolean checkContentType(String contentType) {
		if (allowedContentTypes == null) {
			return true;
		}
		else {
			return allowedContentTypes.contains(contentType);
		}
	}

	/**
	 * @see Form#onSubmit()
	 */
	@Override
	protected void onSubmit() {
		final String project = identifier.getModel().getObject();
		try {
			final FileUpload upload = file.getFileUpload();
			if (upload != null) {
				File dir = projects.reserveResourceId(project);

//				File target = new File(dir, upload.getClientFileName());
				String type = upload.getContentType();
				if (checkContentType(type)) {
//					IOUtils.copy(upload.getInputStream(), new FileOutputStream(target));

					// try extracting the archive
					IOUtils.extract(dir, new BufferedInputStream(upload.getInputStream()));

					// trigger scan after upload
					projects.triggerScan();
					info("Successfully uploaded project");

					onUploadSuccess();
				}
				else {
					projects.releaseResourceId(project);
					error(getTypeErrorMessage(type));
				}
			}
			else {
				warn("Please provide a file for upload");
			}
		} catch (ScavengerException e) {
			error(e.getMessage());
		} catch (Exception e) {
			projects.releaseResourceId(project);
			log.error("Error while uploading file", e);
			error("Error saving the file");
		}
	}

	/**
	 * Called after a successful upload.
	 */
	protected void onUploadSuccess() {
		setResponsePage(ProjectsPage.class);
	}

	/**
	 * Get the error message if the upload of the given type is not supported
	 * 
	 * @param type the content type
	 * 
	 * @return the error message
	 */
	protected String getTypeErrorMessage(String type) {
		if (customTypeErrorMessage != null) {
			return customTypeErrorMessage;
		}
		else {
			return "Files of type " + type + " are not supported.";
		}
	}

	/**
	 * @see Form#onFileUploadException(FileUploadException, Map)
	 */
	@Override
	protected void onFileUploadException(FileUploadException e, Map<String, Object> model) {
		if (e instanceof SizeLimitExceededException) {
			final String msg = "Only files up to  " + bytesToString(getMaxSize(), Locale.US)
					+ " can be uploaded.";
			error(msg);
		}
		else {
			final String msg = "Error uploading the file: " + e.getLocalizedMessage();
			error(msg);

			log.warn(msg, e);
		}
	}

	/**
	 * @param customTypeErrorMessage the customTypeErrorMessage to set
	 */
	public void setCustomTypeErrorMessage(String customTypeErrorMessage) {
		this.customTypeErrorMessage = customTypeErrorMessage;
	}

	/**
	 * Convert {@link Bytes} to a string, produces a prettier output than
	 * {@link Bytes#toString(Locale)}
	 * 
	 * @param bytes the bytes
	 * @param locale the locale
	 * 
	 * @return the converted string
	 */
	public static String bytesToString(Bytes bytes, Locale locale) {
		if (bytes.bytes() >= 0) {
			if (bytes.terabytes() >= 1.0) {
				return unitString(bytes.terabytes(), "TB", locale);
			}

			if (bytes.gigabytes() >= 1.0) {
				return unitString(bytes.gigabytes(), "GB", locale);
			}

			if (bytes.megabytes() >= 1.0) {
				return unitString(bytes.megabytes(), "MB", locale);
			}

			if (bytes.kilobytes() >= 1.0) {
				return unitString(bytes.kilobytes(), "KB", locale);
			}

			return Long.toString(bytes.bytes()) + " bytes";
		}
		else {
			return "N/A";
		}
	}

	private static String unitString(final double value, final String units, final Locale locale) {
		return StringValue.valueOf(value, locale) + " " + units;
	}

}
