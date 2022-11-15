package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundManager.class)
public interface SoundSystemAccessor {

    @Accessor("soundPoolSounds")
    SoundPool getSoundsLoader();

    @Accessor("soundPoolStreaming")
    SoundPool getStreamingLoader();

    @Accessor("soundPoolMusic")
    SoundPool getMusicLoader();
}
