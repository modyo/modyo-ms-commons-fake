# modyo-ms-commons

## Abstracto

Artefacto común a todos los microservicios. Incluye elementos transversales de configuración, aspectos y seguridad.

## Instalación

`mvn clean install`

## Uso en proyectos de integración

### Dependencia en repositorio Maven local

```xml
<dependency>
    <groupId>com.modyo.services</groupId>
    <artifactId>modyo-ms-commons</artifactId>
    <version>0.0.9</version>
</dependency>
```

### Dependencia local en dir lib/

```xml
<dependency>
    <groupId>com.modyo.services</groupId>
    <artifactId>modyo-ms-commons</artifactId>
    <version>0.0.9</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/modyo-ms-commons-0.0.9.jar</systemPath>
</dependency>
```

### Variables de Entorno

| Variable                           | Description                                          | Default value                                                                       |
|:-----------------------------------|:-----------------------------------------------------|:------------------------------------------------------------------------------------|
| API_GATEWAY_NAME                   | Nombre del API en el API Gateway.                    | consorcio-certification-api
| API_GATEWAY_AUTHORIZER_CREDENTIALS | Rol para invocar al authorizer desde el API Gateway. | arn:aws:iam::762916547384:role/consorcio-certification-RestApiAuthorizerRole-14IUOS14IORZB
| API_GATEWAY_URI_AUTHORIZER         | URL del Authorizer.                                  | arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:762916547384:function:LambdaAuthorizer/invocations
| API_GATEWAY_CONNECTION_ID          | ID del VPC Link.                                     | 9t44js
| API_GATEWAY_BASE                   | URL base del API GW.                                 | http://consorcio-certification-nlb-int-2987ec161d61becd.elb.us-east-1.amazonaws.com
| API_GATEWAY_AUTHORIZER_NAME        | Nombre del Lambda Authorizer.                        | ApiGWLambdaAuthorizer
| MODO_PRIVADO                       | Si está en true, se aplicará el lambda authorizer a los endpoints que no son OPTIONS. Si es falso no se aplica Lambda authorizer. | false

Modyo ©

