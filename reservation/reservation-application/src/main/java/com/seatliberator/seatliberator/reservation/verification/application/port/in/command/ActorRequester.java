package com.seatliberator.seatliberator.reservation.verification.application.port.in.command;

public record ActorRequester(
        RequesterType type,
        String actorId
) implements Requester {
}
