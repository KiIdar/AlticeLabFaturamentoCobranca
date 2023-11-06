package org.example.subscription_plan.tarifarios.b;

import org.example.charging.ChargingReply;
import org.example.charging.ChargingRequest;
import org.example.client.BillingAccount;
import org.example.client.CDR;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Beta3 {
    private CDR cdr = CDR.getInstance();

    public ChargingReply chargingRequest(ChargingRequest chargingRequest, BillingAccount billingAccount) {

        cdr.newDataRecordInitialization(chargingRequest.getTIMESTAMP(),
                chargingRequest.getMSISDN(),
                chargingRequest.getSERVICE(),
                this.getClass().getName(),
                chargingRequest);

        String checkEligibility = eligibility(chargingRequest, billingAccount);
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

            billingAccount.addToCounterC(+1);

            billingAccount.setCounterD(chargingRequest.getTIMESTAMP());
        } else {
            chargingReply = new ChargingReply(chargingRequest.getREQUEST_ID(), checkEligibility, 0);
        }

        saveLog(billingAccount, chargingReply);
        return chargingReply;
    }

    private String eligibility(ChargingRequest chargingRequest, BillingAccount billingAccount) {
        if (!chargingRequest.isROAMING()) {
            if (billingAccount.getBucketC() > 10) {
                return "Não Eligivel: Bucket C > 10";
            }
            return "Não Eligivel: Call is local";
        }

        return "Ok";
    }

    private double rating(ChargingRequest chargingRequest, BillingAccount billingAccount) {
        double unitPrice = 0.1; // Default price per UNIT.

        // Convert the TIMESTAMP to a LocalDateTime
        LocalDateTime localDateTime = chargingRequest.getTIMESTAMP().toLocalDateTime();

        // Get the day of the week
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();

        // Apply pricing rules based on the situation
        // Check if it's a weekend
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            unitPrice = 0.025;
        }

        // Apply Delta pricing based criteria
        if (billingAccount.getCounterB() > 10) {
            unitPrice -= 0.02;
        }
        if (billingAccount.getBucketB() > 15) {
            unitPrice -= 0.005;
        }
        double totalCharge = chargingRequest.getRSU() * unitPrice;
        return totalCharge;
    }

    private String charging(ChargingRequest chargingRequest, BillingAccount billingAccount, double totalCharge) {

        if (chargingRequest.isROAMING()) {
            if (totalCharge <= billingAccount.getBucketC()) {
                billingAccount.chargeBucketC(totalCharge);
                return "Ok";
            } else {
                return "CreditLimitReached";
            }
        }
        return "Não Eligivel: Call is local";
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
}
