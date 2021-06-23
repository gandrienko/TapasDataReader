package TapasUtilities;

import TapasUtilities.gunther_foidl.SammonsProjection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MySammonsProjection extends SammonsProjection {
  /**
   * Last stored projection copy.
   */
  public double lastPojectionCopy[][]=null;
  public boolean done=false;
  
  public MySammonsProjection(double[][] distanceMatrix,int outputDimension,int maxIterations,
                             boolean useEuclid) {
    super(distanceMatrix,null,outputDimension,maxIterations,useEuclid);
  }
  
  public void runProjection(int nStepsBetweenNotifications,
                            ChangeListener listener, double minStressImprovement) {
    if (this._distanceMatrix==null || listener==null)
      return;
    int nSteps=0;
    int i0=this.Iteration;
    double lastStress=Double.NaN;
    for (int i = this._maxIteration; i >= i0; i--) {
      this.Iterate();
      ++nSteps;
      double stress=computeStress();
      if (lastStress-stress<minStressImprovement)
        break;
      if (nSteps>=nStepsBetweenNotifications) {
        double copy[][]=new double[Projection.length][Projection[0].length];
        for (int j=0; j<Projection.length; j++)
          for (int k=0; k<Projection[j].length; k++)
            copy[j][k]=Projection[j][k];
        lastPojectionCopy=copy;
        nSteps=0;
        listener.stateChanged(new ChangeEvent(this));
      }
    }
    done=true;
  }
}
