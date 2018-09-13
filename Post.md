
# Refactoring - A real case of a nested if structure transformed into a chain of responsibility
In this post I describe step by step a process used to transform 
a nested if structure into a chain of responsibility. 
The code used in this post is based on a real piece of code used to satisfy a real business need, 
we just removed the business related details.
The language used is java.

## The need
It seemed a normal day of work when one of our managers called a meeting 
to inform us of a very urgent business need that should be put in production 
within 2 days.
So, as usually happens in this case, between the deriving chaos and the ton of alignment 
meetings that continuously interrupt us, 
we produced a code that basically "worked", but it was a bit chaotic. 
Luckily we were able at least to write the tests.

## The process
We are going to see a step by step refactor of a specific class 
that transform the if nested structure into a chain of responsibility.
We are not going to change the tests.
The idea behind this refactor is to proceed with small steps, 
possibly using the IDE functionality (I used IDEA that allows very good), 
and run the tests after every operation.
Also, after each step, there is a commit so that, in case of errors 
(and they already happen), you can just use ```git checkout .``` 
to come back to the previous working version. 
All pf this, will allow us to keep the code strictly under control and avoid to introduce bugs.

##The initial code
#### a6681dd
Here you can find the code we were not very proud of. 
In particular, I will report the nested if structure, which is the part we are going to refactor.
```java
public class HandBaggageInformationFactory {

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        if (flight.isOneWay()) {
            if (isMyCompany(flight)) {
                LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
                if (flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                } else {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            } else {
                return noMyCompanyInformationInfo();
            }
        } else { //round trip
            if (isMyCompany(flight)) {
                LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
                LocalDate returnDepartureDate = order.getReturnDepartureDate();
                if (outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                } else {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            } else {
                return noMyCompanyInformationInfo();
            }
        }
    }
}
```