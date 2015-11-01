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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.headless.EnvironmentService;
import eu.esdihumboldt.hale.common.headless.HeadlessIO;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.transform.TransformationWorkspace;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * Form for uploading and transforming data.
 * 
 * @author Simon Templer
 */
public class UploadAndTransForm extends Form<Void> {

	/**
	 * Model for a parameter field.
	 */
	private class FieldFieldModel implements IModel<String> {

		private static final long serialVersionUID = 7866817725347689084L;

		private final String param;

		/**
		 * Create a model for a parameter field.
		 * 
		 * @param param the parameter name
		 */
		public FieldFieldModel(String param) {
			this.param = param;
		}

		@Override
		public void detach() {
			// nuthin'
		}

		@Override
		public String getObject() {
			Value value = getTarget().getConfig().getProviderConfiguration().get(param);
			if (value != null) {
				return value.as(String.class);
			}

			return null;
		}

		@Override
		public void setObject(String object) {
			getTarget().getConfig().getProviderConfiguration().put(param, Value.of(object));
		}

	}

	/**
	 * Choice renderer for content type IDs.
	 */
	private static class ContentTypeChoiceRenderer implements IChoiceRenderer<String> {

		private static final long serialVersionUID = -6542046435287452517L;

		@Override
		public Object getDisplayValue(String object) {
			return Platform.getContentTypeManager().getContentType(object).getName();
		}

		@Override
		public String getIdValue(String object, int index) {
			return object;
		}

	}

	private static final long serialVersionUID = 8904573677189598470L;

	private static final ALogger log = ALoggerFactory.getLogger(UploadAndTransForm.class);

	@SpringBean
	private EnvironmentService environmentService;

	private final String projectId;

	private final FileUploadField file;

	private NamedIOConfiguration target;

	private static final IOptionRenderer<NamedIOConfiguration> RENDERER = new IOptionRenderer<NamedIOConfiguration>() {

		private static final long serialVersionUID = 4714894437575668850L;

		@Override
		public String getDisplayValue(NamedIOConfiguration object) {
			String name = object.getName();
			if (name != null && !name.isEmpty()) {
				return name;
			}
			return object.getConfig().getProviderId();
		}

		@Override
		public IModel<NamedIOConfiguration> getModel(NamedIOConfiguration value) {
			return new Model<NamedIOConfiguration>(value);
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
		Select<NamedIOConfiguration> selectTarget = new Select<NamedIOConfiguration>("target",
				new PropertyModel<NamedIOConfiguration>(this, "target"));
		add(selectTarget);

		TransformationEnvironment env = environmentService.getEnvironment(projectId);

		// determine presets
		Collection<NamedIOConfiguration> presetList = new ArrayList<>();
		for (Entry<String, ? extends IOConfiguration> entry : env.getExportPresets().entrySet()) {
			presetList.add(new NamedIOConfiguration(entry.getKey(), entry.getValue()));
		}
		SelectOptions<NamedIOConfiguration> presets = new SelectOptions<NamedIOConfiguration>(
				"presets", presetList, RENDERER);
		selectTarget.add(presets);

		// determine valid exporters
		Collection<NamedIOConfiguration> templateList = new ArrayList<>();
		for (Entry<String, ? extends IOConfiguration> entry : env.getExportTemplates().entrySet()) {
			presetList.add(new NamedIOConfiguration(entry.getKey(), entry.getValue()));
		}
		SelectOptions<NamedIOConfiguration> exporters = new SelectOptions<NamedIOConfiguration>(
				"exporters", templateList, RENDERER);
		selectTarget.add(exporters);

		// initial selection
		if (!presetList.isEmpty()) {
			setTarget(presetList.iterator().next());
		}
		else if (!templateList.isEmpty()) {
			setTarget(templateList.iterator().next());
		}

		// panel for I/O configuration
		final WebMarkupContainer config = new WebMarkupContainer("config");
		config.setOutputMarkupId(true);
		add(config);

		IModel<List<String>> parameterModel = new LoadableDetachableModel<List<String>>() {

			private static final long serialVersionUID = 1018038661733512580L;

			@Override
			protected List<String> load() {
				Set<String> properties = new LinkedHashSet<String>();

				if (target != null && target.getConfig().getProviderId() != null) {
					// must have
					properties.add(IOProvider.PARAM_CONTENT_TYPE);

					// what is supported

					IOProvider p = HeadlessIO.loadProvider(target.getConfig());
					properties.addAll(p.getSupportedParameters());

					// not allowed
					properties.remove(ExportProvider.PARAM_TARGET);
				}

				return new ArrayList<String>(properties);
			}
		};

		ListView<String> parameterView = new ListView<String>("param", parameterModel) {

			private static final long serialVersionUID = -7838477347365823022L;

			@Override
			protected void populateItem(ListItem<String> item) {
				boolean isContentType = IOProvider.PARAM_CONTENT_TYPE.equals(item.getModelObject());

				// name
				item.add(new Label("name", item.getModelObject()));

				// text field
				TextField<String> textField = new TextField<String>("field", new FieldFieldModel(
						item.getModelObject()));
				textField.setVisible(!isContentType);
				item.add(textField);

				// contentType select field
				DropDownChoice<String> contentType;
				if (isContentType) {
					IOProviderDescriptor pf = HaleIO.findIOProviderFactory(InstanceWriter.class,
							null, getTarget().getConfig().getProviderId());
					List<String> types = new ArrayList<String>();
					for (IContentType type : pf.getSupportedTypes()) {
						types.add(type.getId());
					}
					contentType = new DropDownChoice<String>("contentType", new FieldFieldModel(
							item.getModelObject()), types, new ContentTypeChoiceRenderer());
				}
				else {
					contentType = new DropDownChoice<String>("contentType", new ArrayList<String>());
					contentType.setVisible(false);
				}
				item.add(contentType);
			}

		};
		config.add(parameterView);

		// update parameter panel on target selection change
		selectTarget.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 8004015871380712045L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// update config panel

				target.add(config);
			}

		});
	}

	/**
	 * @see Form#onSubmit()
	 */
	@Override
	protected void onSubmit() {
		List<FileUpload> uploads = file.getFileUploads();
		if (uploads == null || uploads.isEmpty()) {
			error("Please specify files to transform.");
			return;
		}

		final TransformationWorkspace workspace = new TransformationWorkspace();

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
						File file = new File(workspace.getSourceFolder(), (count++) + "_"
								+ input.getClientFileName());
						try {
							input.writeTo(file);
						} catch (IOException e) {
							throw new IllegalStateException("Unable to read uploaded source file",
									e);
						}

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
							throw new IllegalStateException("Unable to read uploaded source file",
									e);
						}
						if (reader == null) {
							throw new IllegalStateException(
									"Could not find I/O provider for source file.");
						}
						return reader;
					}
				});

		try {
			workspace.transform(projectId, readers, target.getConfig());
			setResponsePage(StatusPage.class,
					new PageParameters().add(StatusPage.PARAMETER_WORKSPACE, workspace.getId()));
		} catch (Exception e) {
			log.error("Error launching transformation process", e);
			error("Error launching transformation process");
			workspace.delete();
		}
	}

	/**
	 * @return the target
	 */
	public NamedIOConfiguration getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(NamedIOConfiguration target) {
		this.target = target;
	}

}
