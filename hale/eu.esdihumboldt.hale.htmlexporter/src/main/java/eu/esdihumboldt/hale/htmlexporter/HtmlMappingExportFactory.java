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
package eu.esdihumboldt.hale.htmlexporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.omwg.ComposedFeatureClass;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.rcp.views.mappingGraph.MappingGraphView;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportException;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportProvider;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Export a Mapping to HTML for documentation purposes.
 * 
 * @author Stefan Gessner
 * @version $Id$
 */
public class HtmlMappingExportFactory implements MappingExportProvider {
	
	/**
	 * The context which gets written in the template file
	 */
	private VelocityContext context;
	
	/**
	 * The alignment
	 */
	private Alignment alignment = null;

	/**
	 * Set the PictureNames
	 */
	private String pictureNames = "image";
	
	/**
	 * Contains ICells from type retype
	 */
	private List<ICell> retypes = new ArrayList<ICell>();
	
	/**
	 * Contains ICells from type transformation
	 */
	private List<ICell> transformations = new ArrayList<ICell>();
	
	/**
	 * Contains ICells from type augmentation
	 */
	private List<ICell> augmentations = new ArrayList<ICell>();
	
	/**
	 * 
	 */
	private Collection<SchemaElement> sourceSchema;
	
	/**
	 * 
	 */
	private Collection<SchemaElement> targetSchema;

	/**
	 * @see MappingExportProvider#export(Alignment, String, Collection, Collection)
	 */
	@Override
	public MappingExportReport export(final Alignment alignment, final String path,
			Collection<SchemaElement> sourceSchema,
			Collection<SchemaElement> targetSchema)
			throws MappingExportException{
		
		this.alignment = alignment;
		this.sourceSchema = sourceSchema;
		this.targetSchema = targetSchema;
		
		String[] pathSpilt = path.split("\\\\");
		path.replace(pathSpilt[pathSpilt.length-1] , "");
		
		//Sort the alignment
		this.sortAlignment();
		
		final String filesSubDir = FilenameUtils.removeExtension(FilenameUtils.getName(path)) + "_files";
		final File filesDir = new File(FilenameUtils.getFullPath(path), filesSubDir);
		
		//Create the images of the cells
		if (Display.getCurrent() != null) {
			new MappingGraphView(alignment, this.makeSections(), this.pictureNames, filesDir);
		}
		else {
			Display display =  PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {
				
				@Override
				public void run() {
					new MappingGraphView(alignment, makeSections(), pictureNames, filesDir);
				}
			});
		}
		
		StringWriter stringWriter = new StringWriter();
		this.context = new VelocityContext();
		
		//Gets the path to the template file and style sheet
		URL templatePath = this.getClass().getResource("template.html"); 
		URL cssPath = this.getClass().getResource("style.css"); 
		URL headlinePath = this.getClass().getResource("bg-headline.png"); 
		URL linkPath = this.getClass().getResource("int_link.png"); 
		
		//generates a byteArray out of the template
		byte[] templateByteArray = null;
		try {
			templateByteArray = this.urlToByteArray(templatePath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		//creates the temporary file
		File tempFile = null;
		try {
			tempFile = File.createTempFile("template", ".vm");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//writes the byteArray from the template into the temporary file
		try {
			this.byteArrayToFile(tempFile, templateByteArray);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//Set temporary template path
		String tempPath = tempFile.getPath().replace(tempFile.getName(), "");
		Velocity.setProperty("file.resource.loader.path", tempPath);
			try {
				//Initiate Velocity
				Velocity.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Fill the context-variables with data
			try {
				this.fillContext(filesSubDir);
			} catch (MalformedURLException e1) {
				throw new RuntimeException(e1);
			}
			Template template = null;
			
			try {
				//Load template
				if(tempFile!=null){
					//FIXME URLResourceLoader is not working. So the file can't
					//be loaded directly in the bundle. 
					//It has to be temporary saved to use it.
					template = Velocity.getTemplate(tempFile.getName());
				}
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				//Merge Content with template
				template.merge(this.context,stringWriter);
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (MethodInvocationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Create HTML export file
			 File htmlOutputFile = new File(path);
			 try {
				this.stringWriterToFile(htmlOutputFile, stringWriter);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//generates a byteArray out of the style sheet
			byte[] cssByteArray = null;
			try {
				cssByteArray = this.urlToByteArray(cssPath);
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			//Create CSS export file
			File cssOutputFile = new File(filesDir, "style.css");
			 try {
				this.byteArrayToFile(cssOutputFile, cssByteArray);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//generates a byteArray out of the headline picture
			byte[] headlineByteArray = null;
			try {
				headlineByteArray = this.urlToByteArray(headlinePath);
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			//Create headline picture
			
			File headlineOutputFile = new File(filesDir, "bg-headline.png");
			 try {
				this.byteArrayToFile(headlineOutputFile, headlineByteArray);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//generates a byteArray out of the link picture
			byte[] linkByteArray = null;
			try {
				linkByteArray = this.urlToByteArray(linkPath);
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			//Create link picture
			
			File linkOutputFile = new File(filesDir, "int_link.png");
			 try {
				this.byteArrayToFile(linkOutputFile, linkByteArray);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//delete tempFile for cleanup
			tempFile.deleteOnExit();
			
			return null;
	}
	
	/**
	 * Sorts the alignment into Retypes, Transformations and Augmentations
	 */
	private void sortAlignment(){
		for (Iterator<ICell> iterator = this.alignment.getMap().iterator();iterator.hasNext();) {
			ICell cell = iterator.next();
			
			//Retype
			String cellName;
			if (cell.getEntity1().getTransformation() == null) {
				cellName = cell.getEntity2()
						.getTransformation().getService()
						.getLocation();
			} else {
				cellName = cell.getEntity1()
						.getTransformation().getService()
						.getLocation();
			}
			String[] tempSplit = cellName.split("\\.");
			String graphConnectionNodeName = tempSplit[tempSplit.length - 1];
			if(graphConnectionNodeName.equals("RenameFeatureFunction")){
				this.retypes.add(cell);
			}
			
			//Augmentation
			if(cell.getEntity1().getTransformation() == null || cell.getEntity1().getAbout().getAbout().equals("entity/null")){
				this.augmentations.add(cell);
			}

			//Transformation
			if(cell.getEntity1().getTransformation() != null){
				this.transformations.add(cell);
			}
		}	
	}
	
	/**
	 * Is looking for all appropriate ICells for the Retypes
	 * @return Vector with Vector which contains ICells
	 */
	private Vector<Vector<ICell>> makeSections(){
		Vector<Vector<ICell>> sectionVector = new Vector<Vector<ICell>>();	
		for(ICell retypeCell : this.retypes){
			Vector<ICell> icellVector = new Vector<ICell>();
			String[] retypeTargetName = this.entityNameSplitter(retypeCell.getEntity2());
			/**
			 * TRANSFORMATIONS
			 * Is looking for all appropriate Transformations
			 */
			for(ICell transformationCell : this.transformations){
				if(transformationCell.getEntity2().getAbout().getAbout().contains(retypeTargetName[0])){
					icellVector.addElement(transformationCell);
				}
			}
			
			/**
			 * AUGMENTATIONS
			 * Is looking for all appropriate Augmentations
			 */
			for(ICell augmentationCell : this.augmentations){
				if(augmentationCell.getEntity2().getAbout().getAbout().contains(retypeTargetName[0])){
					icellVector.addElement(augmentationCell);
				}
			}	
			sectionVector.addElement(icellVector);
		}
		return sectionVector;
	}
	
	 /**
	 * Create context-variables and fills them with data
	 * @param filesSubDir the sub-directory where the files reside
	 * @throws MalformedURLException 
	 */
	private void fillContext(String filesSubDir) throws MalformedURLException {
		Date date = new Date();
		SimpleDateFormat dfm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
		
		//Head informations
		this.context.put("title", "Mapping Documentation");
		this.context.put("headline", "<h1>Mapping Documentation</h1>");
		this.context.put("author", "Project Author : " + System.getProperty("user.name"));
		this.context.put("project", "Project Name : " + ((ps != null && ps.getProjectName() != null)?(ps.getProjectName()):("")));
		this.context.put("exportdate", "Export Date : "+ dfm.format(date));
		this.context.put("haleversion", "Hale Version : " + ((ps != null)?(ps.getHaleVersion()):("unknown")));
		
		this.context.put("sourceschema", "Source Schema Information : ");
		this.context.put("sourceformalism", "<li>Formalism : "+this.alignment.getSchema1().getFormalism().getName()
				+"  <img src='" + filesSubDir +"/int_link.png' alt='linkpicture'><a href='"+this.alignment.getSchema1().getFormalism().getLocation().toURL()+"'>"+this.alignment.getSchema1().getFormalism().getLocation().toString()+"</a></li>");
		this.context.put("sourcenamespace", "<li>Namespace : "+this.alignment.getSchema1().getAbout().getAbout()+"</li>");
		this.context.put("sourcelocation", "<li>Schema Location : "+this.alignment.getSchema1().getLocation()+"</li>");
		
		this.context.put("targetschema", "Target Schema Information : ");
		this.context.put("targetformalism", "<li>Formalism : "+this.alignment.getSchema2().getFormalism().getName()
				+"  <img src='" + filesSubDir + "/int_link.png' alt='linkpicture'><a href='"+this.alignment.getSchema2().getFormalism().getLocation().toURL()+"'>"+this.alignment.getSchema2().getFormalism().getLocation().toString()+"</a></li>");
		this.context.put("targetnamespace", "<li>Namespace : "+this.alignment.getSchema2().getAbout().getAbout()+"</li>");
		this.context.put("targetlocation", "<li>Schema Location : "+this.alignment.getSchema2().getLocation()+"</li>");
		
		this.context.put("filesDir", filesSubDir);
		
		//Link-generator
		Vector<String> linkListVector = new Vector<String>();
		//link counter
		int j=1;
		for (Iterator<ICell> iterator = this.retypes.iterator();iterator.hasNext();) {
			ICell cell = iterator.next();
			String[] temp = this.entityNameSplitter(cell.getEntity2());
			linkListVector.addElement("<li><img src='" + filesSubDir + "/int_link.png' alt='linkpicture'><a href='#link"+j+"'>"+temp[0]+"</a></li>");
			j++;
		}
		this.context.put("linklist", linkListVector);
		
		//Overview picture
		this.context.put("overviewpicture", "<img src='" + filesSubDir + "/" +this.pictureNames+"_Overview"+".png'alt='Overview'>");
		
		Vector<Vector<String>> cellListVector = new Vector<Vector<String>>();
		Vector<String> cellVector;
		
		/**
		 * Runs through all target FeatureTypes
		 */
		//FeatureClass Counter
		int e = 1;
		//Cell Counter
		int i = 1;
		for(ICell retypeCell : this.retypes){
			/**
			 * RETYPE
			 */
			String[] retypeSourceName = this.entityNameSplitter(retypeCell.getEntity1());
			String[] retypeTargetName = this.entityNameSplitter(retypeCell.getEntity2());
			cellVector = new Vector<String>();			
			//Link
			cellVector.addElement("<a name='link"+e+"'></a>");
			//Headline
			cellVector.addElement("<h2>"+retypeTargetName[0]+"</h2>");
			//Image
			cellVector.addElement("<img src='" + filesSubDir + "/" +this.pictureNames+"_Section_"+(e-1)+".png' alt='picture'>");
			//Header
			cellVector.addElement("<h3>Retypes</h3>");
			cellVector.addElement("<h4>"+retypeSourceName[0]+" to "+retypeTargetName[0]+"</h4>");
			cellVector.addElement("Entity 1 : "+retypeSourceName[0]);
			cellVector.addElement("Entity 2 : "+retypeTargetName[0]);
			
			//Filters
			this.getFilters(cellVector, retypeCell);
			
			//Parameters
			this.getParameters(cellVector, retypeCell);
			
			cellListVector.addElement(cellVector);	
			
			/**
			 * TRANSFORMATIONS
			 * Is looking for all appropriate Transformations
			 */
			cellVector = new Vector<String>();	
			boolean transformationHeader = true;
			for(ICell transformationCell : this.transformations){
				if(transformationCell.getEntity2().getAbout().getAbout().contains(retypeTargetName[0])){
					String[] entity1Name = this.entityNameSplitter(transformationCell.getEntity1());
					entity1Name[entity1Name.length-1] = entity1Name[entity1Name.length-1].replace(";", " --> ");
					String[] entity2Name = this.entityNameSplitter(transformationCell.getEntity2());
					entity2Name[entity2Name.length-1] = entity2Name[entity2Name.length-1].replace(";", " --> ");
					
					String functioncellName;
					if (transformationCell.getEntity1().getTransformation() == null) {
						functioncellName = transformationCell.getEntity2()
								.getTransformation().getService()
								.getLocation();
					} else {
						functioncellName = transformationCell.getEntity1()
								.getTransformation().getService()
								.getLocation();
					}
					String[] tempSplit = functioncellName.split("\\.");
					String functionName = tempSplit[tempSplit.length - 1];
					
					//Header
					if(transformationHeader){
						Vector<String> transformationcellVector = new Vector<String>();
						transformationcellVector.addElement("<h3>Transformations</h3>");
						cellListVector.addElement(transformationcellVector);	
						transformationHeader = false;
					}
					cellVector.addElement("<h4>Cell "+i+" : </h4>");
					cellVector.addElement("Function : "+functionName);
					
					//entity1
					if (transformationCell.getEntity1() instanceof ComposedProperty) {
						cellVector.addElement("Entity 1 : ComposedProperty");
						for(int z=0; z < entity1Name.length; z++){
							cellVector.addElement("<ul><li>Entity 1."+z+" : "+retypeSourceName[0]+" / "+entity1Name[z]+"</li></ul>");
						}
					}
					else{
						cellVector.addElement("Entity 1 : "+retypeSourceName[0]+" / "+entity1Name[0]);
					}
					
					//entity2
					if (transformationCell.getEntity2() instanceof ComposedProperty) {
						cellVector.addElement("Entity 2 : ComposedProperty");
						for(int z=0; z < entity2Name.length; z++){
							cellVector.addElement("<ul><li>Entity 2."+z+" : "+retypeTargetName[0]+" / "+entity2Name[z]+"</li></ul>");
						}
					}
					else{
						cellVector.addElement("Entity 2 : "+retypeTargetName[0]+" / "+entity2Name[0]);
					}
					
					//Image
//					cellVector.addElement("<img src='"+this.pictureNames+(i-1)+".png'>");
					
					//Filters
					this.getFilters(cellVector, transformationCell);
					
					//Parameters
					this.getParameters(cellVector, transformationCell);
					
					//Spaceholder
					cellVector.addElement("&emsp;");
					
					i++;
				}
			}
			cellListVector.addElement(cellVector);	
			
			/**
			 * AUGMENTATIONS
			 * Is looking for all appropriate Augmentations
			 */
			String superTypeName="";
			for (Iterator<SchemaElement> iterator = this.targetSchema.iterator();iterator.hasNext();) {
				SchemaElement schemaElement = iterator.next();
				if(schemaElement.getIdentifier().contains(retypeTargetName[0])){
					String temp = new String();
					String[] split = schemaElement.getType().getSuperType().getIdentifier().split("/");
					temp = split[split.length-1];
					temp = temp.replace("Type", "");
					superTypeName = temp;
					break;
				}
			}
			cellVector = new Vector<String>();	
			boolean augmentationHeader = true;
			for(ICell augmentationCell : this.augmentations){
				if(augmentationCell.getEntity2().getAbout().getAbout().contains(superTypeName)){
					String[] entity1Name = this.entityNameSplitter(augmentationCell.getEntity1());
					entity1Name[entity1Name.length-1] = entity1Name[entity1Name.length-1].replace(";", " --> ");
					String[] entity2Name = this.entityNameSplitter(augmentationCell.getEntity2());
					entity2Name[entity2Name.length-1] = entity2Name[entity2Name.length-1].replace(";", " --> ");
					
					String functioncellName;
					if (augmentationCell.getEntity1().getTransformation() == null) {
						functioncellName = augmentationCell.getEntity2()
								.getTransformation().getService()
								.getLocation();
					} else {
						functioncellName = augmentationCell.getEntity1()
								.getTransformation().getService()
								.getLocation();
					}
					String[] tempSplit = functioncellName.split("\\.");
					String functionName = tempSplit[tempSplit.length - 1];
					
					//Header
					if(augmentationHeader){
						Vector<String> augmentationcellVector = new Vector<String>();
						augmentationcellVector.addElement("<h3>Augmentations</h3>");
						cellListVector.addElement(augmentationcellVector);	
						augmentationHeader=false;
					}
					
					cellVector.addElement("<h4>Cell "+i+" : </h4>");
					cellVector.addElement("Function : "+functionName);
					
					//entity2
					if (augmentationCell.getEntity2() instanceof ComposedProperty) {
						cellVector.addElement("Entity 2 : ComposedProperty");
						for(int z=0; z < entity2Name.length; z++){
							cellVector.addElement("<ul><li>Entity 2."+z+" : "+superTypeName+" / "+retypeTargetName[0]+" / "+entity2Name[z]+"</li></ul>");
						}
					}
					else{
						cellVector.addElement("Entity 2 : "+superTypeName+" / "+retypeTargetName[0]+" / "+entity2Name[0]);
					}
					
					//Image
//					cellVector.addElement("<img src='"+this.pictureNames+(i-1)+".png'>");
					
					//Filters
					this.getFilters(cellVector, augmentationCell);
					
					//Parameters
					this.getParameters(cellVector, augmentationCell);
					
					//Spaceholder
					cellVector.addElement("&emsp;");
					
					i++;
				}
			}
			
			cellListVector.addElement(cellVector);	
			e++;
		}
		this.context.put("cellList", cellListVector);
	}
	
	/**
	 * Returns the filters
	 * @param cellVector
	 * @param cell
	 */
	private void getFilters(Vector<String> cellVector, ICell cell){
		//Filter Rules
		if(cell.getEntity1() instanceof ComposedProperty){
			if (((ComposedProperty) cell.getEntity1())
					.getValueCondition() != null) {
				// Filter strings are added to the Vector
				cellVector.addElement("Filter Rules: ");
				for (Restriction restriction : ((ComposedProperty) cell
						.getEntity1()).getValueCondition()) {
					cellVector.addElement("&emsp;&emsp;"+restriction.getCqlStr());
				}
			}
		}
		else if(cell.getEntity1() instanceof Property){
			if (((Property) cell.getEntity1())
					.getValueCondition() != null) {
				// Filter strings are added to the Vector
				cellVector.addElement("Filter Rules: ");
				for (Restriction restriction : ((Property) cell
						.getEntity1()).getValueCondition()) {
					cellVector.addElement("&emsp;&emsp;"+restriction.getCqlStr());
				}
			}
		}
		else if(cell.getEntity1() instanceof ComposedFeatureClass){
			if (((ComposedFeatureClass) cell.getEntity1())
					.getAttributeValueCondition() != null) {
				// Filter strings are added to the Vector
				cellVector.addElement("Filter Rules: ");
				for (Restriction restriction : ((ComposedFeatureClass) 
						cell
						.getEntity1()).getAttributeValueCondition()) {
					cellVector.addElement("&emsp;&emsp;"+restriction.getCqlStr());
				}
			}
		}
		else if(cell.getEntity1() instanceof FeatureClass){
			if (((FeatureClass) cell.getEntity1())
					.getAttributeValueCondition() != null) {
				// Filter strings are added to the Vector
				cellVector.addElement("Filter Rules: ");
				for (Restriction restriction : ((FeatureClass) 
						cell
						.getEntity1()).getAttributeValueCondition()) {
					cellVector.addElement("&emsp;&emsp;"+restriction.getCqlStr());
				}
			}
		}
	}
		
	/**
	 * Returns the parameters
	 * @param cellVector
	 * @param cell
	 */
	private void getParameters(Vector<String> cellVector, ICell cell){
		//Parameters
		List<IParameter> parameterList = new ArrayList<IParameter>();
		if (cell.getEntity1().getTransformation() != null) {
			parameterList = cell.getEntity1().getTransformation().getParameters();
		}
		else{
			parameterList = cell.getEntity2().getTransformation().getParameters();
		}
		if (!parameterList.isEmpty()) {
			cellVector.addElement("Parameters: ");
			for (IParameter parameter : parameterList) {
				cellVector.addElement("&emsp;&emsp;"+parameter.getName() + " : "+ parameter.getValue());
			}
		}
	}
	
	/**
	 * @param entity
	 * @return cellName
	 */
	private String[] entityNameSplitter(IEntity entity){
		String[] entityNames = new String[1];
		if(!(entity instanceof ComposedProperty)){
			String[] entitySplit = entity.getAbout().getAbout().split("/");
			entityNames[0] = entitySplit[entitySplit.length-1];
			return entityNames;
		}
		else{
			entityNames = new String[((ComposedProperty)entity).getCollection().size()];
			int i=0;
			for(IEntity tempEntity : ((ComposedProperty)entity).getCollection()){
				String[] entitySplit = tempEntity.getAbout().getAbout().split("/");
				entityNames[i] = entitySplit[entitySplit.length-1];
				i++;
			}
			return entityNames;
		}
	}

	/**
	 * @param url 
	 * @return The File as a Byte[]
	 * @throws Exception
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public byte[] urlToByteArray(URL url) throws Exception, IOException,
     UnsupportedEncodingException {
        URLConnection connection = url.openConnection();
        int contentLength = connection.getContentLength();
        InputStream inputStream = url.openStream();
		byte[] data = new byte[contentLength];
		inputStream.read(data);
		inputStream.close();
		return data;
	}
	
	/**
	 * @param file
	 * @param byteArray 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void byteArrayToFile(File file, byte [] byteArray) throws 
	 FileNotFoundException, IOException {
		if(byteArray!=null){
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(byteArray);
			fileOutputStream.close();
		}
	}
	
	/**
	 * @param file 
	 * @param writer 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void stringWriterToFile(File file, StringWriter writer) throws 
	 FileNotFoundException, IOException {
		if(writer!=null && file!=null){
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(writer.toString().getBytes());
			fileOutputStream.close();
		}
	}
}
