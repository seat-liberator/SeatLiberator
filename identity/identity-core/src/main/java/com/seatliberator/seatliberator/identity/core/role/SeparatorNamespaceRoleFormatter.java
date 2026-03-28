package com.seatliberator.seatliberator.identity.core.role;

public class SeparatorNamespaceRoleFormatter implements NamespaceRoleFormatter {
    private final String separator;

    public SeparatorNamespaceRoleFormatter(
            String separator
    ) {
        this.separator = separator;
    }

    @Override
    public String format(NamespaceRole namespaceRole) {
        return namespaceRole.namespace() + separator + namespaceRole.role().name();
    }
}
