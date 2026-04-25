package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

public record UpdateRoomCommand(
        String oldRoomId,
        String newRoomId
) {
}
