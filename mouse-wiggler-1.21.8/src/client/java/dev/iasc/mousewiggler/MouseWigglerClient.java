package dev.iasc.mousewiggler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MouseWigglerClient implements ClientModInitializer {
	private static int wiggleTick = 0;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.currentScreen == null) {
				if (org.lwjgl.glfw.GLFW.glfwGetKey(org.lwjgl.glfw.GLFW.glfwGetCurrentContext(),
						org.lwjgl.glfw.GLFW.GLFW_KEY_R) == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
					// Smooth circular movement
					int steps = 10; // Fewer steps = faster movement
					wiggleTick = (wiggleTick + 1) % steps;
					double radius = 7.0; // Circle radius (smaller = tighter)
					double angle = 2 * Math.PI * wiggleTick / steps;
					float yawOffset = (float) (radius * Math.cos(angle));
					float pitchOffset = (float) (radius * Math.sin(angle));
					float baseYaw = client.player.getYaw();
					float basePitch = client.player.getPitch();
					client.player.setYaw(baseYaw + yawOffset);
					client.player.setPitch(basePitch + pitchOffset);
				} else {
					wiggleTick = 0;
				}
			}
		});
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