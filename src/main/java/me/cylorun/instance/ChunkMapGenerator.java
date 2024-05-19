package me.cylorun.instance;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ChunkMapGenerator {
    private final long seed;
    private final Dimension dim;
    private final java.awt.Dimension size;
    private final RunCoords runCoords;
    private BastionRemnant bastion;
    private Fortress fort;
    private Stronghold sh;
    private RuinedPortal owRp;
    private RuinedPortal netherRp;
    private Mansion mansion;
    private ChunkRand rand;

    public ChunkMapGenerator(long seed, java.awt.Dimension size, Dimension dim, RunCoords runCoords) {
        this.seed = seed;
        this.dim = dim;
        this.size = size;
        this.runCoords = runCoords;

        this.fort = new Fortress(MCVersion.v1_16_1);
        this.bastion = new BastionRemnant(MCVersion.v1_16_1);
        this.sh = new Stronghold(MCVersion.v1_16_1);
        this.mansion = new Mansion(MCVersion.v1_16_1);
        this.owRp = new RuinedPortal(Dimension.OVERWORLD, MCVersion.v1_16_1);
        this.netherRp = new RuinedPortal(Dimension.NETHER, MCVersion.v1_16_1);
        this.rand = new ChunkRand(this.seed);
    }

    public void generate() {
        BiomeSource source = this.getBiomeSource();
        BufferedImage image = new BufferedImage(size.width * 16, size.height * 16, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < size.width; i++) {
            for (int j = 0; j < size.height; j++) {
                Biome biomeId = source.getBiome(i * 16, 63, j * 16);
                Color color = mapBiomeToColor(biomeId.getName());

                Graphics2D g = image.createGraphics();
                g.setColor(color);
                g.fillRect(i * 16, j * 16, 16, 16);
                g.dispose();
            }
        }

        try {
            File output = new File(dim.getName().toLowerCase() + ".png");
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStructure(int x, int z) { // chunk coords

        return "";
    }

    private BiomeSource getBiomeSource() {
        switch (this.dim) {
            case OVERWORLD -> {
                return new OverworldBiomeSource(MCVersion.v1_16_1, this.seed);
            }
            case NETHER -> {
                return new NetherBiomeSource(MCVersion.v1_16_1, this.seed);
            }
            case END -> {
                return new EndBiomeSource(MCVersion.v1_16_1, this.seed);
            }
            default -> throw new RuntimeException("Not a valid dimension: " + this.dim.getName());
        }
    }

    private Color mapBiomeToColor(String biome) {
        switch (biome.toLowerCase()) {
            // Overworld Biomes
            case "plains":
                return new Color(151, 255, 97);
            case "sunflower_plains":
                return new Color(247, 255, 60);
            case "forest":
                return new Color(34, 139, 34);
            case "flower_forest":
                return new Color(0, 205, 102);
            case "birch_forest":
                return new Color(190, 240, 163);
            case "birch_forest_hills":
                return new Color(114, 174, 97);
            case "tall_birch_forest":
                return new Color(183, 229, 165);
            case "tall_birch_hills":
                return new Color(157, 205, 128);
            case "dark_forest":
                return new Color(0, 51, 0);
            case "dark_forest_hills":
                return new Color(0, 34, 0);
            case "mountains":
                return new Color(105, 105, 105);
            case "wooded_hills":
                return new Color(76, 153, 0);
            case "wooded_mountains":
                return new Color(128, 128, 128);
            case "gravelly_mountains":
                return new Color(170, 170, 170);
            case "mountain_edge":
                return new Color(128, 128, 128);
            case "taiga":
                return new Color(0, 51, 0);
            case "taiga_hills":
                return new Color(0, 34, 0);
            case "giant_tree_taiga":
                return new Color(0, 102, 51);
            case "giant_tree_taiga_hills":
                return new Color(0, 76, 38);
            case "snowy_taiga":
                return new Color(192, 192, 192);
            case "snowy_taiga_hills":
                return new Color(128, 128, 128);
            case "taiga_mountains":
                return new Color(56, 72, 56);
            case "swamp":
                return new Color(0, 102, 102);
            case "swamp_hills":
                return new Color(0, 77, 77);
            case "desert":
                return new Color(255, 255, 102);
            case "desert_hills":
                return new Color(255, 255, 153);
            case "savanna":
                return new Color(204, 153, 0);
            case "savanna_plateau":
                return new Color(204, 153, 0);
            case "shattered_savanna":
                return new Color(204, 153, 0);
            case "shattered_savanna_plateau":
                return new Color(204, 153, 0);
            case "jungle":
                return new Color(0, 153, 0);
            case "jungle_edge":
                return new Color(0, 102, 0);
            case "jungle_hills":
                return new Color(0, 76, 0);
            case "bamboo_jungle":
                return new Color(0, 102, 51);
            case "modified_jungle":
                return new Color(51, 102, 0);
            case "bamboo_jungle_hills":
                return new Color(0, 76, 38);
            case "ocean":
                return new Color(0, 0, 255);
            case "deep_ocean":
                return new Color(0, 0, 128);
            case "frozen_ocean":
                return new Color(192, 192, 255);
            case "warm_ocean":
                return new Color(0, 128, 255);
            case "lukewarm_ocean":
                return new Color(0, 102, 255);
            case "cold_ocean":
                return new Color(0, 76, 255);
            case "deep_cold_ocean":
                return new Color(9, 9, 102);
            case "deep_frozen_ocean":
                return new Color(7, 7, 63);
            case "deep_lukewarm_ocean":
                return new Color(42, 112, 110);
            case "frozen_river":
                return new Color(192, 192, 255);
            case "river":
                return new Color(0, 0, 255);
            case "beach":
                return new Color(255, 255, 204);
            case "stone_beach":
                return new Color(210, 210, 180);
            case "snowy_beach":
                return new Color(255, 255, 204);
            case "snowy_tundra":
                return new Color(239, 239, 228);
            case "snowy_mountains":
                return new Color(106, 108, 106);
            case "stone_shore":
                return new Color(128, 122, 115);
            case "mushroom_fields":
                return new Color(255, 0, 255);
            case "mushroom_field_shore":
                return new Color(255, 0, 255);
            case "desert_lakes":
                return new Color(255, 255, 0);
            case "modified_badlands_plateau":
                return new Color(128, 128, 128);
            case "modified_gravelly_mountains":
                return new Color(170, 170, 170);
            case "eroded_badlands":
                return new Color(210, 180, 100);
            case "wooded_badlands_plateau":
                return new Color(145, 120, 87);
            case "badlands":
                return new Color(154, 103, 82);
            case "badlands_plateau":
                return new Color(154, 103, 82);
            // Nether Biomes
            case "nether_wastes":
                return new Color(200, 100, 100);
            case "crimson_forest":
                return new Color(164, 49, 49);
            case "warped_forest":
                return new Color(77, 129, 122);
            case "basalt_deltas":
                return new Color(84, 82, 82);
            case "soul_sand_valley":
                return new Color(38, 29, 18);
            // End Biomes
            case "end_barrens":
                return new Color(100, 100, 100);
            case "end_highlands":
                return new Color(200, 200, 200);
            case "end_midlands":
                return new Color(150, 150, 150);
            case "small_end_islands":
                return new Color(50, 50, 50);
            case "the_end":
                return Color.BLACK;
            default:
                System.out.println(biome);
                return Color.GRAY;
        }
    }


}

