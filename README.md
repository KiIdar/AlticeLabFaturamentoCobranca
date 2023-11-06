# AlticeLabFaturamentoCobranca
Sistema de Faturamento e Cobrança em Telecomunicações com Tarifários Dinâmicos

# Documentation
MAIN
- Creation of Billing accounts and setting the value of their buckets
-Loop -> Prompted to weather you want to make a request(multiple questions will be asked and if the input doesn't match what is expected the process ends) or check the logs of a specific phone number (msisdn)
- Once a ChargingRequest is created, we call the function of the billing account to prepare the process of making a request

BILLINGACCOUNT
- Will check which service and tariff(all tariffs are a class) will be used and send the request to that specific teriff (Example, Alpha1)

ALPHA1
- All tarrif classes share the same instance of CDR so throughout the process we will prepare a CDR to save to an arrayList<CDR>. At the end we call cdr.saveLogs() to essencially "commit" the log
- There are 3 main parts on these class
  - Eligibility -> Checks if the request is elibible or not
  - Rating -> calculates the price per unit based on criteria and returns the total price for that request
  - Charging -> will charge on the specified bucket depending on the criteria. If the bucket doesn't have enough, it returns a "CreditLimitReached" error on the ChargingReply
- Returns a ChargingReply back to the BillingAccount and back to Main

#####################################################################################################################################################################################################################
- Reason why I seperated all tarrifs (Alpha1, Alpha2, etc)  
R: Easier expansion later in case we would want to make new tarrifs and localized logic as well as keep the classes small. Each type also has it's own package for easy sorting so we have package "A" and package "B"
We also have the added benefit of them all sharing the same method names so when we call it on BillingAccount we don't need extra work; we only need to check which tarrif the billing account uses and parse the object

- Why CDR instance is shared between all tarrifs  
 R: Easier on the memory instead of having each instance of a BillingAccount to have their own CDR. We can simply take the cdr with the msisdn we want and sort it later when we want to look at the logs

- Private versus Public  
R: I tried to keep every method used by other classes public and those that are used within the class private for safety reasons. So generic reasons.

EXTRA
- On the exercise, I notice some inconsistencies such as the graph at the end would have extra conditions compared to when reading them a bit earlier. I assumed the table was the one I should follow
- Alpha 2 and Beta 2 have a condition of: if bucketB > 10 then not eligible........ But on the rating it showed a discount of: if bucketB > 15 than discount by -0.05. I don't know if this was intentional or not but based on the first condition, doesn't it mean the second one is impossible to happend?
- Wasn't sure how you wanted to handle CreditLimitReached situations so I just made it abort the entire process instead of trying to charge what it could and returning that.

# Tests
- Kept trying to provide wrong input during the preparation part of a ChargingReply. This included negative values / null inputs / characters when numbers were expected
- Mostly tested Alpha1 with diferent RSU to check if the correct bucket was charged and for the right amount. During the implementation of a test class, I also tested it for a weekend and it trigered as expected. Since most tarrifs are similar with minor changes, didn't test to much the others

