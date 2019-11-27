package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.PlayerResources;
import com.axlan.fogofwar.models.ShopItem;
import com.axlan.gdxtactics.StageBasedScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;

/**
 * A screen that allows the player to purchase items before a mission.
 */
public class StoreView extends StageBasedScreen {

  private final Runnable observer;
  private final VisTable itemListWidget = new VisTable();
  private final VisLabel moneyLabel = new VisLabel();
  private final VisLabel description = new VisLabel();
  private PlayerResources playerResources;
  private List<ShopItem> shopItems;

  public StoreView(Runnable observer) {
    this.observer = observer;
    this.stage.addActor(this.makeStoreView());
    setData(
        LoadedResources.getGameStateManager().gameState.campaign.getItems(),
        LoadedResources.getGameStateManager().gameState.playerResources);
  }

  /**
   * Redraw labels and enable buttons based on new amount of money left after a purchase.
   */
  private void updateMoney() {
    this.moneyLabel.setText(String.format("Money Available: %d", playerResources.getMoney()));
    for (int i = 0; i < shopItems.size(); i++) {
      VisTextButton button = (VisTextButton) itemListWidget.getChild(i);
      button.setDisabled(playerResources.getMoney() < shopItems.get(i).cost);
    }
  }

  /**
   * Update the UI based on data about the current level, and game state.
   *
   * @param shopItems list of available items
   * @param playerResources players current state
   */
  @SuppressWarnings("SameParameterValue")
  private void setData(List<ShopItem> shopItems, final PlayerResources playerResources) {
    this.playerResources = playerResources;
    this.shopItems = shopItems;

    this.description.setText("");

    this.itemListWidget.clear();
    for (final ShopItem item : shopItems) {
      String buttonString = String.format("%s: $%d", item.name, item.cost);
      final VisTextButton shopItemButton = new VisTextButton(buttonString);
      shopItemButton.addListener(
          new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
              super.enter(event, x, y, pointer, fromActor);
              description.setText(item.description);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
              super.exit(event, x, y, pointer, fromActor);
              if (pointer == -1 && description.textEquals(item.description)) {
                description.setText("");
              }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
              super.clicked(event, x, y);
              event.stop();
            }
          });
      shopItemButton.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              if (playerResources.getMoney() >= item.cost) {
                playerResources.makePurchase(item);
                updateMoney();
              }
              shopItemButton.setDisabled(true);
              shopItemButton.setColor(Color.GREEN);
            }
          });
      this.itemListWidget.add(shopItemButton).expand().fill();
      this.itemListWidget.row();
    }
    updateMoney();
  }

  /**
   * Lay out UI elements
   *
   * @return Root table for UI
   */
  private VisTable makeStoreView() {
    // TODO-P3 Reskin and pretty up
    VisTable rootTable = new VisTable();
    rootTable.setFillParent(true);
    // rootTable.setDebug(true);
    // this.itemListWidget.setDebug(true);

    VisLabel headingLabel = new VisLabel("Intel Expenditures Selection");
    headingLabel.setAlignment(Align.center);

    this.moneyLabel.setAlignment(Align.right);

    LabelStyle labelStyle = new LabelStyle(headingLabel.getStyle());
    labelStyle.background = VisUI.getSkin().getDrawable("textfield");

    this.description.setStyle(labelStyle);
    this.description.setAlignment(Align.topLeft);

    VisTextButton doneButton = new VisTextButton("Done");
    doneButton.setColor(Color.BLUE);
    doneButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            observer.run();
          }
        });

    VisLabel padding1 = new VisLabel();
    padding1.setStyle(labelStyle);
    VisLabel padding2 = new VisLabel();
    padding2.setStyle(labelStyle);

    rootTable.top();
    rootTable.add(headingLabel).expandX().fill().colspan(2);
    rootTable.row();
    rootTable.add(padding1).fill();
    rootTable.add(this.moneyLabel).fill();
    rootTable.row();
    rootTable.add(this.itemListWidget).expandY().fill().uniform();
    rootTable.add(this.description).fill().uniform();
    rootTable.row();
    rootTable.add(padding2).fill();
    rootTable.add(doneButton).right().fill();

    return rootTable;
  }
}
