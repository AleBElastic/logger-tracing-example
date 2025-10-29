# logger-tracing-example
This repository contains configuration files and examples to deploy a simple java application that generates logs and traces. We will see how to collect logs relying on the EDOT collector provided by Elastic and how to manipulate its configuration file

##ecs-log4j-generator
It includes a simple Java application that uses Log4j2 and integrates the ECS plugin to properly format logs into ECS. The collection is done via the EDOT collector and send data via OTLP to an Elastic serverless deployment

##ecs-logger-tracing
It includes a simple Java application that generates fake spans and rely on EDOT Java SDK to collect traces and logs and send via OTLP to an Elastic serverless deployment
