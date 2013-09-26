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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.ui;

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.InspireConstants;
import eu.esdihumboldt.hale.io.gml.writer.InspireInstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.util.OpenFileFieldEditor;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Configuration page for configuring a SpatialDataSet.
 * 
 * @author Kai Schwierczek
 */
@SuppressWarnings("restriction")
public class SpatialDataSetConfigurationPage extends
		AbstractConfigurationPage<InspireInstanceWriter, IOWizard<InspireInstanceWriter>> {

	private Editor<?> localIdEditor;
	private Editor<?> namespaceEditor;
	private OpenFileFieldEditor metadataFile;

	/**
	 * Default constructor
	 */
	public SpatialDataSetConfigurationPage() {
		super("inspire.sds");

		setTitle("SpatialDataSet configuration");
		setDescription("Please configure the data set INSPIRE identifier and optionally included metadata");
		setPageComplete(false);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InspireInstanceWriter provider) {
		if (localIdEditor.isValid() && namespaceEditor.isValid() && metadataFile.isValid()) {
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_LOCALID,
					Value.of(localIdEditor.getAsText()));
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_NAMESPACE,
					Value.of(namespaceEditor.getAsText()));
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_METADATA,
					Value.of(metadataFile.getStringValue().trim()));
			return true;
		}
		else {
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_LOCALID, null);
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_NAMESPACE, null);
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_METADATA, null);
			return false;
		}
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).spacing(6, 12)
				.applyTo(page);

		QName sdsQName = new QName(InspireConstants.INSPIRE_NAMESPACE_BASETYPES, "SpatialDataSet");
		QName identifierQName = new QName(InspireConstants.INSPIRE_NAMESPACE_BASETYPES,
				"identifier");
		QName innerIdentifierQName = new QName(InspireConstants.INSPIRE_NAMESPACE_BASETYPES,
				"Identifier");
		QName localIdQName = new QName(InspireConstants.INSPIRE_NAMESPACE_BASETYPES, "localId");
		QName nsQName = new QName(InspireConstants.INSPIRE_NAMESPACE_BASETYPES, "namespace");

		// Get the property definitions of localId and namespace.
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		SchemaSpace target = ss.getSchemas(SchemaSpaceID.TARGET);
		XmlIndex index = StreamGmlWriter.getXMLIndex(target);
		TypeDefinition sdsType = index.getElements().get(sdsQName).getType();
		// If the type was not found, simply return and do not throw an
		// exception. Currently all pages are created, even if they aren't
		// applicable.
		if (sdsType == null)
			return;

		// SpatialDataSet/identifier.Identifier.
		ChildDefinition<?> identifier = sdsType.getChild(identifierQName);
		identifier = DefinitionUtil.getChild(identifier, innerIdentifierQName);
		PropertyDefinition localIdDef = DefinitionUtil.getChild(identifier, localIdQName)
				.asProperty();
		PropertyDefinition nsDef = DefinitionUtil.getChild(identifier, nsQName).asProperty();

		IPropertyChangeListener changeListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(Editor.IS_VALID)
						|| event.getProperty().equals(FileFieldEditor.IS_VALID))
					updateState();
			}
		};

		// inspire identifier
		Label inspireId = new Label(page, SWT.NONE);
		inspireId
				.setText("Please specify the local ID and the namespace as part of the INSPIRE identifier of the Spatial Data Set:");
		GridDataFactory.fillDefaults().span(2, 1).applyTo(inspireId);

		AttributeEditorFactory aef = (AttributeEditorFactory) PlatformUI.getWorkbench().getService(
				AttributeEditorFactory.class);

		Composite localIdtitle = new Composite(page, SWT.NONE);
		localIdtitle
				.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(
				DefinitionLabelFactory.class);
		dlf.createLabel(localIdtitle, localIdDef, false);
		Label label = new Label(localIdtitle, SWT.NONE);
		label.setText(" = ");

		localIdEditor = aef.createEditor(page, localIdDef, null, false);
		localIdEditor.getControl()
				.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		localIdEditor.setPropertyChangeListener(changeListener);

		Composite namespacetitle = new Composite(page, SWT.NONE);
		namespacetitle.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0)
				.create());
		dlf.createLabel(namespacetitle, nsDef, false);
		label = new Label(namespacetitle, SWT.NONE);
		label.setText(" = ");

		namespaceEditor = aef.createEditor(page, nsDef, null, false);
		namespaceEditor.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		namespaceEditor.setPropertyChangeListener(changeListener);

		// spacer
		Composite spacer = new Composite(page, SWT.NONE);
		GridDataFactory.fillDefaults().hint(0, 8).applyTo(spacer);

		// metadata file
		Label metadataLabel = new Label(page, SWT.NONE);
		metadataLabel
				.setText("You can include metadata in the Spatial Data Set from a XML file with a MD_Metadata element:");
		GridDataFactory.fillDefaults().span(2, 1).applyTo(metadataLabel);

		// source file
		Composite fileFieldComposite = new Composite(page, SWT.NONE);
		fileFieldComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		metadataFile = new OpenFileFieldEditor("metadataFile", "ISO Geographic MetaData XML", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, fileFieldComposite);
		metadataFile.setPage(this);
		metadataFile.setEmptyStringAllowed(true);
		metadataFile.setFileExtensions(new String[] { "*.xml" });
		metadataFile.setPropertyChangeListener(changeListener);
		// isValid starts with false even if emptyStringAllowed is true.
		// -> force validation hack
		metadataFile.setStringValue(" ");
		metadataFile.setStringValue("");

		updateState();
	}

	private void updateState() {
		setPageComplete(localIdEditor.isValid() && namespaceEditor.isValid()
				&& metadataFile.isValid());
	}

	/**
	 * @see DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		localIdEditor.getControl().setFocus();
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// Controls are created early, even if this page is never selected.
		if (localIdEditor == null)
			throw new IllegalStateException("SpatialDataSet type not found.");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#loadPreSelection(eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration)
	 */
	@Override
	public void loadPreSelection(IOConfiguration conf) {
		Value localId = conf.getProviderConfiguration().get(
				InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_LOCALID);
		Value namespace = conf.getProviderConfiguration().get(
				InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_NAMESPACE);
		Value metadata = conf.getProviderConfiguration().get(
				InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_METADATA);
		if (localId != null)
			localIdEditor.setAsText(localId.getStringRepresentation());
		if (namespace != null)
			namespaceEditor.setAsText(namespace.getStringRepresentation());
		if (metadata != null)
			metadataFile.setStringValue(metadata.getStringRepresentation());
	}
}
