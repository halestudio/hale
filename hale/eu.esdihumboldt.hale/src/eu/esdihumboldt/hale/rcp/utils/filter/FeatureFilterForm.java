/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.filter;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
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
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Form for creating a CQL filter based on a feature type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureFilterForm extends Composite {
	
	private static Logger _log = Logger.getLogger(FeatureFilterForm.class);

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
	private Text selectedGeomProperty;
	private Combo geomProperties;
	
	/**
	 * Create a new feature filter form
	 * 
	 * @param featureType the feature type
	 * @param parent the parent composite
	 * @param style the composite style
	 */
	public FeatureFilterForm(FeatureType featureType, Composite parent, int style) {
		super(parent, style);
		
		SortedSet<String> attributeNames = new TreeSet<String>();
		Collection<PropertyDescriptor> properties = featureType.getDescriptors();
		if (properties != null) {
			for (PropertyDescriptor property : properties) {
				String name = property.getName().getLocalPart();
	            attributeNames.add(name);
			}
		}
		
		// create layout
		GridLayout gl = new GridLayout();
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		gl.numColumns = 5;
		setLayout(gl);
		
		// Feature Type area
		featureTypeLabel = new Label(this, SWT.TITLE);
		featureTypeLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		FontData labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);

		featureTypeLabel.setFont(new Font(parent.getDisplay(),
				labelFontData));

		featureTypeLabel.setText("FeatureType: ");
		featureTypeEditor = new Text(this, SWT.BORDER);
		
		featureTypeEditor.setText(featureType.getName().getLocalPart());
		featureTypeEditor.setEnabled(false);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 4;
		featureTypeEditor.setLayoutData(gd);

		extentLabel = new Label(this, SWT.TITLE);
		extentLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		extentLabel.setSize(this
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		extentLabel.setFont(new Font(parent.getDisplay(), labelFontData));

		extentLabel.setText("Extent: ");
		// Xmin,Ymin,Xmax, Ymax area
		extentXmin = new Text(this, SWT.BORDER);
		extentXmin.setText("X_MIN");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		extentXmin.setLayoutData(gd);
		
		extentYmin = new Text(this, SWT.BORDER);
		extentYmin.setText("Y_MIN");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		extentYmin.setLayoutData(gd);

		extentXmax = new Text(this, SWT.BORDER);
		extentXmax.setText("X_MAX");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		extentXmax.setLayoutData(gd);

		extentYmax = new Text(this, SWT.BORDER);
		extentYmax.setText("Y_MAX");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		extentYmax.setLayoutData(gd);
		
    	final Label placeHolder = new Label(this, SWT.TITLE);
		placeHolder.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		
		//combo for the geometry:attributes
		geomProperties = new Combo(this, SWT.NULL);
		selectedGeomProperty = new Text(geomProperties, SWT.NULL);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		//gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		this.geomProperties.setLayoutData(gd);
		this.geomProperties.setText("select geometry property");
		// read attributes from the schema service
		for (String name : attributeNames) {
			//TODO determine geometry property types
			PropertyDescriptor property = featureType.getDescriptor(name);
			if (property != null && Geometry.class.isAssignableFrom(property.getType().getBinding())) {
				geomProperties.add(name);
			}
			else if (name.contains("geom")) {	
            	geomProperties.add(name);
            }
		}
		// add listener to select attribute
		geomProperties.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int selectionIndex = geomProperties.getSelectionIndex();
				_log.debug("Selected Property: "
						+ geomProperties.getItem(selectionIndex));

				selectedGeomProperty.setText(geomProperties
						.getItem(selectionIndex));

			}

		});
		
		
		extentSRS = new Text(this, SWT.BORDER);
		extentSRS.setText("SRS");
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 2;
		extentSRS.setLayoutData(gd);
		
		// by properties
		propertyLabel = new Label(this, SWT.TITLE);
		propertyLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		propertyLabel
				.setFont(new Font(parent.getDisplay(), labelFontData));
		propertyLabel.setText("By Property: ");
		final Combo attributesCombo = new Combo(this, SWT.NULL);
		selectedAttribute = new Text(attributesCombo, SWT.NULL);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 4;
		attributesCombo.setLayoutData(gd);
		attributesCombo.setText("select attribute");
		// read attributes from the schema service
		for (String name : attributeNames) {
			attributesCombo.add(name);
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
		operatorsLabel = new Label(this, SWT.TITLE);
		operatorsLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		operatorsLabel
				.setFont(new Font(parent.getDisplay(), labelFontData));
		operatorsLabel.setText("OperatorType: ");
		final Combo operatorsCombo = new Combo(this, SWT.NULL);
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
					blankLabel = new Label(FeatureFilterForm.this, SWT.TITLE);
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
*/					intervallBegin = new Text(FeatureFilterForm.this, SWT.BORDER);

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
*/					intervallEnd = new Text(FeatureFilterForm.this, SWT.BORDER);

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
					FeatureFilterForm.this.layout(true, true);
					break;
				default:
					comparisonValueLabel = new Label(FeatureFilterForm.this, SWT.TITLE);
					comparisonValueLabel.setLayoutData(new GridData(
							GridData.VERTICAL_ALIGN_FILL
									| GridData.HORIZONTAL_ALIGN_FILL));
					FontData labelFontData = FeatureFilterForm.this.getParent().getFont()
							.getFontData()[0];
					labelFontData.setStyle(SWT.BOLD);

					comparisonValueLabel.setFont(new Font(FeatureFilterForm.this.getParent()
							.getDisplay(), labelFontData));

					comparisonValueLabel.setText("Value: ");
					// this.featureTypeEditor = new Text(composite,SWT.BORDER |
					// SWT.WRAP| SWT.MULTI |SWT.V_SCROLL );
					attributeValue = new Text(FeatureFilterForm.this, SWT.BORDER);

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
					FeatureFilterForm.this.layout(true, true);
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
	    if (!extentSRS.getText().equals("")) BBOXCQL = BBOXCQL + ","+ extentSRS.getText();
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
