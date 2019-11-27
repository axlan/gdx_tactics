package com.axlan.fogofwar.models;

import java.util.Collections;
import java.util.List;

/**
 * Data that describes a briefing dialogue
 */
public class BriefingData {

  /**
   * The string to display describing where the briefing is occurring
   */
  public final String briefSetting;
  /**
   * The pages of the briefing to step through
   */
  public final List<BriefPage> briefPages;

  public BriefingData(String briefSetting, List<BriefPage> briefPages) {
    this.briefSetting = briefSetting;
    this.briefPages = Collections.unmodifiableList(briefPages);
  }

  /**
   * Description of a page of briefing dialogue
   */
  public static class BriefPage {

    /**
     * The identification for the speaker
     */
    public final String speaker;
    /**
     * The page of dialogue
     */
    public final String dialogue;

    public BriefPage(String speaker, String dialogue) {
      this.speaker = speaker;
      this.dialogue = dialogue;
    }
  }

}
