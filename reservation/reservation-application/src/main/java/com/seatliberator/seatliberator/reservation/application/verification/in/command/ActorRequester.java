package com.seatliberator.seatliberator.reservation.application.verification.in.command;

public record ActorRequester(
        RequesterType type,
        String actorId
) implements Requester {
}
