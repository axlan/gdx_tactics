import com.axlan.fogofwar.models.GameStateManager;
import com.axlan.fogofwar.screens.SceneLabel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameStateTest {

//  @BeforeAll
//  static void setup(){
//    LoadedResources.initializeGlobal(new StringObserver() {
//      @Override
//      public void processString(String str) {
//      }
//    });
//    LoadedResources.initializeLevel();
//  }
//
//  @BeforeEach
//  void setupThis(){
//    System.out.println("@BeforeEach executed");
//  }

  @Test
  void testSerialization() {
    GameStateManager gameStateManager = new GameStateManager((SceneLabel a) -> {
    });
    gameStateManager.loadFile("data/example_saves/test1_battle.json");
    Assertions.assertEquals(gameStateManager.gameState.scene, SceneLabel.BATTLE_MAP);
    //TODO-P2 Make test more robust
  }

//
//  @AfterEach
//  void tearThis(){
//    System.out.println("@AfterEach executed");
//  }
//
//  @AfterAll
//  static void tear(){
//    System.out.println("@AfterAll executed");
//  }
}
