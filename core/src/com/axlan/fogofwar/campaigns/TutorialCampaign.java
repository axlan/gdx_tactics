package com.axlan.fogofwar.campaigns;

import com.axlan.fogofwar.models.*;
import com.axlan.fogofwar.screens.SceneLabel;
import com.axlan.gdxtactics.TilePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO-P1 Make the attributes returned by these functions respond to game state

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class TutorialCampaign implements CampaignBase {

  private static final String NAME = "Tutorial";
  private static final String MAP_NAME = "tutorial_overworld";

  private final WorldData worldData;

  private GameState getState() {
    return LoadedResources.getGameStateManager().gameState;
  }

  private static final List<ShopItem> ITEMS = Collections.unmodifiableList(Arrays.asList(
      new ShopItem("Farmer",
          10,
          "Old man Peters (you know the farmer)\nis a retired infantryman in the\narea. Paying for a secure call may\nreveal if there were troops visible.",
          Collections.unmodifiableList(Arrays.asList(new ShopItem.Intel(1, ShopItem.SpotType.RANDOM)))),
      new ShopItem("Spy Sat",
          100000000,
          "Top of the line spy sattelite.\nCapable of 10m resolution imaging.",
          Collections.unmodifiableList(Arrays.asList(new ShopItem.Intel(1, ShopItem.SpotType.RANDOM))))
  ));


  TutorialCampaign() {
    worldData = new WorldData(
        MAP_NAME,
        Collections.unmodifiableList(Arrays.asList(
            new WorldData.CityData(
                "Alpha",
                2,
                5,
                1,
                3
            ),
            new WorldData.CityData(
                "Omega",
                0,
                0,
                0,
                0
            )
        ))
    );
  }

  private TutorialCampaign(TutorialCampaign other) {
    this.worldData = new WorldData(other.worldData);
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
    return new TutorialCampaign(this);
  }

  @Override
  public List<ShopItem> getItems() {
    ArrayList<ShopItem> items = new ArrayList<>();
    if (getState().scene == SceneLabel.CAMPAIGN_MAP) {
      items.addAll(ITEMS);
    }
    return items;
  }

  @Override
  public BriefingData getMapBriefing() {
    List<BriefingData.BriefPage> pages = new ArrayList<>();
    String setting = "SpyVSpy Training Camp";
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
  public LevelData getLevelData() {
    //TODO-P1 make this different for different contested city.
    //TODO-P1 make it use the deployment data for the city
    //TODO-P1 allow deploying different unit types
    return new LevelData(
        new TilePoint(8, 4),
        Collections.unmodifiableList(Arrays.asList(
            new LevelData.UnitAllotment("tank", 2)
        )),
        Collections.unmodifiableList(Arrays.asList(
            new TilePoint(3, 6),
            new TilePoint(4, 6),
            new TilePoint(8, 6),
            new TilePoint(12, 6),
            new TilePoint(13, 6)
        )),
        Collections.unmodifiableList(Arrays.asList(
            new LevelData.Formation(
                Collections.unmodifiableList(Arrays.asList(
                    new TilePoint(4, 2),
                    new TilePoint(12, 2)
                )),
                Collections.unmodifiableList(Arrays.asList(
                    new LevelData.UnitStart("scout", new TilePoint(0, 0))
                ))
            )
        )),
        "advanced1",
        new LevelData.UnitBehavior(LevelData.UnitBehaviorType.MOVE, "{\"target\": {\"x\": 8, \"y\": 7}}"),
        null,
        new LevelData.AlternativeWinConditions(new TilePoint(8, 7))
    );
  }

  @Override
  public BriefingData getLevelBriefing() {
    List<BriefingData.BriefPage> pages = new ArrayList<>();
    String setting;
    if (getState().contestedCity.equals("Alpha")) {
      setting = "Alpha city";
      pages.add(
          new BriefingData.BriefPage(
              "Commander",
              "Your command center is under attack!"));
    } else {
      return null;
    }
    return new BriefingData(setting, pages);
  }

  @Override
  public WorldData getOverWorldData() {
    return worldData;
  }

}
