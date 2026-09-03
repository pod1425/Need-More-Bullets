package net.pod.cnmb.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

public class ModKeyBinds {
    private static final List<KeyMapping> keybinds = new ArrayList<>();

    public static KeyMapping STOP_CONTROL = add(new KeyMapping(
            "key.cnmb.stop_control",
            InputConstants.KEY_X,
            "key.categories.cnmb"));

    private static KeyMapping add(KeyMapping keymapping) {
        keybinds.add(keymapping);
        return keymapping;
    }

    public static void register(RegisterKeyMappingsEvent event) {
        for (KeyMapping key : keybinds) {
            event.register(key);
        }
    }
}
