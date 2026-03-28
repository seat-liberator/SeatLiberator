package com.seatliberator.seatliberator.notification.application.port.in;

public interface NotificationRegistrar {
    NotificationEntry register(NotificationRegisterCommand command);
}
