package mx.diegopmz.fixlogapi.common.domain.orderAggregate;

public enum OrderStatus {
    RECIBIDO,
    EN_DIAGNOSTICO,
    ESPERANDO_REPUESTOS,
    REPARADO,
    ENTREGADO,
    CANCELADO
}