package cc.soham.toggle.enums;

/**
 * A 3 state enum that represents the result of the processing of a {@link cc.soham.toggle.objects.Feature}
 */
public enum ResponseDecision {
    RESPONSE_UNDECIDED,
    RESPONSE_ENABLED,
    RESPONSE_DISABLED;

    private String metadata;

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getMetadata() {
        return metadata;
    }
}

