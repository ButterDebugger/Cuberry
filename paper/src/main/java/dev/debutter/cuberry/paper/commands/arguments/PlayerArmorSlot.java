package dev.debutter.cuberry.paper.commands.arguments;

import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum PlayerArmorSlot {
    HEAD(EquipmentSlot.HEAD),
    CHEST(EquipmentSlot.CHEST),
    LEGS(EquipmentSlot.LEGS),
    FEET(EquipmentSlot.FEET);

    private final EquipmentSlot slot;

    PlayerArmorSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    public EquipmentSlot getEquipmentSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
