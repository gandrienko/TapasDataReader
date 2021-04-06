import TapasDataReader.Flight;
import TapasDataReader.Record;

import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

public class Main {

  public static void main(String[] args) {
    //System.out.println("Hello World!");
    String fnCapacities="C:\\CommonGISprojects\\tracks-avia\\TAPAS\\ATFCM-20210331\\0_delays\\scenario_20190801_capacities",
           fnDecisions="C:\\CommonGISprojects\\tracks-avia\\TAPAS\\ATFCM-20210331\\0_delays\\scenario_20190801_exp0_decisions",
           fnFlightPlans="C:\\CommonGISprojects\\tracks-avia\\TAPAS\\ATFCM-20210331\\0_delays\\scenario_20190801_exp0_baseline_flight_plans";
    TreeSet<Integer> steps=TapasDataReader.Readers.readStepsFromDecisions(fnDecisions);
    System.out.println(steps);
    Hashtable<String,Flight> flights=TapasDataReader.Readers.readFlightDelaysFromDecisions(fnDecisions,steps);
    System.out.println(flights.get("EDDK-LEPA-EWG598-20190801083100").delays[2]);
    Hashtable<String,Vector<Record>> records=TapasDataReader.Readers.readFlightPlans(fnFlightPlans,flights);
  }
}
