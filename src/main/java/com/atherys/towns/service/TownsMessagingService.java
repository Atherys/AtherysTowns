package com.atherys.towns.service;

import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@Singleton
public class TownsMessagingService {

    public static final Text PREFIX = Text.of(TextColors.DARK_AQUA, "[", TextColors.DARK_GREEN, "Towns", TextColors.DARK_AQUA, "] ");

}
