# Contexto Tecnológico - Infinia Sports

## Tecnologías Utilizadas

### Backend
- **Lenguaje**: Java 17
- **Framework**: Spring Boot
- **Gestión de Dependencias**: Maven
- **Bases de Datos**:
  - PostgreSQL (para Productos)
  - MongoDB (para Pedidos)
- **Documentación API**: OpenAPI 3.0.x / Swagger

### Frontend
- **Framework**: React.js
- **Gestión de Estado**: Redux o Context API
- **Formularios**: Formik con Yup para validaciones
- **Componentes UI**: Material-UI o Bootstrap
- **Estilos**: Styled-components
- **Comunicación API**: Axios

### Herramientas de Desarrollo
- **Control de Versiones**: Git
- **IDE**: Visual Studio Code
- **Pruebas E2E**: Playwright con Java
- **Gestión de Tareas**: Pendiente de definir

## Configuración del Entorno de Desarrollo

### Requisitos Previos
- Java 17 JDK
- Node.js y npm
- PostgreSQL
- MongoDB
- Git

### Configuración Backend
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/infinia/sports/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
└── pom.xml
```

#### Configuración de Bases de Datos
- **PostgreSQL**: Configurado en `application.properties`
- **MongoDB**: Configurado en `application.properties`

### Configuración Frontend
```
frontend/
├── public/
├── src/
│   ├── components/
│   ├── pages/
│   ├── services/
│   └── ...
├── package.json
└── README.md
```

## Restricciones Técnicas
- Compatibilidad con navegadores modernos
- Diseño responsive para múltiples dispositivos
- Optimización de rendimiento para importación masiva de productos
- Seguridad en transacciones de pago

## Dependencias Principales

### Backend
- Spring Boot Starter Web
- Spring Data JPA
- Spring Data MongoDB
- PostgreSQL Driver
- MongoDB Driver
- Spring Boot Starter Validation
- springdoc-openapi (para OpenAPI/Swagger)
- Spring Kafka (para notificaciones asíncronas)
- JavaMail API (para envío de correos)
- com.microsoft.playwright (para pruebas E2E)

### Frontend
- React
- React Router
- Redux o Context API
- Formik y Yup
- Material-UI o Bootstrap
- Styled-components
- Axios

## Integración con Servicios Externos
- **Bizum**: API para procesamiento de pagos
- **Redsys**: Pasarela de pago para tarjetas
- **SendGrid**: Para envío de correos de confirmación. Requiere la variable de entorno `SENDGRID_API_KEY`.
- **Apache Kafka**: Para la comunicación asíncrona y envío de notificaciones.

## Consideraciones de Despliegue
- Entorno de desarrollo local
- Entorno de producción (pendiente de definir)
- Estrategia de migración de datos (si es necesario)

## Convenciones de Código
- Idioma: Español para nombres de clases, métodos y variables en el backend. Inglés en el frontend.
- No utilizar importaciones con asterisco (*)
- Mantener homogeneidad en el estilo de código
- Seguir convenciones de Java y React respectivamente
- **Playwright (Java)**:
  - Importar explícitamente todas las clases de Playwright (`Page`, `Locator`, etc.).
  - Usar la clase `Options` correcta según el contexto: `new Page.GetByRoleOptions()` para llamadas desde `page` y `new Locator.GetByRoleOptions()` para llamadas desde un `locator`.

## Notas de Configuración Específica

### Configuración del Deserializador de Kafka
Para resolver problemas de deserialización de DTOs entre productor y consumidor (cuando los paquetes son diferentes), se deben aplicar las siguientes propiedades en la configuración del consumidor de Kafka:

1.  **Confiar en todos los paquetes**:
    `props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");`
2.  **Ignorar cabeceras de tipo**:
    `props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);`

Esto asegura que el consumidor utilice su propia versión de la clase DTO sin depender de la información de tipo enviada por el productor.
