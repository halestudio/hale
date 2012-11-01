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

package eu.esdihumboldt.hale.server.webtransform.war.pages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.joda.time.Duration;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.headless.EnvironmentService;
import eu.esdihumboldt.hale.common.headless.HeadlessIO;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.WorkspaceService;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;
import eu.esdihumboldt.hale.common.headless.transform.Transformation;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * Form for uploading and transforming data.
 * 
 * @author Simon Templer
 */
public class UploadAndTransForm extends Form<Void> {

	private static final long serialVersionUID = 8904573677189598470L;

	@SpringBean
	private EnvironmentService environmentService;

	@SpringBean
	private WorkspaceService workspaceService;

	private final String projectId;

	private final FileUploadField file;

	private IOConfiguration target;

	private static final IOptionRenderer<IOConfiguration> RENDERER = new IOptionRenderer<IOConfiguration>() {

		private static final long serialVersionUID = 4714894437575668850L;

		@Override
		public String getDisplayValue(IOConfiguration object) {
			String name = object.getName();
			if (name != null && !name.isEmpty()) {
				return name;
			}
			return object.getProviderId();
		}

		@Override
		public IModel<IOConfiguration> getModel(IOConfiguration value) {
			return new Model<IOConfiguration>(value);
		}
	};

	/**
	 * Create a form for uploading and transforming data.
	 * 
	 * @param id the component ID
	 * @param projectId the project ID
	 */
	public UploadAndTransForm(String id, String projectId) {
		super(id);
		this.projectId = projectId;

		// multi-part always needed for uploads
		setMultiPart(true);

		add(new FeedbackPanel("feedback"));

		// Add file input field
		add(file = new FileUploadField("upload"));
		file.add(new IValidator<List<FileUpload>>() {

			private static final long serialVersionUID = -5668788086384105101L;

			@Override
			public void validate(IValidatable<List<FileUpload>> validatable) {
				if (validatable.getValue().isEmpty()) {
					validatable.error(new ValidationError("No source files specified."));
				}
			}

		});

		// target selection
		Select<IOConfiguration> selectTarget = new Select<IOConfiguration>("target",
				new PropertyModel<IOConfiguration>(this, "target"));
		selectTarget.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 8004015871380712045L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// TODO update config panel
//				target.add(components);
			}

		});
		add(selectTarget);

		TransformationEnvironment env = environmentService.getEnvironment(projectId);

		// determine presets
		SelectOptions<IOConfiguration> presets = new SelectOptions<IOConfiguration>("presets",
				env.getExportPresets(), RENDERER);
		selectTarget.add(presets);

		// determine valid exporters
		SelectOptions<IOConfiguration> exporters = new SelectOptions<IOConfiguration>("exporters",
				env.getExportTemplates(), RENDERER);
		selectTarget.add(exporters);

		// TODO panel for I/O configuration
	}

	/**
	 * @see Form#onSubmit()
	 */
	@Override
	protected void onSubmit() {
		List<FileUpload> uploads = file.getFileUploads();

		TransformationEnvironment env = environmentService.getEnvironment(projectId);

		if (env != null) {
			// lease workspace folder
			File workspace = workspaceService.leaseWorkspace(Duration.standardDays(1));
			final File source = new File(workspace, "source");
			source.mkdir();

			List<InstanceReader> readers = Lists.transform(uploads,
					new Function<FileUpload, InstanceReader>() {

						private int count;

						@Override
						public InstanceReader apply(@Nullable final FileUpload input) {
							/*
							 * Copy uploaded file to source folder, because the
							 * input stream retrieved from the FileUpload is
							 * automatically closed with the end of the request.
							 */
							File file = new File(source, (count++) + "_"
									+ input.getClientFileName());
							try {
								input.writeTo(file);
							} catch (IOException e) {
								throw new IllegalStateException(
										"Unable to read uploaded source file", e);
							}
							// TODO clean up later on?!

							InstanceReader reader = null;
							try {
								LocatableInputSupplier<? extends InputStream> in = new FileIOSupplier(
										file);
								reader = HaleIO.findIOProvider(InstanceReader.class, in,
										input.getClientFileName());
								if (reader != null) {
									reader.setSource(in);
								}
							} catch (Exception e) {
								throw new IllegalStateException(
										"Unable to read uploaded source file", e);
							}
							if (reader == null) {
								throw new IllegalStateException(
										"Could not find I/O provider for source file.");
							}
							return reader;
						}
					});

			InstanceWriter writer = (InstanceWriter) HeadlessIO.loadProvider(target);

			// report file
			File reportFile = new File(workspace, "reports.log");
			// output file
			File out = new File(workspace, "out.xml"); // TODO file extension
			writer.setTarget(new FileIOSupplier(out));

			Transformation.transform(readers, writer, env, new ReportFile(reportFile),
					workspace.getName());

			setResponsePage(StatusPage.class,
					new PageParameters().add(StatusPage.PARAMETER_WORKSPACE, workspace.getName()));
		}
		else {
			error("Could not retrieve associated transformation environment, please ensure the corresponding project is activated.");
		}
	}

	/**
	 * @return the target
	 */
	public IOConfiguration getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(IOConfiguration target) {
		this.target = target;
	}

}
