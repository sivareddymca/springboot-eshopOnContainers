package com.eshoponcontainers.catalogapi.integrationevents.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStockItem {

    private int productId;
    private int units;
}
