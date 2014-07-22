/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.data;

import java.util.ArrayList;

import lib.math.Numbers;
import processing.data.Table;
import processing.data.TableRow;

/**
 * 1-dimensional data slice, i.e.: all the (normalized) data values for a single
 * variable from the rows that satisfy the range conditions. 
 *
 */

public class DataSlice1D {
  final static public int MAX_SLICE_SIZE = Integer.MAX_VALUE;
  
  public Variable varx;
  public DataRanges ranges;
  public ArrayList<Value1D> values;
  public long countx;
  public float missing;
  
  public DataSlice1D(Variable varx, DataRanges ranges) {
    this.varx = varx;
    this.values = new ArrayList<Value1D>();
    
    // Create a copy of the ranges, because they can change after the slice 
    // has been constructed    
    this.ranges = new DataRanges(ranges);
  }
  
  public DataSlice1D(Table data, Variable varx, DataRanges ranges) {
    this.varx = varx;
    this.values = new ArrayList<Value1D>();
    
    // Create a copy of the ranges, because they can change after the slice 
    // has been constructed    
    this.ranges = new DataRanges(ranges);
    
    init(data);
  } 
  
  public void dispose() {
    values.clear();
  } 
  
  public void add(Value1D value) {
    values.add(value);
  }

  public void add(double x, double w) {
    values.add(new Value1D(x, w));
  } 
  
  public void setMissing(float missing) {
    this.missing = missing;
  }
  
  public void setCount(long countx) {
    this.countx = countx;
  } 
  
  public void normalizeWeights(double factor) {
    for (Value1D val: values) {
      val.w *= factor;
    }
  }  
  
  public double resx() {
    double res = Double.POSITIVE_INFINITY; 
    for (int i = 0; i < values.size(); i++) {
      Value1D vali = values.get(i);
      for (int j = i + 1; j < values.size(); j++) {
        Value1D valj = values.get(j); 
        double diff = Math.abs(valj.x - vali.x);
        if (0 < diff) {
          res = Math.min(res, diff);
        }
      }
    }
    return res;
  }
  
  protected void init(Table data) {
    int ntot = 0;
    int nmis = 0;
    double wsum = 0;
    int rcount = data.getRowCount();
    float p = (float)MAX_SLICE_SIZE / (float)rcount;
    for (int r = 0; r < rcount; r++) {
      if (p < 1 && p < Math.random()) continue;
      TableRow row = data.getRow(r);       
      if (!DataSet.insideRanges(row, ranges)) continue;
      ntot++;      
      double valx = varx.getValue(row, ranges);
      double w = varx.getWeight(row);
      if (valx < 0 || w < 0) {
        nmis++;
        continue;
      }
      add(valx, w);      
      wsum += w;
    }
    long countx = varx.getCount(ranges);
    setCount(countx);
    setMissing((float)nmis/(float)ntot);
    double factor = (ntot - nmis) / wsum;
    if (Numbers.different(factor, 1)) {
      normalizeWeights(factor);  
    }    
  }
}
