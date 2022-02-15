package net.cyanmarine.simple_veinminer;

import net.minecraft.util.Identifier;

public enum Constants {
    NETWORKING_VEINMINE ("networking.channel.veinmine");

    public final Identifier identifier;
    private Constants(String id) {
        this.identifier = new Identifier(id);
    }

}
