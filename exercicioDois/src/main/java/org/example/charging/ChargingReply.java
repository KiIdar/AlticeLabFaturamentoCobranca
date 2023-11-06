package org.example.charging;

public class ChargingReply {
    private final long REQUEST_ID;
    private final String RESULT;
    //Granted Service Units
    private final int GSU;

    public ChargingReply(long REQUEST_ID, String RESULT, int GSU) {
        this.REQUEST_ID = REQUEST_ID;
        this.RESULT = RESULT;
        this.GSU = GSU;
    }

    @Override
    public String toString() {
        return String.format("ChargingReply{REQUEST_ID=%d, RESULT=%s, GSU=%d}", REQUEST_ID, RESULT, GSU);
    }
}
