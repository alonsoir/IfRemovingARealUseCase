
# Refactoring - A real case of a nested if structure transformed into a chain of responsibility
In this post I describe step by step a process used to transform 
a nested if structure into a chain of responsibility. 
The code of this post is based on a piece of code used to satisfy a real business need, 
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
that transforms the if-nested structure into a chain of responsibility.
We are not going to change the tests.
The idea behind this refactor is to proceed with small steps, 
possibly using the IDE functionality (I used IDEA which is very good at it), 
and run the tests after every operation.
Also, after each step, there is a commit so that, in case of errors 
(and they happen), you can just use ```git checkout .``` 
to come back to the previous working version. 
All of this, will allow us to keep the code strictly under control and avoid to introduce bugs during the refactoring.

## The initial code
Here you can find the code we were not very proud of. 
In particular, I will report the nested if structure, which is the part we are going to refactor.
([Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/a6681dd088d06244878e0527e87b4c6b5bbfd50d/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java))
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

## The execution
### 1 - Flatten the if structure
The idea here is to transform the nested if structure into a sequence of flat ifs in order to isolate
and explicit each single condition.  
To do so with very small steps, we are going to remove all the `else` parts of the ifs, by transforming 
each one into an if with the condition which is the negation of the original.
In the following piece of code, you can notice how the outer if-else has become a couple of conditions, 
one for the original condition `flight.isOneWay()` and the other one with the opposite condition `!flight.isOneWay()`
([Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/a49340d05153074158cc59c130de6875276a92ab/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java))
```diff
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
        }
-       else {
+       if (!flight.isOneWay()) {  //round trip
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

        return noMyCompanyInformationInfo();
    }
}
```

Once done this, we are going to proceed with the inner `if-else` conditions, which is `isMyCompany(flight)`.
([Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/193aab6e25e83ba9c453b87961fb1582b0a63828/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)) 
```diff
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
            }
-           else {
+           if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        if (!flight.isOneWay()) {  //round trip
            if (isMyCompany(flight)) {
                LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
                LocalDate returnDepartureDate = order.getReturnDepartureDate();
                if (outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                } else {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }

            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        return noMyCompanyInformationInfo();
    }
}
```

We proceed in this way until we have removed all the `else` conditions from the code. 
Notice that, here, you are not forced to start from the outside, but you can choose whatever position you prefer to start with.  
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        if (flight.isOneWay()) {
            if (isMyCompany(flight)) {
                LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
                if (flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                }

                if (!flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }

            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        if (!flight.isOneWay()) {  //round trip
            if (isMyCompany(flight)) {
                LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
                LocalDate returnDepartureDate = order.getReturnDepartureDate();
                if (outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                }

                if (!(outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31)))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }

            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        return noMyCompanyInformationInfo();
    }
}
```
>[Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/d496798575f2ee7487f1f2a04d0ce124dbb921c2/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)


Once removed all the `else`, we are going to duplicate the conditions 
in order to have only one condition inside another condition.  
We start with `isMyCompany(flight)` in case of one way flight.   
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        if (flight.isOneWay()) {
            LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
            if (isMyCompany(flight)) {
                if (flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                }

            }
            if (isMyCompany(flight)) {
                if (!flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }

            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        if (!flight.isOneWay()) {  //round trip
            LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
            LocalDate returnDepartureDate = order.getReturnDepartureDate();
            if (isMyCompany(flight)) {
                if (outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                }

            }
            if (isMyCompany(flight)) {
                if (!(outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31)))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }

            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        return noMyCompanyInformationInfo();
    }
}
```  
>[Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/623019ec167ea7a0e6e5c0b0057d2bf8a83da9f1/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

After having done this process for all the if conditions,
 we will finally get the flatten if structure
```java
public class HandBaggageInformationFactory {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);
    private static final LocalDate THIRTY_FIRST_OF_OCTOBER = LocalDate.of(2018, 10, 31);

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        if (flight.isOneWay()
                && isMyCompany(flight)
                && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
        }

        if (flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
        }

        if (flight.isOneWay() && !isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
        }

        if (!flight.isOneWay() && !isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
        }

        return noMyCompanyInformationInfo();
    }
}
``` 
>[Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/82d8c21bf684feeaf6d342a7b6f36409bd30acb6/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

### Intermediate step - Extracting factories
Before keeping on with the extraction of the chain of responsibility from the if structure, we will make some intermediate steps. 
In order to reduce the responsibilities of the `HandBaggageInformationFactory`, here, 
we are going to extract three factories, each one responsible for creating a specific `HandBaggageInformation`.
Without diving into the code used to create the object, we just extract the `NewMyCompanyHandBaggageInformationFactory`,
out of the method `newMyCompanyHandBaggageInformation`.
If you are using IDEA, an easy way is to do it is to use the `Extract method object` feature of the IDE. 
I won't explain how to do it here, because it is out of the scope of this topic, but I have just realized I have found 
the next topic of my blog (this is great! isn't it? ;)). 
 
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);

        if (flight.isOneWay()
                && isMyCompany(flight)
                && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return newMyCompanyHandBaggageInformationFactory.execute(renderLanguage);
        }

        if (flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
        }

        if (flight.isOneWay() && !isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
            return newMyCompanyHandBaggageInformationFactory.execute(renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
        }

        if (!flight.isOneWay() && !isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
        }

        return noMyCompanyInformationInfo();
    }
}
```
>[Step 1](https://github.com/bonfa/IfRemovingARealUseCase/blob/5419c7d777f7562f89c65d55a83181b787a7c9eb/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java),
[Step 2](https://github.com/bonfa/IfRemovingARealUseCase/blob/3b6651b149932befca35c40a02c4bbd79cfab8d9/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)
and [Step 3](https://github.com/bonfa/IfRemovingARealUseCase/blob/ed33c4490bccfc828391516c12561b83d0428000/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

Once done this, we repeat the operation for the other two methods that create the objects, obtaining the 
`NotMyCompanyHandBaggageInformationFactory` and the `OldMyCompanyHandBaggageInformationFactory`.
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        if (flight.isOneWay()
                && isMyCompany(flight)
                && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (flight.isOneWay() && !isMyCompany(flight)) {
            return notMyCompanyHandBaggageInformationFactory.make();
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
            return newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && !isMyCompany(flight)) {
            return notMyCompanyHandBaggageInformationFactory.make();
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/71a7962d72ffa2581beef76c494f2389f0526059/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

### 2 - Creating the components of the chain

By using again the `Extract method object feature` of Idea, you can easily extract the first condition into a class.
In this way we get `new MyCompanyOneWayAfterTheFirstOfNovember().canHandle(flight, flightOutboundDate)` in the first 
`if` condition.

```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        if (new MyCompanyOneWayAfterTheFirstOfNovember().canHandle(flight, flightOutboundDate)) return newMyCompanyHandBaggageInformationFactory.from(renderLanguage);

        if (flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (flight.isOneWay() && !isMyCompany(flight)) {
            return notMyCompanyHandBaggageInformationFactory.make();
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
            return newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && !isMyCompany(flight)) {
            return notMyCompanyHandBaggageInformationFactory.make();
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }

    private class MyCompanyOneWayAfterTheFirstOfNovember {
        public boolean canHandle(Flight flight, LocalDateTime flightOutboundDate) {
            return flight.isOneWay()
                    && isMyCompany(flight)
                    && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER);
        }
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/6767ecfcbc8b1f90f38f525c2d2d7522d25fafb4/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

And, after that, we can move `newMyCompanyHandBaggageInformationFactory.from(renderLanguage)` inside 
`MyCompanyOneWayAfterTheFirstOfNovember`. 
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                        new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
                if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight, flightOutboundDate)) {
                    return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
                }

        if (flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (flight.isOneWay() && !isMyCompany(flight)) {
            return notMyCompanyHandBaggageInformationFactory.make();
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
            return newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && !isMyCompany(flight)) {
            return notMyCompanyHandBaggageInformationFactory.make();
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }

    private class MyCompanyOneWayAfterTheFirstOfNovember {
    
        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;
    
        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }
        
        public boolean canHandle(Flight flight, LocalDateTime flightOutboundDate) {
            return flight.isOneWay()
                        && isMyCompany(flight)
                        && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER);
        }
    
        public HandBaggageInformation getFrom(String renderLanguage) {
                return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
```

Then, we repeat the operation for all the conditions but the ones that use our default value, which is the one 
created with `notMyCompanyHandBaggageInformationFactory.make()`. 
To be short here, we make every single condition to create different values, and join all the ones that creates the 
default one into the default behavior. So we won't extract any class for that. 
I skip this logic, because it's not so interesting but there are commits that show this step by step. 
The resulting code, after having extracted the conditions, is the following.
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight, flightOutboundDate)) {
            return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyOneWayBeforeTheFirstOfNovember myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight, flightOutboundDate)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember.canHandle(flight, outboundDepartureDate, returnDepartureDate)) {
            return myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember
                    .getFrom(renderLanguage);
        }

        MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.canHandle(flight, outboundDepartureDate, returnDepartureDate)) {
            return myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }

    private class MyCompanyOneWayAfterTheFirstOfNovember {

        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight, LocalDateTime flightOutboundDate) {
            return flight.isOneWay()
                    && isMyCompany(flight)
                    && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER);
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyOneWayBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight, LocalDateTime flightOutboundDate) {
            return flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER);
        }

        private HandBaggageInformation getFrom(String renderLanguage) {
            return this.oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember {
        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        public MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight, LocalDateTime outboundDepartureDate, LocalDate returnDepartureDate) {
            return !flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                    || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER));
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        private boolean canHandle(Flight flight, LocalDateTime outboundDepartureDate, LocalDate returnDepartureDate) {
            return !flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                    || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)));
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/529ca3d37906d6c94ae3bb28ecf810e3f9e75e3b/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

### 3 - Create a common signature to extract the chain item
With another intermediate step, we are going to simplify again the code. 
I am not going to explain in detail this part because it's more domain oriented 
but I need to show the differences in order to justify the modifications on the code you will notice. 
Just notice that the `Order` passed into some conditions has the same data as the `Flight` so we remove it. 
Also, we perform some simplifications on the dates, basically we keep only one date, which is our threshold. 
Finally, we move some operations from the HandBaggageInformationFactory class to the Flight one, which is more
domain oriented.
So, after these operations the code looks like
```java
public class HandBaggageInformationFactory {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyOneWayBeforeTheFirstOfNovember myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember
                    .getFrom(renderLanguage);
        }

        MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }


    private class MyCompanyOneWayAfterTheFirstOfNovember {

        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight) {
            return flight.isOneWay()
                    && flight.isMyCompany()
                    && flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyOneWayBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight) {
            return flight.isOneWay()
                    && flight.isMyCompany()
                    && !flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
        }

        private HandBaggageInformation getFrom(String renderLanguage) {
            return this.oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember {
        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        public MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                    );
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        private boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (!(flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER))
                    );
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
``` 
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/7936f7daf9d01d4139b0fcda9980078978009d7a/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java) 

### 4 - Extracting the interface of the chain item
By watching closely all the extracted conditions after the simplifications made, 
you can notice that now all the items has a common method signature.
And if you think that is time of an interface, you are totally right. 
So, thanks again to Idea, we can easily extract an interface from one of our conditions, 
for example `MyCompanyOneWayAfterTheFirstOfNovember`.
If you use Idea, `Extract interface` can be helpful.
```java
public class HandBaggageInformationFactory {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyOneWayBeforeTheFirstOfNovember myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember
                    .getFrom(renderLanguage);
        }

        MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }


    private class MyCompanyOneWayAfterTheFirstOfNovember implements HandBaggageInformationPolicy {

        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return flight.isOneWay()
                    && flight.isMyCompany()
                    && flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyOneWayBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight) {
            return flight.isOneWay()
                    && flight.isMyCompany()
                    && !flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
        }

        private HandBaggageInformation getFrom(String renderLanguage) {
            return this.oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember {
        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        public MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                    );
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        private boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (!(flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER))
                    );
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
``` 
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/3e1cac2443d4bd5b0929917b2fc95808a21bc9ca/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

And then, unfortunately Idea won't help us in this, we are going to make all the conditions implement the interface `HandBaggageInformationPolicy` 
```java
public class HandBaggageInformationFactory {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        //same as before
    }


    private class MyCompanyOneWayAfterTheFirstOfNovember implements HandBaggageInformationPolicy {

        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return flight.isOneWay()
                    && flight.isMyCompany()
                    && flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyOneWayBeforeTheFirstOfNovember implements HandBaggageInformationPolicy {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return flight.isOneWay()
                    && flight.isMyCompany()
                    && !flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember implements HandBaggageInformationPolicy {
        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        public MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                    );
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember implements HandBaggageInformationPolicy {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (!(flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER))
                    );
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/9cf7b408ff15217894b3e101b47886f8ce993a97/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

Then it's time to extract each policy into its own file and move all the policies into a package, 
that for the sake of fanciful names, we will call `policy`. 
To do this, we need to duplicate our threshold into more than one policy implementation. 
This could be arguable because it is a duplication. Yes, actually it is. But in our real case problem, 
the date will pass soon, wo we applied the ostrich algorithm (i.e. we ignored this discussion).  
Also we decide to move the creation of all the policies before the evaluation, for a reason you will understand in the next step.
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        MyCompanyOneWayBeforeTheFirstOfNovember myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

        if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
        }

        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        if (myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember
                    .getFrom(renderLanguage);
        }

        if (myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/66da72572de64865537c8baf8f24499ee6b841b7/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

### 6 - Inject the chain at construction time

And then we create a loop of policies and move all of them inside the loop, so we have removed the chain of `if`.
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        HandBaggageInformationPolicy myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

        List<HandBaggageInformationPolicy> policies = Arrays.asList(
                myCompanyOneWayAfterTheFirstOfNovember,
                myCompanyOneWayBeforeTheFirstOfNovember,
                myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember,
                myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember
        );

        for (HandBaggageInformationPolicy policy : policies) {
            if (policy.canHandle(flight)) {
                return policy.getFrom(renderLanguage);
            }
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/4320112c0b1080ad59d89af2ea530c10f47ba57c/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

If you prefer, you can use a stream instead of a classical loop
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        HandBaggageInformationPolicy myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

        List<HandBaggageInformationPolicy> policies = Arrays.asList(
                myCompanyOneWayAfterTheFirstOfNovember,
                myCompanyOneWayBeforeTheFirstOfNovember,
                myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember,
                myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember
        );


        return policies.stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(notMyCompanyHandBaggageInformationFactory.make());
    }
}
```


Now we are going to use again the `Extract method object` feature of the IDE (I know, it's getting kind of repetitive) 
in order to extract the `HandBaggagePoliciesFactory` object, which is responsible for creating the policies.
 
```java
public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        List<HandBaggageInformationPolicy> policies = new HandBaggagePoliciesFactory().make(translationRepository);

        return policies.stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(notMyCompanyHandBaggageInformationFactory.make());
    }

    private static class HandBaggagePoliciesFactory {
        public List<HandBaggageInformationPolicy> make(TranslationRepository translationRepository) {
            NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                    new NewMyCompanyHandBaggageInformationFactory(translationRepository);
            OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                    new OldMyCompanyHandBaggageInformationFactory(translationRepository);

            HandBaggageInformationPolicy myCompanyOneWayAfterTheFirstOfNovember =
                    new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
            HandBaggageInformationPolicy myCompanyOneWayBeforeTheFirstOfNovember =
                    new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
            HandBaggageInformationPolicy myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                    new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
            HandBaggageInformationPolicy myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                    MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

            return Arrays.asList(
                    myCompanyOneWayAfterTheFirstOfNovember,
                    myCompanyOneWayBeforeTheFirstOfNovember,
                    myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember,
                    myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember
            );
        }
    }
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/0255c3442abd34a07832a367ca0ee04bfc624659/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

And after extracting the class in its own file, we are going to inject the policies as parameter at construction time
of HandBaggageInformationFactory.
To make the IDE work for you, from the previous code, you can make the `policies` variable of `from` method become a field, 
with the command `Extract field`, and decide to define it in the constructor. 
Then in the constructor, you can simply use the `Extract parameter` feature in order to update all the constructors of 
your object.
```java
public class HandBaggageInformationFactory {

    private final List<HandBaggageInformationPolicy> handBaggageInformationPolicies;

    public HandBaggageInformationFactory(List<HandBaggageInformationPolicy> handBaggageInformationPolicies) {
        this.handBaggageInformationPolicies = handBaggageInformationPolicies;
    }

    public HandBaggageInformation from(Order order, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        return handBaggageInformationPolicies.stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(notMyCompanyHandBaggageInformationFactory.make());
    }

}
```
In our example, only the `HandBaggageInformationFactoryTest` has been updated.
```java
public class HandBaggageInformationFactoryTest {

    private HandBaggageInformationFactory handBaggageInformationFactory;

    @Before
    public void setUp() {
        TranslationRepository translationRepository = Mockito.mock(TranslationRepository.class);
        handBaggageInformationFactory =
                new HandBaggageInformationFactory(HandBaggagePoliciesFactory.make(translationRepository));
        }
        
    //The rest is the same    
}
```
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/99d743baa3d65c8041cb09af6dde757408914272/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)   

For consistency purpose, we are going to inject also the `NotMyCompanyHandBaggageInformationFactory` into the 
`HandBaggageInformationFactory`.
```java
public class HandBaggageInformationFactory {

    private final List<HandBaggageInformationPolicy> handBaggageInformationPolicies;
    private final NotMyCompanyHandBaggageInformationFactory fallbackHandBaggageFactory;

    public HandBaggageInformationFactory(List<HandBaggageInformationPolicy> handBaggageInformationPolicies,
                                         NotMyCompanyHandBaggageInformationFactory fallbackHandBaggageFactory) {
        this.handBaggageInformationPolicies = handBaggageInformationPolicies;
        this.fallbackHandBaggageFactory = fallbackHandBaggageFactory;
    }

    public HandBaggageInformation from(Order order, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        return handBaggageInformationPolicies.stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(fallbackHandBaggageFactory.make());
    }
}
``` 
and then, again, the `HandBaggageInformationFactoryTest` gets updated.
```java
public class HandBaggageInformationFactoryTest {

 private HandBaggageInformationFactory handBaggageInformationFactory;

     @Before
     public void setUp() {
         TranslationRepository translationRepository = Mockito.mock(TranslationRepository.class);
         handBaggageInformationFactory =
            new HandBaggageInformationFactory(
                HandBaggagePoliciesFactory.make(translationRepository),
                    new NotMyCompanyHandBaggageInformationFactory()
            );
         
     //The rest is the same    
    }
}
```   
> [Source code](https://github.com/bonfa/IfRemovingARealUseCase/blob/eac1ba3e1dc8f5cce3ba6c545349cd6d4730671e/src/main/java/it/fbonfadelli/hand_baggage/HandBaggageInformationFactory.java)

## Conclusion
todo