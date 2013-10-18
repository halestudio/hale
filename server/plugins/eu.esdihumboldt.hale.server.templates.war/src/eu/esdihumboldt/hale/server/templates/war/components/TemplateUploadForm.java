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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.upload.FileUploadBase.SizeLimitExceededException;
import org.apache.wicket.util.upload.FileUploadException;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.google.common.io.ByteStreams;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.agilecoders.wicket.extensions.javascript.jasny.FileUploadField;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.templates.TemplateProject;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.hale.server.templates.war.pages.NewTemplatePage;
import eu.esdihumboldt.hale.server.templates.war.pages.TemplatePage;
import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;
import eu.esdihumboldt.hale.server.webapp.components.bootstrap.BootstrapFeedbackPanel;
import eu.esdihumboldt.hale.server.webapp.util.UserUtil;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.scavenger.ScavengerException;

/**
 * Upload form for new templates or updating templates.
 * 
 * @author Simon Templer
 */
public class TemplateUploadForm extends Panel {

	private static final long serialVersionUID = -8077630706189091706L;

	private static final ALogger log = ALoggerFactory.getLogger(TemplateUploadForm.class);

	private final FileUploadField file;

	@SpringBean
	private TemplateScavenger templates;

	private final String templateId;

	private CheckBox updateInfo;

	/**
	 * Constructor
	 * 
	 * @param id the component ID
	 * @param templateId the identifier of the template to update, or
	 *            <code>null</code> to create a new template
	 */
	public TemplateUploadForm(String id, String templateId) {
		super(id);
		this.templateId = templateId;

		Form<Void> form = new Form<Void>("upload") {

			private static final long serialVersionUID = 716487990605324922L;

			@Override
			protected void onSubmit() {
				List<FileUpload> uploads = file.getFileUploads();
				if (uploads != null && !uploads.isEmpty()) {
					final boolean newTemplate = TemplateUploadForm.this.templateId == null;
					final String templateId;
					final File dir;
					File oldContent = null;
					if (newTemplate) {
						// attempt to reserve template ID
						Pair<String, File> template;
						try {
							template = templates.reserveResource(determinePreferredId(uploads));
						} catch (ScavengerException e) {
							error(e.getMessage());
							return;
						}
						templateId = template.getFirst();
						dir = template.getSecond();
					}
					else {
						templateId = TemplateUploadForm.this.templateId;
						dir = new File(templates.getHuntingGrounds(), templateId);

						// archive old content
						try {
							Path tmpFile = Files.createTempFile("hale-template", ".zip");
							try (OutputStream out = Files.newOutputStream(tmpFile);
									ZipOutputStream zos = new ZipOutputStream(out)) {
								IOUtils.zipDirectory(dir, zos);
							}
							oldContent = tmpFile.toFile();
						} catch (IOException e) {
							log.error("Error saving old template content to archive", e);
						}

						// delete old content
						try {
							FileUtils.cleanDirectory(dir);
						} catch (IOException e) {
							log.error("Error deleting old template content", e);
						}
					}

					try {
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
						if (newTemplate) {
							templates.triggerScan();
						}
						else {
							templates.forceUpdate(templateId);
						}

						TemplateProject ref = templates.getReference(templateId);
						if (ref != null && ref.isValid()) {
							info("Successfully uploaded project");
							boolean infoUpdate = (updateInfo != null) ? (updateInfo
									.getModelObject()) : (false);
							onUploadSuccess(this, templateId, ref.getProjectInfo(), infoUpdate);
						}
						else {
							if (newTemplate) {
								templates.releaseResourceId(templateId);
							}
							else {
								restoreContent(dir, oldContent);
							}
							error((ref != null) ? (ref.getNotValidMessage())
									: ("Uploaded files could not be loaded as HALE project"));
						}

					} catch (Exception e) {
						if (newTemplate) {
							templates.releaseResourceId(templateId);
						}
						else {
							restoreContent(dir, oldContent);
						}
						log.error("Error while uploading file", e);
						error("Error saving the file");
					}
				}
				else {
					warn("Please provide a file for upload");
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

		// add anonym/recaptcha panel
		boolean loggedIn = UserUtil.getLogin() != null;
		WebMarkupContainer anonym = new WebMarkupContainer("anonym");

		if (loggedIn) {
			anonym.add(new WebMarkupContainer("recaptcha"));
		}
		else {
			anonym.add(new RecaptchaPanel("recaptcha"));
		}

		anonym.add(new BookmarkablePageLink<>("login", ((BaseWebApplication) getApplication())
				.getLoginPageClass()));

		anonym.setVisible(!loggedIn);
		form.add(anonym);

		// update panel
		WebMarkupContainer update = new WebMarkupContainer("update");
		update.setVisible(templateId != null);

		updateInfo = new CheckBox("updateInfo", Model.of(true));
		update.add(updateInfo);

		form.add(update);

		// feedback panel
		form.add(new BootstrapFeedbackPanel("feedback"));
	}

	/**
	 * Determine the preferred resource identifier based on the uploaded files.
	 * 
	 * @param uploads the uploaded files
	 * @return the preferred identifier or <code>null</code>
	 */
	protected String determinePreferredId(List<FileUpload> uploads) {
		if (uploads != null && !uploads.isEmpty()) {
			String filename = uploads.iterator().next().getClientFileName();

			// strip extension
			int i = filename.lastIndexOf('.');
			if (i > 0) {
				filename = filename.substring(0, i);
			}

			return filename;
		}

		return null;
	}

	/**
	 * Restore the old content of a template from an archive.
	 * 
	 * @param dir the template directory
	 * @param oldContent the archive with the old content
	 */
	protected void restoreContent(File dir, File oldContent) {
		try {
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			log.error("Error deleting new invalid template content", e);
		}
		if (oldContent != null) {
			// try to restore old content
			try (InputStream in = new BufferedInputStream(new FileInputStream(oldContent))) {
				IOUtils.extract(dir, in);
			} catch (IOException e) {
				log.error("Error restoring old template content", e);
			}
			oldContent.delete();
			oldContent = null;
			templates.forceUpdate(templateId);
		}
	}

	/**
	 * Determines if a file upload is a ZIP file.
	 * 
	 * @param upload the file upload
	 * @return if the file upload is a ZIP file and should be extracted to the
	 *         target directory
	 */
	protected boolean isZipFile(FileUpload upload) {
		String lowerFileName = upload.getClientFileName().toLowerCase();

		if (lowerFileName.endsWith(".hale")) {
			// do not extract .hale files
			return false;
		}

		switch (upload.getContentType()) {
		case "application/zip":
		case "application/x-zip":
		case "application/x-zip-compressed":
			return true;
		}

		// by default extract .zip and .halez files
		return lowerFileName.endsWith(".zip") || lowerFileName.endsWith(".halez");
	}

	/**
	 * Called after a successful upload.
	 * 
	 * @param form the form
	 * @param templateId the template identifier
	 * @param projectInfo the project info
	 * @param updateInfo if for an updated template, the template information
	 *            should be updated from the project
	 */
	protected void onUploadSuccess(Form<?> form, String templateId, ProjectInfo projectInfo,
			boolean updateInfo) {
		boolean newTemplate = TemplateUploadForm.this.templateId == null;

		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			Template template = Template.getByTemplateId(graph, templateId);
			if (template == null) {
				form.error("Template could not be created");
				return;
			}

			if (newTemplate) {
				// created template was a new template

				// associate user as owner to template
				String login = UserUtil.getLogin();
				if (login != null) {
					User user = User.getByLogin(graph, login);
					graph.addEdge(null, template.getV(), user.getV(), "owner");
				}

				// forward to page to fill in template information
				setResponsePage(new NewTemplatePage(templateId));
			}
			else {
				// created template already existed

				// set last updated
				template.setLastUpdate(new Date());

				// update template info from project info
				if (updateInfo) {
					template.setName(projectInfo.getName());
					template.setAuthor(projectInfo.getAuthor());
					template.setDescription(projectInfo.getDescription());
				}

				// forward to template page
				setResponsePage(TemplatePage.class, new PageParameters().set(0, templateId));
			}
		} catch (NonUniqueResultException e) {
			form.error("Internal error");
			log.error("Duplicate template or user");
		} finally {
			graph.shutdown();
		}
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
