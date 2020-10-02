package de.dev.eth0.libgdx.demo.generation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MarchingSquaresSelectorTest {

  @Mock
  private TextureAtlas textureAtlas;

  private TextureAtlas.AtlasRegion[] mockRegions = new TextureAtlas.AtlasRegion[16];

  @BeforeEach
  void setUp() {
    for (int i = 0; i < 16; i++) {
      mockRegions[i] = mock(TextureAtlas.AtlasRegion.class);
      mockRegions[i].name = String.valueOf(i);
      when(textureAtlas.findRegion(String.valueOf(i))).thenReturn(mockRegions[i]);
    }
  }

  @Test
  void case0() {
    boolean[][] map = {
        { true, true },
        { true, true }
    };
    assertRegion(map, 0);
  }

  @Test
  void case1() {
    boolean[][] map = {
        { true, true },
        { false, true }
    };
    assertRegion(map, 1);
  }

  @Test
  void case2() {
    boolean[][] map = {
        { true, true },
        { true, false }
    };
    assertRegion(map, 2);
  }

  @Test
  void case3() {
    boolean[][] map = {
        { true, true },
        { false, false }
    };
    assertRegion(map, 3);
  }

  @Test
  void case4() {
    boolean[][] map = {
        { true, false },
        { true, true }
    };
    assertRegion(map, 4);
  }

  @Test
  void case5() {
    boolean[][] map = {
        { true, false },
        { false, true }
    };
    assertRegion(map, 5);
  }

  @Test
  void case6() {
    boolean[][] map = {
        { true, false },
        { true, false }
    };
    assertRegion(map, 6);
  }

  @Test
  void case7() {
    boolean[][] map = {
        { true, false },
        { false, false }
    };
    assertRegion(map, 7);
  }

  @Test
  void case8() {
    boolean[][] map = {
        { false, true },
        { true, true }
    };
    assertRegion(map, 8);
  }

  @Test
  void case9() {
    boolean[][] map = {
        { false, true },
        { false, true }
    };
    assertRegion(map, 9);
  }

  @Test
  void case10() {
    boolean[][] map = {
        { false, true },
        { true, false }
    };
    assertRegion(map, 10);
  }

  @Test
  void case11() {
    boolean[][] map = {
        { false, true },
        { false, false }
    };
    assertRegion(map, 11);
  }

  @Test
  void case12() {
    boolean[][] map = {
        { false, false },
        { true, true }
    };
    assertRegion(map, 12);
  }

  @Test
  void case13() {
    boolean[][] map = {
        { false, false },
        { false, true }
    };
    assertRegion(map, 13);
  }

  @Test
  void case14() {
    boolean[][] map = {
        { false, false },
        { true, false }
    };
    assertRegion(map, 14);
  }

  @Test
  void case15() {
    boolean[][] map = {
        { false, false },
        { false, false }
    };
    assertRegion(map, 15);
  }

  private void assertRegion(boolean[][] map, int region) {
    assertRegion(map, region, 0, 0);
  }

  private void assertRegion(boolean[][] map, int region, int x, int y) {
    MarchingSquaresSelector underTest = new MarchingSquaresSelector(map, textureAtlas);

    TextureAtlas.AtlasRegion atlasRegion = (TextureAtlas.AtlasRegion)underTest.getTextureRegion(x, y);
    //override message to get better output
    assertThat(atlasRegion)
        .as("%s equal to %s", atlasRegion.name, mockRegions[region].name)
        .isEqualTo(mockRegions[region]);

  }
}