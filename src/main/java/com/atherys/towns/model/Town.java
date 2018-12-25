package com.atherys.towns.model;

import com.atherys.towns.persistence.converter.TextConverter;
import com.atherys.towns.persistence.converter.TransformConverter;
import com.atherys.towns.persistence.converter.WorldConverter;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import java.util.UUID;

@Entity
public class Town {

    @Id
    private UUID uuid;

    @Convert(converter = TextConverter.class)
    private Text name;

    @Convert(converter = TextConverter.class)
    private Text description;

    @Convert(converter = TextConverter.class)
    private Text motd;

    private Resident leader;

    @Convert(converter = WorldConverter.class)
    private World world;

    @Convert(converter = TransformConverter.class)
    private Transform<World> spawn;

    private Set<Resident> residents;

    private Set<Plot> plots;

}
