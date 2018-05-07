package com.atherys.towns.plot;

import com.atherys.core.views.Viewable;
import com.atherys.towns.plot.flags.Extent;
import com.atherys.towns.plot.flags.Extents;
import com.atherys.towns.plot.flags.Flag;
import com.atherys.towns.plot.flags.Flags;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.views.PlotFlagsView;
import java.util.HashMap;
import java.util.Map;

public class PlotFlags implements Viewable<PlotFlagsView> {

  private Map<Flag, Extent> flags;

  private PlotFlags() {
    flags = new HashMap<>();
  }

  public static PlotFlags empty() {
    return new PlotFlags();
  }

  public static PlotFlags create(Flag flag, Extent value) {
    PlotFlags flags = new PlotFlags();
    flags.set(flag, value);
    return flags;
  }

  public static PlotFlags regular() {
    return create(Flags.BUILD, Extents.TOWN)
        .set(Flags.DESTROY, Extents.TOWN)
        .set(Flags.PVP, Extents.ANY)
        .set(Flags.SWITCH, Extents.TOWN)
        .set(Flags.DAMAGE_ENTITY, Extents.TOWN)
        .set(Flags.JOIN, Extents.NONE);
  }

  public PlotFlags set(Flag flag, Extent value) {
    flags.put(flag, value);
    return this;
  }

  public PlotFlags remove(Flag flag) {
    flags.remove(flag);
    return this;
  }

  public boolean isAllowed(Resident res, Flag flag, Plot plot) {
    if (!res.getPlayer().isPresent()) {
      return false;
    }
    if (!flags.containsKey(flag)) {
      return true;
    }

    Extent e = flags.get(flag);
    return e.check(res, flag, plot);
  }

  public Extent get(Flag flag) {
    return flags.get(flag);
  }

  public PlotFlags copy() {
    PlotFlags copy = new PlotFlags();
    for (Map.Entry<Flag, Extent> entry : flags.entrySet()) {
      copy.set(entry.getKey(), entry.getValue());
    }
    return copy;
  }

  public Map<Flag, Extent> getAll() {
    return flags;
  }

  public boolean equals(PlotFlags flags) {
    if (this == flags) {
      return true;
    }
    for (Map.Entry<Flag, Extent> entry : flags.getAll().entrySet()) {
      if (!entry.getValue().equals(this.get(entry.getKey()))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public PlotFlagsView createView() {
    return new PlotFlagsView(this);
  }
}
