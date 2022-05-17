package fr.catcore.fabricatedforge.forged;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class FatalErrorScreenForged extends Screen {
    private String title = "help_me";
    private String message = "help_me";

    public FatalErrorScreenForged() {
    }

    public void render(int par1, int par2, float par3) {
        this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.textRenderer, this.message, this.width / 2, 110, 16777215);
        super.render(par1, par2, par3);
    }

    protected void keyPressed(char par1, int par2) {
    }
}
