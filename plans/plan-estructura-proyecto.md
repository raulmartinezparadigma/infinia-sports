# Plan de Estructura del Proyecto

## Objetivo
Definir una estructura de carpetas y archivos clara y organizada para el proyecto de e-commerce de productos deportivos, siguiendo las mejores prácticas para proyectos Java con Spring Boot y React.js.

## Estructura General

```
infinia-sports/
├── backend/                  # Proyecto Java (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/infinia/sports/
│   │   │   │   ├── config/               # Configuraciones
│   │   │   │   ├── controller/           # Controladores REST
│   │   │   │   ├── model/                # Modelos y entidades
│   │   │   │   ├── repository/           # Capa de persistencia
│   │   │   │   ├── service/              # Capa de servicios
│   │   │   │   ├── exception/            # Manejo de excepciones
│   │   │   │   ├── util/                 # Utilidades
│   │   │   │   └── InfiniaSportsApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       └── application-prod.properties
│   │   └── test/                         # Pruebas
│   ├── pom.xml                           # Dependencias Maven
│   └── README.md
│
├── frontend/                 # Proyecto React.js
│   ├── public/
│   │   ├── index.html
│   │   ├── favicon.ico
│   │   └── assets/
│   ├── src/
│   │   ├── components/                   # Componentes React
│   │   │   ├── common/                   # Componentes comunes
│   │   │   ├── product/                  # Componentes de productos
│   │   │   ├── cart/                     # Componentes de carrito
│   │   │   ├── checkout/                 # Componentes de checkout
│   │   │   └── payment/                  # Componentes de pago
│   │   ├── pages/                        # Páginas principales
│   │   ├── services/                     # Servicios API
│   │   ├── hooks/                        # Hooks personalizados
│   │   ├── context/                      # Contextos de React
│   │   ├── utils/                        # Utilidades
│   │   ├── styles/                       # Estilos globales
│   │   ├── App.js
│   │   └── index.js
│   ├── package.json
│   └── README.md
│
├── docs/                     # Documentación
│   ├── api/                              # Documentación de API
│   │   └── openapi.yaml                  # Especificación OpenAPI
│   └── guides/                           # Guías y manuales
│
├── scripts/                  # Scripts de utilidad
│   ├── setup-db.sh                       # Configuración de BD
│   └── deploy.sh                         # Script de despliegue
│
├── plans/                    # Planes de implementación
│
└── README.md                 # Documentación principal
```

## Estructura Detallada por Módulos

### Backend (Java)

#### Módulo de Productos
```
backend/src/main/java/com/infinia/sports/
├── controller/
│   └── ProductoController.java
├── service/
│   └── ProductoService.java
├── repository/
│   └── ProductoPersistence.java
└── model/
    └── Producto.java
```

#### Módulo de Checkout
```
backend/src/main/java/com/infinia/sports/
├── controller/
│   └── CheckoutController.java
├── service/
│   └── CheckoutService.java
├── repository/
│   └── CheckoutPersistence.java
└── model/
    ├── Carrito.java
    ├── CarritoItem.java
    └── Order.java
```

#### Módulo de Pagos
```
backend/src/main/java/com/infinia/sports/
├── controller/
│   └── PagoController.java
├── service/
│   └── PagoService.java
├── repository/
│   └── PagoPersistence.java
├── model/
│   ├── Pago.java
│   └── EstadoPago.java
└── util/
    └── EmailSender.java
```

### Frontend (React.js)

#### Componentes de Productos
```
frontend/src/components/product/
├── ProductList.jsx
├── ProductCard.jsx
├── ProductDetail.jsx
└── ProductFilter.jsx
```

#### Componentes de Carrito
```
frontend/src/components/cart/
├── CartView.jsx
├── CartItem.jsx
├── CartSummary.jsx
└── AddToCartButton.jsx
```

#### Componentes de Checkout
```
frontend/src/components/checkout/
├── CheckoutStepper.jsx
├── ShippingForm.jsx
├── BillingForm.jsx
└── OrderSummary.jsx
```

#### Componentes de Pago
```
frontend/src/components/payment/
├── PaymentSelector.jsx
├── BizumPayment.jsx
├── RedsysPayment.jsx
├── BankTransferPayment.jsx
└── PaymentConfirmation.jsx
```

## Consideraciones Adicionales

### Gestión de Dependencias
- Backend: Maven para gestión de dependencias Java
- Frontend: npm para gestión de paquetes JavaScript

### Configuración de Bases de Datos
- PostgreSQL: Configuración en `application.properties`
- MongoDB: Configuración en `application.properties`

### Seguridad
- Estructura para implementación de seguridad (si se requiere en el futuro)

### Internacionalización
- Estructura para soporte multiidioma (si se requiere en el futuro)

## Próximos Pasos
1. Crear estructura básica de carpetas
2. Configurar proyecto Spring Boot
3. Configurar proyecto React
4. Implementar modelos de datos básicos
5. Configurar conexiones a bases de datos
