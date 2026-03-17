package com.seatliberator.seatliberator.verification.application.port.in.command;

public record ActorRequester(
        RequesterType type,
        String actorId
) implements Requester {
}
