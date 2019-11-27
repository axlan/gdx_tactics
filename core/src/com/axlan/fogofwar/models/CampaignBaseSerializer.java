package com.axlan.fogofwar.models;


import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.fogofwar.campaigns.TutorialCampaign;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Class for Serializing CampaignBase for Gson
 */
public class CampaignBaseSerializer implements JsonSerializer<CampaignBase> {
  @Override
  public JsonElement serialize(CampaignBase src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonSubscription = (JsonObject) context.serialize(src, TutorialCampaign.class);
    jsonSubscription.addProperty("type", src.getName());
    return jsonSubscription;
  }
}