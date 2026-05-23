package com.seatliberator.seatliberator.identity.server.application.role.port.in;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.query.FindUserGrantedSummaryQuery;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedSummaryResult;

public interface FindUserGrantedSummaryUseCase {
    UserGrantedSummaryResult find(FindUserGrantedSummaryQuery query);
}
