package org.example.subscription_plan.tarifarios.b;

import org.example.charging.ChargingReply;
import org.example.charging.ChargingRequest;
import org.example.client.BillingAccount;
import org.example.client.CDR;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Beta2 {
    private CDR cdr = CDR.getInstance();
    public ChargingReply chargingRequest(ChargingRequest chargingRequest, BillingAccount billingAccount) {

        cdr.newDataRecordInitialization(chargingRequest.getTIMESTAMP(),
                chargingRequest.getMSISDN(),
                chargingRequest.getSERVICE(),
                this.getClass().getName(),
                chargingRequest);

        String checkEligibility = eligibility(chargingRequest, billingAccount);
        if(checkEligibility.equals("Ok"))
        {
            double totalCharge = rating(chargingRequest, billingAccount);
            checkEligibility = charging(chargingRequest, billingAccount, totalCharge);
        }
        else{
            ChargingReply chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(), checkEligibility, 0);
            saveLog(billingAccount, chargingReply);
            return chargingReply;
        }

        ChargingReply chargingReply;
        if(checkEligibility.equals("Ok"))
        {
            chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(),checkEligibility, chargingRequest.getRSU());

            billingAccount.setCounterD(chargingRequest.getTIMESTAMP());
        }else {
            chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(),checkEligibility, 0);
        }

        saveLog(billingAccount, chargingReply);
        return chargingReply;
    }

    private String eligibility(ChargingRequest chargingRequest, BillingAccount billingAccount) {
        if (chargingRequest.isROAMING()) {
            if (billingAccount.getBucketB() > 10)
            {
                return "Não Eligivel: Bucket B > 10";
            }
            return "Não Eligivel: Is roaming";
        }
        return "Ok";

    }

    private double rating(ChargingRequest chargingRequest, BillingAccount billingAccount) {
        double unitPrice = 0.05; // Default price per UNIT

        // Apply pricing rules based on the situation
        if (isNightTime(chargingRequest.getTIMESTAMP())) {
            unitPrice = 0.025; // 0.25€ for requests local at night
        }

        //Apply delta rules based on situation
        if (billingAccount.getCounterB() > 10) {
            unitPrice -= 0.02;
        }
        //How the heck does this condition work if all the requests are rejected on bucketB > 10?
        //How can we get a bucketB > 15 to get this discount?
        if (billingAccount.getBucketB() > 15) {
            unitPrice -= 0.005;
        }
        double totalCharge = chargingRequest.getRSU() * unitPrice;
        return totalCharge;
    }

    private String charging(ChargingRequest chargingRequest, BillingAccount billingAccount, double totalCharge) {

        if (!chargingRequest.isROAMING()) {
            if (totalCharge <= billingAccount.getBucketB()) {
                billingAccount.chargeBucketB(totalCharge);
                return "Ok";
            } else {
                return "CreditLimitReached";
            }
        }
        return "Não Eligivel: Is roaming";
    }
    private void saveLog(BillingAccount billingAccount, ChargingReply chargingReply)
    {
        cdr.setChargingReply(chargingReply);
        cdr.setBucketsValue(new double[]{billingAccount.getBucketA(),billingAccount.getBucketB(),billingAccount.getBucketC()});

        ArrayList<Object> counterValues = new ArrayList<>();
        counterValues.add(billingAccount.getCounterA());
        counterValues.add(billingAccount.getCounterB());
        counterValues.add(billingAccount.getCounterC());
        counterValues.add(billingAccount.getCounterD());

        cdr.setCountersValue(counterValues);
        cdr.saveLog();
    }
    public static boolean isNightTime(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        LocalTime currentTime = localDateTime.toLocalTime();

        // Define the start and end times for "night"
        LocalTime nightStartTime = LocalTime.of(22, 0); // 10 PM
        LocalTime nightEndTime = LocalTime.of(6, 0);   // 6 AM

        // Check if the current time is between the start and end times
        if (currentTime.isAfter(nightStartTime) || currentTime.isBefore(nightEndTime)) {
            return true;
        }

        return false;
    }
}
