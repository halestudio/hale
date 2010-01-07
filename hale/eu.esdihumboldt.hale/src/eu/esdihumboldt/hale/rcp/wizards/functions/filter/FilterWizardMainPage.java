package eu.esdihumboldt.hale.rcp.wizards.functions.filter;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;

/**
 * Filter wizard page
 * 
 * @author ?, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FilterWizardMainPage extends AbstractSingleCellWizardPage {

	private Label featureTypeLabel;
	private Text featureTypeEditor;
	private Label extentLabel;
	private Text extentXmin;
	private Text extentYmin;
	private Text extentXmax;
	private Text extentYmax;
	private Text extentSRS;
	private Label propertyLabel;
	private Label operatorsLabel;
	private Label comparisonValueLabel;
	private Text attributeValue;
	private Label beginLabel;
	private Label endLabel;
	private Label blankLabel;
	private Text intervallBegin;
	private Text intervallEnd;
	private Text selectedAttribute;
	private Text selectedOperator;
	//private Composite attrValueComposite;
	private Composite composite;
	//private Composite statComposite;
	private Text selectedGeomProperty;
	private Combo geomProperties;
	
	private static Logger _log = Logger.getLogger(FilterWizardMainPage.class);

	/**
	 * Constructor
	 * 
	 * @param pageName
	 * @param title
	 */
	protected FilterWizardMainPage(String pageName, String title) {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName);
		setDescription("Configure your CQL-Expression to proceed filter operation.");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		
		/*
		 * TODO if this page shall also handle filter editing:
		 * determine if there is already a filter present and
		 * parse the filter expression
		 */
		//Cell cell = getParent().getResultCell();

		// create a composite to hold the widgets
		this.composite = new Composite(parent, SWT.NULL);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		gl.numColumns = 5;
		composite.setLayout(gl);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		composite.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setFont(parent.getFont());

		// Feature Type area
		this.featureTypeLabel = new Label(composite, SWT.TITLE);
		this.featureTypeLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		this.featureTypeLabel.setSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		FontData labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);

		this.featureTypeLabel.setFont(new Font(parent.getDisplay(),
				labelFontData));

		this.featureTypeLabel.setText("FeatureType: ");
		// this.featureTypeEditor = new Text(composite, SWT.BORDER | SWT.WRAP|
		// SWT.MULTI |SWT.V_SCROLL);
		this.featureTypeEditor = new Text(composite, SWT.BORDER);
		
		this.featureTypeEditor.setText(getParent().getSourceItem().getName().getLocalPart());
		featureTypeEditor.setEnabled(false);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		/*
		 * gd.verticalAlignment = SWT.FILL; gd.grabExcessVerticalSpace = true;
		 */
		gd.horizontalSpan = 4;
		this.featureTypeEditor.setLayoutData(gd);

		// add listener to update the source feature name
		this.featureTypeEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String sourceName = featureTypeEditor.getText();
				_log.debug("Source Feature Type " + sourceName);
				/*
				 * if(sourceName.length() == 0)
				 * setErrorMessage("FeatureType  can not be empty"); else
				 * setErrorMessage(null); setPageComplete(sourceName.length() >
				 * 0);
				 */
				setPageComplete(true);

			}

		});
		this.extentLabel = new Label(composite, SWT.TITLE);
		this.extentLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		this.extentLabel.setSize(composite
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		this.extentLabel.setFont(new Font(parent.getDisplay(), labelFontData));

		this.extentLabel.setText("Extent: ");
		// Xmin,Ymin,Xmax, Ymax area
		this.extentXmin = new Text(composite, SWT.BORDER);
		this.extentXmin.setText("X_MIN");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		/*
		 * gd.verticalAlignment = SWT.FILL; gd.grabExcessVerticalSpace = true;
		 */
		this.extentXmin.setLayoutData(gd);
		// add listener to update XMIN
		this.extentXmin.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String x_min = extentXmin.getText();
				_log.debug("Extent x_min " + x_min);
				setPageComplete(true);
			}
		});

		this.extentYmin = new Text(composite, SWT.BORDER);
		this.extentYmin.setText("Y_MIN");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		/*
		 * gd.verticalAlignment = SWT.FILL; gd.grabExcessVerticalSpace = true;
		 */
		this.extentYmin.setLayoutData(gd);

		// add listener to update YMIN
		this.extentYmin.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String y_min = extentYmin.getText();
				_log.debug("Extent y_min " + y_min);
				setPageComplete(true);
			}
		});

		this.extentXmax = new Text(composite, SWT.BORDER);
		this.extentXmax.setText("X_MAX");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		/*
		 * gd.verticalAlignment = SWT.FILL; gd.grabExcessVerticalSpace = true;
		 */
		this.extentXmax.setLayoutData(gd);

		// add listener to update XMAX
		this.extentXmax.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String x_max = extentXmax.getText();
				_log.debug("Extent x_max " + x_max);
				setPageComplete(true);
			}
		});

		this.extentYmax = new Text(composite, SWT.BORDER);
		this.extentYmax.setText("Y_MAX");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		/*
		 * gd.verticalAlignment = SWT.FILL; gd.grabExcessVerticalSpace = true;
		 */
		this.extentYmax.setLayoutData(gd);
		// add listener to update YMAX
		this.extentYmax.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String y_max = extentYmax.getText();
				_log.debug("Extent y_max " + y_max);
				setPageComplete(true);
			}
		});

    	final Label placeHolder = new Label(composite, SWT.TITLE);
		placeHolder.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		
		
		placeHolder.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		//combo for the geometry:attributes
		
		this.geomProperties = new Combo(composite, SWT.NULL);
		selectedGeomProperty = new Text(geomProperties, SWT.NULL);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		//gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		this.geomProperties.setLayoutData(gd);
		this.geomProperties.setText("select geometry property");
		// read attributes from the schema service
		SchemaItem sourceItem = getParent().getSourceItem();
		if (sourceItem.hasChildren()) {
			for (SchemaItem child : sourceItem.getChildren()) {
				if (child.isAttribute()) {
					String name = child.getName().getLocalPart();
		            //TODO how define an attribute type having a name
		            if (name.contains("geom")) {	
		            	this.geomProperties.add(name);
		            }
				}
			}
		}
		// add listener to select attribute
		this.geomProperties.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int selectionIndex = geomProperties.getSelectionIndex();
				_log.debug("Selected Property: "
						+ geomProperties.getItem(selectionIndex));

				selectedGeomProperty.setText(geomProperties
						.getItem(selectionIndex));

			}

		});
		
		
		this.extentSRS = new Text(composite, SWT.BORDER);
		this.extentSRS.setText("SRS");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 2;
		//gd.grabExcessHorizontalSpace = true;
		
		this.extentSRS.setLayoutData(gd);
		// add listener to update SRS
		this.extentSRS.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String srs = extentSRS.getText();
				_log.debug("Extent srs " + srs);
				setPageComplete(true);
			}
		});

		// by properties
		this.propertyLabel = new Label(composite, SWT.TITLE);
		this.propertyLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		this.propertyLabel.setSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		this.propertyLabel
				.setFont(new Font(parent.getDisplay(), labelFontData));
		this.propertyLabel.setText("By Property: ");
		final Combo attributesCombo = new Combo(composite, SWT.NULL);
		selectedAttribute = new Text(attributesCombo, SWT.NULL);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 4;
		attributesCombo.setLayoutData(gd);
		attributesCombo.setText("select attribute");
		// read attributes from the schema service
		if (sourceItem.hasChildren()) {
			for (SchemaItem child : sourceItem.getChildren()) {
				if (child.isAttribute()) {
					String name = child.getName().getLocalPart();
					attributesCombo.add(name);
				}
			}
		}
		// add listener to select attribute
		attributesCombo.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int selectionIndex = attributesCombo.getSelectionIndex();
				_log.debug("Selected Property: "
						+ attributesCombo.getItem(selectionIndex));

				selectedAttribute.setText(attributesCombo
						.getItem(selectionIndex));

			}

		});
		// operators
		this.operatorsLabel = new Label(composite, SWT.TITLE);
		this.operatorsLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		this.operatorsLabel.setSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		this.operatorsLabel
				.setFont(new Font(parent.getDisplay(), labelFontData));
		this.operatorsLabel.setText("OperatorType: ");
		final Combo operatorsCombo = new Combo(composite, SWT.NULL);
		selectedOperator = new Text(operatorsCombo, SWT.NULL);
		operatorsCombo.setLayoutData(gd);
		operatorsCombo.setText("select Operator Type");
		// TODO read attributes from the schema service
		String[] operators = new String[] { "PropertyIsEqualTo",
				"PropertyIsNotEqualTo", "PropertyIsLessThan",
				"PropertyIsGreaterThan", "PropertyIsLessThanOrEqualTo",
				"PropertyIsGreaterThanOrEqualTo", "PropertyIsLike",
				"PropertyIsNull", "PropertyIsBetween" };
		for (int i = 0; i < operators.length; i++) {
			operatorsCombo.add(operators[i]);
		}

		// add listener to select operator
		operatorsCombo.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// dispose attributevalue field and its label
				disposeValueFields();
				
				int selectionIndex = operatorsCombo.getSelectionIndex();
				_log.debug("Selected Operator: "
						+ operatorsCombo.getItem(selectionIndex));
				selectedOperator
						.setText(operatorsCombo.getItem(selectionIndex));
				switch (CQLOperators.valueOf(operatorsCombo
						.getItem(selectionIndex))) {
				case PropertyIsNull:
					// IS NUL: no comparison value needed
					break;

				case PropertyIsBetween:
					blankLabel = new Label(composite,SWT.TITLE);
					/*beginLabel = new Label(composite, SWT.TITLE);
					beginLabel.setLayoutData(new GridData(
							GridData.VERTICAL_ALIGN_FILL
									| GridData.HORIZONTAL_ALIGN_FILL));
					beginLabel.setSize(composite.computeSize(
							SWT.DEFAULT, SWT.DEFAULT));
					FontData labelFontData = composite.getParent().getFont()
							.getFontData()[0];
					labelFontData.setStyle(SWT.BOLD);

					beginLabel.setFont(new Font(composite.getParent()
							.getDisplay(), labelFontData));

					beginLabel.setText("Origin Value: ");
					// this.featureTypeEditor = new Text(composite,SWT.BORDER |
					// SWT.WRAP| SWT.MULTI |SWT.V_SCROLL );
*/					intervallBegin = new Text(composite, SWT.BORDER);

					// TODO replace it with the selected source FeatureType
					// value
					intervallBegin.setText("Begin");
					GridData gd = new GridData();
					gd.horizontalAlignment = SWT.FILL;
					gd.grabExcessHorizontalSpace = true;
					/*
					 * gd.verticalAlignment = SWT.FILL;
					 * gd.grabExcessVerticalSpace = true;
					 */
					gd.horizontalSpan = 2;
					intervallBegin.setLayoutData(gd);
					// add listener to update the comparison value
					intervallBegin.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent event) {
							String comparison = intervallBegin.getText();
							_log.debug("Origin of the Interval " + comparison);

							/*
							 * if(sourceName.length() == 0)
							 * setErrorMessage("FeatureType  can not be empty");
							 * else setErrorMessage(null);
							 * setPageComplete(sourceName.length() > 0);
							 */
							setPageComplete(true);

						}

					});
				/*	endLabel = new Label(composite, SWT.TITLE);
					endLabel.setLayoutData(new GridData(
							GridData.VERTICAL_ALIGN_FILL
									| GridData.HORIZONTAL_ALIGN_FILL));
					endLabel.setSize(composite.computeSize(
							SWT.DEFAULT, SWT.DEFAULT));
					labelFontData = composite.getParent().getFont()
							.getFontData()[0];
					labelFontData.setStyle(SWT.BOLD);

					endLabel.setFont(new Font(composite.getParent()
							.getDisplay(), labelFontData));

					endLabel.setText("End Value: ");
					// this.featureTypeEditor = new Text(composite,SWT.BORDER |
					// SWT.WRAP| SWT.MULTI |SWT.V_SCROLL );
*/					intervallEnd = new Text(composite, SWT.BORDER);

					// TODO replace it with the selected source FeatureType
					// value
					intervallEnd.setText("End");
					gd = new GridData();
					gd.horizontalAlignment = SWT.FILL;
					gd.grabExcessHorizontalSpace = true;
					/*
					 * gd.verticalAlignment = SWT.FILL;
					 * gd.grabExcessVerticalSpace = true;
					 */
					gd.horizontalSpan = 2;
					intervallEnd.setLayoutData(gd);
					// add listener to update the comparison value
					intervallEnd.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent event) {
							String comparison = intervallEnd.getText();
							_log.debug("End of the interval " + comparison);

							/*
							 * if(sourceName.length() == 0)
							 * setErrorMessage("FeatureType  can not be empty");
							 * else setErrorMessage(null);
							 * setPageComplete(sourceName.length() > 0);
							 */
							setPageComplete(true);

						}

					});
					composite.layout(true, true);
					break;
				default:
					comparisonValueLabel = new Label(composite, SWT.TITLE);
					comparisonValueLabel.setLayoutData(new GridData(
							GridData.VERTICAL_ALIGN_FILL
									| GridData.HORIZONTAL_ALIGN_FILL));
					comparisonValueLabel.setSize(composite.computeSize(
							SWT.DEFAULT, SWT.DEFAULT));
					FontData labelFontData = composite.getParent().getFont()
							.getFontData()[0];
					labelFontData.setStyle(SWT.BOLD);

					comparisonValueLabel.setFont(new Font(composite.getParent()
							.getDisplay(), labelFontData));

					comparisonValueLabel.setText("Value: ");
					// this.featureTypeEditor = new Text(composite,SWT.BORDER |
					// SWT.WRAP| SWT.MULTI |SWT.V_SCROLL );
					attributeValue = new Text(composite, SWT.BORDER);

					// TODO replace it with the selected source FeatureType
					// value
					attributeValue.setText("Value");
					gd = new GridData();
					gd.horizontalAlignment = SWT.FILL;
					gd.grabExcessHorizontalSpace = true;
					/*
					 * gd.verticalAlignment = SWT.FILL;
					 * gd.grabExcessVerticalSpace = true;
					 */
					gd.horizontalSpan = 4;
					attributeValue.setLayoutData(gd);
					// add listener to update the comparison value
					attributeValue.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent event) {
							String comparison = attributeValue.getText();
							_log.debug("Comparison value " + comparison);

							/*
							 * if(sourceName.length() == 0)
							 * setErrorMessage("FeatureType  can not be empty");
							 * else setErrorMessage(null);
							 * setPageComplete(sourceName.length() > 0);
							 */
							setPageComplete(true);

						}

					});
					composite.layout(true, true);
				}
				
			}

			/**
			 * disposed all comparison fields.
			 */
			private void disposeValueFields() {
				if (attributeValue != null)attributeValue.dispose();
				if (comparisonValueLabel != null)comparisonValueLabel.dispose();
				if (intervallBegin != null)intervallBegin.dispose();
				if (intervallEnd != null) intervallEnd.dispose();
				if (beginLabel != null) beginLabel.dispose();
				if (endLabel != null) endLabel.dispose();
				if (blankLabel != null) blankLabel.dispose();
				
			}

		});

		setErrorMessage(null); // should not initially have error message
		super.setControl(composite);

	}

	/**
	 * 
	 * @return CQL expression based on the pageinput.
	 */
	public String buildCQL() {
		String CQLexpression = "";
		
		//1. Bild CQL using BBOX
		
		CQLexpression = buildBBOX();
		
		if (!CQLexpression.equals(""))
			CQLexpression+= " AND ";
		//2. build using property, comparison operator and comparison value

		// get attribute name - String between ::
		String fullPropertyName = selectedAttribute.getText();
		int firstIndexOfColon = fullPropertyName.indexOf(":");
		// String attributeValue = attributeValue.getText();
		String propertyLocalName = fullPropertyName
				.substring(firstIndexOfColon + 1);
		// if
		// ((selectedOperator.getText()).equals(CQLOperators.PropertyIsLike.name()))
		// attributeValue = "'" + attributeValue + "'";
		CQLexpression = propertyLocalName
				+ " "
				+ getCQLOperator(CQLOperators.valueOf(selectedOperator
						.getText()));
		_log.debug("CQL Expression " + CQLexpression);

		return CQLexpression;
	}

	/**
	 * Build the bounding box string
	 * 
	 * @return the bounding box string
	 */
	private String buildBBOX() {
		String BBOXCQL = "BBOX(" + selectedGeomProperty.getText() + "," + extentXmin.getText()+ "," + extentYmin.getText()+"," + extentXmax.getText()+  ","+ extentYmax.getText();
		//SRS Attribute is optional
	    if (!extentSRS.equals("")) BBOXCQL = BBOXCQL + ","+ extentSRS.getText();
		BBOXCQL += ")";
		return BBOXCQL;
		
	}

	/**
	 * Get the operator string from the given operator
	 * 
	 * @param operator the operator
	 * @return the operator string
	 */
	private String getCQLOperator(CQLOperators operator) {
		// The full operator list can be found at
		String cqlOperator = "";
		switch (operator) {
		case PropertyIsEqualTo:
			cqlOperator = "== " + attributeValue.getText();
			break;
		case PropertyIsNotEqualTo:
			cqlOperator = "<> " + attributeValue.getText();
			break;
		case PropertyIsLessThan:
			cqlOperator = "< " + attributeValue.getText();
			break;
		case PropertyIsGreaterThan:
			cqlOperator = "> " + attributeValue.getText();
			break;
		case PropertyIsLessThanOrEqualTo:
			cqlOperator = "<= " + attributeValue.getText();
			break;
		case PropertyIsGreaterThanOrEqualTo:
			cqlOperator = ">= " + attributeValue.getText();
			break;
		case PropertyIsLike:
			cqlOperator = "LIKE" + "'" + attributeValue.getText() + "'";
			break;
		case PropertyIsNull:
			cqlOperator = "IS NULL";
			break;
		case PropertyIsBetween:
			cqlOperator = "BETWEEN " + intervallBegin.getText() + " AND "
					+ intervallEnd.getText();
		}

		return cqlOperator;
	}

	/**
	 * enum contains allowed CQL operators for the CST Filter CstFunction
	 */
	@SuppressWarnings("all")
	public enum CQLOperators {
		PropertyIsEqualTo, PropertyIsNotEqualTo, PropertyIsLessThan, PropertyIsGreaterThan, PropertyIsLessThanOrEqualTo, PropertyIsGreaterThanOrEqualTo, PropertyIsLike, PropertyIsNull, PropertyIsBetween
	}

}
