package eu.codlab.cyphersend.messages.model.content;

import org.json.JSONObject;

import eu.codlab.cyphersend.messages.model.content.MessageContent;

/**
 * Created by kevinleperf on 01/04/2014.
 */
public class MessageImage extends MessageContent {
    protected MessageImage(boolean incognito) {
        super(incognito);
    }

    @Override
    public void fromJSON(JSONObject object) {

    }

    @Override
    public JSONObject toJSON() {
        return new JSONObject();
    }
}
