# Plan de Migración a MapStruct

## 📋 Resumen Ejecutivo

**Objetivo**: Migrar 7 mappers manuales (513 líneas) a MapStruct  
**Estimación**: 2-3 días  
**Reducción esperada**: -70% código (~360 líneas)  
**Prioridad**: 🔴 Alta

---

## 📊 Estado Actual

### Mappers Existentes

| Mapper | Líneas | Complejidad | Dependencias |
|--------|--------|-------------|--------------|
| OrderMapper | 285 | 🔴 Alta | ProductRepository |
| CartMapper | 68 | 🟡 Media | - |
| ProductMapper | 30 | 🟢 Baja | - |
| AddressMapper | 43 | 🟢 Baja | - |
| PaymentMapper | 54 | 🟡 Media | - |
| PriceInfoMapper | 17 | 🟢 Baja | - |
| TaxInfoMapper | 16 | 🟢 Baja | - |

**Total**: 513 líneas, 20 métodos

---

## 🎯 Objetivos

✅ Migrar 7 mappers sin romper funcionalidad  
✅ Reducir código de 513 a ~150 líneas  
✅ Mantener 100% tests pasando  
✅ Documentar patrones MapStruct

---

## 📅 Plan de Ejecución

### Fase 1: Preparación (2-3h)

#### 1.1 Configurar Dependencias Maven
`backend/pom.xml`

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

Configurar `maven-compiler-plugin`:
```xml
<annotationProcessorPaths>
    <!-- ORDEN CRÍTICO: Lombok primero -->
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
    </path>
</annotationProcessorPaths>
```

**Verificar**: `mvn clean compile`

---

#### 1.2 Crear Estructura
`mapper/mapstruct/MapStructConfig.java`:

```java
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface MapStructConfig {}
```

---

#### 1.3 Backup
```bash
git checkout -b feature/migrate-to-mapstruct
mvn clean test  # Baseline
```

---

### Fase 2: Migración Incremental (8-12h)

#### 2.1 Mappers Simples (2h)

**PriceInfoMapper, TaxInfoMapper**:
```java
@Mapper(config = MapStructConfig.class)
public interface PriceInfoMapperMS {
    PriceInfoDTO toDTO(Order.PriceInfo priceInfo);
}
```

**AddressMapper** (bidireccional):
```java
@Mapper(config = MapStructConfig.class)
public interface AddressMapperMS {
    AddressDTO toDTO(Order.Address address);
    Order.Address fromDTO(AddressDTO dto);
}
```

---

#### 2.2 Mappers Medios (3h)

**ProductMapper**:
```java
@Mapper(config = MapStructConfig.class)
public interface ProductMapperMS {
    @Mapping(target = "type", 
             expression = "java(product.getType() != null ? product.getType().name() : null)")
    ProductDTO toDTO(Product product);
    
    List<ProductDTO> toDTOList(List<Product> products);
}
```

**PaymentMapper**:
```java
@Mapper(config = MapStructConfig.class)
public interface PaymentMapperMS {
    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "status", 
             expression = "java(payment.getStatus().name())")
    BizumPaymentResponseDTO toBizumPaymentResponseDTO(Payment payment);
    // ... otros métodos
}
```

---

#### 2.3 CartMapper (2h)

```java
@Mapper(config = MapStructConfig.class)
public interface CartMapperMS {
    CartDTO toDTO(Cart cart);
    List<CartItemDTO> toItemDTOList(List<Cart.CartItem> items);
}
```

---

#### 2.4 OrderMapper - Lógica Compleja (4h)

**Problema**: Necesita inyectar `ProductRepository`

**Solución**: Abstract class

`OrderMapperMSBase.java`:
```java
public abstract class OrderMapperMSBase {
    @Autowired
    protected ProductRepository productRepository;
    
    @Autowired
    protected AddressMapperMS addressMapper;
    
    protected void enrichLineItemWithImage(Order.LineItem item) {
        if (item.getProductImageUrl() == null) {
            try {
                Product product = productRepository
                    .findById(UUID.fromString(item.getProductId()))
                    .orElse(null);
                if (product != null) {
                    item.setProductImageUrl(product.getImageUrl());
                }
            } catch (Exception e) {
                // Log
            }
        }
    }
}
```

`OrderMapperMS.java`:
```java
@Mapper(
    config = MapStructConfig.class,
    uses = {AddressMapperMS.class, PriceInfoMapperMS.class, TaxInfoMapperMS.class}
)
public abstract class OrderMapperMS extends OrderMapperMSBase {
    
    public abstract OrderDTO toDTO(Order order);
    
    // Método custom para lógica compleja
    public Order fromCart(Cart cart, AddressDTO shippingAddress, 
                          AddressDTO billingAddress) {
        Order order = new Order();
        order.setId(cart.getId());
        order.setOrderId(cart.getId());
        order.setLanguage("ES");
        order.setSubmitDate(LocalDateTime.now());
        order.setStatus("pending");
        order.setEmail(shippingAddress.getEmail());
        
        // ShippingGroup
        Order.ShippingGroup shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId("1");
        shippingGroup.setShippingMethod("Infinia Sports");
        shippingGroup.setShippingCost(cart.getShippingCost());
        
        // LineItems con enriquecimiento
        List<Order.LineItem> lineItems = cart.getItems().stream()
            .map(this::cartItemToLineItem)
            .peek(this::enrichLineItemWithImage)
            .collect(Collectors.toList());
        
        shippingGroup.setLineItems(lineItems);
        order.setShippingGroups(List.of(shippingGroup));
        
        // Direcciones y PriceInfo...
        return order;
    }
    
    protected abstract Order.LineItem cartItemToLineItem(Cart.CartItem item);
}
```

---

### Fase 3: Integración (2-3h)

#### 3.1 Actualizar Servicios

**CheckoutServiceImpl**:
```java
// Antes
import com.infinia.sports.mapper.OrderMapper;
Order order = OrderMapper.fromCart(...);

// Después
import com.infinia.sports.mapper.mapstruct.OrderMapperMS;

@Autowired
private OrderMapperMS orderMapper;

Order order = orderMapper.fromCart(...);
```

**Archivos a modificar**:
- CheckoutServiceImpl (7 refs)
- OrderServiceImpl (3 refs)

---

#### 3.2 Eliminar Inicialización Estática

Buscar y eliminar:
```java
OrderMapper.setProductRepository(productRepository);
```

---

#### 3.3 Tests

```bash
mvn clean test
mvn verify -P e2e-test
```

---

### Fase 4: Limpieza (1-2h)

#### 4.1 Eliminar Mappers Antiguos
```bash
rm mapper/OrderMapper.java
rm mapper/CartMapper.java
# ... resto
```

#### 4.2 Renombrar (eliminar sufijo MS)
- `OrderMapperMS` → `OrderMapper`
- Refactor imports en servicios

#### 4.3 Documentación
Crear `mapper/README.md`

---

## ✅ Checklist

### Pre-Migración
- [ ] Branch Git creado
- [ ] Tests baseline ejecutados
- [ ] Dependencias añadidas
- [ ] `mvn compile` sin errores

### Migración
- [ ] PriceInfoMapper ✅
- [ ] TaxInfoMapper ✅
- [ ] AddressMapper ✅
- [ ] ProductMapper ✅
- [ ] PaymentMapper ✅
- [ ] CartMapper ✅
- [ ] OrderMapper ✅

### Post-Migración
- [ ] Servicios actualizados
- [ ] Tests pasan 100%
- [ ] Mappers antiguos eliminados
- [ ] Documentación actualizada

---

## 🚨 Riesgos

### 1. Conflicto Lombok-MapStruct
**Solución**: Lombok primero en `annotationProcessorPaths`

### 2. ProductRepository no inyecta
**Solución**: Usar `abstract class` en OrderMapper

### 3. Tests fallan por null handling
**Solución**: Configurar `NullValueMappingStrategy.RETURN_NULL`

---

## 📈 Métricas de Éxito

| Métrica | Antes | Después | Mejora |
|---------|-------|---------|--------|
| Líneas código | 513 | ~150 | -70% |
| Tests pasando | 100% | 100% | ✅ |
| Mantenibilidad | 🟡 | 🟢 | Alta |

---

## 📚 Referencias

- [MapStruct Docs](https://mapstruct.org/)
- [Spring Integration](https://mapstruct.org/documentation/stable/reference/html/#spring)
- [Lombok Integration](https://mapstruct.org/documentation/stable/reference/html/#lombok)

---

## 📝 Log de Progreso

| Fecha | Fase | Estado |
|-------|------|--------|
| 2025-01-13 | Plan creado | ✅ |
| | Fase 1 | ⏳ |
| | Fase 2 | ⏳ |
| | Fase 3 | ⏳ |
| | Fase 4 | ⏳ |
