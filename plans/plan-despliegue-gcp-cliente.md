# Propuesta de Despliegue en Google Cloud para la Demo de "Infinia Sports"

## Resumen Ejecutivo

A continuación, se detalla una propuesta técnica para el despliegue de la aplicación "Infinia Sports" en Google Cloud Platform (GCP). El objetivo es crear un entorno de demostración robusto, seguro y rentable, manteniendo la arquitectura tecnológica existente (Spring Boot, React, PostgreSQL, Kafka, MongoDB).

Esta arquitectura utiliza los servicios gestionados de GCP para minimizar el mantenimiento y garantizar un rendimiento estable durante la presentación al cliente.

## Arquitectura Propuesta en Google Cloud

| Componente          | Servicio GCP Recomendado                                     | Justificación                                                                                                                                      |
| ------------------- | ------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Backend (Java)**  | **Cloud Run**                                                | Servicio sin servidor que escala a cero, ideal para tráfico esporádico de demo. Paga solo por el uso real, manteniendo los costes al mínimo.        |
| **Frontend (React)**| **Cloud Run**                                                | Misma ventaja que el backend. Sirve la aplicación de forma eficiente y económica, con la capacidad de escalar si es necesario.                   |
| **Imágenes (Productos)** | **Cloud Storage**                                            | Almacenamiento de objetos escalable y de bajo coste para servir los activos estáticos (imágenes, etc.) directamente al cliente.                   |
| **PostgreSQL**      | **Cloud SQL para PostgreSQL**                                | Base de datos totalmente gestionada, segura y con copias de seguridad automáticas. Ofrece una integración perfecta y baja latencia con Cloud Run. |
| **Kafka**           | **Confluent Cloud en GCP Marketplace**                       | Proporciona un clúster de Apache Kafka real y totalmente gestionado. Es la forma más fiel de mantener la tecnología Kafka sin gestionar servidores. |
| **MongoDB**         | **MongoDB Atlas en GCP Marketplace**                         | El estándar de la industria para MongoDB. Ofrece una integración nativa con GCP (VPC Peering) para máxima seguridad y rendimiento.              |
| **Secretos**        | **Secret Manager**                                           | Almacena de forma centralizada y segura todas las credenciales, como claves de API y contraseñas de bases de datos.                             |
| **Imágenes Docker** | **Artifact Registry**                                        | Repositorio privado y seguro para almacenar las imágenes de contenedor del backend y frontend.                                                     |

### Diagrama de la Arquitectura

```
                  ┌──────────────────┐
                  │      Cliente     │
                  └─────────┬────────┘
                            │
                            ▼
      ┌──────────────────────────────────────────┐
      │         Google Cloud Platform (GCP)      │
      │                                          │
      │   ┌─────────────┐      ┌─────────────┐   │   ┌─────────────────┐
      │   │ Cloud Run   │      │ Cloud Run   │───► │ Cloud Storage   │
      │   │ (Frontend)  │◄─────►│ (Backend)   │   │ (Imágenes)      │
      │   └─────────────┘      └──────┬──────┘   └─────────────────┘
      │                              ┌┴┐         
      │         ┌────────────────────┼─┼─────────┐
      │         │                    │ │         │
      │         ▼                    ▼ │         ▼
      │   ┌───────────┐      ┌─────────┴───┐   ┌─────────────┐
      │   │ Cloud SQL │      │ Confluent   │   │ MongoDB     │
      │   │ (PostgrSQL) │      │   (Kafka)   │   │   Atlas     │
      │   └───────────┘      └─────────────┘   └─────────────┘
      │                                          │
      └──────────────────────────────────────────┘
```

## Gestión de Activos Estáticos (Imágenes)

Para el manejo de imágenes de productos se utilizará **Google Cloud Storage** siguiendo este flujo:

1.  **Subida:** Un administrador sube una imagen a través de la aplicación. El backend recibe el fichero y lo sube a un *bucket* de Cloud Storage configurado para ser públicamente legible.
2.  **Almacenamiento:** El backend guarda la URL pública y permanente de la imagen en la base de datos PostgreSQL, asociada al producto correspondiente.
3.  **Visualización:** El frontend solicita los datos del producto al backend, recibe la URL de la imagen y la renderiza. La imagen se carga en el navegador del cliente directamente desde Google Cloud Storage, optimizando el rendimiento.

## Estimación de Costes Mensuales (Entorno Demo)

Los costes se han calculado utilizando las instancias y configuraciones más pequeñas y rentables, adecuadas para un entorno de demostración con tráfico bajo o esporádico.

| Servicio                                  | Configuración Recomendada (Demo)                     | Coste Mensual Estimado |
| ----------------------------------------- | ---------------------------------------------------- | ---------------------- |
| **Cloud Run (Backend + Frontend)**        | 1 vCPU, 512MB RAM, escala a cero (min-instances: 0)  | ~€5 - €10              |
| **Cloud Storage**                         | Estándar, ~5GB de almacenamiento y operaciones       | ~€1 - €2               |
| **Cloud SQL (PostgreSQL)**                | Instancia `db-f1-micro` (núcleo compartido), 20GB SSD | ~€15                   |
| **Confluent Cloud (Kafka)**               | Plan Básico                                          | ~€45                   |
| **MongoDB Atlas**                         | Cluster M0 (Gratuito) o M2 (si se requiere)          | €0 - €8                |
| **Otros (Secret Manager, Artifact Registry)** | Uso bajo                                             | ~€2                    |
| **Total Estimado**                        |                                                      | **~€68 - €82 / mes**   |

**Nota:** Esta estimación es conservadora. Gracias a que Cloud Run escala a cero, los costes reales podrían ser inferiores si la demo no se utiliza de forma continua.

## Próximos Pasos

1.  **Configuración del Proyecto en GCP:** Crear un nuevo proyecto y habilitar las APIs necesarias.
2.  **Aprovisionamiento de Servicios:** Desplegar las instancias de Cloud SQL, Confluent Cloud y MongoDB Atlas.
3.  **Adaptación y Despliegue:** Ajustar las configuraciones de la aplicación para conectar con los nuevos servicios y desplegar en Cloud Run.
