package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;

public interface FederatedSignInProcessor {
    Actor authenticate(FederatedPrincipal principal);
}
