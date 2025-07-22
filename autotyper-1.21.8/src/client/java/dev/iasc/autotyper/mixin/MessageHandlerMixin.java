package dev.iasc.autotyper.mixin.client;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params,
            CallbackInfo ci) {
        // Your logic for handling received chat messages from the server
        String chat = message.getContent().getString();
        String name = sender.getName();
        // Example: Print to log, or trigger your mod's features
        // System.out.println("[AutoTyper] Server chat: <" + name + "> " + chat);
        // System.out.println("MessageHandlerMixin");
        assert client.player != null;
        client.player.sendMessage(Text.literal(name + " " + chat), false);
        if (name.isEmpty()) {
            client.player.sendMessage(Text.literal("was empty"), false);
        }

        boolean found = chat.matches("The first person to type*"); // true if "text" is anywhere in the string

        String input = "one two three four five six seven";
        Pattern pattern = Pattern.compile("(?:\\S+\\s+){5}(\\S+)");
        Matcher matcher = pattern.matcher(input);

        if (found) {
            String sixthWord = matcher.group(1); // "six"

            client.player.sendMessage(Text.literal("the word was: " + sixthWord), false);
        }
    }
}
