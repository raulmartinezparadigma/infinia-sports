package com.infinia.sports.service;

import java.io.IOException;

/**
 * Servicio para manejar el almacenamiento de archivos, como imágenes de productos.
 */
public interface ImageStorageService {

    /**
     * Descarga una imagen desde una URL, la guarda en el sistema de archivos local
     * y devuelve la nueva ruta de acceso local.
     *
     * @param imageUrl La URL de la imagen a descargar.
     * @param productName El nombre del producto, usado para generar el nombre del archivo.
     * @return La ruta local donde se ha guardado la imagen (p. ej., /images/nombre-del-producto.jpg).
     * @throws IOException Si ocurre un error durante la descarga o el guardado.
     */
    String storeImage(String imageUrl, String productName) throws IOException;

}
