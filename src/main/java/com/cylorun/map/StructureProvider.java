package com.cylorun.map;

import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;

public class StructureProvider {
    public StructureSupplier structureSupplier;
    public Dimension dimension;
    public String asset;

    public StructureProvider(StructureSupplier structureSupplier, Dimension dimension, String asset) {
        this.structureSupplier = structureSupplier;
        this.dimension = dimension;
        this.asset = asset;
    }

    public interface StructureSupplier {
        RegionStructure<?, ?> create(MCVersion version);
    }

}
