/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.data;

import lib.math.Numbers;

/**
 * A 1-dimensional weighted value.
 *
 */

public class Value1D {
  public double x;
  public double w;
  public String label;
  
  public Value1D(double x) {
    this.x = x;
    this.w = 1;
  }

  public Value1D(double x, double w) {
    this.x = x;
    this.w = w;
  } 
  
  public Value1D(Value1D src) {
    this.x = src.x;
    this.w = src.w;
  }
  
  public boolean equals(Value1D other) {
    return Numbers.equal(x, other.x);
  }  
}
