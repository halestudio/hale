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

package eu.esdihumboldt.hale.server.templates.war.components;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.upload.FileUploadBase.SizeLimitExceededException;
import org.apache.wicket.util.upload.FileUploadException;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.google.common.io.ByteStreams;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.headless.scavenger.ProjectReference;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.hale.server.webapp.components.bootstrap.BootstrapFeedbackPanel;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.scavenger.ScavengerException;

/**
 * Upload form for new templates.
 * 
 * @author Simon Templer
 */
public abstract class UploadForm extends Panel {

	private static final long serialVersionUID = -8077630706189091706L;

	private static final ALogger log = ALoggerFactory.getLogger(UploadForm.class);

	private final FileUploadField file;

	@SpringBean
	private TemplateScavenger templates; // =
											// OsgiUtils.getService(TemplateScavenger.class);

	/**
	 * @see Panel#Panel(String)
	 */
	public UploadForm(String id) {
		super(id);

		Form<Void> form = new Form<Void>("upload") {

			private static final long serialVersionUID = 716487990605324922L;

			@Override
			protected void onSubmit() {
				// attempt to reserve template ID
				Pair<String, File> template;
				try {
					template = templates.reserveResource(null);
				} catch (ScavengerException e) {
					error(e.getMessage());
					return;
				}
				final String templateId = template.getFirst();
				final File dir = template.getSecond();

				try {
					List<FileUpload> uploads = file.getFileUploads();
					if (!uploads.isEmpty()) {
						for (FileUpload upload : uploads) {
							if (isZipFile(upload)) {
								// extract uploaded file
								IOUtils.extract(dir,
										new BufferedInputStream(upload.getInputStream()));
							}
							else {
								// copy uploaded file
								File target = new File(dir, upload.getClientFileName());
								ByteStreams.copy(upload.getInputStream(), new FileOutputStream(
										target));
							}
						}

						// trigger scan after upload
						templates.triggerScan();

						ProjectReference<Void> ref = templates.getReference(templateId);
						if (ref != null && ref.getProjectInfo() != null) {
							info("Successfully uploaded project");
							onUploadSuccess(this, templateId, ref.getProjectInfo());
						}
						else {
							templates.releaseResourceId(templateId);
							error("Uploaded files could not be loaded as HALE project");
						}
					}
					else {
						templates.releaseResourceId(templateId);
						warn("Please provide a file for upload");
					}
				} catch (Exception e) {
					templates.releaseResourceId(templateId);
					log.error("Error while uploading file", e);
					error("Error saving the file");
				}
			}

			@Override
			protected void onFileUploadException(FileUploadException e, Map<String, Object> model) {
				if (e instanceof SizeLimitExceededException) {
					final String msg = "Only files up to  "
							+ bytesToString(getMaxSize(), Locale.US) + " can be uploaded.";
					error(msg);
				}
				else {
					final String msg = "Error uploading the file: " + e.getLocalizedMessage();
					error(msg);

					log.warn(msg, e);
				}
			}

		};
		add(form);

		// multipart always needed for uploads
		form.setMultiPart(true);

		// max size for upload
		form.setMaxSize(Bytes.megabytes(1));

		// Add file input field for multiple files
		form.add(file = new FileUploadField("file"));
		file.add(new IValidator<List<FileUpload>>() {

			private static final long serialVersionUID = -5668788086384105101L;

			@Override
			public void validate(IValidatable<List<FileUpload>> validatable) {
				if (validatable.getValue().isEmpty()) {
					validatable.error(new ValidationError("No source files specified."));
				}
			}

		});

		form.add(new BootstrapFeedbackPanel("feedback"));
	}

	/**
	 * Determines if a file upload is a ZIP file.
	 * 
	 * @param upload the file upload
	 * @return if the file upload is a ZIP file and should be extracted to the
	 *         target directory
	 */
	protected boolean isZipFile(FileUpload upload) {
		switch (upload.getContentType()) {
		case "application/zip":
		case "application/x-zip":
		case "application/x-zip-compressed":
			return true;
		}

		return upload.getClientFileName().toLowerCase().endsWith(".zip");
	}

	/**
	 * Called after a successful upload.
	 * 
	 * @param form the form
	 * @param templateId the template identifier
	 * @param projectInfo the project info
	 */
	protected abstract void onUploadSuccess(Form<?> form, String templateId, ProjectInfo projectInfo);

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
