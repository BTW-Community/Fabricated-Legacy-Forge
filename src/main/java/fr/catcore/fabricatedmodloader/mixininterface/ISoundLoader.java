package fr.catcore.fabricatedmodloader.mixininterface;

import net.minecraft.src.*;

import java.net.URL;

public interface ISoundLoader {

    SoundPoolEntry addSound(String par1Str, URL par2URL);
}
