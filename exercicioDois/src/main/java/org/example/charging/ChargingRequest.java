package org.example.charging;

import java.sql.Timestamp;

public class ChargingRequest {

    private final long REQUEST_ID;
    private final Timestamp TIMESTAMP;
    private final String SERVICE_OPTIONS;
    private final boolean ROAMING;
    //Phone number
    private final String MSISDN;
    //Requested Service Units
    private final int RSU;

    public ChargingRequest(long REQUEST_ID, Timestamp TIME_STAMP, String SERVICE_OPTIONS, boolean ROAMING, String MSISDN, int RSU) {
        this.REQUEST_ID = REQUEST_ID;
        this.TIMESTAMP = TIME_STAMP;
        this.SERVICE_OPTIONS = SERVICE_OPTIONS;
        this.ROAMING = ROAMING;
        this.MSISDN = MSISDN;
        this.RSU = RSU;
    }

    public long getREQUEST_ID() {
        return REQUEST_ID;
    }

    public Timestamp getTIMESTAMP() {
        return TIMESTAMP;
    }

    public String getSERVICE() {
        return SERVICE_OPTIONS;
    }

    public boolean isROAMING() {
        return ROAMING;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public int getRSU() {
        return RSU;
    }

    @Override
    public String toString() {
        return "{" +
                "\"REQUEST_ID\": " + REQUEST_ID +
                ", \"TIMESTAMP\": \"" + TIMESTAMP + "\"" +
                ", \"SERVICE_OPTIONS\": \"" + SERVICE_OPTIONS + "\"" +
                ", \"ROAMING\": " + ROAMING +
                ", \"MSISDN\": \"" + MSISDN + "\"" +
                ", \"RSU\": " + RSU +
                "}";
    }
}
