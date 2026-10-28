package net.pod.cnmb.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

public class ModKeyBinds {
    private static final List<KeyMapping> keybinds = new ArrayList<>();

    public static final KeyMapping RELOAD = add(new KeyMapping(
            "key.cnmb.reload",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_R,
            "key.categories.cnmb"
    ));

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