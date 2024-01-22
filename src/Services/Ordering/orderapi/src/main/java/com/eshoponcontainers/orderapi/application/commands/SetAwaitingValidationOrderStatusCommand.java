package com.eshoponcontainers.orderapi.application.commands;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SetAwaitingValidationOrderStatusCommand implements Command<Boolean> {

    private int orderNumber;
}
