package fr.catcore.fabricatedmodloader.mixin.modloader.client;

import net.minecraft.src.*;
import fr.catcore.fabricatedmodloader.mixininterface.ISoundLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(SoundPool.class)
public class SoundLoaderMixin implements ISoundLoader {

    @Shadow public boolean isGetRandomSound;

    @Shadow private Map nameToSoundPoolEntriesMapping;

    @Shadow private List allSoundPoolEntries;

    @Shadow public int numberOfSoundPoolEntries;

    /**
     * @reason ModLoader patch
     * @author Risugami
     */
    /*@Overwrite
    public SoundPoolEntry addSound(String key, File file) {
        try {
            return this.addSound(key, file.toURI().toURL());
        } catch (MalformedURLException var4) {
            var4.printStackTrace();
            throw new RuntimeException(var4);
        }
    }*/

    @Override
    public SoundPoolEntry addSound(String key, URL url) {
        String var3 = key;
        key = key.substring(0, key.indexOf("."));
        if (this.isGetRandomSound) {
            while (Character.isDigit(key.charAt(key.length() - 1))) {
                key = key.substring(0, key.length() - 1);
            }
        }

        key = key.replaceAll("/", ".");
        if (!this.nameToSoundPoolEntriesMapping.containsKey(key)) {
            this.nameToSoundPoolEntriesMapping.put(key, new ArrayList());
        }

        SoundPoolEntry var4 = new SoundPoolEntry(var3, url);
        ((List) this.nameToSoundPoolEntriesMapping.get(key)).add(var4);
        this.allSoundPoolEntries.add(var4);
        ++this.numberOfSoundPoolEntries;
        return var4;
    }
}
