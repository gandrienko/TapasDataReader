package TapasDataReader;

public class ExplanationItem {
  int level;
  String attr, // feature full name, e.g. DurationInSector02
         attr_core; // core of the attr name, e.g. DurationInSector
  int attr_N; // N in the attr name, e.g. 2
  String sector; // if present, otherwise null
  float value;
  double interval[]={Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY}; // min..max; either min or max is +-inf
}
