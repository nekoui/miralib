/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.shannon;

import java.util.ArrayList;
import java.util.Arrays;

import lib.math.IndexedValue;
import mira.data.DataSlice1D;
import mira.data.DataSlice2D;
import mira.data.Value1D;
import mira.data.Value2D;
import mira.utils.Log;
import processing.core.PApplet;

/**
 * Optimal Histogram bin size calculation from Shimazaki and Shinomoto:
 * http://toyoizumilab.brain.riken.jp/hideaki/res/histogram.html
 *
 */

public class Histogram {
  static boolean PRINT_ERRORS = false;
  
  static public int optBinCount(DataSlice1D slice) {
    if (slice.varx.categorical()) return (int)slice.countx;
      
    int hsize = slice.values.size() / 2;
        
    int minNBins, maxNBins;
    if (slice.countx < 5) {
      minNBins = maxNBins = (int)slice.countx;
    } else {
      minNBins = 2;
      long lcount = slice.countx;
      int icount = Integer.MAX_VALUE < lcount ? Integer.MAX_VALUE : (int)lcount;
      float res = (float)slice.resx();
      maxNBins = PApplet.min((int)(1.0f/res) + 1, icount, hsize);
    }
    
    int numValues = maxNBins - minNBins + 1;
    if (minNBins <= 0 || maxNBins <= 0 || numValues <= 0) {
      if (PRINT_ERRORS) {
        Log.message("Unexpected error number of bin values is negative. Bin limits: " + 
                    "[" + minNBins + ", " + maxNBins + "]");
      }
      return 1;
    }
    IndexedValue[] cost = new IndexedValue[numValues];
    for (int n = minNBins; n <= maxNBins; n++) {
      float bsize = 1.0f / n; 
      double[] counts = hist1D(slice.values, n);

      double k = countsMean(counts);    
      double v = countsDev(counts, k);

      float c = (float)((2 * k - v) / (bsize * bsize));
      cost[n - minNBins] = new IndexedValue(c, n, false);
    }
    Arrays.sort(cost);
    if (cost[0] != null) {
      int n = cost[0].index;
      return n;
    } else {
      if (PRINT_ERRORS) {
        Log.message("Unexpected error cost array has null 0th element after sorting. Bin limits: " + 
                    "[" + minNBins + ", " + maxNBins + "]");
        Log.message("Cost array:");
        Log.message(cost.toString());
      }
      return (minNBins + maxNBins)/2;
    }
  }

  static public int[] optBinCount(DataSlice2D slice) {
    if (slice.varx.categorical() && slice.vary.categorical()) {
      return new int[] {(int)slice.countx, (int)slice.county};
    }
    
    int sqsize = (int)PApplet.sqrt(slice.values.size() / 2);
    
    int minNBins0, maxNBins0;
    if (slice.varx.categorical()) {
      minNBins0 = maxNBins0 = (int)slice.countx;
    } else if (slice.countx < 5) {
      minNBins0 = maxNBins0 = (int)slice.countx;
    } else {
      minNBins0 = 2;
      long lcount = slice.countx;
      int icount = Integer.MAX_VALUE < lcount ? Integer.MAX_VALUE : (int)lcount;
      float res = (float)slice.resx();   
      maxNBins0 = PApplet.min((int)(1.0f/res) + 1, icount, sqsize);
    }
    
    int minNBins1, maxNBins1;
    if (slice.vary.categorical()) {
      minNBins1 = maxNBins1 = (int)slice.county;
    } else if (slice.county < 5) {
      minNBins1 = maxNBins1 = (int)slice.county;
    } else {
      minNBins1 = 2;
      long lcount = slice.county;
      int icount = Integer.MAX_VALUE < lcount ? Integer.MAX_VALUE : (int)lcount;
      float res = (float)slice.resy();            
      maxNBins1 = PApplet.min((int)(1.0f/res) + 1, icount, sqsize);
    }
    
    int blen0 = maxNBins0 - minNBins0 + 1;
    int blen1 = maxNBins1 - minNBins1 + 1;
    int numValues = blen0 * blen1; 
        
    if (minNBins0 <= 0 || maxNBins0 <= 0 || blen0 <= 0 || 
        minNBins1 <= 0 || maxNBins1 <= 1 || blen1 <= 0) {
      if (PRINT_ERRORS) {
        Log.message("Unexpected error number of bin values is negative. Bin limits: " + 
                    "[" + minNBins0 + ", " + maxNBins0 + "] x " + 
                    "[" + minNBins1 + ", " + maxNBins1 + "]");
      }
      return new int[] {1, 1};
    }
    IndexedValue[] cost = new IndexedValue[numValues];
    for (int n0 = minNBins0; n0 <= maxNBins0; n0++) {
      for (int n1 = minNBins1; n1 <= maxNBins1; n1++) {
        float bsize0 = 1.0f / n0; 
        float bsize1 = 1.0f / n1;     
        float barea = bsize0 * bsize1;
        double[][] counts = hist2D(slice.values, n0, n1);

        double k = countsMean(counts);    
        double v = countsDev(counts, k);    
        float c = (float)((2 * k - v) / (barea * barea));
        
        int n = (n0 - minNBins0) * blen1 + (n1 - minNBins1);        
        cost[n] = new IndexedValue(c, n, false);
      }
    }
    Arrays.sort(cost);
    if (cost[0] != null) {
      int n = cost[0].index;    
      int n0 = n / blen1 + minNBins0;
      int n1 = n % blen1 + minNBins1;
      
//      if (var0.isCategorical())
//      System.out.println("X: " + minNBins0 + " " + maxNBins0 + ": " + n0);
//      System.out.println("Y: " + minNBins1 + " " + maxNBins1 + ": " + n1);
      
      return new int[] {n0, n1};
    } else {
      if (PRINT_ERRORS) {
        Log.message("Unexpected error cost array has null 0th element after sorting. Bin limits: " + 
                    "[" + minNBins0 + ", " + maxNBins0 + "] x " + 
                    "[" + minNBins1 + ", " + maxNBins1 + "]");
        Log.message("Cost array:");
        Log.message(cost.toString());
      }
      return new int[] {(minNBins0 + maxNBins0)/2, (minNBins1 + maxNBins1)/2};
    }
  } 
  
  // Converts the 1D histogram defined by the equally-sized numBins bins, into
  // a new binning of the [0, 1] interval so that the probability of inside
  // each bin is uniform and equal to 1/numBins.
  static public float[] uniformBins1D(ArrayList<Value1D> values, int numBins) {
    float[] res = new float[numBins + 1];
    res[0] = 0;
    res[numBins] = 1;
    int N = values.size();
    double[] binCounts = hist1D(values, numBins);
    
    for (int i = 1; i < numBins; i++) {
      float pi = (float)binCounts[i - 1] / N; 
      res[i] = res[i - 1] + pi;
    }
    
    return res;
  }
  
  // Given the "unforming" bins defined by the array ubins, this function returns
  // a value x' that results of applying the "uniformization" transformation
  // on the value x. This transformation consists in choosing the value x'
  // that is inside the i-th ubin, where i is the equally-sized bin x belongs to.
  static public double uniformTransform1D(double x, float[] ubins) {
    int bnum = ubins.length - 1;
    float bsize = 1.0f / bnum;    
    int bin = PApplet.constrain((int)(x / bsize), 0, bnum - 1);    
    return ubins[bin] + Math.random() * (ubins[bin + 1] - ubins[bin]); 
  }

  static public double[] hist1D(ArrayList<Value1D> values, int bnum) {
    double[] counts = new double[bnum];
    float bsize = 1.0f / bnum;
    for (Value1D value: values) { 
      int bin = PApplet.constrain((int)(value.x / bsize), 0, bnum - 1);
      counts[bin] += value.w;
    }
    return counts; 
  }

  static public double[][] hist2D(ArrayList<Value2D> values,                                
                                  int bnumx, int bnumy) {
    double[][] counts = new double[bnumx][bnumy];
    float bsizex = 1.0f / bnumx; 
    float bsizey = 1.0f / bnumy; 
    for (Value2D value: values) { 
      int binx = PApplet.constrain((int)(value.x / bsizex), 0, bnumx - 1);
      int biny = PApplet.constrain((int)(value.y / bsizey), 0, bnumy - 1);    
      counts[binx][biny] += value.w;
    }
    return counts; 
  }

  static protected double countsMean(double[] counts) {
    int n = counts.length;
    double sum = 0;  
    for (int i = 0; i < n; i++) {
      sum += counts[i];   
    }
    return sum / n;  
  }

  static protected double countsDev(double[] counts, double mean) {
    int n = counts.length;
    double sum = 0;  
    for (int i = 0; i < n; i++) {
      sum += (counts[i] - mean) * (counts[i] - mean);
    }
    return sum / n;  
  }

  static protected double countsMean(double[][] counts) {
    int ni = counts.length;
    int nj = counts[0].length;
    double sum = 0;  
    for (int i = 0; i < ni; i++) {
      for (int j = 0; j < nj; j++) {
        sum += counts[i][j];   
      }
    }
    return sum / (ni * nj);  
  }

  static protected double countsDev(double[][] counts, double mean) {
    int ni = counts.length;
    int nj = counts[0].length;
    double sum = 0;  
    for (int i = 0; i < ni; i++) {
      for (int j = 0; j < nj; j++) {
        sum += (counts[i][j] - mean) * (counts[i][j] - mean);
      }    
    }
    return sum / (ni * nj);  
  }
}
