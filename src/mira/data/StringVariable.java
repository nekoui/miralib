/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.data;

import processing.data.Table;
import processing.data.TableRow;

/**
 * String variable.
 *
 */

public class StringVariable extends Variable {
  public StringVariable(String name, int index) {
    super(name, index);
    range = new DummyRange(this);
  }

  public void initValues(String valstr) {
  }

  public int type() {
    return Table.STRING;
  }

  public boolean discrete() {
    return false;
  }

  public boolean numerical() {
    return false;
  }

  public boolean categorical() {
    return false;
  }
  
  public boolean string() {
    return true;
  }  

  public boolean missing(TableRow row) {
    String value = row.getString(index);
    return value == null || value.equals(missingString);
  }

  public double getValue(TableRow row, Range sel, boolean normalized) {
    return 0;
  }

  public String formatValue(TableRow row) {
    String value = row.getString(index);
    if (value == null || value.equals(missingString)) return "missing";
    return value;
  }

  public String formatValue(double value, boolean normalized) {
    return "";
  }

  public String formatValue(double value, Range sel) {
    return "";
  }

  public boolean valueAlias(String value) {
    return false;
  }

  public double snapValue(double value, Range sel, boolean normalized) {
    return 0;
  }

  public String formatRange(Range sel, boolean humanReadable) {
    return "";
  }

  protected double getWeightImpl(TableRow row) {
    return 0;
  }
}
