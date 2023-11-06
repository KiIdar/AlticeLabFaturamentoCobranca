package org.example.client;

import org.example.charging.ChargingReply;
import org.example.charging.ChargingRequest;
import org.example.subscription_plan.tarifarios.Servives;
import org.example.subscription_plan.tarifarios.a.Alpha1;
import org.example.subscription_plan.tarifarios.a.Alpha2;
import org.example.subscription_plan.tarifarios.a.Alpha3;
import org.example.subscription_plan.tarifarios.b.Beta1;
import org.example.subscription_plan.tarifarios.b.Beta2;
import org.example.subscription_plan.tarifarios.b.Beta3;

import java.sql.Timestamp;

public class BillingAccount {
    private String msisdn; // Phone number
    private double bucketA, bucketB, bucketC;
    private int counterA; // Contagem de SU's do Serviço A
    private int counterB; // Contagem do numero de pedidos do Serviço B, sob tarifário Beta1;
    private int counterC; //Contagem de requesições em roaming
    private Timestamp counterD; //Registro da data da ultima requisição feita
    private Object serviceA;
    private Object serviceB;

    public BillingAccount(String msisdn,
                          Object serviceA,
                          Object serviceB) {

        this.msisdn = msisdn;
        this.bucketA = 0;
        this.bucketB = 0;
        this.bucketC = 0;
        this.counterA = 0;
        this.counterB = 0;
        this.counterC = 0;
        this.counterD = null;

        switch (serviceA) {
            case Servives.TarifarioServiçoA.Alfa1:
                this.serviceA = new Alpha1();
                break;
            case Servives.TarifarioServiçoA.Alfa2:
                this.serviceA = new Alpha2();
                break;
            case Servives.TarifarioServiçoA.Alfa3:
                this.serviceA = new Alpha3();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + serviceA);
        }

        switch (serviceB) {
            case Servives.TarifarioServiçoB.Beta1:
                this.serviceB = new Beta1();
                break;
            case Servives.TarifarioServiçoB.Beta2:
                this.serviceB = new Beta2();
                break;
            case Servives.TarifarioServiçoB.Beta3:
                this.serviceB = new Beta3();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + serviceB);
        }

    }

    public ChargingReply chargingRequest(ChargingRequest chargingRequest) {

        switch (chargingRequest.getSERVICE()) {
            case "A":
                return chargingRequestA(chargingRequest);
            case "B":
                return chargingRequestB(chargingRequest);
            default:
                throw new IllegalStateException("Unexpected value: " + chargingRequest.getSERVICE());
        }
    }

    private ChargingReply chargingRequestA(ChargingRequest chargingRequest) {
        if (serviceA instanceof Alpha1) {
            return ((Alpha1) serviceA).chargingRequest(chargingRequest, this);
        } else if (serviceA instanceof Alpha2) {
            return ((Alpha2) serviceA).chargingRequest(chargingRequest, this);
        } else if (serviceA instanceof Alpha3) {
            return ((Alpha3) serviceA).chargingRequest(chargingRequest, this);
        } else {
            System.out.println("Unsupported object type.");
        }
        return null;
    }

    private ChargingReply chargingRequestB(ChargingRequest chargingRequest) {
        if (serviceB instanceof Beta1) {
            return ((Beta1) serviceB).chargingRequest(chargingRequest, this);
        } else if (serviceB instanceof Beta2) {
            return ((Beta2) serviceB).chargingRequest(chargingRequest, this);
        } else if (serviceB instanceof Beta3) {
            return ((Beta3) serviceB).chargingRequest(chargingRequest, this);
        } else {
            System.out.println("Unsupported object type.");
        }
        return null;
    }

    public void chargeBucketA(double chargeAmount)
    {
        this.bucketA-= chargeAmount;
    }
    public void chargeBucketB(double chargeAmount)
    {
        this.bucketB-= chargeAmount;
    }
    public void chargeBucketC(double chargeAmount)
    {
        this.bucketC-= chargeAmount;
    }

    public void addToCounterA(int amount){this.counterA+= amount;}
    public void addToCounterB(int amount){this.counterB+= amount;}
    public void addToCounterC(int amount){this.counterC+= amount;}
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public double getBucketA() {
        return bucketA;
    }

    public void setBucketA(double bucketA) {
        this.bucketA = bucketA;
    }

    public double getBucketB() {
        return bucketB;
    }

    public void setBucketB(double bucketB) {
        this.bucketB = bucketB;
    }

    public double getBucketC() {
        return bucketC;
    }

    public void setBucketC(double bucketC) {
        this.bucketC = bucketC;
    }

    public int getCounterA() {
        return counterA;
    }

    public void setCounterA(int counterA) {
        this.counterA = counterA;
    }

    public int getCounterB() {
        return counterB;
    }

    public void setCounterB(int counterB) {
        this.counterB = counterB;
    }

    public int getCounterC() {
        return counterC;
    }

    public void setCounterC(int counterC) {
        this.counterC = counterC;
    }

    public Timestamp getCounterD() {
        return counterD;
    }

    public void setCounterD(Timestamp counterD) {
        this.counterD = counterD;
    }

    public Object getServiceA() {
        return serviceA;
    }

    public void setServiceA(Object serviceA) {
        this.serviceA = serviceA;
    }

    public Object getServiceB() {
        return serviceB;
    }

    public void setServiceB(Object serviceB) {
        this.serviceB = serviceB;
    }
}
