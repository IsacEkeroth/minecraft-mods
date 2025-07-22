package dev.iasc.mousewiggler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class MouseWigglerClient implements ClientModInitializer {
	private static int wiggleTick = 0;
	private static boolean toggled = false;
	private static boolean prevRPressed = false;
	private static long lastTurnTime = System.currentTimeMillis();
	private static float accumulatedYawOffset = 0f;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.currentScreen == null) {
				boolean rPressed = org.lwjgl.glfw.GLFW.glfwGetKey(
						org.lwjgl.glfw.GLFW.glfwGetCurrentContext(),
						org.lwjgl.glfw.GLFW.GLFW_KEY_R) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

				boolean toggledPrev = toggled;
				if (rPressed && !prevRPressed) {
					toggled = !toggled;
				}
				prevRPressed = rPressed;

				if (toggled) {
					client.options.forwardKey.setPressed(true);
					client.options.attackKey.setPressed(true);
					// Turn 60 degrees to the right every 10 seconds
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastTurnTime >= 10000) {
						accumulatedYawOffset += 60f;
						lastTurnTime = currentTime;
					}
					// Smooth circular movement
					moveInCircle(client, accumulatedYawOffset);
					// Reset yaw offset after applying for one tick
					if (accumulatedYawOffset != 0f && currentTime - lastTurnTime < 10) {
						accumulatedYawOffset = 0f;
					}
				} else {
					wiggleTick = 0;
					if (toggledPrev) {
						accumulatedYawOffset = 0f;
						lastTurnTime = System.currentTimeMillis();
						client.options.forwardKey.setPressed(false);
						client.options.attackKey.setPressed(false);
					}
				}
			}
		});
	}

	private void moveInCircle(MinecraftClient client, float extraYaw) {
		int steps = 10; // Fewer steps = faster movement
		wiggleTick = (wiggleTick + 1) % steps;
		double radius = 7.0; // Circle radius (smaller = tighter)
		double angle = 2 * Math.PI * wiggleTick / steps;
		float yawOffset = (float) (radius * Math.cos(angle));
		float pitchOffset = (float) (radius * Math.sin(angle));
		float baseYaw = client.player.getYaw();
		float basePitch = client.player.getPitch();
		client.player.setYaw(baseYaw + yawOffset + extraYaw);
		client.player.setPitch(basePitch + pitchOffset);
	}
}

// { 3f, 0f }, // Right
// { 3f, 3f }, // Down-Right
// { 0f, 3f }, // Down
// { -3f, 3f }, // Down-Left
// { -3f, 0f }, // Left
// { -3f, -3f }, // Up-Left
// { 0f, -3f }, // Up
// { 3f, -3f } // Up-Right