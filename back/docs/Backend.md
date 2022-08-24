# Documentación Backend

Bienvenido/a a la documentación del backend de **SISDEP**. En este documento encontrarás una explicación general del proyecto, respuestas a las dudas que pueden surgir al momento de cambiar el código, y ejemplos de cómo y en dónde mover cosas.



## Stack tecnológico

### Play Framework 2.8.x

El backend esta hecho usando Play Framework, un framework web moderno (escrito en Scala pero con api para Java). Este proyecto hace uso del api en Java. A diferencia de otros framewoks de Java, el mismo Play es quien sirve las peticiones, por ello no necesita ningún servidor de aplicaciones ni hace uso de las apis de JavaEE mas allá de la definición de los Beans.

### Akka Streams 2.8.x

Akka es una librería que permite escribir aplicaciones reactivas. Play está construido sobre Akka, por lo que se usó Akka en algunas partes del sistema, particularmente, para hacer stream de datos desde la base de datos y para la notificación en tiempo real del estado los procesos relacionados a la gestión.

### Vavr 0.9.x

Es una librería para programación funcional sobre Java. Está influenciada por Scala por lo que muchos patrones funcionales son muy similares a los usados allí. Contiene varios paquetes siendo los más importantes el de colecciones (para manejo de Listas), tipos de datos para flujo de información como Option, Try o Either, y un mejor api para programación asíncrona con el uso de Futuros.

### Ebean

Ebean es un ORM para Java simple y rápido. Requiere poca configuración y gracias a la integración que tiene con Play, el manejo del pool de conexiones es automático.



## Conocimiento previo

Hay cierto conocimiento que es conveniente tener a la hora de adentrarse al código.

- **Java11 y lambdas.** Las funciones en estilo lambda son usadas por todo el sistema, por ejemplo `s -> s.toString()` es una lambda que también puede simplificarse como `Object::toString`. Si este tipo de notación es extraña, es conveniente investigar un poco más del tema.
- **Indicios básicos de programación funcional.** Junto al punto anterior, tener conocimientos del paradigma funcional es muy importante porque 90% del sistema usa ese estilo. La documentación oficial de Vavr es una buena introducción: http://www.vavr.io/vavr-docs/
- **Programación genérica.** Las funcionalidades más usadas en el sistema están escritas de forma genérica para permitir ser usadas fácilmente en todo el sistema.
- **Programación asíncrona.** Play es un framework reactivo que permite sacarle el máximo provecho a la programación asíncrona, ello permite aprovechar al máximo los ciclos de CPU del servidor pudiendo manejar muchas más peticiones más rápido y con los mismos recursos.



## Estructura del proyecto

La estructura del proyecto se ciñe a lo descrito en la documentación de Play: https://www.playframework.com/documentation/2.6.x/Anatomy#The-app/-directory

Lo único extra es dentro del directorio `app/utilities` donde están implementadas varias funcionalidades propias. De esas las dos más interesantes están en `generic` y en `security`.



## Filosofia

1. _Programación objetual en lo macro, programación funcional en lo micro._
2. _Evitar la mutación de variables siempre que sea posible._
3. _Evitar el lanzamiento de Excepciones y cuando se haga, controlarlas usando estructuras funcionales._
4. _Evitar el uso de `null` a toda costa, los NPE son de lo peor y son evitables._



## Como agregar nuevas entidades

El flujo mental desde la creación de una entidad en la base de datos hasta tener una entrada en el API del backend es la siguiente:

1. Dentro de `app/models/`, en el directorio adecuado según la naturaleza de la entidad, se debe crear una clase modelo que herede de `SimpleModel` que servirá como base para las clases genéricas.

   Esta clase, como cualquier bean, debe llevar las anotaciones que ayudarán a Ebean a mapear entre el SQL y el objeto. http://ebean-orm.github.io/docs/mapping/

   Particularmente es importante no olvidar poner el nombre del Schema en la anotación `@Table` ni tampoco olvidar que si se hace un directorio nuevo, el path se debe agregar en el `conf/application.conf` para que Ebean pueda generar las consultas adecuadamente. https://www.playframework.com/documentation/2.6.x/JavaEbean#Configuring-the-runtime-library

   Por ejemplo una clase _Contrato.java_:

   ```java
   @Entity
   @Table(name = "DSI_CONTRATO", schema = "esquema")
   public class Contrato extends Model implements SimpleModel {
     @Id
     @NotNull
     @Column(name = "ID")
     public Long id;
   
     @Column(name = "DS_DESCRIPCION")
     @Size(max = 200)
     public String descripcion;
   
     @Column(name = "DS_NOMBRE")
     @NotNull
     @Size(max = 100)
     public String nombre;
   
     @Override
     public Long getId() {
       return id;
     }
   
     @Override
     public void setId(Long id) {
       this.id = id;
     }
   }
   ```

   Es importante notar que las entidades **deben tener un ID**. Para algunas entidades la columna se llama _"CODIGO"_, aunque el nombre en la base de datos no importa (sí en el objeto), lo importante es que sea de tipo `Long`.
   Y todos los atributos de una entidad deben tener su getter y setter de la forma convencional: setNombreAtributo - getNombreAtributo

2. Dentro de `app/repository/`, siguiendo la misma estructura de directorio que en el modelo (esto es para mantener el orden), se crea una clase que extienda de `GenericRepository<U>` donde `U` es la clase modelo.

   ```java
   public class ContratoRepository extends GenericRepository<Contrato> {
     @Inject
     public ContratoRepository(
       EbeanConfig ebeanConfig,
       DatabaseExecutionContext databaseExecutionContext
     ) {
       super(ebeanConfig, databaseExecutionContext, Contrato.class, "contrato");
       super.idNameDB = "ID";
     }
   }
   ```

   En esta clase al inicializarla se debe pasar el `ebeanConfig` ó el nombre del esquema a nivel de Ebean (es decir, `default` para _OOPP5_ ó `gestion` para _OOPPGESTION_), el contexto de ejecución (para el pool de conexiones), la referencia del tipo de la clase (es decir, el `Contrato.class` en el ejemplo), y un nombre de la entidad que sea amigable con el formato JSON (por convención, hacerlo en _camelCase_ como se usa en JavaScript).

   Opcionalmente, si el nombre de la columna que hace de ID no tiene ese nombre, se puede reasignar con `super.idNameDB`, esa variable se usa solo para cuando se va a generar el número consecutivo siguiente (esta funcionalidad, debido a que Oracle 11g no la tiene de forma nativa, se hace en el backend).

3. Dentro de `app/controllers/`, de la misma estructura que los anteriores, se crea una clase de tipo controller que herede de `GenericController<U>` donde `U` es la clase modelo. A esta clase se le inyecta el repositorio anterior, y el repositorio de Acceso (necesario para los permisos de seguridad).

   ```java
   public class ContratoController extends GenericController<Contrato> {
     @Inject
     public ContratoController(
       ContratoRepository contratacionRepository,
       HttpExecutionContext httpExecutionContext,
       AccessRepository accessRepository
     ) {
       super(contratacionRepository, httpExecutionContext, accessRepository);
       super.modulosAsociados = List.of(Modulo.solo_lectura, Modulo.admin_dominio)
         .map(Modulo::getId);
     }
   }
   ```

   En esta clase al inizializarla se se pasa el repositorio de la clase, el contexto de ejecución para las peticiones HTTP, y el repositorio de Acceso.

   Opcionalmente, se debería especificar los módulos asociados como una List de vavr. Estos módulos se encuentran en `app/utilities/security/Modulo.java` y deben concordar con los que haya en la base de datos.

4. Finalmente, dentro de `app/conf/rutas.routes` o el archivo de rutas que sea adecuado según la naturaleza de la nueva entidad, se deben agregar los "endpoints" del API como se describe en la documentación de Play: https://www.playframework.com/documentation/2.6.x/JavaRouting

   La clase _GenericController_ viene con varias actions disponibles, no es obligatorio usarlos todos, solo los que sean necesarios. Más adelante hay una explicación al respecto:

   - getAll()
   - getAllStream()
   - getExcel()
   - getOne(id: Long)
   - add()
   - patch(id: Long)
   - remove(id: Long)



## Clases genéricas

Dentro del paquete `app/utilities/generic/` se encuentran las clases genéricas que implementan el grueso de las funcionalidades CRUD. Hay dos familias: las clases para los Repository, y una clase para los Controllers.

Aunque la mayoría de las entidades en el sistema usan estas clases genéricas, y se recomiendo enfáticamente que las usen, hay casos donde es más conveniente hacer las clases própias, como es el caso de los llamados a procedimientos almacenados para la gestión.

A continuación se hace un resumen de lo más importante a conocer sobre estas clases, cualquier otro detalle de la implementación puede verse en el código fuente.

### Clases de tipo Repository

La responsabilidad de un repositorio es hacer la comunicación con la base de datos devolviendo siempre objetos de java, cualquier llamado SQL o cualquier comunicación con la base de datos debe hacerse en un repositorio. Hay una interfaz que, hasta el momento, se ha implementado en 3 clases:

- Una clase repositorio de propósito general.
- Una clase repositorio para las vistas de la base de datos, que se usan en los reportes (solo lectura).
- Una clase repositorio para las tablas de Logs de errores de gestión (que no tienen ID en la base de datos).

Cada método público está implementado de forma asíncrona usando Futuros de Vavr, excepto los que son de consulta por Stream, donde se usa Akka Streams y se devuelve un Source.

Hay dos métodos privados, uno es _getNextId()_ que ayuda a generar el siguiente ID, el otro es _buildQuery()_ que permite generar una consulta de Ebean con base en un Map de forma recursiva.

### Clase de tipo Controller

La responsabilidad de un controlador es hacer la comunicación entre el repositorio y las peticiones al API REST. Es allí donde se comprueba la seguridad y los permisos sobre los recursos, y donde se transforman los objetos consultados en objetos JSON.

Dependiendo cómo vaya el proceso, da respuestas adecuadas según el estandar HTTP usando el API de Play; los procesos asíncronos se hacen con Futuros de vavr y usan el contexto de ejecución para peticiones HTTP de Play, aunque al final se deben convertir a `CompletionStage<Result>` con el método _toCompletableFuture()_, ya que es el tipo que Play espera.

Para los métodos _getAll()_, _getAllStream()_ y _getExcel()_, se pueden hacer consultas por queryParams (también se le conoce como searchParams https://developer.mozilla.org/en-US/docs/Web/API/URL/searchParams), devolviendo los objetos que cumplan estrictamente con esos criterios.

El método _getAll()_ solo devuelve un máximo de 2000 objetos. Esta restricción es para asegurar que haya una respuesta a la petición ya que petiones de muchos objetos pueden no terminar nunca. Para responder peticiones de más de 2000 objetos se debe usar _getAllStream()_, que haciendo uso de Akka Streams, transmite de forma eficiente los objetos consultados sin importar la cantidad, agrupándolos de a 100 en 100, y enviándolos por el estandar SSE (server-sent events) https://developer.mozilla.org/en-US/docs/Web/API/EventSource



## Ayudas para trabajar con JSON

En el paquete `app/utilities/json/` se encuentran varias utilidades como la que permite personalizar la serialización de las fechas para que usen el formato ISO 8601 https://es.wikipedia.org/wiki/ISO_8601.

Hay también una clase para generar las respuestas en Json adecuadas al formato de JSON normalizado que el frontend espera. Este formato está descrito en https://github.com/paularmstrong/normalizr

También hay una clase para generar las respuestas de error en un formato similar al usado en el estandar JSON:API http://jsonapi.org/examples/#error-objects. No es una implementación completa, si no simplificada.



## Seguridad

En el paquete `app/utilities/security/` se encuentran varias clases para el manejo de la seguridad en el SENA.

- Una clase `UserinfoContext` lleva la información básica del usuario necesaria para las comprovaciones de seguridad.

- Un enum `Permission` con los tipos de permisos que hay, son solo _NINGUNO_, _LEER_, _ESCRIBIR_, _BORRAR_, _EXPORTAR_.

- un enum `Modulo` donde están los módulos del sistema, y que **deben coincidir** con los que haya en la tabla `TSS_MODULO_SGV`. Esto se hace para no depender de tantas consultas a la DB y hacer el sistema más ágil.

- Una clase `PermissionChecker` con una función _checkPermission()_, que es estática y pura para comprovar la seguridad.

- Una clase `LdapClient` que implementa el login por LDAP.

- Una clase `AuthAction` que implementa la comprovación de estar logueado. Esta se agrega por medio de la anotación `@With({AuthAction.class})` como se describe en la documentación de Play: https://www.playframework.com/documentation/2.6.x/JavaActionsComposition#defining-custom-action-annotations

  Para ello se vale del estandar JWT (JSON Web Token), con tokens que duran una semana. El sistema espera encontrar el token en una cabecera http `x-access`, y en caso que no la encuentre o esta no sea válida (por ejemplo, si se venció o si la ip de generación del token es distinta a la de consumo) va a rechazar la petición.



