package dev.debutter.cubefruit.paper.utils;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceStorage {

	private String content;
	private File file = null;
	
	public ResourceStorage(Plugin plugin, String filepath, boolean storeLocal) {
		if (storeLocal) {
			plugin.saveResource(filepath, false);
			file = new File(plugin.getDataFolder() + File.separator + filepath);
			
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				content = br.lines().collect(Collectors.joining("\n"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			InputStream input = plugin.getResource(filepath);
			content = new BufferedReader(new InputStreamReader(input, StandardCharsets.ISO_8859_1))
					.lines()
					.collect(Collectors.joining("\n"));
		}
	}

	public String getContent() {
		return content;
	}

	public File getFile() {
		return file;
	}
}
