package com.infinia.sports.mail;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.Order.ShippingGroup;
import com.infinia.sports.model.Order.LineItem;
import com.infinia.sports.model.Order.Address;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.math.RoundingMode; // Añadir import para RoundingMode
import java.util.stream.Collectors;
import java.util.List;

/**
 * Utilidad para generar el HTML del resumen de pedido a partir de la plantilla.
 * Se adapta a la estructura de modelo de Infinia Sports.
 */
public class OrderMailTemplateUtil {

    /**
     * Genera el HTML del resumen de pedido usando la plantilla y los datos del pedido.
     * @param order Pedido a procesar
     * @return HTML rellenado
     */
    public static String generateOrderSummaryHtml(Order order) {
        try {
            // Leer la plantilla (ajusta la ruta si es necesario)
            String template = Files.readString(Paths.get("src/main/resources/templates/order-summary.html"));

            // Asumimos un solo ShippingGroup principal (ajusta si hay varios)
            ShippingGroup shippingGroup = order.getShippingGroups().get(0);
            Address address = order.getShippingAddress();

            // Construir las filas de productos del pedido
            List<LineItem> lineItems = shippingGroup.getLineItems();
            String orderLines = lineItems.stream()
                .map(line -> "<tr><td>" + line.getProductName() + "</td><td>" + line.getQuantity() + "</td><td>" + line.getUnitPrice() + "€</td></tr>")
                .collect(Collectors.joining());

            // Nombre completo del destinatario
            String customerName = address.getFirstName() + " " + address.getLastName();

            // Dirección en formato legible
            String shippingAddress = address.getAddressLine1() + " " +
                                     (address.getAddressLine2() != null ? address.getAddressLine2() + ", " : "") +
                                     address.getCity() + ", " +
                                     address.getState() + ", " +
                                     address.getPostalCode() + ", " +
                                     address.getCountry();

            // Total del pedido
            BigDecimal total = order.getPriceInfo().getTotal();

            // Reemplazar los placeholders
            return template
                .replace("{{customerName}}", customerName)
                .replace("{{orderId}}", order.getOrderId())
                .replace("{{orderDate}}", order.getSubmitDate().toString())
                .replace("{{orderLines}}", orderLines)
                .replace("{{orderTotal}}", total.setScale(2, RoundingMode.HALF_UP) + "€")
                .replace("{{shippingAddress}}", shippingAddress);
        } catch (Exception e) {
            // En caso de error, devolver un texto plano simple
            return "Gracias por tu compra. Pedido: " + order.getOrderId();
        }
    }
}
