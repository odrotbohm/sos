# System of Systems interaction using messaging

This module contains an implementation of the domain using messaging via Kafka.

![Bounded Context interaction](images/sos-messaging.png?raw=true "Messaging-based Bounded Context interaction")

## Important aspects

* The centerpiece of this architecture is a Kafka message broker that the individual systems talk to for publication of messages and subscriptions.
* The domain code is still using Spring's application events and the Kafka communication is the added via dedicated components (see `integration` packages of the individual modules).
