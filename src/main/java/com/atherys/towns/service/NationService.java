package com.atherys.towns.service;

import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Town;
import com.atherys.towns.persistence.NationRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

@Singleton
public class NationService {

    public static final Text DEFAULT_NATION_DESCRIPTION = Text.of("No description available.");

    private NationRepository nationRepository;

    private TownService townService;

    private PermissionService permissionService;

    @Inject
    NationService(
            NationRepository nationRepository,
            TownService townService,
            PermissionService permissionService
    ) {
        this.nationRepository = nationRepository;
        this.townService = townService;
        this.permissionService = permissionService;
    }

    public Nation createNation(Text name, Town capital) {
        Nation nation = new Nation();
        nation.setName(name);
        nation.setDescription(DEFAULT_NATION_DESCRIPTION);

        townService.setTownNation(capital, nation);
        nation.setCapital(capital);

        nation.setLeader(capital.getLeader());

        nationRepository.saveOne(nation);

        return nation;
    }

    public void removeNation(Nation nation) {

    }
}
