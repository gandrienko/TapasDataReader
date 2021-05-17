package TapasDataReader;

import java.util.Hashtable;
import java.util.Vector;

/**
 * an array of Explanations for all steps is attached to each flight.
 * to save memory, step and flight id are not recorder here (to be deleted from here after debugging)
 */
public class Explanation {
  public String FlightID, step;
  public int action;
  public float Q;
  public ExplanationItem eItems[]=null;

  public ExplanationItem[] getExplItemsCombined (ExplanationItem ei[]) {
    if (ei==null || ei.length==0)
      return null;
    Vector<ExplanationItem> vei=new Vector<ExplanationItem>(ei.length);
    vei.add(ei[0]);
    for (int i=1; i<ei.length; i++)
    if (ei[i]==null)
      System.out.println("null eItem, flight "+FlightID+", step = "+step);
    else {
      int n=-1;
      for (int j=0; n==-1 && j<vei.size(); j++)
        if (vei.elementAt(j).attr.equals(ei[i].attr))
          n=j;
      if (n==-1) // add Ith condition to the explanatio
        vei.add(ei[i]);
      else { // combine Nth and Ith conditions
        ExplanationItem e=vei.elementAt(n);
        e.interval[0]=Math.max(e.interval[0],ei[i].interval[0]);
        e.interval[1]=Math.min(e.interval[1],ei[i].interval[1]);
      }
    }
    ExplanationItem eItemsCombined[]=new ExplanationItem[vei.size()];
    for (int i=0; i<vei.size(); i++)
      eItemsCombined[i]=vei.elementAt(i);
    return eItemsCombined;
  }

  public ExplanationItem[] getExplItemsAsIntegeres (ExplanationItem ei[], Hashtable<String,int[]> attrs) {
    if (ei==null || ei.length==0)
      return null;
    ExplanationItem eii[]=new ExplanationItem[ei.length];
    for (int i=0; i<ei.length; i++) {
      eii[i]=ei[i].clone();
      int minmax[]=attrs.get(ei[i].attr);
      eii[i].interval[0]=Math.max(minmax[0],Math.ceil(ei[i].interval[0]));
      eii[i].interval[1]=Math.min(minmax[1],Math.floor(ei[i].interval[1]));
    }
    return eii;
  }
}
