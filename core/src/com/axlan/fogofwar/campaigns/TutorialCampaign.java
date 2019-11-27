package com.axlan.fogofwar.campaigns;

import com.axlan.fogofwar.models.BriefingData;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.ShopItem;
import com.axlan.fogofwar.models.WorldData;
import com.axlan.gdxtactics.TilePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO-P1 Make the attributes returned by these functions respond to game state

public class TutorialCampaign implements CampaignBase {

  private static final String NAME = "Tutorial";
  private static final String MAP_NAME = "tutorial_overworld";

  private final WorldData worldData;

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


  public TutorialCampaign() {
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

  public TutorialCampaign(TutorialCampaign other) {
    this.worldData = other.worldData;
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
    ArrayList<ShopItem> items = new ArrayList<>(ITEMS);

    return items;
  }

  @Override
  public BriefingData getMapBriefing() {
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
  public LevelData getLevelData() {
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
    return null;
  }

  @Override
  public WorldData getOverWorldData() {
    return worldData;
  }

}
