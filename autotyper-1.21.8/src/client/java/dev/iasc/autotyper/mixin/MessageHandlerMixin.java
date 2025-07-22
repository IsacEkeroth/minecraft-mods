package dev.iasc.autotyper.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params,
            CallbackInfo ci) {
        // Your logic for handling received chat messages from the server
        String chat = message.getContent().getString();
        String name = sender.getName();
        // Example: Print to log, or trigger your mod's features
//        System.out.println("[AutoTyper] Server chat: <" + name + "> " + chat);
//        System.out.println("MessageHandlerMixin");
        assert client.player != null;
        client.player.sendMessage(Text.literal(name + " " + chat), false);
        if (name.isEmpty()) {
            client.player.sendMessage(Text.literal("was empty"), false);
        }

    }
}
