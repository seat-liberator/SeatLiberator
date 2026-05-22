package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.server.application.shared.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.FederatedPrincipal;

public interface FederatedSignInProcessor {
    AuthEntry process(FederatedPrincipal principal);
}
