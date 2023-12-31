package org.example.subscription_plan.tarifarios.b;

import org.example.charging.ChargingReply;
import org.example.charging.ChargingRequest;
import org.example.client.BillingAccount;
import org.example.client.CDR;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Beta1 {
    private CDR cdr = CDR.getInstance();

    public ChargingReply chargingRequest(ChargingRequest chargingRequest, BillingAccount billingAccount) {

        billingAccount.addToCounterB(+1);

        cdr.newDataRecordInitialization(chargingRequest.getTIMESTAMP(),
                chargingRequest.getMSISDN(),
                chargingRequest.getSERVICE(),
                this.getClass().getName(),
                chargingRequest);

        String checkEligibility = eligibility(chargingRequest);
        if (checkEligibility.equals("Ok")) {
            double totalCharge = rating(chargingRequest, billingAccount);
            checkEligibility = charging(chargingRequest, billingAccount, totalCharge);
        } else {
            ChargingReply chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(), checkEligibility, 0);
            saveLog(billingAccount, chargingReply);
            return chargingReply;
        }

        ChargingReply chargingReply;
        if (checkEligibility.equals("Ok")) {
            chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(), checkEligibility, chargingRequest.getRSU());

            billingAccount.setCounterD(chargingRequest.getTIMESTAMP());
        } else {
            chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(), checkEligibility, 0);
        }

        saveLog(billingAccount, chargingReply);
        return chargingReply;
    }

    private String eligibility(ChargingRequest chargingRequest) {

        // Convert the TIMESTAMP to a LocalDateTime
        LocalDateTime localDateTime = chargingRequest.getTIMESTAMP().toLocalDateTime();

        // Get the day of the week
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();

        // Check if it's a weekday (Monday to Friday)
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            return "Ok";
        } else {
            if (isNightTime(chargingRequest.getTIMESTAMP())) {
                return "Ok";
            } else {
                return "Não Eligivel: It's not night time during a weekend";
            }
        }
    }

    private double rating(ChargingRequest chargingRequest, BillingAccount billingAccount) {
        double unitPrice = 0.1; // Default price per UNIT

        // Apply pricing rules based on the situation
        if (chargingRequest.isROAMING()) {
            unitPrice = 2.0; // 2€ in roaming
            billingAccount.addToCounterC(+1);
        } else if (isNightTime(chargingRequest.getTIMESTAMP())) {
            unitPrice = 0.05; // 0.05€ for requests local at night
        }

        // Apply Delta pricing based on situation
        if (billingAccount.getCounterA() > 10) {
            unitPrice -= 0.025;
        }
        if (billingAccount.getBucketC() > 50) {
            unitPrice -= 0.010;
        }
        double totalCharge = chargingRequest.getRSU() * unitPrice;
        return totalCharge;
    }

    private String charging(ChargingRequest chargingRequest, BillingAccount billingAccount, double totalCharge) {

        if (!chargingRequest.isROAMING()) {
            if (totalCharge <= billingAccount.getBucketA()) {
                billingAccount.chargeBucketA(totalCharge);
                return "Ok";
            } else {
                return "CreditLimitReached";
            }
        } else if (chargingRequest.isROAMING() && billingAccount.getCounterB() > 5) {
            if (totalCharge <= billingAccount.getBucketB()) {
                billingAccount.chargeBucketB(totalCharge);
                return "Ok";
            } else {
                return "CreditLimitReached";
            }
        } else {
            if (totalCharge <= billingAccount.getBucketC()) {
                billingAccount.chargeBucketC(totalCharge);
                return "Ok";
            } else {
                return "CreditLimitReached";
            }
        }
    }

    private void saveLog(BillingAccount billingAccount, ChargingReply chargingReply) {
        cdr.setChargingReply(chargingReply);
        cdr.setBucketsValue(new double[]{billingAccount.getBucketA(), billingAccount.getBucketB(), billingAccount.getBucketC()});

        ArrayList<Object> counterValues = new ArrayList<>();
        counterValues.add(billingAccount.getCounterA());
        counterValues.add(billingAccount.getCounterB());
        counterValues.add(billingAccount.getCounterC());
        counterValues.add(billingAccount.getCounterD());

        cdr.setCountersValue(counterValues);
        cdr.saveLog();
    }

    private static boolean isNightTime(Timestamp timestamp) {
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
