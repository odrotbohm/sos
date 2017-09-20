# The Modulith

This module contains a more modular but monolithic implementation of the domain.
Bounded Context interaction is implemented using JVM-local Spring application events.

![Bounded Context interaction](images/modulith.png?raw=true "Modulithic Bounded Context interaction")

## Important aspects

* Compared to the monolith we have cut the dependency cycle between the orders and the inventory as now the inventory listens to the events that are published by orders.
* We're using a [tiny Spring extension](https://github.com/olivergierke/spring-domain-events) to make sure that transactional event publications survive listener execution or system failure.
