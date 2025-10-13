package com.infinia.sports.mapper.mapstruct;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Configuración base para todos los mappers de MapStruct.
 * Define políticas comunes como el modelo de componente Spring y el manejo de campos no mapeados.
 */
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface MapStructConfig {
}
