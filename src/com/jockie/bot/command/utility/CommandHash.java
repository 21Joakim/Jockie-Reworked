package com.jockie.bot.command.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandHash extends CommandImpl {

	public CommandHash() {
		super("hash", new ArgumentTypeValue("Hash algorithm", 
			new ArgumentEntry("Secure Hash Algorithm 2 (256-bit)", "SHA-256", "SHA-256"),
			new ArgumentEntry("Secure Hash Algorithm 2 (512-bit)", "SHA-512", "SHA-512"),
			new ArgumentEntry("Message Digest Algorithm 5 (128-bit)", "MD5", "MD5")
		), new ArgumentString("Text"));
		super.setCommandDescription("Hash a text");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String algorithm = (String) arguments[0];
		
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			
			event.getChannel().sendMessage(new String(byteArrayToHex(digest.digest(((String) arguments[1]).getBytes(StandardCharsets.UTF_8))))).queue();
		}catch(NoSuchAlgorithmException e) {
			throw new IllegalStateException("This should not have happened");
		}
	}
	
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    private static char[] byteArrayToHex(byte[] bytes) {
        char[] hex_chars = new char[bytes.length * 2];
        for(int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF, t = i << 1;
            hex_chars[t] = hexArray[v >>> 4];
            hex_chars[t + 1] = hexArray[v & 0x0F];
        }
        return hex_chars;
    }
}