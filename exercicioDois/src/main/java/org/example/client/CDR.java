package org.example.client;

import org.example.charging.ChargingRequest;
import org.example.charging.ChargingReply;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;

public class CDR {
    //Client Data Record
    private Timestamp timeStamp;
    private String msisdn; // Phone number
    private String service; // class.getName();
    private String tarifarioApplied;
    private ChargingRequest chargingRequest;
    private ChargingReply chargingReply;
    private double[] bucketsValue;
    private ArrayList<Object> countersValue;
    private static CDR instanceLogger;
    private static ArrayList<CDR> savedLogs = new ArrayList<>();

    private CDR() {
    }

    private CDR(Timestamp timeStamp,
                String msisdn,
                String service,
                String tarifarioApplied,
                ChargingRequest chargingRequest,
                ChargingReply chargingReply,
                double[] bucketsValue,
                ArrayList<Object> countersValue) {
        this.timeStamp = timeStamp;
        this.msisdn = msisdn;
        this.service = service;
        this.tarifarioApplied = tarifarioApplied;
        this.chargingRequest = chargingRequest;
        this.chargingReply = chargingReply;
        this.bucketsValue = bucketsValue;
        this.countersValue = countersValue;
    }

    public static CDR getInstance() {
        if (instanceLogger == null) {
            instanceLogger = new CDR();
        }
        return instanceLogger;
    }

    public void newDataRecordInitialization(Timestamp timeStamp,
                                            String msisdn,
                                            String service,
                                            String tarifarioApplied,
                                            ChargingRequest chargingRequest) {

        this.timeStamp = timeStamp;
        this.msisdn = msisdn;
        this.service = service;
        this.tarifarioApplied = tarifarioApplied;
        this.chargingRequest = chargingRequest;
    }

    public void saveLog() {
        savedLogs.add(new CDR(timeStamp,
                msisdn,
                service,
                tarifarioApplied,
                chargingRequest,
                chargingReply,
                bucketsValue,
                countersValue));
    }

    public void setChargingReply(ChargingReply chargingReply) {
        this.chargingReply = chargingReply;
    }

    public void setBucketsValue(double[] bucketsValue) {
        this.bucketsValue = bucketsValue;
    }

    public void setCountersValue(ArrayList<Object> countersValue) {
        this.countersValue = countersValue;
    }


    public void showCDRClient(String msisdn) {
        ArrayList<CDR> matchingCDRs = new ArrayList<>();

        for (CDR savedLog : savedLogs) {
            if (savedLog.msisdn.equals(msisdn)) {
                matchingCDRs.add(savedLog);
            }
        }

        if (matchingCDRs.isEmpty()) {
            System.out.println("No matching CDRs found for MSISDN: " + msisdn);
            return;
        }

        // Sort the matching CDRs by timestamp
        Collections.sort(matchingCDRs, new Comparator<CDR>() {
            @Override
            public int compare(CDR cdr1, CDR cdr2) {
                return cdr1.msisdn.compareTo(cdr2.msisdn);
            }
        });

        // Print the matching CDRs in a table format... or try to
        System.out.println("+---------------------+----------------------+----------------------+----------------------+----------------------+----------------------+----------------------+----------------------+");
        System.out.println("|     Timestamp      |       MSISDN         |       Service        |  Charging Request   |  Charging Reply     |       Buckets        |      Counters        |   Tarifário aplicado  |");
        System.out.println("+---------------------+----------------------+----------------------+----------------------+----------------------+----------------------+----------------------+----------------------+");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (CDR cdr : matchingCDRs) {
            String timestamp = sdf.format(cdr.timeStamp);
            String msisdnValue = cdr.msisdn;
            String service = cdr.service;
            String chargingRequest = cdr.chargingRequest.toString();
            String chargingReply = cdr.chargingReply.toString();
            String buckets = Arrays.toString(cdr.bucketsValue);
            String counters = cdr.countersValue.toString();
            String tarifarioAplicado = cdr.tarifarioApplied;

            System.out.println(String.format("| %s | %s | %s | %s | %s | %s | %s | %s |", timestamp, msisdnValue, service, chargingRequest, chargingReply, buckets, counters, tarifarioAplicado));
        }

        System.out.println("+---------------------+----------------------+----------------------+");
    }
}
