package me.cylorun.map;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.math.Vec3i;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.TerrainGenerator;
import me.cylorun.Tracker;
import me.cylorun.instance.world.WorldFile;
import me.cylorun.utils.ResourceUtil;
import me.cylorun.utils.Vec2i;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChunkMap {
    private final long seed;
    private Dimension dim;
    private final int radius;
    private final WorldFile world;
    private List<StructureProvider> structures;
    private List<Pair<String, CPos>> structureCoords;
    private ChunkRand rand;
    private final int ICON_SIZE = 48;
    private final Font FONT = new Font("Arial", Font.BOLD, 30);

    public ChunkMap(long seed, int radius, Dimension dim, WorldFile world) {
        this.seed = seed;
        this.dim = dim;
        this.radius = radius;
        this.world = world;

        this.structures = new ArrayList<>();
        this.structureCoords = new ArrayList<>();

        this.rand = new ChunkRand(this.seed);
        this.registerAll();
    }

    public void setDimension(Dimension dim) {
        this.structureCoords.clear();
        this.dim = dim;
    }

    private void registerAll() {
        this.registerFeature(new StructureProvider((version -> new RuinedPortal(Dimension.NETHER, version)), Dimension.NETHER, "icons/ruined_portal.png"));
        this.registerFeature(new StructureProvider((version -> new RuinedPortal(Dimension.OVERWORLD, version)), kaptainwutax.mcutils.state.Dimension.OVERWORLD, "icons/ruined_portal.png"));
        this.registerFeature(new StructureProvider(BuriedTreasure::new, Dimension.OVERWORLD, "icons/buried_treasure.png"));
        this.registerFeature(new StructureProvider(Village::new, Dimension.OVERWORLD, "icons/village.png"));
        this.registerFeature(new StructureProvider(Shipwreck::new, Dimension.OVERWORLD, "icons/shipwreck.png"));
        this.registerFeature(new StructureProvider(Mansion::new, Dimension.OVERWORLD, "icons/mansion.png"));
        this.registerFeature(new StructureProvider(BastionRemnant::new, Dimension.NETHER, "icons/bastion.png"));
        this.registerFeature(new StructureProvider(Fortress::new, Dimension.NETHER, "icons/fortress.png"));
    }

    public void generate() {
        long start = System.currentTimeMillis();
        BiomeSource source = this.getBiomeSource();
        BufferedImage image = new BufferedImage(this.radius * 16, this.radius * 16, BufferedImage.TYPE_INT_RGB);
        this.generateStructures();

        for (int i = 0; i < this.radius; i++) {
            for (int j = 0; j < this.radius; j++) {
                Graphics2D g = image.createGraphics();
                Biome biomeId = source.getBiome(i * 16 - (this.radius * 8), 63, j * 16 - (this.radius * 8));
                Color color = this.mapBiomeToColor(biomeId.getName());
                g.setColor(color);
                g.fillRect(i * 16, j * 16, 16, 16);
                g.dispose();
            }
        }

        for (Pair<String, CPos> p : this.structureCoords) {
            Graphics2D g = image.createGraphics();
            Image img = this.getResourceImage(p.getLeft());
            int x = p.getRight().getX();
            int z = p.getRight().getZ();

            int pixelX = (x + (this.radius / 2)) * 16;
            int pixelZ = (z + (this.radius / 2)) * 16;

            g.setFont(this.FONT);
            g.drawImage(img, pixelX, pixelZ, this.ICON_SIZE, this.ICON_SIZE, null);
            this.drawCoords(g, x, z, pixelX, pixelZ);
            g.dispose();
        }


        this.drawPath(image);
        this.drawPlayerEvents(image);

        try {
            File output = new File(dim.getName().toLowerCase() + ".png");
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Tracker.log(Level.DEBUG, String.format("Generated chunk map for %s in %s ms", this.dim, System.currentTimeMillis() - start));
    }

    private void drawPlayerEvents(BufferedImage i) {
        if (this.world == null) {
            return;
        }

        for (Pair<Pair<String, Vec2i>, Dimension> p : this.world.playerLocations) {
            if (!p.getRight().equals(this.dim)) continue;
            Graphics2D g = i.createGraphics();
            Image img = this.getResourceImage(p.getLeft().getLeft());
            int x = p.getLeft().getRight().getX();
            int z = p.getLeft().getRight().getZ();

            int pixelX = x + (this.radius * 8);
            int pixelZ = z + (this.radius * 8);

            g.setFont(this.FONT);
            g.drawImage(img, pixelX, pixelZ, 64, 64, null);
            this.drawCoords(g, x, z, pixelX, pixelZ);
            g.dispose();
        }
    }


    public void drawPath(BufferedImage i) {
        if (this.world == null) {
            return;
        }

        GeneralPath path = new GeneralPath();

        int xOff = (this.radius * 8);
        int zOff = (this.radius * 8);

        boolean firstPoint = true;
        for (Pair<Pair<Vec2i, Vec2i>, Dimension> p : this.world.playerPath) {
            if (!p.getRight().equals(this.dim)) continue;

            int x1 = p.getLeft().getLeft().getX() + xOff;
            int z1 = p.getLeft().getLeft().getZ() + zOff;

            int x2 = p.getLeft().getRight().getX() + xOff;
            int z2 = p.getLeft().getRight().getZ() + zOff;

            if (firstPoint) {
                path.moveTo(x1, z1);
                firstPoint = false;
            } else {
                path.curveTo(x1, z1, x2, z2, x2, z2);
            }
        }

        Graphics2D g = i.createGraphics();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(10));
        g.draw(path);
        g.dispose();
    }

    private void drawCoords(Graphics2D g, int x, int y, int pixelX, int pixelY) {
        Color borderColor = Color.BLACK;
        Color backgroundColor = Color.WHITE;
        Color textColor = Color.BLACK;

        FontMetrics fontMetrics = g.getFontMetrics();
        String coordsText = String.format("%s | %s", x * 16, y * 16);
        g.setFont(this.FONT);
        int textWidth = fontMetrics.stringWidth(coordsText);
        int textHeight = fontMetrics.getHeight();

        int rectWidth = textWidth + 40;
        int rectHeight = textHeight + 20;

        g.setStroke(new BasicStroke(7));
        g.setColor(borderColor);
        pixelX-=64;
        g.drawRoundRect(pixelX, pixelY - 64, rectWidth, rectHeight, 5, 5);

        g.setColor(backgroundColor);
        g.fillRoundRect(pixelX, pixelY - 64, rectWidth, rectHeight, 5, 5);

        g.setColor(textColor);
        int textX = pixelX + (rectWidth - textWidth) / 2;
        int textY = pixelY - 64 + (rectHeight - textHeight) / 2 + fontMetrics.getAscent();
        g.drawString(coordsText, textX, textY);
    }


    private BufferedImage getResourceImage(String path) {
        BufferedImage image;
        try {
            image = ResourceUtil.loadImageResource(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    private List<Pair<String, CPos>> generateStructures() {
        int searchrad = radius * 16;
        for (StructureProvider search : this.structures) {
            RegionStructure<?, ?> structure = search.structureSupplier.create(MCVersion.v1_16_1);

            RegionStructure.Data<?> lowerBound = structure.at(-searchrad / 16, -searchrad / 16);
            RegionStructure.Data<?> upperBound = structure.at(searchrad / 16, searchrad / 16);
            for (int regionX = lowerBound.regionX; regionX <= upperBound.regionX; regionX++) {
                for (int regionZ = lowerBound.regionZ; regionZ <= upperBound.regionZ; regionZ++) {
                    CPos cpos = structure.getInRegion(this.seed, regionX, regionZ, rand);

                    if (cpos == null) continue;
                    if (cpos.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > (double) searchrad / 16) continue;
                    if (!structure.canSpawn(cpos.getX(), cpos.getZ(), this.getBiomeSource()) || !this.dim.equals(search.dimension))
                        continue;
                    Pair<String, CPos> pair = Pair.of(search.asset, cpos);
                    this.structureCoords.add(pair);
                }
            }

        }

        Stronghold sh = new Stronghold(MCVersion.v1_16_1);
        for (CPos cpos : sh.getAllStarts(this.getBiomeSource(), this.rand)) {
            if (cpos.distanceTo(Vec3i.ZERO, DistanceMetric.CHEBYSHEV) > (double) radius / 16) continue;
            Pair<String, CPos> pair = Pair.of("icons/stronghold.png", cpos);
            this.structureCoords.add(pair);
        }

        return this.structureCoords;
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
            case "giant_spruce_taiga_hills":
                return new Color(37, 47, 18);
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
            case "modified_jungle_edge":
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
            case "modified_wooded_badlands_plateau":
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
                return new Color(248, 239, 222);
            case "end_highlands":
                return new Color(220, 218, 158);
            case "end_midlands":
                return new Color(175, 171, 142);
            case "small_end_islands":
                return new Color(0, 0, 0);
            case "the_end":
                return new Color(232, 222, 169, 255);
            default:
                Tracker.log(Level.DEBUG, String.format("Color for biome %s is missing", biome));
                return Color.GRAY;
        }
    }


}

