package cc.soham.toggle.objects;

/**
 * Created by sohammondal on 21/01/16.
 */
public class ResponseDecisionMeta {
    public String state;
    public String metadata;

    public ResponseDecisionMeta(Rule rule) {
        this.state = rule.getState();
        this.metadata = rule.getMetadata();
    }

    public ResponseDecisionMeta(String state, String metadata) {
        this.state = state;
        this.metadata = metadata;
    }

    public ResponseDecisionMeta(String state) {
        this.state = state;
    }
}
