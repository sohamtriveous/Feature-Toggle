package cc.soham.toggle.enums;

import cc.soham.toggle.FeatureCheckRequest;

/**
 * An enum that shows the type of source in the config
 * This is needed so that we can do things like {@link FeatureCheckRequest.Builder#getLatest()} when the source type is URL
 */
public enum SourceType {
    STRING,
    JSONOBJECT,
    PRODUCT,
    URL,
    NONE
}
