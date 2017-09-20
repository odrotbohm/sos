# Refactoring to a System of Systems

_NOTE: This is currently highly work in progress. Be sure to check out every now and then for more detailed information and explanations about the individual modules._

This repository contains some sample code for my talk "Refactoring to a System of Systems" that outlines what problems developers can run into if they split up a system into multiple ones but transfer interaction patterns of typical monolithic applications as is.
Slides of that talk and a recording of it will be linked here as soon as they become available.

The repository contains five different projects, each of them potentially split into multiple ones in turn.
For details on the individual ones, please refer to the READMEs conatined in the individual project's root.

## Context

The sample application is built around an e-commerce domain with the following Bounded Contexts:

* _Catalog_ - containing product master data (in our case product name and price).
* _Inventory_ - keeping track of the number of available items per product.
* _Order_ - keeping track of orders placed by customers.

![Domain](images/domain.png?raw=true "Domain")

The individual modules provide sketch implementations of these Bounded Context and the following interactions between them implemented:

1. When a product is added, the inventory needs to register empty stock for that product.
2. When an order is completed, the inventory needs to update the stock for the products ordered.

## Modules

* [_The Monolith_](00-monolith) - a typical monolithic Spring Boot application with the Bounded Contexts implemented in packages and the interaction being based on active invocations of Spring beans residing in a different BC.
* [_The Microlith_](10-microlith) - the former approach transferred into separate systems but keeping the same interaction patter of synchronous, non-idempotent operations. The systems invoking each other via HTTP calls.
* [_The Modulith_](20-modulith) - an improved version of the modulith with the Bounded Contexts interacting via Spring application events and event listeners.
* [_SOS Messaging_](30-messaging-sos) - the individual Bounded Contexts implemented as separate systems and the interaction implemented via a Kafka message broker.
* [_SOS REST_](40-rest-sos) - the individual Bounded Contexts implemented as separate systems and the interaction implemented via the events exposed as REST resources.

![Project structure](images/structure.png?raw=true "Project structure")

## Build

The repository should build by simply running `mvn clean install` using a recent JDK 8.
Sample code uses Lombok, which means that you need a Lombok-enabled IDE in case you want to import the projects into it.
For detailed instructions about how to work with the individual projects, see the individual module's READMEs.
