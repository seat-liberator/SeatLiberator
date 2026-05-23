package com.seatliberator.seatliberator.identity.core.actor;

public enum TestCapability implements Capability {
    TEST_CAPABILITY("test.capability", "this is test capability.");

    private final String scope;
    private final String description;

    TestCapability(String scope, String description) {
        this.scope = scope;
        this.description = description;
    }

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public String description() {
        return description;
    }
}
