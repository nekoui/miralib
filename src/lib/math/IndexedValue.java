/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package lib.math;

/**
 * Comparable implementation for indexed values
 * 
 * @author Andres Colubri
 */

public class IndexedValue implements Comparable<IndexedValue> {
  public float value;
  public int index;
  public boolean up;
  
  public IndexedValue(float value, int index) {
    this(value, index, true);
  }
  
  public IndexedValue(float value, int index, boolean up) {
    this.value = value; // Assuming value is normalized (0, 1, -1 for missing data).
    this.index = index;
    this.up = up;
  }
  
  // Returns a negative integer, zero, or a positive integer as this object is 
  // less than, equal to, or greater than the specified object.
  public int compareTo(IndexedValue obj) {
    float a = this.value;
    float b = obj.value;
    
    boolean bErr = b == -1 || Float.isNaN(b);
    boolean aErr = a == -1 || Float.isNaN(a);
    
    if (bErr || aErr) {
      // Missing/NaN data always at the bottom:
      if (bErr && aErr) {
        return 0;
      } else if (bErr) {
        return -1;
      } else {
        return +1; 
      }
    }
    
    if (!up) {
      a = -a;
      b = -b;
    }
    
    if (a < b) return +1;
    else if (b < a) return -1;
    else return 0;
  }
}
