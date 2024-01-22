package com.eshoponcontainers.orderapi.application.domainEventHandlers;

import org.springframework.stereotype.Component;

import com.eshoponcontainers.aggregatesModel.orderAggregate.IOrderRepository;
import com.eshoponcontainers.events.BuyerAndPaymentMethodVerifiedDomainEvent;

import an.awesome.pipelinr.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderWhenBuyerAndPaymentMethodVerifiedDomainEventHandler implements Notification.Handler<BuyerAndPaymentMethodVerifiedDomainEvent> {

    private final IOrderRepository orderRepository;

    @Override
    public void handle(BuyerAndPaymentMethodVerifiedDomainEvent event) {
        var order = orderRepository.get(event.getOrderId());
        order.setBuyer(event.getBuyer());
        order.setPaymentMethod(event.getPaymentMethod());

        log.trace("Order with Id: {} has been successfully updated with a payment method {} {}", event.getOrderId(), event.getPaymentMethod().getId());
    }

}
