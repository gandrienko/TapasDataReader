package TapasDataReader;

import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

public class Readers {

  public static Hashtable<String,Integer> readCapacities (String fname) {
    Hashtable<String,Integer> capacities=new Hashtable(100);
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fname+".csv")))) ;
      String strLine;
      try {
        br.readLine();
        while ((strLine = br.readLine()) != null) {
          String str=strLine.replaceAll(" ","");
          String[] tokens=str.split(",");
          String s=tokens[0];
          if (!s.equals("NONE") && !s.equals("NULL")) {
            Integer capacity = Integer.valueOf(tokens[1]);
            capacities.put(s, capacity);
          }
        }
        br.close();
      } catch  (IOException io) {}
    } catch (FileNotFoundException ex) {System.out.println("problem reading sectors from "+fname+" : "+ex);}
    return capacities;
  }

  public static TreeSet<Integer> readStepsFromDecisions (String fname) {
    TreeSet<Integer> steps=new TreeSet();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fname+".csv")))) ;
      String strLine;
      try {
        String header=br.readLine();
        int stepColN=getFieldN(header,"TimeStep");
        if (stepColN>=0)
          while ((strLine = br.readLine()) != null) {
            String str=strLine.replaceAll(" ","");
            String[] tokens=str.split(",");
            Integer step=Integer.valueOf(tokens[stepColN]);
            steps.add(step);
          }
        br.close();
      } catch  (IOException io) {}
    } catch (FileNotFoundException ex) {System.out.println("problem reading sectors from "+fname+" : "+ex);}
    steps.add(new Integer(-1)); // step -1 represents the baseline solution
    return steps;
  }

  public static Hashtable<String,Flight> readFlightDelaysFromDecisions (String fname, TreeSet<Integer> steps) {
    Hashtable<String,Flight> flights=new Hashtable<String, Flight>(1000);
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fname+".csv")))) ;
      String strLine;
      try {
        String header=br.readLine();
        int flightColN=getFieldN(header,"FlightID"),
            delayColN=getFieldN(header,"TotalDelay"),
            stepColN=getFieldN(header,"TimeStep");
        if (stepColN>=0 && flightColN>=0 && delayColN>=0)
          while ((strLine = br.readLine()) != null) {
            String str=strLine.replaceAll(" ","");
            String[] tokens=str.split(",");
            Flight flight=flights.get(tokens[flightColN]);
            if (flight==null) {
              flight=new Flight(tokens[flightColN],steps);
              flights.put(tokens[flightColN],flight);
            }
            flight.delays[steps.headSet(new Integer(tokens[stepColN])).size()]=Integer.valueOf(tokens[delayColN]).intValue();
            Integer step=Integer.valueOf(tokens[stepColN]);
            steps.add(step);
          }
        br.close();
      } catch  (IOException io) {}
    } catch (FileNotFoundException ex) {System.out.println("problem reading sectors from "+fname+" : "+ex);}
    return flights;
  }

  public static Hashtable<String,Vector<Record>> readFlightPlans (String fname, Hashtable<String,Flight> flights) {
    return readFlightPlans(fname,-1,flights);
  }
  public static Hashtable<String,Vector<Record>> readFlightPlans (String fname, int theStep, Hashtable<String,Flight> flights) {
    Hashtable<String,Vector<Record>> records=new Hashtable(100000);
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fname+".csv")))) ;
      String strLine;
      int N=0, M=0, K=0;
      try {
        String header=br.readLine();
        int columnFlightID=countCommas(header.substring(0,header.indexOf("FlightID"))),
            columnDelays=countCommas(header.substring(0,header.indexOf("Delays"))),
            columnSector=countCommas(header.substring(0,header.indexOf("Sector_0"))),
            columnEntryTime=countCommas(header.substring(0,header.indexOf("EntryTime_0")));
        //String[] columns=header.split(",");
        Flight flight=null;
        String flightID=null;
        while ((strLine = br.readLine()) != null) {
          String str=strLine.replaceAll(" ","");
          String[] tokens=str.split(",");
          String id=tokens[columnFlightID];
          if (!id.equals(flightID)) { // search in hashtable only if a new flight
            if (flights==null)
              flight=null;
            else
              flight=flights.get(id);
            flightID=id;
            M++;
          }
          int delay=Integer.valueOf(tokens[columnDelays]);
          if ((flight==null && delay==0) || (flight!=null && delay<=flight.delays[flight.delays.length-1])) {
            //if (delay>0)
              //System.out.println("* delay="+delay);
            // 1. parse record into a sequence of sectors with times
            Vector<Record> vr=new Vector<Record>(columnEntryTime-columnSector);
            for (int i=columnSector; i<columnEntryTime; i++)
              if (tokens[i].equals("NULL") || tokens[i].equals("NONE"))
                ;
              else {
                Record r=new Record();
                r.flight=id;
                r.sector=tokens[i];
                r.delay=delay;
                if (i>columnSector)
                  r.FromS=tokens[i-1];
                else
                  r.FromS="NULL";
                if (i<columnEntryTime)
                  r.ToS=tokens[i+1];
                else
                  r.ToS="NULL";
                r.FromT=tokens[columnEntryTime+i-columnSector];
                r.ToT=tokens[1+columnEntryTime+i-columnSector];
                r.calc();
                vr.add(r);
              }
            // 2. output for all steps with the same delay
            for (int step = 0; step < ((flight==null) ? 1 : flight.delays.length); step++)
              if ((theStep==-1 || step==theStep) && (flight==null || flight.delays[step]==delay))
                for (Record r:vr) {
                  //bw.write("FLIGHTID,STEP,DELAY,SECTOR,ENTRYTIME,EXITTIME,ENTRYTIMEN,EXITTIMEN,FROMSECTOR,TOSECTOR\n");
                  //bw.write(flight.id+","+step+","+delay+","+r.Sector+","+r.EntryTime+","+r.ExitTime+","+r.EntryTimeN+","+r.ExitTimeN+","+r.FromSector+","+r.ToSector+"\n");
                  K++;
                  Record rr=r.clone();
                  rr.step=step;
                  String key=rr.sector+"_"+rr.step;
                  Vector<Record> vrr=records.get(key);
                  if (vrr==null) {
                    vrr=new Vector<Record>(100);
                    records.put(key,vrr);
                  }
                  vrr.add(rr);
                }
          }
          N++;
          if (N % 10000 == 0)
            System.out.println("* flights: "+M+" flights in "+N+" flightplans lines processed, "+K+" outputs recorded");
        }
        br.close();
        //bw.close();
        System.out.println("* flights: "+M+" flights in "+N+" flightplans lines processed, "+K+" outputs recorded");
      } catch  (IOException io) {}
    } catch (FileNotFoundException ex) {System.out.println("problem reading file "+fname+" : "+ex);}
    return records;
  }

  public static Hashtable<String,Vector<Record>> readSolutionAsStep (String fname, int theStep) {
    Hashtable<String,Vector<Record>> records=new Hashtable(100000);
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fname+".csv")))) ;
      String strLine;
      int N=0, M=0, K=0;
      try {
        String header=br.readLine();
        int columnFlightID=countCommas(header.substring(0,header.indexOf("FlightID"))),
                columnDelays=(header.indexOf("Delays")==-1) ? -1 : countCommas(header.substring(0,header.indexOf("Delays"))),
                columnSector=countCommas(header.substring(0,header.indexOf("Sector_0"))),
                columnEntryTime=countCommas(header.substring(0,header.indexOf("EntryTime_0")));
        //String[] columns=header.split(",");
        //Flight flight=null;
        //String flightID=null;
        while ((strLine = br.readLine()) != null) {
          String str=strLine.replaceAll(" ","");
          String[] tokens=str.split(",");
          String id=tokens[columnFlightID];
          int delay=(columnDelays==-1) ? 0 : Integer.valueOf(tokens[columnDelays]);
          Vector<Record> vr=new Vector<Record>(columnEntryTime-columnSector);
          for (int i=columnSector; i<columnEntryTime; i++)
            if (tokens[i].equals("NULL") || tokens[i].equals("NONE"))
              ;
            else {
              Record r=new Record();
              r.flight=id;
              r.sector=tokens[i];
              r.delay=delay;
              if (i>columnSector)
                r.FromS=tokens[i-1];
              else
                r.FromS="NULL";
              if (i<columnEntryTime)
                r.ToS=tokens[i+1];
              else
                r.ToS="NULL";
              r.FromT=tokens[columnEntryTime+i-columnSector];
              r.ToT=tokens[1+columnEntryTime+i-columnSector];
              r.calc();
              vr.add(r);
            }
            for (Record r:vr) {
              //bw.write("FLIGHTID,STEP,DELAY,SECTOR,ENTRYTIME,EXITTIME,ENTRYTIMEN,EXITTIMEN,FROMSECTOR,TOSECTOR\n");
              //bw.write(flight.id+","+step+","+delay+","+r.Sector+","+r.EntryTime+","+r.ExitTime+","+r.EntryTimeN+","+r.ExitTimeN+","+r.FromSector+","+r.ToSector+"\n");
              K++;
              Record rr=r.clone();
              rr.step=theStep;
              String key=rr.sector+"_"+rr.step;
              Vector<Record> vrr=records.get(key);
              if (vrr==null) {
                vrr=new Vector<Record>(100);
                records.put(key,vrr);
              }
              vrr.add(rr);
            }
          N++;
          if (N % 10000 == 0)
            System.out.println("* flights: "+M+" flights in "+N+" flightplans lines processed, "+K+" outputs recorded");
        }
        br.close();
        //bw.close();
        System.out.println("* flights: "+M+" flights in "+N+" flightplans lines processed, "+K+" outputs recorded");
      } catch  (IOException io) {}
    } catch (FileNotFoundException ex) {System.out.println("problem reading file "+fname+" : "+ex);}
    return records;
  }

  public static void readExplanatios (String path, Hashtable<String,Flight> flights) {
    File folder = new File(path+"VA");
    File[] listOfFiles = folder.listFiles();
    for (int i=0; i<listOfFiles.length; i++)
      if (listOfFiles[i] != null) {
        String fn=listOfFiles[i].getName();
        if (fn.startsWith("xaiQ")) {
          int fN=Integer.valueOf(fn.substring(4,6)).intValue();
          System.out.println(i + ". " + fn+", N="+fN);
          try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(listOfFiles[i])));
            try {
              String strLine=br.readLine();
              int stepColN=getFieldN(strLine,"Step"),
                  flightColN=getFieldN(strLine,"FlightID"),
                  actionColN=getFieldN(strLine,"Action"),
                  qColN=getFieldN(strLine,"Q"),
                  explColN=getFieldN(strLine,"Expl1");

              while ((strLine = br.readLine()) != null) {
                String str=strLine.replaceAll(" ","");
                String[] tokens=str.split(",");
                if (Integer.valueOf(tokens[actionColN]).intValue()==fN) { // explanation corresponds to the action taken

                }
              }
              br.close();
            } catch  (IOException io) {}
          } catch (FileNotFoundException ex) {System.out.println("problem reading explanations from "+listOfFiles[i].getPath());}

      }
      }

  }

  protected static int countCommas (String str) {
    int n=0;
    for (int i=0; i<str.length(); i++)
      if (str.charAt(i)==',')
        n++;
    return n;
  }

  protected static int getFieldN (String str, String field) {
    int n=str.indexOf(field);
    if (n>0)
      return countCommas(str.substring(0,n));
    else
      return n;
  }

}
