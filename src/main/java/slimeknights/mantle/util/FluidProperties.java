package slimeknights.mantle.util;

import slimeknights.mantle.registration.object.MantleFluid;

public class FluidProperties {

  public final MantleFluid.Flowing flowing;
  public final MantleFluid.Still still;

  public FluidProperties(MantleFluid.Flowing flowing, MantleFluid.Still still) {
    this.flowing = flowing;
    this.still = still;
  }
}
