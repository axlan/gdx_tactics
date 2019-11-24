package com.axlan.fogofwar.campaigns;

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
}
