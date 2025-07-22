package dev.iasc.autosell;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.minecraft.item.ItemStack;

public class AutosellClient implements ClientModInitializer {
	private static KeyBinding autosellKeyBinding;
	private final int LAST_SLOT = 30;

	private final String commandString = "sellall";
	private ItemStack lastStack = ItemStack.EMPTY;

	@Override
	public void onInitializeClient() {
		setKeyBinding();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				// Always initialize lastStack to current item in LASTS_SLOT on join
				if (lastStack == null) {
					lastStack = client.player.getInventory().getStack(LAST_SLOT).copy();
				}
				// If inventory (and chests) are open don't sell.
				if (client.currentScreen == null) {
					manualSell(client);
					autosell(client);
				}
			}
		});
	}

	private static void setKeyBinding() {
		autosellKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.autosell.autosell", // translation key
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R, // default key
				"category.autosell" // category in controls menu
		));
	}

	private void manualSell(MinecraftClient client) {
		if (autosellKeyBinding.wasPressed()) {
			assert client.player != null;
			client.player.networkHandler.sendChatCommand(commandString);

		}
	}

	private void autosell(MinecraftClient client) {
		assert client.player != null;
		ItemStack stack = client.player.getInventory().getStack(LAST_SLOT);
		boolean wasEmpty = lastStack == null || lastStack.isEmpty();
		boolean isEmpty = stack.isEmpty();

		// Only trigger when slot transitions from empty to non-empty
		lastStack = stack.copy();
		if (wasEmpty && !isEmpty) {
			client.player.networkHandler.sendChatCommand(commandString);
		}
	}
}