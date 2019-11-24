package com.axlan.fogofwar.campaigns;

public interface CampaignBase {
  String getName();

  CampaignBase makeNew();

  CampaignBase makeCopy();
}
