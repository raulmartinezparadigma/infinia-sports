# Propuesta de Despliegue Gratuito para "Infinia Sports"

## Resumen Ejecutivo

A continuación, se detalla una propuesta técnica para el despliegue de la aplicación "Infinia Sports" en un entorno de nube **totalmente gratuito**, ideal para fines de demostración, desarrollo y pruebas.

El objetivo es lograr un despliegue funcional **sin incurrir en costes de infraestructura** y **manteniendo la arquitectura tecnológica actual** (PostgreSQL, Kafka, MongoDB), combinando los generosos niveles gratuitos de Google Cloud con proveedores externos que ofrecen planes gratuitos permanentes.

## Arquitectura Propuesta

| Componente          | Servicio Recomendado                                         | Justificación                                                                                                                                      |
| ------------------- | ------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Backend (Java)**  | **Google Cloud Run**                                         | El nivel gratuito y su capacidad de escalar a cero lo hacen perfecto para un entorno de demo sin coste.                                          |
| **Frontend (React)**| **Google Cloud Run**                                         | Se beneficia de las mismas ventajas que el backend, sirviendo la aplicación de forma eficiente y gratuita.                                       |
| **Imágenes (Productos)** | **Google Cloud Storage**                                     | El nivel gratuito es suficiente para almacenar las imágenes de una demo y servirlas de forma rápida y eficiente.                                   |
| **PostgreSQL**      | **ElephantSQL (Plan "Tiny Turtle")**                       | Proveedor externo que ofrece una base de datos PostgreSQL gestionada y gratuita, 100% compatible con la aplicación.                            |
| **Kafka**           | **CloudKarafka (Plan "Ducky")**                            | Proporciona un clúster de Kafka gestionado y gratuito, evitando cualquier modificación de código en la integración existente.                       |
| **MongoDB**         | **MongoDB Atlas (Plan "M0")**                              | El estándar de la industria para MongoDB, con un plan gratuito robusto y permanente.                                                               |
| **Secretos**        | **Google Secret Manager**                                    | El nivel gratuito permite almacenar de forma segura todas las credenciales necesarias sin coste.                                                   |
| **Imágenes Docker** | **Google Artifact Registry**                                 | Ofrece un repositorio privado gratuito para almacenar las imágenes de contenedor.                                                                  |

### Diagrama de la Arquitectura

```
                  ┌──────────────────┐
                  │      Cliente     │
                  └─────────┬────────┘
                            │
  ┌─────────────────────────▼──────────────────────────┐
  │                                                    │
  │   ┌──────────────────────────────────────────┐     │
  │   │         Google Cloud Platform (GCP)      │     │
  │   │                                          │     │
  │   │   ┌─────────────┐      ┌─────────────┐   │     │   ┌─────────────────┐
  │   │   │ Cloud Run   │      │ Cloud Run   │───┼─────┼───► │ ElephantSQL     │
  │   │   │ (Frontend)  │◄─────►│ (Backend)   │   │     │   │ (PostgreSQL)    │
  │   │   └─────────────┘      └──────┬──────┘   │     │   └─────────────────┘
  │   │                              ┌┴┐         │     │
  │   │                              │ │         │     │   ┌─────────────────┐
  │   │                              │ └─────────┼─────┼───► │ CloudKarafka    │
  │   │                              │           │     │   │ (Kafka)         │
  │   │                              └───────────┘     │   └─────────────────┘
  │   │                                          │
  │   └──────────────────────────────────────────┘
  │
  └────────────────────────────────────────────────────┘
```

## Gestión de Activos Estáticos (Imágenes)

Se utilizará **Google Cloud Storage** de la misma forma que en el plan de cliente, aprovechando su generoso nivel gratuito. El flujo de subida, almacenamiento de la URL y visualización se mantiene idéntico.

## Estimación de Costes Mensuales

| Servicio                                  | Plan / Configuración Recomendada                     | Coste Mensual Estimado |
| ----------------------------------------- | ---------------------------------------------------- | ---------------------- |
| **Cloud Run (Backend + Frontend)**        | Dentro del nivel gratuito mensual                    | €0                     |
| **Cloud Storage**                         | Dentro del nivel gratuito mensual                    | €0                     |
| **Cloud SQL (PostgreSQL)**                | Plan "Tiny Turtle" de ElephantSQL                    | €0                     |
| **Confluent Cloud (Kafka)**               | Plan "Ducky" de CloudKarafka                         | €0                     |
| **MongoDB Atlas**                         | Plan "M0"                                            | €0                     |
| **Otros (Secret Manager, Artifact Registry)** | Dentro del nivel gratuito mensual                    | €0                     |
| **Total Estimado**                        |                                                      | **€0**                 |

**Nota:** Este plan requiere la activación de una cuenta de GCP con un método de pago para verificación de identidad, pero no generará cargos si el uso se mantiene dentro de los límites gratuitos especificados.

## Próximos Pasos

1.  **Creación de Cuentas:** Configurar el proyecto en GCP y registrarse en los planes gratuitos de ElephantSQL y CloudKarafka.
2.  **Gestión de Secretos:** Guardar todas las credenciales obtenidas en Google Secret Manager.
3.  **Adaptación y Despliegue:** Ajustar las configuraciones de la aplicación para conectar con los nuevos servicios y desplegar en Cloud Run.
