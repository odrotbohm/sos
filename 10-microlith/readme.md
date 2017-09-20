# The Microlith

This module contains an implementation of the domain with each Bounded Context implemented as separate Spring Boot application but keeping the same active invocation of functionality in other BCs around but lifting it from a JVM-local call to a remote HTTP invocation.

![Bounded Context interaction](images/microlith.png?raw=true "Microlithic Bounded Context interaction")

## Important aspects

* Synchronous remote communication opens up a big set of new error scenarios and challenges in general:
** We have to guard against the remote system being slow or completely unavailable (usually using libraries like Hystrix and reactive technologies).
** We have to have the downstream system (or a mock of it) running, when running an upstream system, which significantly complicates testing.
** We have to manage API evolution of downstream systems carefully.
