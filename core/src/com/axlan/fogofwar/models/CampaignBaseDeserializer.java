package com.axlan.fogofwar.models;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.fogofwar.campaigns.TutorialCampaign;
import com.google.gson.*;

import java.lang.reflect.Type;


public class CampaignBaseDeserializer implements JsonDeserializer<CampaignBase> {
  @Override
  public CampaignBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = (JsonObject) json;
      //TODO-P2 Avoid needing to update this file for each Campaign class
      if (object.get("type").getAsString().equals(TutorialCampaign.NAME)) {
        return new TutorialCampaign();
      }
    }
    throw new JsonSyntaxException("Unknown Campaign Type");
  }
}
