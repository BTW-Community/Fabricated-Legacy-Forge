package fr.catcore.fabricatedmodloader.mixin.modloader.common;

import modloader.ModLoader;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProviderServer.class)
public class ServerChunkProviderMixin {

    @Shadow private IChunkProvider currentChunkProvider;

    @Shadow private WorldServer worldObj;

    @Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Chunk;setChunkModified()V"))
    private void modLoaderPopulateChunk(IChunkProvider provider, int x, int z, CallbackInfo ci) {
        ModLoader.populateChunk(this.currentChunkProvider, x, z, this.worldObj);
    }
}
