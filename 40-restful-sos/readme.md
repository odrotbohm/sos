# System of System integration using REST

This module contains the domain implemented as System of Systems using REST as means for Bounded Context interaction.

![Bounded Context interaction](images/sos-rest.png?raw=true "RESTful Bounded Context interaction")

## Important aspects

* Domain events are treated as entities that can be persisted within the local transaction.
* They are then exposed as REST resources via HTTP (code for that lives in [sos-rest-events]).
* Event listeners are using service and resource discovery as well as polling to receive events.
