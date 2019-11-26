package com.axlan.fogofwar.campaigns;

import com.axlan.fogofwar.models.BriefingData;
import com.axlan.fogofwar.models.GameState;
import com.axlan.fogofwar.models.LevelData;

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
   * @param gameState current game state
   * @return List of items that can be purchased.
   */
  List<LevelData.ShopItem> getItems(GameState gameState);

  /**
   * Based on the current game state get the briefing to show for the overworld map
   *
   * @param gameState current game state
   * @return data for briefing to show. null if no briefing
   */
  BriefingData getMapBriefing(GameState gameState);

  /**
   * Based on the current game state get the briefing to show when entering a level
   *
   * @param gameState current game state
   * @return data for briefing to show. null if no briefing
   */
  BriefingData getLevelBriefing(GameState gameState);


}
