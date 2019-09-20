package com.axlan.gdxtactics;

import com.axlan.gdxtactics.LevelData.ShopItem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.VisUI;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisWindow;
import java.util.Vector;

class StoreView extends ClickListener {
  private LevelData levelData;
  private PlayerResources playerResources;
  final VisTable rootTable;
  private final CompletionObserver observer;
  private final VisTable itemListWidget = new VisTable();
  private final VisLabel moneyLabel = new VisLabel();
  private final VisLabel description = new VisLabel();

  StoreView(CompletionObserver observer) {
    this.observer = observer;
    this.rootTable = this.makeStoreView();
  }

  private void updateMoney() {
    this.moneyLabel.setText(String.format("Money Available: %d", playerResources.money));
    for (int i = 0; i < levelData.shopItems.length; i++) {
      VisTextButton button = (VisTextButton) itemListWidget.getChild(i);
      button.setDisabled(playerResources.money < levelData.shopItems[i].cost);
    }
  }

  void setData(LevelData levelData, final PlayerResources playerResources) {
    this.playerResources = playerResources;
    this.levelData = levelData;

    this.description.setText("");

    this.itemListWidget.clear();
    for (int i = 0; i < levelData.shopItems.length; i++) {
      final ShopItem item = levelData.shopItems[i];
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
              if (playerResources.money >= item.cost) {
                playerResources.money -= item.cost;
                playerResources.purchases.add(item);
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

  @Override
  public void clicked(InputEvent event, float x, float y) {
    observer.onDone();
    event.stop();
  }

  private VisTable makeStoreView() {
    VisTable rootTable = new VisTable();
    rootTable.setFillParent(true);
    // rootTable.setDebug(true);
    // this.itemListWidget.setDebug(true);

    VisLabel headingLabel = new VisLabel("Intel Expenditures Selection");
    headingLabel.setAlignment(Align.center);

    this.moneyLabel.setAlignment(Align.right);

    LabelStyle labelStyle = headingLabel.getStyle();
    labelStyle.background = VisUI.getSkin().getDrawable("textfield");

    this.description.setStyle(labelStyle);
    this.description.setAlignment(Align.topLeft);

    VisTextButton doneButton = new VisTextButton("Done");
    doneButton.addListener(this);
    doneButton.setColor(Color.BLUE);
    doneButton.addListener(this);

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
