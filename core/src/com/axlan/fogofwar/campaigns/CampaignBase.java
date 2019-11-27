package com.axlan.fogofwar.campaigns;

import com.axlan.fogofwar.models.BriefingData;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.ShopItem;

import java.util.List;

/**
 * Base interface for campaigns
 */
public interface CampaignBase {

  /**
   * Get the string identifier for campaign type
   *
   * @return campaign identifier
   */
  String getName();

  /**
   * Get the string identifier for campaign map
   *
   * @return campaign map identifier
   */
  String getOverWorldMap();

  /**
   * used to construct new instance of a campaign
   * @return new instance of campaign
   */
  CampaignBase makeNew();

  /**
   * used to construct copy instance of a campaign
   * @return copy of campaign
   */
  CampaignBase makeCopy();

  /**
   * Based on the current game state get the items for sale in the shop
   *
   * @return List of items that can be purchased.
   */
  List<ShopItem> getItems();

  /**
   * Based on the current game state get the briefing to show for the overworld map
   *
   * @return data for briefing to show. null if no briefing
   */
  BriefingData getMapBriefing();

  /**
   * Based on the current game state get the briefing to show when entering a level
   *
   * @return data for briefing to show. null if no briefing
   */
  BriefingData getLevelBriefing();

  /**
   * Based on the current game state get the data for the selected level
   *
   * @return data for level. null if there's an error
   */
  LevelData getLevelData();


}
