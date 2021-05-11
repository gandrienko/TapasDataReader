package TapasDataReader;

public class ExplanationItem {
  public int level;
  public String attr, // feature full name, e.g. DurationInSector02
         attr_core; // core of the attr name, e.g. DurationInSector
  public int attr_N; // N in the attr name, e.g. 2
  public String sector; // if present, otherwise null
  public float value;
  public double interval[]={Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY}; // min..max; either min or max is +-inf
}
