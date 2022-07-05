package net.cyanmarine.simple_veinminer.components;


import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.cyanmarine.simple_veinminer.Constants;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class VeinminingShapeComponent implements Component, AutoSyncedComponent {
    private Constants.SHAPES shape = Constants.SHAPES.REGULAR;
    private int radius = 1;
    private String shapeNbtKey = "VeinminingShape";
    private String radiusNbtKey = "VeinminingRadius";
    PlayerEntity provider;

    public VeinminingShapeComponent(PlayerEntity provider) { this.provider = provider; }

    @Override
    public void readFromNbt(NbtCompound tag) {
        setRadius(tag.getInt(radiusNbtKey));
        try {
            setShape(Constants.SHAPES.valueOf(tag.getString(shapeNbtKey)));
        } catch (IllegalArgumentException e) {
            setShape(Constants.SHAPES.REGULAR);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString(shapeNbtKey, getShape().name());
        tag.putInt(radiusNbtKey, getRadius());
    }

    public Constants.SHAPES getShape() { return shape; }
    public void setShape(Constants.SHAPES newShape) {
        shape = newShape;
        SimpleVeinminer.VEINMINING_SHAPE.sync(this.provider);
    }

    public int getRadius() { return radius; }

    public void setRadius(int newRadius) {
        this.radius = newRadius;
        SimpleVeinminer.VEINMINING_SHAPE.sync(this.provider);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.radius);
        buf.writeInt(shape.ordinal());
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        this.radius = buf.readInt();
        this.shape = Constants.SHAPES.values()[buf.readInt()];
    }
}
