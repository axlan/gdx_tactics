package com.axlan.fogofwar.campaigns;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Class for listing available campaigns and a factory for building them by name
 */
public class CampaignSet {

  /**
   * List of instances of all campaign classes. Not directly used except to instantiate new instances
   */
  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  static private final List<CampaignBase> CAMPAIGNS = Collections.unmodifiableList(Arrays.asList(
      new TutorialCampaign()
  ));
  /**
   * List of string identifiers for all campaigns
   */
  static public final List<String> CAMPAIGN_NAMES = Collections.unmodifiableList(CAMPAIGNS.stream().map(CampaignBase::getName).collect(toList()));

  /**
   * Takes a string identifier and returns a new instance of the corresponding campaign
   *
   * @param name campaign identifier
   * @return new instance of the corresponding campaign, or null if invalid
   */
  public static CampaignBase newCampaignByName(String name) {
    for (int i = 0; i < CAMPAIGN_NAMES.size(); i++) {
      if (name.equals(CAMPAIGN_NAMES.get(i))) {
        return CAMPAIGNS.get(i).makeNew();
      }
    }
    return null;
  }

  /**
   * Get Class types for deserializing Campaign objects
   *
   * @param name campaign identifier
   * @return class for that campaign, or null if invalid
   */
  public static Type getType(String name) {
    for (int i = 0; i < CAMPAIGN_NAMES.size(); i++) {
      if (name.equals(CAMPAIGN_NAMES.get(i))) {
        return CAMPAIGNS.get(i).getClass();
      }
    }
    return null;
  }

}
