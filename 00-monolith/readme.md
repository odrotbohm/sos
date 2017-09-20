# The Monolith

This module contains a monolithic implementation of the domain.
The individual Bounded Contexts are represented by Java packages.

![Bounded Context interaction](images/monolith.png?raw=true "Monolithic Bounded Context interaction")

## Important aspects

* There's a strong relationship between the domain objects even across BC boundaries.
* The interaction between the BCs is implemented by injecting a Spring bean into a service of the BC that wants to trigger the functionality and invoking a method on that bean (see `OrderManager.completeOrder(…)`)
