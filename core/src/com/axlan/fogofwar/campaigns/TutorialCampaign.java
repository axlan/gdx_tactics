package com.axlan.fogofwar.campaigns;

import com.axlan.fogofwar.models.BriefingData;
import com.axlan.fogofwar.models.GameState;
import com.axlan.fogofwar.models.LevelData;

import java.util.ArrayList;
import java.util.List;

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

  @Override
  public List<LevelData.ShopItem> getItems(GameState gameState) {
    return null;
  }

  @Override
  public BriefingData getMapBriefing(GameState gameState) {
    List<BriefingData.BriefPage> pages = new ArrayList<>();
    String setting = "";

    setting = "SpyVSpy Training Camp";
    pages.add(new BriefingData.BriefPage("Commander",
        "Hello class!\nWhile the situation on the front is dire, we still have our standards.\nThis is what separates us from the animals."
    ));
    pages.add(new BriefingData.BriefPage("Commander",
        "While in the past you would need to take classes, pass an exam, and shadow senior officers\nfor years, you will need to complete this mock exercise before we release you to active service.\nYou will lead a battle between city Alpha and Omega, and capture Omega city"
    ));
    pages.add(new BriefingData.BriefPage("Commander",
        "While you could go in guns ablazing, I recommend you take advantage\nof the intelligence available.\nI'll give  you one piece for free, defend Alpha city before leading a counter attack."
    ));

    return new BriefingData(setting, pages);
  }

  @Override
  public BriefingData getLevelBriefing(GameState gameState) {
    return null;
  }
}
