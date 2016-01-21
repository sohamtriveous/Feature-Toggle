package cc.soham.toggle.objects;

import cc.soham.toggle.enums.ResponseDecision;

/**
 * Created by sohammondal on 21/01/16.
 */
public class ResponseDecisionMeta {
    public ResponseDecision responseDecision;
    public String metadata;

    public ResponseDecisionMeta(ResponseDecision responseDecision) {
        this.responseDecision = responseDecision;
    }

    public ResponseDecisionMeta(ResponseDecision responseDecision, String metadata) {
        this.responseDecision = responseDecision;
        this.metadata = metadata;
    }
}
