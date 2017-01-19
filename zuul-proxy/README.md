**Zuul Proxy Configuration**

Here is the sample config for Zuul proxy:


    spring:
      application:
        name: 'zuul-proxy'
    server:
      port: ${PORT:8989}
    zuul:
      routes:
        validator: /validator/**
    peppol:
      zuul:
        proxy:
          allowFrom: '*'
          denyFrom: 192.168.1.200
          servicesAllowFrom:
            validator: 192.168.1.110
          servicesDenyFrom:
            validator: 192.168.1.200


`spring.application.name` usually should be left as it is given in the begininng,
 unless you want some customization based on the app name 
 (separate routine related to service discovery).
 
 `server.port` - the port on which this service should run, it's either coming from 
 environment or defaults to _**8989**_
 
 `zuul.routes` sections is used for actual routing, note the format:
 _**service name** : **route**_, where _**route**_ can point to either service discovery 
  endpoint (e.g. service which is registered via service discovery) or 
  to actual URL in case of external(not registered) service.
  So our example shows that for any request coming to _**/validator**_ and any 
  depth of child sub-URLs 
  
  `peppol.zuul.proxy` is a section for configuring access either on 
  global level or fine tuned per-service basis (both ways work nicely together).
  Actually, when combining global setting such as 
  __**allowFrom**__ and __**denyFrom**__ then per-service settings allow to 
  override the global settings.
  
  Supported values for settings are:
  1. `'*'` - all
  2. `xxx.xxx.xxx.xxx` - IP address, yet you can give a range in a simplified
  form, like `xxx.xxx.xxx` or even `xxx.xxx`. Any string which would be sort of
  mask, yet without denoting the group, just host under check will be 
  checked if it starts with given value.
  Example:
  
            allowFrom: 192.168
        
        This will match any address starting with 192.168
  
  **NB!** Currently only single range is supported as a value for access settings, 
  addition of support for comma-separated list of ranges is planned in nearest future.
