package com.axlan.fogofwar.models;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.fogofwar.campaigns.CampaignSet;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Class for Deserializing CampaignBase for Gson
 */
public class CampaignBaseDeserializer implements JsonDeserializer<CampaignBase> {
  @Override
  public CampaignBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    Type type = null;
    if (json.isJsonObject()) {
      JsonObject object = (JsonObject) json;
      type = CampaignSet.getType(object.get("type").getAsString());
      object.remove("type");
    }
    if (type == null) {
      throw new JsonSyntaxException("Unknown Campaign Type");
    }
    return context.deserialize(json, type);
  }
}
