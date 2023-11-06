package org.example;

import org.example.charging.ChargingRequest;
import org.example.client.BillingAccount;
import org.example.client.CDR;
import org.example.subscription_plan.tarifarios.Servives;

import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static CDR cdr = CDR.getInstance();

    public static void main(String[] args) {
        int id = 0;
        String userInput;


        //Creating some billing accounts
        BillingAccount[] billingAccounts = new BillingAccount[2];
        billingAccounts[0] = new BillingAccount("1111111111", Servives.TarifarioServiçoA.Alfa1, Servives.TarifarioServiçoB.Beta2);
        billingAccounts[1] = new BillingAccount("2222222222", Servives.TarifarioServiçoA.Alfa3, Servives.TarifarioServiçoB.Beta1);

        //Adding values to buckets
        billingAccounts[0].setBucketA(100);
        billingAccounts[0].setBucketB(100);
        billingAccounts[0].setBucketC(100);


        while (true) {
            printMenu();
            userInput = scanner.nextLine();


            switch (userInput) {
                case "1":
                    ChargingRequest chargingRequest = createChargingRequest(id);
                    if (chargingRequest != null) {
                        int billingAccountIndex = isPartOfTheSystemPhoneNumber(billingAccounts, chargingRequest.getMSISDN());

                        if (billingAccountIndex != -1) {
                            System.out.println(billingAccounts[billingAccountIndex].chargingRequest(chargingRequest).toString());
                        }
                        id++;
                    }
                    break;
                case "2":
                    System.out.println("Phone number to search");
                    userInput = scanner.nextLine();
                    cdr.showCDRClient(userInput);
                    break;
                default:
                    System.out.println(userInput);
                    System.out.println("Not a valid option. Try again.");
            }
            scanner.nextLine();
        }
    }


    private static void printMenu() {
        System.out.println("1 - Make a charging request\n" +
                "2 - Check Logs");
    }

    private static ChargingRequest createChargingRequest(int nextRequestId) {
        // ChargingRequest variables
        long requestId = nextRequestId;
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        String serviceOptions;
        boolean roaming;
        String msisdn;
        int rsu;

        //Service Option
        System.out.println("Select which service you will use (A / B)");
        String userInput = scanner.nextLine().toUpperCase();

        switch (userInput) {
            case "A":
                serviceOptions = userInput;
                break;
            case "B":
                serviceOptions = userInput;
                break;
            default:
                System.out.println("Not a valid option. Ending charging request progress");
                return null;
        }

        //Roaming
        System.out.println("Roaming?\n" +
                "1 - Yes\n" +
                "2 - No");
        userInput = scanner.nextLine();

        switch (userInput) {
            case "1":
                roaming = true;
                break;
            case "2":
                roaming = false;
                break;
            default:
                System.out.println("Not a valid option. Ending charging request progress");
                return null;
        }

        //MSISDN
        System.out.println("Enter a phone number (10 digits)");
        userInput = scanner.nextLine();

        if (isValidPhoneNumber(userInput)) {
            msisdn = userInput;
        } else {
            System.out.println("Wrong format for a phone number, make sure it's all numbers and has 10 digit. Ending charging request progress");
            return null;
        }
        //RSU
        System.out.print("Enter RSU\n");

        try {
            int userNumber = scanner.nextInt();
            if (userNumber <= 0) {
                System.out.println("Please enter a positive number. Ending charging request progress.");
                return null;
            }
            rsu = userNumber;
        } catch (InputMismatchException e) {
            System.out.println("Wrong input detected. Ending charging request progress.");
            return null;
        }

        return new ChargingRequest(requestId, timeStamp, serviceOptions, roaming, msisdn, rsu);
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters (e.g., spaces, dashes)
        phoneNumber = phoneNumber.replaceAll("\\D", "");


        return phoneNumber.length() == 10;
    }

    public static int isPartOfTheSystemPhoneNumber(BillingAccount[] billingAccounts, String phoneNumber) {

        for (int i = 0; i < billingAccounts.length; i++) {
            if (billingAccounts[i].getMsisdn().equals(phoneNumber)) {
                return i;
            }
        }
        System.out.println("Phone number doesn't match with any of our billing accounts");
        return -1;
    }
}