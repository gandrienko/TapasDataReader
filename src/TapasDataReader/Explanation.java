package TapasDataReader;

import java.util.Vector;

/**
 * an array of Explanations for all steps is attached to each flight.
 * to save memory, step and flight id are not recorder here (to be deleted from here after debugging)
 */
public class Explanation {
  String FlightID, step;
  int action;
  float Q;
  ExplanationItem eItems[];
}
