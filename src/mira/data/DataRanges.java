/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mira.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import mira.utils.Log;

/**
 * Dictionary that links variables with their respective ranges.
 *
 */

@SuppressWarnings("serial")
public class DataRanges extends HashMap<Variable, Range> {
  
  public DataRanges() {
    super();
  }
  
  public DataRanges(DataRanges ranges) {
    super();
    Set<Variable> variables = ranges.keySet();
    for (Variable var: variables) {
      if (var == null) {
        Log.message("Found null variable in the ranges, something is going on (threading problems maybe)");
        continue;
      }
      this.put(var, Range.create(ranges.get(var)));
    }
  }
  
  synchronized public Range get(Object key) {
    return super.get(key);
  }
  
  synchronized public Set<Variable> keySet() {
    return super.keySet();
  }
  
  synchronized public boolean update(Variable var, Range range) {
    boolean change = false;
    Range range0 = get(var);
    if (range0 == null) {
      if (!var.maxRange(range)) {
        // Adding range for variable var for the first time.
        put(var, range);
        change = true;  
      }      
    } else if (!range.equals(range0)) {
      change = true;      
      if (var.maxRange(range)) {
        // Removing range for variable var as it is set to its maximum range.
        remove(var);
      } else {
        // Replacing range0 by range1 for new variable.
        put(var, range);
      }
    }
    return change;      
  }
  
  public String toString() {
    Iterator<Entry<Variable, Range>> i = entrySet().iterator();
    if (! i.hasNext())
        return "{}";

    StringBuilder sb = new StringBuilder();
    sb.append('{');
    for (;;) {
        Entry<Variable, Range> e = i.next();
        Variable var = e.getKey();
        Range range = e.getValue();
        sb.append(var.getName());
        sb.append('=');
        sb.append(var.formatRange(range, false));
        if (! i.hasNext())
            return sb.append('}').toString();
        sb.append(',').append(' ');
    }

  }
}
