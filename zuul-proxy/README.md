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
  Order of processing:
  1. Deny global settings
  2. Allow global settings
  3. Deny service level settings
  4. Allow service level settings
  
  **The later settings have more priority.**
  
  Supported values for settings are:
  1. `'*'` - all
  2. `xxx.xxx.xxx.xxx/xx` - CIDR notation
  Example:
  
            allowFrom: 192.168.0.0/12
        
        This will match any address starting with 192.168
  
  Multiple ranges are now supported for every setting, they must be comma separated.
  
  `peppol.zuul.headers` is a section for configuring headers to preserve for requests.
  
  It has only one directive: `peppol.zuul.headers.headersToPreserve` which takes comma separated list of values.
   Example: 
   
        headersToPreserve: Host, Content-Type

  Note: "Service" header is reserved for internal use. Passing it in configuration can lead to unpredictable results. 