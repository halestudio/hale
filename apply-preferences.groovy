#!/usr/bin/env groovy

import java.text.DateFormat
import java.util.Map.Entry
import java.util.jar.Manifest

/*
 * Class and interface definitions
 */

class SortedProperties extends Properties {

  synchronized Enumeration<Object> keys() {
    Enumeration keysEnum = super.keys()
    Vector keyList = new Vector()
    while (keysEnum.hasMoreElements()) {
      keyList.add(keysEnum.nextElement())
    }
    Collections.sort(keyList)
    return keyList.elements()
  }

}

interface ProjectFilter {

  /**
   * Determines if a project shall be processed
   * 
   * @param projectDir the project directory
   * 
   * @return if the project is accepted 
   */
  boolean acceptProject(File projectDir);
  
}

class ProjectPreferencesApplier {
  
  private final Map<String, SortedProperties> defaults = [:]
  
  private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)

  /**
   * Load default preferences from all .prefs files in the given directory
   * 
   * @param prefsPath the path to the default preferences directory
   * 
   * @return if any preference files were found
   */
  boolean loadDefaults(File path) {
    File[] prefFiles = path.listFiles(new FilenameFilter() {
      
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".prefs")
      }
      
    });
    
    boolean foundPreferences = false
    
    if (prefFiles != null) {
      for (File prefFile : prefFiles) {
        SortedProperties properties = new SortedProperties()
        try {
          properties.load(new BufferedReader(new FileReader(prefFile)))
          if (properties.isEmpty()) {
            println("Warning: empty preference file: " + prefFile.getAbsolutePath())
          }
          
          String key = prefFile.getName()
          defaults.put(key, properties)
          foundPreferences = true
          
          // println("Loaded default preferences: " + prefFile.getName())
        } catch (FileNotFoundException e) {
          // ignore
        } catch (IOException e) {
          println("Error reading preference file: " + prefFile.getAbsolutePath())
        }
      }

      println('Loaded preferences from: ' + path as String)
    }
    
    return foundPreferences
  }
  
  /**
   * Apply the preference defaults to projects found in the given search path
   * 
   * @param searchPath the search path
   * @param projectFilter the project filter 
   */
  public void applyDefaults(File searchPath, ProjectFilter projectFilter) {
    Collection<? extends File> projectDirs = findProjectDirectories(searchPath, true)
    
    int ignored = 0
    
    for (File projectDir : projectDirs) {
      if (projectFilter.acceptProject(projectDir)) {
        // apply project preferences
        applyProjectDefaults(projectDir)
      }
      else {
        ignored++
      }
    }
    
    println("Ignored " + ignored + " project directories.")
  }

  /**
   * Apply the project preference defaults to the project in the given
   *   directory
   * 
   * @param projectDir the project direktory
   */
  private void applyProjectDefaults(File projectDir) {
    // settings directory
    File settingsDir = new File(projectDir, ".settings")
    if (!settingsDir.exists()) {
      settingsDir.mkdir()
    }

    boolean success = true;
    
    for (Entry<String, SortedProperties> entry : defaults.entrySet()) {
      String filename = entry.getKey()
      SortedProperties defaults = entry.getValue()
      
      File propertiesFile = new File(settingsDir, filename)
      if (propertiesFile.exists()) {
        // apply defaults to existing file
        SortedProperties current = new SortedProperties()
        try {
          current.load(new BufferedReader(new FileReader(propertiesFile)))
          
          boolean changed = false
          
          // copy defaults
          Enumeration<Object> en = defaults.keys()
          while (en.hasMoreElements()) {
            String key = (String) en.nextElement()
            
            String defaultValue = defaults.getProperty(key)
            String oldValue = current.getProperty(key)
            
            if (oldValue == null || !oldValue.equals(defaultValue)) {
              current.setProperty(key, defaultValue)
              
              changed = true
            }
          }

          // save file
          if (changed) {
            current.store(new FileWriter(propertiesFile), "Updated from default preferences " + dateFormat.format(new Date()))
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace()
          success = false
        } catch (IOException e) {
          println("Error updating preferences '" + filename + "' for project in " + projectDir.getAbsolutePath())
          success = false
        }
      }
      else {
        // create initial file
        try {
          defaults.store(new FileWriter(propertiesFile), "Created from default preferences " + dateFormat.format(new Date()))
        } catch (IOException e) {
          println("Error saving default preferences '" + filename + "' for project in " + projectDir.getAbsolutePath())
          success = false
        }
      }
    }
    
    if (success) {
      println("Successfully updated project preferences for " + projectDir.getName())
    }
  }

  /**
   * Find the project directories in the given search path
   * 
   * @param searchPath the search path
   * @param searchInProjects if inside project directories shall also be searched for projects
   * 
   * @return the project directories
   */
  private Collection<? extends File> findProjectDirectories(File searchPath,
      boolean searchInProjects) {
    List<File> projectDirs = []

    boolean project = false;
    File projectFile = new File(searchPath, ".project")
    File classpathFile = new File(searchPath, ".classpath") // check also for classpath file to filter non-Java projects
    if (projectFile.exists() && classpathFile.exists()) {
      // search path is project dir
      projectDirs.add(searchPath);
      project = true;
    }
    
    if (!project || searchInProjects) {
      File[] candidates = searchPath.listFiles(new FilenameFilter() {
        
        public boolean accept(File dir, String name) {
          // ignore files starting with .
          if (name.startsWith(".")) {
            return false
          }
          
          // ignore build directories
          if (name.equals("build")) {
            return false
          }
          
          return true
        }
      });
      
      if (candidates != null) {
        for (File candidate : candidates) {
          projectDirs.addAll(findProjectDirectories(candidate, false))
        }
      }
    }
    
    return projectDirs
  }

}

void apply(File prefs, File searchPath, ProjectFilter projectFilter = { File projectDir -> true }) {
  ProjectPreferencesApplier applier = new ProjectPreferencesApplier()
  if (!applier.loadDefaults(prefs)) {
    println("No preference files found in " + prefs + ".")
  }
  else {
    applier.applyDefaults(searchPath, projectFilter)
  }
}


/*
 * Script
 */

// filter that detects Java 8 projects and updates the classpath setting
def isJava8 = { File projectDir ->
  File manifestFile = new File(new File(projectDir, 'META-INF'), 'MANIFEST.MF')
  boolean match = false
  if (manifestFile.exists()) {
    manifestFile.withInputStream {
      def manifest = new Manifest(it)
      def attributes = manifest.getMainAttributes()
      def env = attributes.getValue('Bundle-RequiredExecutionEnvironment')
      match = (env == 'JavaSE-1.8')
    }
  }
  
  if (match) {
    // update to Java 8 -> also replace classpath setting
    // XXX kind of a hack to do it in the filter
    File classpathFile = new File(projectDir, '.classpath')
    if (classpathFile.exists()) {
      def fileContent = classpathFile.text
      def java7cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7'
      def java8cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8'
      def java17cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17'
      def java18cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-18'
      if (fileContent.contains(java8cp)) {
        classpathFile.text = fileContent.replace(java8cp, java18cp)
      }
    }
  }

  match
}

// filter that detects Java 8 projects and updates the classpath setting
def isJava18 = { File projectDir ->
  File manifestFile = new File(new File(projectDir, 'META-INF'), 'MANIFEST.MF')
  boolean match = false
  if (manifestFile.exists()) {
    manifestFile.withInputStream {
      def manifest = new Manifest(it)
      def attributes = manifest.getMainAttributes()
      def env = attributes.getValue('Bundle-RequiredExecutionEnvironment')
      match = (env == 'JavaSE-1.8')
    }
  }
  
  if (match) {
    // update to Java 8 -> also replace classpath setting
    // XXX kind of a hack to do it in the filter
    File classpathFile = new File(projectDir, '.classpath')
    if (classpathFile.exists()) {
      def fileContent = classpathFile.text
      def java7cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7'
      def java8cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8'
      def java17cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17'
      def java18cp = 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-18'
      if (fileContent.contains(java18cp)) {
        classpathFile.text = fileContent.replace(java18cp, java17cp)
      }
    }
  }

  match
}

// apply preferences to projects

def java7 = 'platform/preferences/java7' as File
def java8 = 'platform/preferences/java8' as File
def java17 = 'platform/preferences/java17' as File
def java18 = 'platform/preferences/java18' as File
def searchPaths = ['common', 'cst', 'io', 'server', 'doc', 'ui', 'util', 'app', 'ext/styledmap', 'ext/geom', 'ext/adv']

searchPaths.each {
    // default: Java 8
  apply(java17, it as File, { !isJava18(it) } as ProjectFilter)
  // Java 17
  apply(java17, it as File, isJava18 as ProjectFilter)
}

//searchPaths.each {
    // default: Java 8
//  apply(java18, it as File, { !isJava8(it) } as ProjectFilter)
  // Java 8
//  apply(java18, it as File, isJava8 as ProjectFilter)
//}

// external contributions w/ different project settings

apply('platform/preferences/xslt' as File, 'ext/xslt' as File)
apply('platform/preferences/xslt' as File, 'ext/ageobw' as File)
apply('platform/preferences/mdl' as File, 'ext/mdl' as File)
