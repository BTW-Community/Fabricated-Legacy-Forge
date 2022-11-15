package modloader;

import net.minecraft.src.*;
import net.minecraft.client.Minecraft;

public class EntityRendererProxy extends EntityRenderer {
    private final Minecraft game;

    public EntityRendererProxy(Minecraft minecraft) {
        super(minecraft);
        this.game = minecraft;
    }

    @Override
    public void updateCameraAndRender(float f) {
        super.updateCameraAndRender(f);
        ModLoader.onTick(f, this.game);
    }
}
