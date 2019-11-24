package com.axlan.fogofwar.campaigns;

public class TutorialCampaign implements CampaignBase {

  public static final String NAME = "Tutorial";

  public TutorialCampaign() {
  }

  public TutorialCampaign(TutorialCampaign other) {
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public CampaignBase makeNew() {
    return new TutorialCampaign();
  }

  @Override
  public CampaignBase makeCopy() {
    return new TutorialCampaign();
  }
}
