package TapasDataReader;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Represents a re-occurring explanation.
 */
public class CommonExplanation {
  public ExplanationItem eItems[]=null;
  /**
   * The action (decision) explained by this explanation.
   */
  public int action=-1;
  /**
   * Individual explanations where the same attributes and conditions are used.
   * The individual explanations are grouped by the identifiers of the flights they refer to.
   * The keys of the hash table are the flight identifiers, the elements are the corresponding
   * individual explanations.
   */
  public Hashtable<String, ArrayList<Explanation>> uses=null;
  /**
   * Total number of uses of this explanation.
   */
  public int nUses=0;
  /**
   * Coordinate of this explanation in a 1D projection.
   */
  public double x1D=Double.NaN;
  /**
   * Coordinates of this explanation in a 2D or 3D projection (the length of the
   * array correspond to the number of dimensions)
   */
  public double coord[]=null;
  /**
   * A color assigned to this explanation.
   */
  public Color color=null;
  
  /**
   * Creates a new instance of CommonExplanation from the given individual explanations.
   * Puts the individual explanation in the hash table "uses".
   */
  public static CommonExplanation createCommonExplanation(Explanation ex,
                                                          boolean transformConditionsToInteger,
                                                          Hashtable<String,int[]> attrMinMax,
                                                          boolean combineConditions) {
    if (ex==null || ex.eItems==null || ex.eItems.length<1)
      return null;
    ExplanationItem ei[]=makeCopyAndTransform(ex.eItems,
        transformConditionsToInteger,attrMinMax,combineConditions);
    
    CommonExplanation cEx=new CommonExplanation();
    cEx.eItems=ei;
    cEx.action=ex.action;
    cEx.uses=new Hashtable<String, ArrayList<Explanation>>(25);
    ArrayList<Explanation> flightExpl=new ArrayList<Explanation>(10);
    flightExpl.add(ex);
    cEx.uses.put(ex.FlightID,flightExpl);
    cEx.nUses=1;
    return cEx;
  }
  
  public static ExplanationItem[] makeCopy(ExplanationItem ei[]){
    if (ei==null || ei.length==0)
      return null;
    ExplanationItem eii[]=new ExplanationItem[ei.length];
    for (int i=0; i<ei.length; i++)
      eii[i]=ei[i].clone();
    return eii;
  }
  
  public static ExplanationItem[] makeCopyAndTransform(ExplanationItem eiOrig[],
                                                       boolean transformConditionsToInteger,
                                                       Hashtable<String,int[]> attrMinMax,
                                                       boolean combineConditions){
    ExplanationItem ei[]=makeCopy(eiOrig);
    if (ei==null)
      return null;
    if (transformConditionsToInteger)
      for (int i=0; i<ei.length; i++) {
        ei[i].isInteger=true;
        double a=ei[i].interval[0], b=ei[i].interval[1];
        if (Math.floor(a)==Math.ceil(a) && Math.floor(b)==Math.ceil(b))
          continue; //both are already integers
        int minmax[]=(attrMinMax==null)?null:attrMinMax.get(ei[i].attr);
        if (minmax!=null) {
          ei[i].interval[0]=Math.max(minmax[0],Math.ceil(a));
          ei[i].interval[1]=Math.min(minmax[1],Math.floor(b));
        }
        else {
          ei[i].interval[0]=Math.ceil(a);
          ei[i].interval[1]=Math.floor(b);
        }
      }
    if (combineConditions)
      ei=Explanation.getExplItemsCombined(ei);
    return ei;
  }
  
  public static boolean sameExplanations(ExplanationItem e1[], ExplanationItem e2[]) {
    if (e1==null || e1.length<1)
      return e2==null || e2.length<1;
    boolean ok[]=new boolean[e2.length];
    for (int i=0; i<ok.length; i++)
      ok[i]=false;
    for (int i=0; i<e1.length; i++) {
      boolean found=false;
      for (int j=0; j<e2.length && !found; j++) {
        found = e1[i].sameCondition(e2[j]);
        if (found)
          ok[j]=true;
      }
      if (!found)
        return false;
    }
    for (int i=0; i<e2.length; i++)
      if (!ok[i]) {
        boolean found=false;
        for (int j=0; j<e1.length && !found; j++)
          found = e2[i].sameCondition(e1[j]);
        if (!found)
          return false;
      }
    return true;
  }
  
  public static ArrayList<CommonExplanation> addExplanation(ArrayList<CommonExplanation> exList,
                                                            Explanation exToAdd,
                                                            boolean transformConditionsToInteger,
                                                            Hashtable<String,int[]> attrMinMax,
                                                            boolean combineConditions) {
    if (exToAdd==null || exToAdd.eItems==null || exToAdd.eItems.length<1)
      return null;
    ExplanationItem ei[]=makeCopyAndTransform(exToAdd.eItems,
        transformConditionsToInteger,attrMinMax,combineConditions);
    if (exList==null)
      exList=new ArrayList<CommonExplanation>(1000);
    CommonExplanation cEx=null;
    for (int i=0; i<exList.size() && cEx==null; i++) {
      cEx=exList.get(i);
      if (exToAdd.action!=cEx.action || !sameExplanations(ei,cEx.eItems))
        cEx=null;
    }
    if (cEx==null) {
      cEx=new CommonExplanation();
      cEx.eItems=ei;
      cEx.action=exToAdd.action;
      cEx.uses=new Hashtable<String, ArrayList<Explanation>>(25);
      exList.add(cEx);
    }
    ArrayList<Explanation> flightExpl=cEx.uses.get(exToAdd.FlightID);
    if (flightExpl==null)
      flightExpl=new ArrayList<Explanation>(10);
    flightExpl.add(exToAdd);
    cEx.uses.put(exToAdd.FlightID,flightExpl);
    ++cEx.nUses;
    return exList;
  }
  
  public static ArrayList<CommonExplanation> getCommonExplanations(ArrayList<Explanation> explanations,
                                                                   boolean transformConditionsToInteger,
                                                                   Hashtable<String,int[]> attrMinMax,
                                                                   boolean combineConditions) {
    if (explanations==null || explanations.isEmpty())
      return null;
    ArrayList<CommonExplanation> exList=null;
    for (int e=0; e<explanations.size(); e++) {
      Explanation ex = explanations.get(e);
      if (ex == null || ex.eItems == null)
        continue;
      exList=addExplanation(exList,ex,transformConditionsToInteger,attrMinMax,combineConditions);
    }
    return exList;
  }
  
  public static double[][] computeDistances(ArrayList<CommonExplanation> explanations,
                                            Hashtable<String,int[]> attrMinMaxValues) {
    if (explanations==null || explanations.size()<2)
      return null;
    double d[][]=new double[explanations.size()][explanations.size()];
    for (int i=0; i<d.length; i++) {
      d[i][i]=0;
      for (int j=i+1; j<d.length; j++)
        d[i][j]=d[j][i]=distance(explanations.get(i).eItems,explanations.get(j).eItems,attrMinMaxValues);
    }
    return d;
  }
  
  public static double distance(ExplanationItem e1[], ExplanationItem e2[],
                                Hashtable<String,int[]> attrMinMaxValues) {
    if (e1==null || e1.length<1)
      if (e2==null) return 0; else return e2.length;
    if (e2==null || e2.length<1)
      return e1.length;
    double d=e1.length+e2.length;
    for (int i=0; i<e1.length; i++) {
      int i2=-1;
      for (int j=0; j<e2.length && i2<0; j++)
        if (e1[i].attr.equals(e2[j].attr))
          i2=j;
      if (i2<0)
        continue;
      d-=2; //corresponding items found
      int minmax[]=attrMinMaxValues.get(e1[i].attr);
      double min=(minmax==null)?Double.NaN:minmax[0], max=(minmax==null)?Double.NaN:minmax[1];
      d+=IntervalDistance.distanceRelative(e1[i].interval[0],e1[i].interval[1],
          e2[i2].interval[0],e2[i2].interval[1],min,max);
    }
    return d;
  }
}