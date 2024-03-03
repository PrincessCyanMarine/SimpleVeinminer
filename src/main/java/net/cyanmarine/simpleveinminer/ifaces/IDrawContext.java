package net.cyanmarine.simpleveinminer.ifaces;

import net.minecraft.client.font.TextRenderer;
import org.jetbrains.annotations.Nullable;

/**
 * Intended for use with DrawContextMixin **ONLY**
 **/
public interface IDrawContext {
    @SuppressWarnings("EmptyMethod")
    void invokeTryDraw();

    @SuppressWarnings("UnusedReturnValue")
    int drawText(TextRenderer textRenderer, @Nullable String text, float x, float y, int color, boolean shadow);
}
