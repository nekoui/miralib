/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.utils;

import java.io.File;
import java.io.IOException;
import mira.shannon.Similarity;

/**
 * Mirador preferences, that are used when no project file is provided.
 *
 * @author Andres Colubri
 */

public class Preferences {
  static final protected int defPValue = Project.P0_05;
  static final protected String defMissingString = "?";
  static final protected int defMissThreshold = Project.MISS_80;    
  static final protected int defDepTest = Similarity.SURROGATE_GAUSS;
  static final protected int defSurrCount = 100;
  static final protected float defThreshold = 1E-3f;  
  
  public String projectFolder;
  
  public int pValue;
  public String missingString; 
  public int missingThreshold;   
  public int depTest;
  public int surrCount; 
  public float threshold;

  protected Settings settings;
  
  public Preferences() throws IOException {
    this("");
  }  
  
  public Preferences(String defFolder) throws IOException {
    File home = new File(System.getProperty("user.home"));
    File path = new File(home, ".mirador");
    if (!path.exists()) {
      if (!path.mkdirs()) {
        String err = "Cannot create a folder to store the preferences";
        Log.error(err, new RuntimeException(err));
      }
    }
    
    File file = new File(path, "preferences.cfg");
    settings = new Settings(file);
    if (file.exists()) {      
      projectFolder = settings.get("data.folder", defFolder);      
      missingString = settings.get("missing.string", defMissingString);
      missingThreshold = Project.stringToMissing(settings.get("missing.threshold", 
                         Project.missingToString(defMissThreshold)));
      
      pValue = Project.stringToPValue(settings.get("correlation.pvalue", 
               Project.pvalueToString(defPValue)));
      depTest = Similarity.stringToAlgorithm(settings.get("correlation.algorithm", 
                Similarity.algorithmToString(defDepTest)));
      surrCount = settings.getInteger("correlation.surrogates", defSurrCount);
      threshold = settings.getFloat("correlation.threshold", defThreshold);      
    } else {
      projectFolder = defFolder;
      pValue = defPValue;             
      missingString = defMissingString;
      missingThreshold = defMissThreshold;
      depTest = defDepTest;
      surrCount = defSurrCount;
      threshold = defThreshold;
      
      save();
    }
  }
  
  public void save() {
    settings.set("data.folder", projectFolder);
    settings.set("missing.string", missingString);
    settings.set("missing.threshold", Project.missingToString(missingThreshold));      
    settings.set("correlation.pvalue", Project.pvalueToString(pValue));
    settings.set("correlation.algorithm", Similarity.algorithmToString(depTest));
    settings.setInteger("correlation.surrogates", surrCount);
    settings.setFloat("correlation.threshold", threshold);      
    settings.save();    
  }
}
