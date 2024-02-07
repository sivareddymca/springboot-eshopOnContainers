package com.eshoponcontainers.basketapi.controllers;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshoponcontainers.basketapi.integrationevents.events.UserCheckoutAcceptedIntegrationEvent;
import com.eshoponcontainers.basketapi.model.BasketCheckout;
import com.eshoponcontainers.basketapi.model.CustomerBasket;
import com.eshoponcontainers.basketapi.repositories.RedisBasketDataRepository;
import com.eshoponcontainers.eventbus.abstractions.EventBus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/basket")
@Slf4j
public class BasketController {

    private final RedisBasketDataRepository basketDataRepository;
    private final EventBus eventBus;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerBasket> getBasket(@PathVariable String id, Principal principal) {
        if(principal != null)
            log.info(principal.getName());
        else
            log.info("Principal is null");
        CustomerBasket basket = basketDataRepository.getBasket(id);
        if (basket == null)
            basket = new CustomerBasket(id);

        return ResponseEntity.ok(basket);
    }

    @PostMapping()
    public ResponseEntity<CustomerBasket> updateBasket(@RequestBody CustomerBasket basket) {
        CustomerBasket updatedBasket = basketDataRepository.updateBasket(basket);
        return ResponseEntity.ok(updatedBasket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable String id) {
        basketDataRepository.deleteBasket(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Void> checkout(@RequestBody BasketCheckout basketCheckout,
            @RequestHeader(name = "x-rquestid") String requestId, Principal principal) {
        // TODO: HIGH : Yet to implement IdentityService
        UUID rquestUUID = null;
        String userId = null;

        if(principal != null)
            log.info(principal.getName());
        else
            log.info("Principal is null");

        try {
            rquestUUID = UUID.fromString(requestId);
            basketCheckout.setRequestId(rquestUUID);
        } catch (Exception e){ }
        
        CustomerBasket basket = basketDataRepository.getBasket(userId);
        if(basket == null)
            return ResponseEntity.badRequest().build();

        //TODO: HIGH : username need to be pulled from httpcontext;
        // userName = this.HttpContext.User.FindFirst(x => x.Type == ClaimTypes.Name).Value;
        String userName = null;

        UserCheckoutAcceptedIntegrationEvent event = new UserCheckoutAcceptedIntegrationEvent(userId, userName, basketCheckout.getCity(), basketCheckout.getStreet(), basketCheckout.getState(),
            basketCheckout.getCountry(), basketCheckout.getZipcode(), basketCheckout.getCardNumber(), basketCheckout.getCardHolderName(),
            basketCheckout.getCardExpiration(), basketCheckout.getCardSecurityNumber(), basketCheckout.getCardTypeId(), basketCheckout.getBuyer(),
            basketCheckout.getRequestId(), basket);

            try {
                    eventBus.publish(event);        
            } catch (Exception e) {
                log.error("ERROR Publishihng Integration event", e);
            }

            return ResponseEntity.accepted().build();       
        
    }

}
