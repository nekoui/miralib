/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package miralib.data;

import java.util.ArrayList;
import java.util.Collections;

import processing.data.Table;
import processing.data.TableRow;
import miralib.math.Numbers;
import miralib.shannon.BinOptimizer;

/**
 * 2-dimensional data slice, i.e.: all the (normalized) data value pairs for two 
 * variables from the rows that satisfy the range conditions. 
 *
 */

public class DataSlice2D {
  final static public int MAX_SLICE_SIZE = Integer.MAX_VALUE;
  
  public Variable varx, vary;
  public DataRanges ranges;
  public ArrayList<Value2D> values;
  public long countx, county;
  public float missing;
  
  public DataSlice2D(Variable varx, Variable vary, DataRanges ranges) {
    this.varx = varx;
    this.vary = vary;
    this.values = new ArrayList<Value2D>();
    
    // Create a copy of the ranges, because they can change after the slice 
    // has been constructed.    
    this.ranges = new DataRanges(ranges);
  }
  
  public DataSlice2D(Table data, Variable varx, Variable vary, DataRanges ranges) {
    this(data, varx, vary, ranges, null);
  }
  
  public DataSlice2D(Table data, Variable varx, Variable vary, 
                     DataRanges ranges, Variable varl) {
    this.varx = varx;
    this.vary = vary;
    this.values = new ArrayList<Value2D>();
    
    // Create a copy of the ranges, because they can change after the slice 
    // has been constructed.    
    this.ranges = new DataRanges(ranges);
    
    init(data, varl);    
  }  
  
  public DataSlice2D shuffle() {
    ArrayList<Value1D> valuesx = new ArrayList<Value1D>();
    ArrayList<Value1D> valuesy = new ArrayList<Value1D>();    
    for (Value2D val: values) {
      valuesx.add(new Value1D(val.x, val.w));
      valuesy.add(new Value1D(val.y, val.w));
    }
    Collections.shuffle(valuesx);
    Collections.shuffle(valuesy);
    DataSlice2D shuffled = new DataSlice2D(varx, vary, ranges);
    for (int n = 0; n < values.size(); n++) {
      shuffled.add(new Value2D(valuesx.get(n), valuesy.get(n))); 
    }    
    shuffled.countx = countx;
    shuffled.county = county;
    shuffled.missing = missing;
    return shuffled;  
  }
  
  public DataSlice2D uniformize(int binx, int biny) {
    ArrayList<Value1D> valuesx = new ArrayList<Value1D>();
    ArrayList<Value1D> valuesy = new ArrayList<Value1D>();    
    for (Value2D val: values) {
      valuesx.add(new Value1D(val.x, val.w));
      valuesy.add(new Value1D(val.y, val.w));
    }    
    float[] ubinsx = BinOptimizer.uniformBins1D(valuesx, binx);
    float[] ubinsy = BinOptimizer.uniformBins1D(valuesy, biny);
    
    DataSlice2D uniform = new DataSlice2D(varx, vary, ranges);
    for (Value2D value: values) {
      double ux = BinOptimizer.uniformTransform1D(value.x, ubinsx);
      double uy = BinOptimizer.uniformTransform1D(value.y, ubinsy);
      uniform.add(ux, uy, value.w); 
    }    
    
    // Testing "uniformization"
//  int[] countsx = Histogram.hist1D(valuesx, binx);
//  int[] countsy = Histogram.hist1D(valuesy, biny);
//  int[] ucountsx = Histogram.hist1D(uvaluesx, binx);
//  int[] ucountsy = Histogram.hist1D(uvaluesy, biny);      
//  print(" countsx: ");
//  for (int i = 0; i < countsx.length; i++) {
//    System.out.print(countsx[i] + " ");
//  }
//  println("");
//  print("ucountsx: ");
//  for (int i = 0; i < ucountsx.length; i++) {
//    System.out.print(ucountsx[i] + " ");
//  }
//  println("");      
//  print(" countsy: ");
//  for (int i = 0; i < countsy.length; i++) {
//    System.out.print(countsy[i] + " ");
//  }            
//  println("");
//  print("ucountsy: ");
//  for (int i = 0; i < ucountsy.length; i++) {
//    System.out.print(ucountsy[i] + " ");
//  }            
//  println("");    

    uniform.countx = countx;
    uniform.county = county;
    uniform.missing = missing;    
    return uniform;
  }
  
  public void dispose() {
    values.clear();
  }
  
  public void add(Value2D value) {
    values.add(value);
  }

  public Value2D add(double x, double y, double w) {
    Value2D value = new Value2D(x, y, w);
    values.add(value);
    return value;
  }  
  
  public void setMissing(float missing) {
    this.missing = missing;
  }
  
  public void setCount(long sizex, long sizey) {
    this.countx = sizex;
    this.county = sizey;
  }
  
  public void normalizeWeights(double factor) {
    for (Value2D val: values) {
      val.w *= factor;
    }
  }
  
  public DataSlice1D getSliceX() {
    DataSlice1D slice = new DataSlice1D(varx, ranges);
    for (Value2D val: values) {
      slice.add(val.x, val.w);
    }
    slice.setCount(countx);
    slice.setMissing(missing);
    return slice;
  }
  
  public DataSlice1D getSliceY() {
    DataSlice1D slice = new DataSlice1D(vary, ranges);
    for (Value2D val: values) {
      slice.add(val.y, val.w);
    }
    slice.setCount(county);
    slice.setMissing(missing);
    return slice;
  }  
  
  protected void init(Table data, Variable varl) {
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
      double valy = vary.getValue(row, ranges);
      double w = Variable.getWeight(row, varx, vary);
      if (valx < 0 || valy < 0 || w < 0) {
        nmis++;
        continue;
      }
      Value2D val = add(valx, valy, w);
      if (varl != null && val != null) {
        val.label = varl.formatValue(row);        
      }
      wsum += w;
    }
    long countx = varx.getCount(ranges);
    long county = vary.getCount(ranges);    
    setCount(countx, county);
    setMissing((float)nmis/(float)ntot);    
    double factor = (ntot - nmis) / wsum;    
    if (Numbers.different(factor, 1)) {
      normalizeWeights(factor);
    }
  }
}
