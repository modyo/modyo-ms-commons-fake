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
    <version>0.0.10</version>
</dependency>
```

### Dependencia local en dir lib/

```xml
<dependency>
    <groupId>com.modyo.services</groupId>
    <artifactId>modyo-ms-commons</artifactId>
    <version>0.0.10</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/modyo-ms-commons-0.0.10.jar</systemPath>
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
| CONTACT_NAME                       | Nombre de Contacto Técnico                           | Walter Farina                                                                                                                             |
| CONTACT_URL                        | Sitio Web de Contacto Técnico                        | http://www.modyo.com                                                                                                                      |
| CONTACT_EMAIL                      | Correo Electrónico de Contacto Técnico               | walter@modyo.com                                                                                                                          |

### Configuración de Controladores

Suponer un controlador con un método HTTP como el siguiente:

```java
@RestController
public class SomeController {
    @GettMapping("/get_something/{id}")
    public ResponseEntity<ResponseDto<SomethingDto>> getSomething(
        @PathVariable("id") String id
    ) {
      // do something awesome
    }
}
```

Para que éste método quede documentado en Swagger y además se pueda registrar automáticamente en el API Gateway, es necesario respetar algunas reglas y agregar elementos al código. Por tanto, es absolutamente necesario hacer lo siguiente:

1. Agregar anotación `@RequestMapping` a nivel de controlador y definir su path. 
2. Agregar anotación `@ApiOperation` a nivel de método y definir sus valores `value` y `httpMethod`.
3. Reemplazar cualquier anotación `@GetMapping` o `@PostMapping` u otra anotación similar a nivel de método por `@RequestMapping` y definir sus valores `path`, `method` y `produces`.
4. Agregar anotación `@ApiParam` a cada parámetro de tipo path, query o body y definir sus argumentos `name`, `value` y `example`. Se puede agregar de manera opcional el argumento `required` con el valor `true` en caso de ser necesario.
5. Definir un método OPTIONS por cada método HTTP con su respectivas anotaciones `@ApiOperation` y `@RequestMapping` para poder confirgurar CORS en el API Gateway.

Si se aplican estas instrucciones al controlador de ejemplo, este queda de la siguiente forma:

```java
@RestController
@RequestMapping("/some_controller")
public class SomeController {
  
  @ApiOperation(value = "Gets something awesome", httpMethod = "OPTIONS")
  @RequestMapping(
      path = "/get_something/{id}",
      method = RequestMethod.OPTIONS,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public void getSomethingOptions() {
  }
  
  @ApiOperation(value = "Gets something awesome", httpMethod = "GET")
  @RequestMapping(
      path = "/get_something/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ResponseDto<SomethingDto>> getSomething(
      @ApiParam(name = "id", value = "Something ID", example = "123", required = true)
      @PathVariable("id") String id
  ) {
    // do something awesome
  }
  
}
```

### Anotaciones

Exiasten dos anotaciones que pueden ser declaradas a nivel de método de controlador para añadir seguridad. Estas son:

- *@RequiresCaptcha*: Obtiene el valor del header `X-Captcha-Response` (o `captcha-response` que será deprecado) desde la consulta y verifica contra un servicio de google si quien está realizando la petición es o no un robot.
- *@RequiresLambdaAuthorization*: Activa una función Lambda preconfigurada en la infraestructura de AWS para que valide el access token de la request antes de que sea procesada por el microservicio.

**Importante:** Si se utiliza *@RequiresLambdaAuthorization* en al menos un método de controlador dentro del proyecto, entonces es necesario agregar en el archivo application.yml del microservicio el siguiente parámetro:

```yml
spring:
  main:
    requiresLambdaAuthorization: true
```

### Clase Dto

Si esta dependencia es usada en un microservicio, todos sus DTOs deben heredar de esta clase.
Al heredar de Dto, se convierte en una clase serializable y cuenta con el método público `toJsonString()` que muy útil para el logging.


### RestRespository

Interfaz común para todos los repositories que ejecutan consultas rest contra un servicio externo.

Se implementa de la siguiente manera:

```java
@Repository
@Qualifier("SomeRestRepository")
public class SomeRestRepository implements RestRepository<SomeRequestDto, SomeResponseDto> {
  
  @Override
  public SomeResponseDto execute(SomeRequestDto request) {
    //call external service and return response
  }
  
}
```

### RestTemplates

Son beans que contienen restTemplates con diferentes configuraciones que pueden ser usados dependiendo del caso.
Se usan generalmente dentro de la implementación del método `execute` de una implementación de la interfaz RestRepository.
Se configuran de la siguiente manera:

```java
RestTemplate restTemplate = (RestTemplate) applicationContext.getBean(<type>, <args...>);
```

Las opciones son:

| Type                              | Args                                            | 
|:----------------------------------|:------------------------------------------------|
| `"restTemplate"`                  |                                                 |
| `"restTemplateBasicAuth"`         | username, password                              |
| `"restTemplateAuthToken"`         | token                                           |
| `"restTemplateTimeouts"`          | connectTimeout, readTimeout                     |
| `"restTemplateBasicAuthTimeouts"` | username, password, connectTimeout, readTimeout |
| `"restTemplateAuthTokenTimeouts"` | token, connectTimeout, readTimeout              |

Aquellas configuraciones que no especifican timeout tienen valores por defecto:

- *connectTimeout:* 2000 ms.
- *readTimeout:* 5000 ms.

### Excepciones
Existe un set de Excepciones que permiten manejar, responder y registrar de forma estándar eventos comunes para todos los microservicios que utilicen esta dependencia:

#### CustomValidationException
Lanzar cuando se requiere validar un parámetro de entrada por medio de algún método personalizado distinto de Bean Validation y este no cumple con las condiciones requeridas. Este tipo de validaciones personalizadas deben ir a nivel Filters o de Controllers.

#### BusinessErrorException
Lanzar cuando a nivel de la capa Services se detecta que la respuesta recibida indica que no se ha cumplido con alguna de las reglas del negocio. Este tipo de respuestas suelen estar documentadas por el proveedor del servicio. No confundir con errores de sistema. Si la documentación no provee estas especificaciones, averiguar con el proveedor si el servicio externo puede llegar retornar errores de negocio, y si lo hace, pedir que indique cuál es el formato de este tipo de respuestas y bajo qué condiciones se producen.

#### TechnicalErrorException
Lanzar cuando a nivel de la capa Services se detecta que algún dato requerido no viene en la respuesta del datasource o su formato es incorrecto. Es importante conocer la naturaleza de los datos de las respuestas que puede retornar un servicio externo. Con el objetivo de hacer un procesamiento adecuado de las respuestas, si la documentación no especifica qué valores de las respuestas son siempre requeridos, averiguar con el proveedor del servicio externo qué valores son requeridos y cuáles son opcionales.

#### ForbiddenException
Lanzar cuando la consulta requerida no cumple con las condiciones para ser procesada. Actualmente se usa cuando el servicio de Captcha determina que quien está realizando la consulta es un "robot".

### Utilidades

#### Rut
Esta clase permite hacer diferentes tipos de operaciones a partir de un RUT chileno.
Para su uso, requiere ser instanciada con un RUT válido.
A continuación se muestran ejemplos de instanciación:

````java
Rut rut = new Rut("11.111.111-1"); //Rut válido con puntos y guión
Rut rut = new Rut("11111111-1"); //Rut válido válido con guión
Rut rut = new Rut("111111111"); //Rut válido sin formato
Rut rut = new Rut("11111111", false); //Rut válido sin formato y sin dígito verificador
Rut rut = new Rut("11.111.111-2"); //Rut inválido, lanza un CustomValidationException
````

Si el RUT es válido y el objeto rut es instanciado exitosamente, se puede hacer uso de sus métodos públicos:

````java
Rut rut = new Rut("11.111.111-1");

rut.unformatted(); // retorna "111111111"
rut.formattedWithPoints(); // retorna "11.111.111-1"
rut.formattedWithoutPoints(); // retorna "11111111-1"
rut.isJuridico(); // retorna false (número de rut jurídico debe mayor a 50.000.000)
rut.isNatural(); // retorna true
rut.getNumeroString(); // retorna "11111111"
rut.getNumeroInt(); // retorna 11111111
rut.getDv(); // retorna "1" (dígito verificador)
````

#### JwtUtils
Esta clase contiene dos métodos estáticos:

````java
public static String getClaimFromAccessToken(String accessToken, String claim) { ... }
// permite extraer el valor de un claim a partir de un access token en formato JWT.
````
````java
public static String createJWT(HashMap<String, Object> claims) { ... }
// permite crear un JWT dummy con claims y valores obtenidos a partir de un HashMap de entrada.
````

Estos dos métodos son muy útiles para hacer testing, ya que permiten reconstruir diferentes escenarios que tienen que ver con el contenido de un access token.

##### 

#License
Copyright (C) Modyo Chile SA - All Rights Reserved
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
