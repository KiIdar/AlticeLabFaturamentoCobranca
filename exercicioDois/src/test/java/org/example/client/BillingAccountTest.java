package org.example.client;

import org.example.charging.ChargingReply;
import org.example.charging.ChargingRequest;
import org.example.subscription_plan.tarifarios.Servives;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class BillingAccountTest {

    @org.junit.Test
    public void chargingRequest() {
        //Setting up a billing account
        BillingAccount billingAccount = new BillingAccount("1111111111", Servives.TarifarioServiçoA.Alfa1, Servives.TarifarioServiçoB.Beta2);
        billingAccount.setBucketA(100);
        billingAccount.setBucketB(100);
        billingAccount.setBucketC(100);

        String dateTimeString = "2023-11-06 15:30:45.123";

        // Define the date and time format to match the provided string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Timestamp customTimestamp = null;
        try {
            // Parse the input date and time string
            java.util.Date parsedDate = dateFormat.parse(dateTimeString);

            // Create a Timestamp object from the parsed Date
            customTimestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date and time format: " + e.getMessage());
        }

        String serviceOption = "A";
        boolean isRoaming = true;
        String msisdn = "1111111111";
        int rsu = 20;

        ChargingRequest chargingRequest = new ChargingRequest(0, customTimestamp, serviceOption, isRoaming, msisdn, rsu);

        ChargingReply expectedChargingReply = new ChargingReply(0,"Ok", 20);
        ChargingReply actualChargingReply = billingAccount.chargingRequest(chargingRequest);

        assertEquals(expectedChargingReply.toString(), actualChargingReply.toString());

    }
}