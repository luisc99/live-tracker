package me.cylorun.map;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.math.Vec3i;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.TerrainGenerator;
import me.cylorun.Tracker;
import me.cylorun.utils.ResourceUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChunkMap {
    private final long seed;
    private final Dimension dim;
    private final java.awt.Dimension size;
    private final RunCoords runCoords;
    private List<StructureProvider> structures;
    private List<Pair<String, CPos>> structureCoords;
    private ChunkRand rand;

    public ChunkMap(long seed, java.awt.Dimension size, Dimension dim, RunCoords runCoords) {
        this.seed = seed;
        this.dim = dim;
        this.size = size;
        this.runCoords = runCoords;

        this.structures = new ArrayList<>();
        this.structureCoords = new ArrayList<>();

        this.rand = new ChunkRand(this.seed);
    }

    public void generate() {
        BiomeSource source = this.getBiomeSource();
        BufferedImage image = new BufferedImage(this.size.width * 16, this.size.height * 16, BufferedImage.TYPE_INT_RGB);
        this.generateStructures();
        int halfWidth = this.size.width / 2;
        int halfHeight = this.size.height / 2;

        for (int i = -halfWidth; i < halfWidth; i++) {
            for (int j = -halfHeight; j < halfHeight; j++) {
                Graphics2D g = image.createGraphics();

                Biome biomeId = source.getBiome(i * 16, 63, j * 16);
                Color color = this.mapBiomeToColor(biomeId.getName());
                g.setColor(color);
                g.fillRect((i + halfWidth) * 16, (j + halfHeight) * 16, 16, 16);
                g.dispose();
            }
        }

        for (Pair<String, CPos> p : this.structureCoords) {
            Graphics2D g = image.createGraphics();
            Image img = this.getImage(p.getLeft());
            int x = p.getRight().getX();
            int z = p.getRight().getZ();
            System.out.printf("name: %s | coords: %s\n",p.getLeft(), p.getRight());
            g.drawImage(img, (x + halfWidth) * 16, (z + halfHeight) * 16, 48, 48, null);
        }

        try {
            File output = new File(dim.getName().toLowerCase() + ".png");
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getImage(String path) {
        BufferedImage image;
        try {
            image = ResourceUtil.loadImageFromResources(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    private List<Pair<String, CPos>> generateStructures() {
        int searchRad = this.size.height > this.size.width ? this.size.height * 16 : this.size.width * 16;
        for (StructureProvider search : this.structures) {
            RegionStructure<?, ?> structure = search.structureSupplier.create(MCVersion.v1_16_1);

            RegionStructure.Data<?> lowerBound = structure.at(-searchRad / 16, -searchRad / 16);
            RegionStructure.Data<?> upperBound = structure.at(searchRad / 16, searchRad / 16);
            for (int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
                for (int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
                    CPos cpos = structure.getInRegion(this.seed, regionX, regionZ, rand);

                    if (cpos == null) continue;
                    if (cpos.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > (double) searchRad / 16) continue;
                    if (!structure.canSpawn(cpos.getX(), cpos.getZ(), this.getBiomeSource())) continue;
                    Pair<String, CPos> pair = Pair.of(search.asset, cpos);
                    this.structureCoords.add(pair);
                }
            }

        }
        return this.structureCoords;
    }

    private Pair<CPos, String> getStructureData(int x, int z) {

        return null;
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

    public ChunkMap registerFeature(StructureProvider s) {
        this.structures.add(s);
        return this;
    }

    private TerrainGenerator getTerrainGenerator() {
        return TerrainGenerator.of(this.getBiomeSource());
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
            case "giant_spruce_taiga":
                return new Color(1, 37, 20);
            case "snowy_taiga":
                return new Color(192, 192, 192);
            case "snowy_taiga_hills":
                return new Color(128, 128, 128);
            case "snowy_taiga_mountains":
                return new Color(61, 58, 58);
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
            case "ice_spikes":
                return new Color(95, 95, 218);
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
                return new Color(232, 222, 169, 255);
            default:
                Tracker.log(Level.DEBUG, String.format("Color for biome %s is missing", biome));
                return Color.GRAY;
        }
    }


}

