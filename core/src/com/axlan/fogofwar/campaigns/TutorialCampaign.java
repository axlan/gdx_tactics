package com.axlan.fogofwar.campaigns;

import com.axlan.fogofwar.models.*;
import com.axlan.fogofwar.screens.SceneLabel;
import com.axlan.gdxtactics.TilePoint;

import java.util.*;

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
          "Old man Peters (you know the farmer)\n" +
              "is a retired infantryman in the\n" +
              "area. Paying for a secure call may\n" +
              "will reveal Alpha city troop deployment.",
          Collections.unmodifiableList(Arrays.asList(new ShopItem.Intel(1, ShopItem.SpotType.RANDOM, Collections.singletonList("Alpha"), false)))),
      new ShopItem("Pay Off General",
          100,
          "General Rick has gambling debt.\n +" +
              "Pay him off for info on Omega city troop deployment",
          Collections.unmodifiableList(Arrays.asList(new ShopItem.Intel(1, ShopItem.SpotType.RANDOM, Collections.singletonList("Omega"), false)))),
      new ShopItem("Spy Sat Photos",
          10,
          "Top of the line spy satellite.\n" +
              "Capable of 10m resolution imaging.\n" +
              "This will show the number of enemy\n" +
              "troops in each city.",
          Collections.unmodifiableList(Arrays.asList(new ShopItem.Intel(0, ShopItem.SpotType.RANDOM, null, true))))
  ));


  TutorialCampaign() {
    worldData = new WorldData(
        MAP_NAME,
        Collections.unmodifiableList(Arrays.asList(
            new WorldData.CityData(
                "Alpha",
                2,
                5,
                2,
                3
            ),
            new WorldData.CityData(
                "Omega",
                0,
                5,
                2,
                3
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
    if (isGameOver()) {
      if (getState().controlledCities.get("Alpha") == City.Controller.ENEMY) {
        pages.add(new BriefingData.BriefPage("Commander", "Your command center has been captured. I'm afraid you failed your training."));
      } else {
        pages.add(new BriefingData.BriefPage("Commander", "Congratulations, you've successfully completed your training"));
      }
    } else if (worldData.getCity("Alpha").isPresent() && worldData.getCity("Alpha").get().stationedEnemyTroops == 0) {
      String dialogue = "Well that went well. Congratulations.\n" +
          "Now you should have enough assets to pay off an enemy commander.\n" +
          "Click on Alpha city and command your troops to move to Omega city.\n" +
          "They will attack once you hit deploy.\n";
      pages.add(new BriefingData.BriefPage("Commander", dialogue));
    } else {
      pages.add(new BriefingData.BriefPage("Commander",
          "Welcome class!\nWhile the situation on the front is dire, we still have our standards, and you'll be required to complete your training.\nThis is what separates us from the animals."
      ));
      pages.add(new BriefingData.BriefPage("Commander",
          "While in the past you would need to take classes, pass an exam, and shadow senior officers for years, you will need to complete this mock exercise before we release you to active service.\nYou will lead a battle between city Alpha and Omega, and capture Omega city"
      ));
      String dialogue = "While you could go in guns ablazing, I recommend you take advantage of the intelligence available. After this briefing you'll be shown the campaign map.\n" +
          "This map lets you see the troop presences and move your troops between cities.\n" +
          "You can also select the items menu at the top of the screen to purchase intel that will give you information about the enemy\n";
      pages.add(new BriefingData.BriefPage("Commander", dialogue));
      dialogue = "Clicking on a city shows you the troops currently stationed there.\n" +
          "The + and - indicate the troops you've commanded to move in and out of the city.\n" +
          "Use the other window to order troops to move from the selected city to a city connected to it.\n" +
          "You can then click on a listed troop movement to cancel it.\n" +
          "If there are both allied and enemy troops in a city it will be marked as contested.\n" +
          "After hitting the deploy button, battles will take place to determine the new controller of each contested city.\n";
      pages.add(new BriefingData.BriefPage("Commander", dialogue));
      dialogue = "I suggest you start by keeping your troops in Alpha city to defend it.\n" +
          "You'll need some additional resources to successfully attack Omega city\n" +
          "You collect money for each city you control after a deployment completes\n";
      pages.add(new BriefingData.BriefPage("Commander", dialogue));
    }
    return new BriefingData(setting, pages);
  }

  @Override
  public LevelData getLevelData() {
    //TODO-P1 allow deploying different unit types
    //TODO-P2 allow multiple instances of a formation to take additional spawn points from the random set
    HashMap<String, LevelData> levels = new HashMap<>();
    Optional<WorldData.CityData> cityDataOptional = getState().campaign.getOverWorldData().getCity(getState().contestedCity);
    if (!cityDataOptional.isPresent()) {
      throw new RuntimeException("City name not valid");
    }
    WorldData.CityData cityData = cityDataOptional.get();

    List<LevelData.UnitStart> enemyPos = new ArrayList<>();
    for (int i = 0; i < cityData.stationedEnemyTroops; i++) {
      enemyPos.add(new LevelData.UnitStart("scout", new TilePoint(i, 0)));
    }
    levels.put("Alpha",
        new LevelData(
            new TilePoint(8, 4),
            Collections.unmodifiableList(Arrays.asList(
                new LevelData.UnitAllotment("tank", cityData.stationedFriendlyTroops)
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
                    Collections.unmodifiableList(enemyPos)
                )
            )),
            "advanced1",
            new LevelData.UnitBehavior(LevelData.UnitBehaviorType.MOVE, "{\"target\": {\"x\": 8, \"y\": 7}}"),
            null,
            new LevelData.AlternativeWinConditions(new TilePoint(8, 7), new TilePoint(8, 7))
        ));

    List<LevelData.Formation> enemyFormations = new ArrayList<>();
    for (int i = 0; i < cityData.stationedEnemyTroops; i++) {
      enemyFormations.add(new LevelData.Formation(
          Collections.unmodifiableList(Arrays.asList(
              new TilePoint(3, 6),
              new TilePoint(8, 6),
              new TilePoint(12, 6)
          )),
          Collections.unmodifiableList(Arrays.asList(
              new LevelData.UnitStart("tank", new TilePoint(0, 0))
          ))
      ));
    }
    levels.put("Omega",
        new LevelData(
            new TilePoint(8, 4),
            Collections.unmodifiableList(Arrays.asList(
                new LevelData.UnitAllotment("scout", cityData.stationedFriendlyTroops)
            )),
            Collections.unmodifiableList(Arrays.asList(
                new TilePoint(3, 2),
                new TilePoint(4, 2),
                new TilePoint(5, 2),
                new TilePoint(8, 2),
                new TilePoint(11, 2),
                new TilePoint(12, 2),
                new TilePoint(13, 2)
            )),
            Collections.unmodifiableList(enemyFormations),
            "advanced1",
            new LevelData.UnitBehavior(LevelData.UnitBehaviorType.ATTACK, "{\"onlyInUnitSight\": false}"),
            new LevelData.AlternativeWinConditions(new TilePoint(8, 7), null),
            null
        ));
    return levels.get(getState().contestedCity);
  }

  @Override
  public BriefingData getLevelBriefing() {
    List<BriefingData.BriefPage> pages = new ArrayList<>();
    String setting;

    if (getState().contestedCity.equals("Alpha")) {
      setting = "Alpha city";
      String dialogue = "Your command center is under attack!\n" +
          "Destroy the enemy scouts before they reach the base!\n";
      pages.add(
          new BriefingData.BriefPage(
              "Commander",
              dialogue));
    } else if (getState().contestedCity.equals("Omega")) {
      setting = "Omega city";
      pages.add(
          new BriefingData.BriefPage(
              "Commander",
              "Capture the enemy command center!"));
    } else {
      return null;
    }
    return new BriefingData(setting, pages);
  }

  @Override
  public WorldData getOverWorldData() {
    return worldData;
  }

  @Override
  public boolean isGameOver() {
    return getState().controlledCities.size() > 0 &&
        (getState().controlledCities.get("Alpha") == City.Controller.ENEMY ||
            getState().controlledCities.get("Omega") == City.Controller.PLAYER);
  }

  @Override
  public void onDeploymentDone() {
    getState().playerResources.addMoney(100);
  }

}
