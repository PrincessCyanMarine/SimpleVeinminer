package net.cyanmarine.simpleveinminer.mixin;

import net.cyanmarine.simpleveinminer.ifaces.IDrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements IDrawContext {
    @Shadow private @Final MatrixStack matrices;
    @Shadow private @Final VertexConsumerProvider.Immediate vertexConsumers;
    @Shadow protected abstract void tryDraw();
    @Invoker("tryDraw") @Override public abstract void invokeTryDraw();

    @Override
    public int drawText(TextRenderer textRenderer, @Nullable String text, float x, float y, int color, boolean shadow){
        if (text == null) {
            return 0;
        }
        int i = textRenderer.draw(text, x, y, color, shadow, this.matrices.peek().getPositionMatrix(), this.vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0, textRenderer.isRightToLeft());
        this.tryDraw();
        return i;
    }
}
