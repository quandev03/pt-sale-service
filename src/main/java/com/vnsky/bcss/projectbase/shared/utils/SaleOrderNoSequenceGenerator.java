package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.domain.entity.SaleOrderEntity;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import jakarta.persistence.ParameterMode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.procedure.ProcedureCall;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

@Slf4j
@Component
public class SaleOrderNoSequenceGenerator implements BeforeExecutionGenerator {

    private static final String PROCEDURE_ORDER_NO_GENERATOR = "generate_order_no";
    private static final String PROCEDURE_ONLINE_ORDER_NO_GENERATOR = "generate_online_order_no";
    private static final String SUPPLIER_ID_PARAM = "orgId";
    private static final String ORDER_NO_PARAM = "v_order_no";

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        // lấy số đơn hàng trong năm theo ncc và đối tác (org_id và request_org_id)
        SaleOrderEntity orderEntity = (SaleOrderEntity) owner;

        if (Objects.equals(orderEntity.getOrderType(), Constant.SaleOrder.Type.PARTNER)) {
            try (ProcedureCall storedProcedure = session.asSessionImplementor().createStoredProcedureCall(PROCEDURE_ORDER_NO_GENERATOR)) {
                storedProcedure.registerStoredProcedureParameter(SUPPLIER_ID_PARAM, String.class, ParameterMode.IN);
                storedProcedure.registerStoredProcedureParameter(ORDER_NO_PARAM, String.class, ParameterMode.OUT);

                storedProcedure.setParameter(SUPPLIER_ID_PARAM, orderEntity.getOrgId());

                storedProcedure.execute();
                return storedProcedure.getOutputParameterValue(ORDER_NO_PARAM);
            }
        } else if (Objects.equals(orderEntity.getOrderType(), Constant.SaleOrder.Type.ONLINE) || Objects.equals(orderEntity.getOrderType(), Constant.SaleOrder.Type.BUY_PACKAGE)) {
            try (ProcedureCall storedProcedure = session.asSessionImplementor().createStoredProcedureCall(PROCEDURE_ONLINE_ORDER_NO_GENERATOR)) {
                storedProcedure.registerStoredProcedureParameter(ORDER_NO_PARAM, String.class, ParameterMode.OUT);
                storedProcedure.execute();
                return storedProcedure.getOutputParameterValue(ORDER_NO_PARAM);
            }
        }
        return orderEntity.getOrderNo();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}


