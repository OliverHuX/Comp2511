package unsw.skydiving;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Skydive Booking System for COMP2511.
 *
 * A basic prototype to serve as the "back-end" of a skydive booking system. Input
 * and output is in JSON format.
 *
 * @author XiaoHu z5223731
 *
 */


public class SkydiveBookingSystem {

    /**
     * Constructs a skydive booking system. Initially, the system contains no flights, skydivers, jumps or dropzones
     */

    private ArrayList<Skydive> skydives;
    private ArrayList<Flight> flights;
    private ArrayList<Skydiver> skydivers;
    private ArrayList<TandemMaster> tandemSkydivers;
    private ArrayList<Instructor> instructorSkydivers;
    private ArrayList<String> ids;
    
    public SkydiveBookingSystem() {
        this.skydives = new ArrayList<Skydive>();
        this.flights = new ArrayList<Flight>();
        this.skydivers = new ArrayList<Skydiver>();
        this.tandemSkydivers = new ArrayList<TandemMaster>();
        this.instructorSkydivers = new ArrayList<Instructor>();
        this.ids = new ArrayList<String>();
    }

    private void processCommand(JSONObject json) {

        switch (json.getString("command")) {

        case "flight":
            String id = json.getString("id");
            int maxload = json.getInt("maxload");
            LocalDateTime starttime = LocalDateTime.parse(json.getString("starttime"));
            LocalDateTime endtime = LocalDateTime.parse(json.getString("endtime"));
            String dropZone = json.getString("dropzone");
            
            Flight flight = new Flight(dropZone, maxload, id, starttime, endtime);
            if(flights.size() == 0) {
                this.flights.add(flight);
            } else {
                int count = 0;
                for(Flight f: flights) {
                    if(starttime.isBefore(f.getStartTime())) {
                        this.flights.add(count, flight);
                        break;
                    }
                    count++;
                }
                this.flights.add(flight);
            }
            break;

        case "skydiver":
            String name = json.getString("skydiver");
            String licence = json.getString("licence");
            Skydiver skydiver;
            Instructor instructor;
            TandemMaster tandemMaster;
            if(licence.equals("student")) {
                skydiver = new Skydiver(name, licence);
            } else if(licence.equals("licenced-jumper")) {
                skydiver = new LicencedJumper(name, licence);
            } else if(licence.equals("instructor")) {
                String dropzone = json.getString("dropzone");
                skydiver = new Instructor(name, licence, dropzone);
                instructor = new Instructor(name, licence, dropzone);
                this.instructorSkydivers.add(instructor);
            } else {
                String dropzone = json.getString("dropzone");
                skydiver = new TandemMaster(name, licence, dropzone);
                tandemMaster = new TandemMaster(name, licence, dropzone);
                instructor = new Instructor(name, licence, dropzone);
                this.instructorSkydivers.add(instructor);
                this.tandemSkydivers.add(tandemMaster);
            }
            this.skydivers.add(skydiver);
            //System.out.println(skydiver.getClass());////////////////
            break;
            
        case "request":
            //String comm = json.getString("command");
            String jumpType = json.getString("type");
            String Id = json.getString("id");
            LocalDateTime startTime = LocalDateTime.parse(json.getString("starttime"));
            //List<String> list = new ArrayList<String>();
            Skydive skydive = null;
            //for (int i = 0; i < jsonArray.length(); i++) {
                //list.add(jsonArray.getString(i));
                //System.out.println(jsonArray.getString(i) + " hhhhhhh");

            //}
            //check fun
            //if so then we check time if it suitable for any flight then we check its load
            //all meet, we add skydivers to new ArrayList then we add it to skydive
            if(jumpType.equals("fun")) {
                //print json rejected
                JSONArray jsonArray = (JSONArray) json.get("skydivers");
                bookFun(jumpType, Id, startTime, skydive, jsonArray);
            } else if(jumpType.equals("tandem")) {
                //JSONArray jsonArray = (JSONArray) json.get("passenger");
                //each skydiver has different time period
                //check is the endtime of each request(by flight) in the middle of time period of skydive
                //time
                //flight capacity
                //tandem master time
                //
                String passengerName = json.getString("passenger");
                bookTandem(passengerName, jumpType, Id, startTime, skydive);
                //check passenger's time
            } else if (jumpType.equals("training")) {
                String traineeName = json.getString("trainee");
                bookTraining(traineeName, jumpType, Id, startTime, skydive);
            }
            
            break;
            
        case "change":
            //Cancel Jump ID
            //Request new Jump given the parameters provided.
            //check id then go through the skydive arraylist find out the correct one
            jumpType = json.getString("type");
            Id = json.getString("id");
            startTime = LocalDateTime.parse(json.getString("starttime"));
            String jumpTypeBackUp = null;
            String idBackUp = null;
            String passenger = null;
            String trainee = null;
            LocalDateTime startTimeBackUp = null;
            JSONArray ja = new JSONArray();
            for(Skydive s: skydives) {
                if(s.getId().equals(Id)) {
                    jumpTypeBackUp = s.getType();
                    idBackUp = s.getId();
                    startTimeBackUp = s.getStartTime();
                    if(jumpTypeBackUp.equals("tandem")) {
                        passenger = s.getPassenger();
                    } else if (jumpTypeBackUp.equals("training")) {
                        trainee = s.getTrainee();
                    } else if (jumpTypeBackUp.equals("fun")) {
                        for(Skydiver newS: s.getSkydivers()) {
                            JSONObject jo = new JSONObject();
                            jo.append("skydivers", newS.getName());
                            ja.put(jo);
                        }
                    }
                    break;
                }
            }
            cancelSkydive(Id);
            skydive = null;
            if(jumpType.equals("fun")) {
                JSONArray jsonArray = (JSONArray) json.get("skydivers");
                if(!bookFun(jumpType, Id, startTime, skydive, jsonArray)) {
                    bookBackUp(jumpTypeBackUp, idBackUp, startTimeBackUp, skydive, ja, passenger, trainee);
                }
            } else if (jumpType.equals("tandem")) {
                String passengerName = json.getString("passenger");
                if(!bookTandem(passengerName, jumpType, Id, startTime, skydive)) {
                    bookBackUp(jumpTypeBackUp, idBackUp, startTimeBackUp, skydive, ja, passenger, trainee);
                }
            } else if (jumpType.equals("training")) {
                String traineeName = json.getString("trainee");
                if(!bookTraining(traineeName, jumpType, Id, startTime, skydive)) {
                    bookBackUp(jumpTypeBackUp, idBackUp, startTimeBackUp, skydive, ja, passenger, trainee);
                }
            }    
            break;
            
        case "cancel":
            String skydiveId = json.getString("id");
            cancelSkydive(skydiveId);
            break;

        case "jump-run":
            String flightId = json.getString("id");
            JSONArray jArray = new JSONArray();
            ArrayList<Skydive> newSkydives = new ArrayList<Skydive>();
            for(Skydive s: skydives) {
                if(s.getFlightId().equals(flightId)) {
                    
                    ids.add(s.getId());
                    if(newSkydives.size() == 0) {
                        newSkydives.add(s);
                    } else {  
                        
                        for(int count = 0; count < newSkydives.size(); count++) {
                            Skydive ns = newSkydives.get(count);
                            if(s.getType().equals("fun") && !ns.getType().equals("fun") && count == 0) {
                                newSkydives.add(count, s);
                                break;
                            } else if (s.getType().equals("fun") && s.getSkydivers().size() > ns.getSkydivers().size()) {
                                newSkydives.add(count, s);
                                break;
                            } else if (s.getType().equals("fun") && s.getSkydivers().size() == ns.getSkydivers().size()) {
                                newSkydives.add(count + 1, s);
                                break;
                            } else if (s.getType().equals("training") && !ns.getType().equals("fun")) {
                                newSkydives.add(count + 1, s);
                                break;
                            } else if (s.getType().equals("tandem")) {
                                newSkydives.add(s);
                                break;
                            }

                        }
                    }
                }
            }
            if(newSkydives.size() == 0) {
                System.out.println(jArray.toString());
                break;
            }
            //jArray = new JSONArray();
            JSONObject jo = null;
            for(Skydive s: newSkydives) {
                ArrayList<String> ar = new ArrayList<String>();

                for(Skydiver skyer: s.getSkydivers()) {
                        ar.add(skyer.getName());
                }
                Collections.sort(ar);
                if(s.getType().equals("fun")) {
                    jo = new JSONObject();
                    
                    for(String str: ar) {
                        jo.append("skydivers", str);
                    }
                    jArray.put(jo);
                } else if (s.getType().equals("training")) {
                    jo = new JSONObject();
                    for(String str: ar) {
                        if(!str.equals(s.getTrainee())) {
                            jo.put("instructor", str);
                        } else {
                            jo.put("trainee", str);
                        }
                    }
                    jArray.put(jo);
                } else if (s.getType().equals("tandem")) {
                    jo = new JSONObject();
                    for(String str: ar) {
                        if(!str.equals(s.getPassenger())) {
                            jo.put("jump-master", str);
                        } else {
                            jo.put("passenger", str);
                        }
                    }
                    jArray.put(jo);
                }
            }

            System.out.println(jArray.toString());
            for(String i: ids) {
                cancelSkydive(i);
            }
            break;
        }
    }
    
    public void bookBackUp(String jumpTypeBackUp, String idBackUp, LocalDateTime startTimeBackUp, Skydive skydive, JSONArray jsonArray, String passenger, String trainee) {
        if(jumpTypeBackUp.equals("fun")) {
            bookFun(jumpTypeBackUp, idBackUp, startTimeBackUp, skydive, jsonArray);
        } else if (jumpTypeBackUp.equals("training")) {
            bookTraining(trainee, jumpTypeBackUp, idBackUp, startTimeBackUp, skydive);
        } else if (jumpTypeBackUp.equals("tandem")) {
            bookTandem(passenger, jumpTypeBackUp, idBackUp, startTimeBackUp, skydive);
        }
    }

    public void cancelSkydive(String skydiveId) {
        Skydive sTarget = null;
        for(Skydive sD: skydives) {
            if(sD.getId().equals(skydiveId)) {
                //TimePeriod t = new TimePeriod(sD.getStartTime(), sD.getEndTime());
                ArrayList<Skydiver> skydivers = sD.getSkydivers();
                for(Skydiver s: skydivers) {
                    //TimePeriod target = null;
                    ArrayList<TimePeriod> tList = s.getPeriods();
                    //System.out.println(s.getPeriods().size() + "before");
                    for(TimePeriod sT: tList) {
                        if(sD.getStartTime().equals(sT.getStartTime()) && sD.getEndTime().equals(sT.getEndTime())) {
                            //System.out.println(s.getPeriods().size() + " loop before");
                            s.removePeriod(sT);
                            break;
                        }
                    }
                    //s.removePeriod(target);
                    
                    //find the time period
                    //delete that period
                    //tList.remove(target);
                    for(TandemMaster tM: tandemSkydivers) {
                        if(tM.getName().equals(s.getName())) {
                            tList = tM.getPeriods();
                            if (tList == null) {
                                break;
                            }
                            for(TimePeriod sT: tList) {
                                if(sD.getStartTime().equals(sT.getStartTime()) && sD.getEndTime().equals(sT.getEndTime())) {
                                    tM.removePeriod(sT);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    for(Instructor inst: instructorSkydivers) {
                        if(inst.getName().equals(s.getName())) {
                            tList = inst.getPeriods();
                            if (tList == null) {
                                break;
                            }
                            for(TimePeriod sT: tList) {
                                if(sD.getStartTime().equals(sT.getStartTime()) && sD.getEndTime().equals(sT.getEndTime())) {
                                    inst.removePeriod(sT);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    //System.out.println(s.getPeriods().size() + "should 0");
                }
                //change the capacity of flight
                for(Flight f: flights) {
                    if(f.getId().equals(sD.getFlightId())) {
                        f.drop(skydivers.size());
                        //System.out.println(f.getMaxLoad());
                        break;
                    }
                }
                sTarget = sD;
                break;
            }
        }
        skydives.remove(sTarget);
    }
    
    public Boolean bookFun(String jumpType, String Id, LocalDateTime startTime, Skydive skydive, JSONArray jsonArray) {
        for(Flight f: flights) {
            boolean flag = false;
            JSONObject ob = new JSONObject();
            if((startTime.isBefore(f.getStartTime()) || startTime.isEqual(f.getStartTime())) && (f.getStartTime().getDayOfMonth() == startTime.getDayOfMonth()) && f.getMaxLoad() >= jsonArray.length()) {             
                String[] strs = {"licenced-jumper", "tandem-master", "instructor"};
                if(checkSkydiver(skydivers, jsonArray, strs)) {
                    ArrayList<Skydiver> newSkydivers = new ArrayList<Skydiver>();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        for(Skydiver s: skydivers) {
                            if(s.getName().equals(jsonArray.getString(i))) {
                                if(checkTimeAvailable(startTime, f.getEndTime(), s.getPeriods(), 10, "fun")) {
                                    TimePeriod t = new TimePeriod(startTime, f.getEndTime());
                                    s.addPeriod(t);
                                    newSkydivers.add(s);
                                    break;
                                } else {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if(flag) {
                            break;
                        }
                    }
                    if(flag) {
                        ob.put("status", "rejected");
                        System.out.println(ob.toString());
                        return false;
                    } else {
                        skydive = new Skydive(f.getId(), Id, jumpType, newSkydivers, startTime, f.getEndTime());
                        skydives.add(skydive);
                        f.load(jsonArray.length());
                        ob.put("flight", f.getId());
                        ob.put("dropzone", f.getDropZone());
                        ob.put("status", "success");
                        System.out.println(ob.toString());
                        //System.out.println(skydive.toString() + " " + f.getMaxLoad());
                        return true;
                    }
                } else {
                    ob.put("status", "rejected");
                    System.out.println(ob.toString());
                }
                return false;
            }
        }
        return false;
    }
            
    public Boolean bookTandem(String passengerName, String jumpType, String Id, LocalDateTime startTime, Skydive skydive) {
        boolean flag1 = false;
        JSONObject ob = new JSONObject();
        for(Flight f: flights) {
            Duration diff = Duration.between(startTime, f.getStartTime());
            //check flight
            boolean flag = false;
            if((startTime.isBefore(f.getStartTime()) && diff.toMinutes() >= 5) && (f.getStartTime().getDayOfMonth() == startTime.getDayOfMonth()) && f.getMaxLoad() >= 2) {
                ArrayList<Skydiver> newSkydivers = new ArrayList<Skydiver>();
                for(Skydiver s: skydivers) {
                    //check passenger
                    if(s.getName().equals(passengerName) && checkTimeAvailable(startTime, f.getEndTime(), s.getPeriods(), 5, jumpType)) {
                        for(TandemMaster ts: tandemSkydivers) {
                            //check tandem master
                            if(ts.getName().equals(passengerName)) {
                                continue;
                            }
                            if(ts.getDropZone().equals(f.getDropZone()) && checkTimeAvailable(startTime, f.getEndTime(), ts.getPeriods(), 15, jumpType)) {
                                TimePeriod t = new TimePeriod(startTime, f.getEndTime());
                                ts.addPeriod(t);
                                for(Skydiver newS: skydivers) {
                                    if(newS.getName().equals(ts.getName())) {
                                        newSkydivers.add(newS);
                                        newS.addPeriod(t);
                                        break;
                                    }
                                }
                                break;
                            } else if (ts.getDropZone().equals(f.getDropZone())) {
                                ob.put("status", "rejected");
                                System.out.println(ob.toString());
                                //flag = true;
                                return false;
                            }
                        }
                        if (!flag) {
                            TimePeriod t = new TimePeriod(startTime, f.getEndTime());
                            s.addPeriod(t);
                            newSkydivers.add(s);
                            skydive = new Skydive(f.getId(), Id, jumpType, newSkydivers, startTime, f.getEndTime());
                            skydive.setPassenger(passengerName);
                            skydives.add(skydive);
                            f.load(2);
                            ob.put("flight", f.getId());
                            ob.put("dropzone", f.getDropZone());
                            ob.put("status", "success");
                            System.out.println(ob.toString());
                            return true;
                        } else {
                            ob.put("status", "rejected");
                            System.out.println(ob.toString());
                        }                      
                    } else if (s.getName().equals(passengerName)) {
                        break;
                    }
                }      
            }
        }
        if(!flag1) {
            ob.put("status", "rejected");
            System.out.println(ob.toString());
            return false;
        }
        return false;
    }
        
    public Boolean bookTraining(String traineeName, String jumpType, String Id, LocalDateTime startTime, Skydive skydive) {
        JSONObject ob = new JSONObject();
        boolean flag1 = false;
        for(Flight f: flights) {
            boolean flag = false;
            if((startTime.isBefore(f.getStartTime()) || startTime.isEqual(f.getStartTime())) && (f.getStartTime().getDayOfMonth() == startTime.getDayOfMonth()) && f.getMaxLoad() >= 2) {
                ArrayList<Skydiver> newSkydivers = new ArrayList<Skydiver>();
                for(Skydiver s: skydivers) {
                    //check trainee
                    boolean flag2 = false;
                    //check if the trainee is not a student then need extra ten mins to repack
                    int interval = 15;
                    if(!s.getLicenced().equals("student")) {
                        interval = 25;
                    }
                    if(s.getName().equals(traineeName) && checkTimeAvailable(startTime, f.getEndTime(), s.getPeriods(), interval, jumpType)) {
                        
                        for(Instructor Is: instructorSkydivers) {
                            //check instructors
                            if(Is.getName().equals(traineeName)) {
                                continue;
                            }
                            if(Is.getDropZone().equals(f.getDropZone()) && checkTimeAvailable(startTime, f.getEndTime(), Is.getPeriods(), 25, jumpType)) {
                                TimePeriod t = new TimePeriod(startTime, f.getEndTime());
                                Is.addPeriod(t);
                                for(Skydiver newS: skydivers) {
                                    if(newS.getName().equals(Is.getName())) {
                                        newSkydivers.add(newS);
                                        newS.addPeriod(t);
                                        break;
                                    }
                                }
                                flag2 = true;
                                break;
                            } else if (Is.getDropZone().equals(f.getDropZone())) {
                                ob.put("status", "rejected");
                                System.out.println(ob.toString());
                                return false;
                            }
                        }
                        if (!flag && flag2) {
                            TimePeriod t = new TimePeriod(startTime, f.getEndTime());
                            newSkydivers.add(s);
                            skydive = new Skydive(f.getId(), Id, jumpType, newSkydivers, startTime, f.getEndTime());
                            skydive.setTrainee(traineeName);
                            skydives.add(skydive);
                            s.addPeriod(t);
                            f.load(2);
                            ob.put("flight", f.getId());
                            ob.put("dropzone", f.getDropZone());
                            ob.put("status", "success");
                            System.out.println(ob.toString());
                            return true;
                        } else {
                            ob.put("status", "rejected");
                            System.out.println(ob.toString());
                            return false;
                        }                      
                    } else if (s.getName().equals(traineeName)) {
                        ob.put("status", "rejected");
                        System.out.println(ob.toString());
                        return false;
                    }
                }
                if(flag1) {
                    break;
                }
            }
        }
        if(!flag1) {
            ob.put("status", "rejected");
            System.out.println(ob.toString());
        }
        return false;
        
    }
    
    public boolean checkSkydiver(ArrayList<Skydiver> skydivers, JSONArray jsonArray, String[] strs) {
        boolean flag1 = false;//check name
        boolean flag2 = false;//check licence
        for(int i = 0; i < jsonArray.length(); i++) {
            flag1 = false;//check name
            flag2 = false;//check licence
            for(Skydiver s: skydivers) {
                flag2 = false;
                for(String str: strs) {
                    if(s.getLicenced().equals(str)) {
                        flag2 = true;
                        break;
                    }
                }
                if(flag2 && s.getName().equals(jsonArray.getString(i))) {
                    flag1 = true;
                break;
            }
        }
            if(!flag1) {
                return false;
            }
        }
        return true;
    }
            
    public boolean checkTimeAvailable(LocalDateTime startTime, LocalDateTime endTime, ArrayList<TimePeriod> periods, int interval, String type) {
        if (periods == null) {
            return true;
        }
        for(TimePeriod t: periods) {
            LocalDateTime starttime = t.getStartTime();
            LocalDateTime endtime = t.getEndTime();
            //licenced jumper need minus 5 mins before start time
            if((endTime.isAfter(starttime) && endTime.isBefore(endtime)) && endTime.getDayOfMonth() == starttime.getDayOfMonth()) {
                return false;
            } else if (startTime.isAfter(starttime) && startTime.isBefore(endtime) && endTime.getDayOfMonth() == starttime.getDayOfMonth()) {
                return false;
            } else if (startTime.isAfter(starttime) && endTime.isBefore(endtime) && endTime.getDayOfMonth() == starttime.getDayOfMonth()) {
                return false;
            } else if (startTime.isEqual(starttime) && endTime.isEqual(endtime) && endTime.getDayOfMonth() == starttime.getDayOfMonth()) {
                return false;
            } else if (startTime.isEqual(starttime) && endTime.isAfter(endtime) && endTime.getDayOfMonth() == starttime.getDayOfMonth()) {
                return false;
            } else if (startTime.isBefore(starttime) && endTime.isEqual(endtime) && endTime.getDayOfMonth() == starttime.getDayOfMonth()) {
                return false;
            } else if ((endTime.isBefore(starttime) || endTime.isEqual(starttime)) && endTime.getDayOfMonth() == starttime.getDayOfMonth() && (type.equals("tandem") || type.equals("fun"))) {
                Duration diff = Duration.between(endTime, starttime);
                if(diff.toMinutes() <= interval) {
                    return false;
                }
            } else if ((startTime.isAfter(endtime) || startTime.isEqual(endtime)) && endTime.getDayOfMonth() == starttime.getDayOfMonth() && (type.equals("tandem") || type.equals("fun"))) {
            Duration diff = Duration.between(endtime, startTime);
                if(diff.toMinutes() <= interval) {
                    return false;
                }
            } else if (type.equals("training") && endTime.getDayOfMonth() == starttime.getDayOfMonth() && endTime.isBefore(starttime)) {
                Duration diff = Duration.between(endtime, startTime);
                if(diff.toMinutes() <= interval) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SkydiveBookingSystem system = new SkydiveBookingSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.trim().equals("")) {
                JSONObject command = new JSONObject(line);
                system.processCommand(command);
            }
        }
        sc.close();
    }

}