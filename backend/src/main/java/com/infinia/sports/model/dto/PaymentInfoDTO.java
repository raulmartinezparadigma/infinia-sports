package com.infinia.sports.model.dto;

/**
 * DTO para exponer información relevante del pago de un pedido
 */
public class PaymentInfoDTO {
    private String method;
    private String status;

    public PaymentInfoDTO(String method, String status) {
        this.method = method;
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
