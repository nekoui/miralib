/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package lib.math;

/**
 * Basic utilities to deal with numeric values.
 * 
 * @author Andres Colubri
 */

public class Numbers {
  static public float FLOAT_EPS   = Float.MIN_VALUE;
  static public double DOUBLE_EPS = Double.MIN_VALUE;
  
  // Calculation of the Machine Epsilon for float precision. From:
  // http://en.wikipedia.org/wiki/Machine_epsilon#Approximation_using_Java
  static {
    float eps = 1.0f;

    do {
      eps /= 2.0f;
    } while ((float)(1.0 + (eps / 2.0)) != 1.0);

    FLOAT_EPS = eps;
  }

  static {
    double eps = 1.0f;

    do {
      eps /= 2.0f;
    } while (1.0 + (eps / 2.0) != 1.0);

    DOUBLE_EPS = eps;
  }  
  
  static public boolean equal(int a, int b) {
    return a == b;
  }

  static public boolean different(int a, int b) {
    return a != b;
  }    
  
  static public boolean equal(long a, long b) {
    return a == b;
  }

  static public boolean different(long a, long b) {
    return a != b;
  }      
  
  static public boolean equal(float a, float b) {
    return Math.abs(a - b) < FLOAT_EPS;
  }

  static public boolean different(float a, float b) {
    return Math.abs(a - b) >= FLOAT_EPS;
  }  

  static public boolean whole(float a) {
    return equal(a, (int)a);
  }
  
  static public boolean equal(double a, double b) {
    return Math.abs(a - b) < DOUBLE_EPS;
  }

  static public boolean different(double a, double b) {
    return Math.abs(a - b) >= DOUBLE_EPS;
  }  

  static public boolean whole(double a) {
    return equal(a, (int)a);
  }
}

