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
- JavaMail API (para envío de correos)

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
- **Servicio SMTP**: Para envío de correos de confirmación

## Consideraciones de Despliegue
- Entorno de desarrollo local
- Entorno de producción (pendiente de definir)
- Estrategia de migración de datos (si es necesario)

## Convenciones de Código
- Idioma: Español para nombres de clases, métodos y variables
- No utilizar importaciones con asterisco (*)
- Mantener homogeneidad en el estilo de código
- Seguir convenciones de Java y React respectivamente
