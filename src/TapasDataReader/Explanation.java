package TapasDataReader;

import java.util.Vector;

/**
 * an array of Explanations for all steps is attached to each flight.
 * to save memory, step and flight id are not recorder here (to be deleted from here after debugging)
 */
public class Explanation {
  public String FlightID, step;
  public int action;
  public float Q;
  public ExplanationItem eItems[];
  public ExplanationItem[] getExplItemsCombined() {
    if (eItems==null || eItems.length==0)
      return null;
    Vector<ExplanationItem> vei=new Vector<ExplanationItem>(eItems.length);
    vei.add(eItems[0]);
    for (int i=1; i<eItems.length; i++)
    if (eItems[i]==null)
      System.out.println("null eItem, flight "+FlightID+", step = "+step);
    else {
      int n=-1;
      for (int j=0; n==-1 && j<vei.size(); j++)
        if (vei.elementAt(j).attr.equals(eItems[i].attr))
          n=j;
      if (n==-1) // add Ith condition to the explanatio
        vei.add(eItems[i]);
      else { // combine Nth and Ith conditions
        ExplanationItem e=vei.elementAt(n);
        e.interval[0]=Math.max(e.interval[0],eItems[i].interval[0]);
        e.interval[1]=Math.min(e.interval[1],eItems[i].interval[1]);
      }
    }
    ExplanationItem ei[]=new ExplanationItem[vei.size()];
    for (int i=0; i<vei.size(); i++)
      ei[i]=vei.elementAt(i);
    return ei;
  }
}
