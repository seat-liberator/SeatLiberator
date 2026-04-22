package com.seatliberator.seatliberator.reservation.room.application.port.in.command;

public record UpdateRoomCommand(
        String oldRoomId,
        String newRoomId
) {
}
