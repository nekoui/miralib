/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.shannon;

import mira.data.DataSlice2D;
import mira.data.Value2D;
import processing.core.PApplet;

/**
 * Calculation of the joint entropy of a 2D slice.
 *
 * @author Andres Colubri
 */

public class JointEntropy {
  static public float calculate(DataSlice2D slice) {
    int[] nbins = Histogram.optBinCount(slice);
    return calculate(slice, nbins[0], nbins[1]);
  }
  
  static public float calculate(DataSlice2D slice, int nbinx, int nbiny) {
    if (nbinx < 2 || nbiny < 2) return 0;
    
    float sbinx = 1.0f / nbinx;
    float sbiny = 1.0f / nbiny;
    double[][] counts = new double[nbinx][nbiny];
    
    double total = 0;
    for (Value2D value: slice.values) {
      int bx = PApplet.constrain((int)(value.x / sbinx), 0, nbinx - 1);  
      int by = PApplet.constrain((int)(value.y / sbiny), 0, nbiny - 1);  
      counts[bx][by] += value.w;
      total += value.w;
    }
    
    double entropy = 0;
    int nonzero = 0;      
    for (int bx = 0; bx < nbinx; bx++) {
      for (int by = 0; by < nbiny; by++) {          
        double pxy = counts[bx][by] / total;
        
        double hbin = 0;
        if (0 < pxy) {
          nonzero++;
          hbin = -pxy * Math.log(pxy);
        }
        
        entropy += hbin;
      }
    }    
    
    if (entropy < 0 || Double.isNaN(entropy)) return 0;
    
    // Finite size correction
//    double correction = (nbinx * nbiny - 1) / (2 * total);
    double correction = (nonzero - 1) / (2 * total);
    return (float)(entropy + correction);    
  }  
}
