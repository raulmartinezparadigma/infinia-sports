package com.infinia.sports.kafka.dto;

import java.math.BigDecimal;

/**
 * DTO para mensajes de producto enviados por Kafka.
 * Se utiliza para la carga asíncrona de productos desde el DataInitializer o el panel de administración.
 */
public class ProductKafkaMessage {
    private String id;
    private String type;
    private String description;
    private BigDecimal price;
    private String size;
    private String imageUrl;

    /**
     * Constructor vacío requerido para deserialización
     */
    public ProductKafkaMessage() {}

    /**
     * Constructor completo
     */
    public ProductKafkaMessage(String id, String type, String description, BigDecimal price, String size, String imageUrl) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.price = price;
        this.size = size;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ProductKafkaMessage{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", size='" + size + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
