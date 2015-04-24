LBS4-Proxy
==========

#Introduction

## Overview
This framework can be used to run lagacy web applications designed to be working only in Internet Explorer (Version 8 or lower) on other browsers. This is done by rewriting parts of the application on the fly as it's bytes pass the wire.
The framework acts as proxy server, which get's requests by the browser, transforms them if needed and rewrites the results of such requests to make them understandable to modern browsers.

## Features
* Rewrite request URLs on the fly
* Rewrite response headers and content on the fly
  * Rewrite contents using regular expressions
  * Rewrite JavaScript based on AST

## Requirements
This framework need just [Java](http://java.com/en/) (Version 1.6 or higher) as runtime environment, to check out and compile you also need [Git](http://git-scm.com/), [Maven](http://maven.apache.org/) and a internet connection to resolve required artifacts.

# Installation & Usage
## Checkout from Git repository

Just run the following command to get a copy of the source code:
> git clone https://github.com/christianmahnke/lbs4-proxy.git

## Compile & package
To build a packaged version just run:
> mvn package

## Runing directly from Maven
To run the proxy directly from surces just call:
> mvn clean compile exec:exec

## Configuration
Configuration is done via Spring, see src/main/resources/beans.xml.
You can set the port the proxy listens to and the filter options there.

## Setting up your browser
To use the proxy server you need to point your browser to it, this is done in the network settings of your browser, without changes to the configuration of the proxy it listens to localhost:8080.