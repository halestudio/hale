/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package test.eu.esdihumboldt.hale.models;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.Envelope2D;
import org.geotools.gui.swing.JMapPane;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ShapeLab {

	static StyleFactory styleFactory = CommonFactoryFinder
			.getStyleFactory(null);
	static FilterFactory filterFactory = CommonFactoryFinder
			.getFilterFactory(null);

    /**
     * Prompt the user for a file and open up ImageLab.
     * 
     * @param args
     *                filename of image
     */
    public static void main(String[] args) throws Exception {
        File file = getShapeFile(args);

        ShapefileDataStore shapefile = new ShapefileDataStore(file.toURL());
        String typeName = shapefile.getTypeNames()[0];
        FeatureSource featureSource = shapefile.getFeatureSource();
        FeatureType schema = featureSource.getSchema();
        CoordinateReferenceSystem crs = schema.getGeometryDescriptor()
                .getCoordinateReferenceSystem();

        MapContext map = new DefaultMapContext(crs);
        Style style = createStyle(file, schema);
        map.addLayer(featureSource, style);

        showMap(map);
    }

    @SuppressWarnings("unchecked")
	private static Style createStyle(File file, FeatureType schema) {
        File sld = toSLDFile(file);
        if (sld.exists()) {
            return createFromSLD(sld);
        }
        Class type = schema.getGeometryDescriptor().getType().getBinding();
        if (type.isAssignableFrom(Polygon.class)
                || type.isAssignableFrom(MultiPolygon.class)) {
            return createPolygonStyle();
        } else if (type.isAssignableFrom(LineString.class)
                || type.isAssignableFrom(MultiLineString.class)) {
            return createLineStyle();
        } else {
            return createPointStyle();
        }
    }

    private static Style createFromSLD(File sld) {
        SLDParser stylereader;
        try {
            stylereader = new SLDParser(styleFactory, sld.toURL());
            Style[] style = stylereader.readXML();
            return style[0];
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
	private static Style createPointStyle() {
        Style style;
        PointSymbolizer symbolizer = styleFactory.createPointSymbolizer();
        symbolizer.getGraphic().setSize(filterFactory.literal(1));
        Rule rule = styleFactory.createRule();
        rule.setSymbolizers(new Symbolizer[] { symbolizer });
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
        fts.setRules(new Rule[] { rule });
        style = styleFactory.createStyle();
        style.addFeatureTypeStyle(fts);
        return style;
    }

    @SuppressWarnings("deprecation")
	private static Style createLineStyle() {
        Style style;

        LineSymbolizer symbolizer = styleFactory.createLineSymbolizer();
        SLD.setLineColour(symbolizer, Color.BLUE);
        symbolizer.getStroke().setWidth(filterFactory.literal(1));
        symbolizer.getStroke().setColor(filterFactory.literal(Color.BLUE));

        Rule rule = styleFactory.createRule();
        rule.setSymbolizers(new Symbolizer[] { symbolizer });
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
        fts.setRules(new Rule[] { rule });
        style = styleFactory.createStyle();
        style.addFeatureTypeStyle(fts);
        return style;
    }

    @SuppressWarnings("deprecation")
	private static Style createPolygonStyle() {
		Style style;
		PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer();
		Fill fill = styleFactory.createFill(filterFactory.literal("#FFAA00"),
				filterFactory.literal(0.5));
		symbolizer.setFill(fill);
		Rule rule = styleFactory.createRule();
		rule.setSymbolizers(new Symbolizer[] { symbolizer });
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
		fts.setRules(new Rule[] { rule });
		style = styleFactory.createStyle();
		style.addFeatureTypeStyle(fts);
		return style;
	}

    private static void showMap(MapContext map) throws IOException {
        final JMapPane mapPane = new JMapPane(new StreamingRenderer(), map);
        mapPane.setMapArea(map.getLayerBounds());
        JFrame frame = new JFrame("ImageLab2");

        frame.setLayout(new BorderLayout());
        frame.add(mapPane, BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPane.setState(JMapPane.ZoomIn);
            }
        });
        buttons.add(zoomInButton);

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPane.setState(JMapPane.ZoomOut);
            }
        });
        buttons.add(zoomOutButton);

        JButton pamButton = new JButton("Move");
        pamButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapPane.setState(JMapPane.Pan);
            }
        });
        buttons.add(pamButton);

        frame.add(buttons, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private static File getShapeFile(String[] args)
            throws FileNotFoundException {
        File file;
        if (args.length == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open Shapefile for Reprojection");
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getPath().endsWith("shp")
                            || f.getPath().endsWith("SHP");
                }

                public String getDescription() {
                    return "Shapefiles";
                }
            });
            int returnVal = chooser.showOpenDialog(null);

            if (returnVal != JFileChooser.APPROVE_OPTION) {
                System.exit(0);
            }
            file = chooser.getSelectedFile();

            System.out
                    .println("You chose to open this file: " + file.getName());
        } else {
            file = new File(args[0]);
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        return file;
    }

    /** Figure out the URL for the "sld" file */
    public static File toSLDFile(File file)  {
        String filename = file.getAbsolutePath();
        if (filename.endsWith(".shp") || filename.endsWith(".dbf")
                || filename.endsWith(".shx")) {
            filename = filename.substring(0, filename.length() - 4);
            filename += ".sld";
        } else if (filename.endsWith(".SLD") || filename.endsWith(".SLD")
                || filename.endsWith(".SLD")) {
            filename = filename.substring(0, filename.length() - 4);
            filename += ".SLD";
        }
        return new File(filename);
    }

}
