package com.butterycode.cubefruit.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

public class resourceStorage {

	private String content;
	private File file = null;
	
	public resourceStorage(Plugin plugin, String filepath, boolean storeLocal) {
		if (storeLocal) {
			plugin.saveResource(filepath, false);
			file = new File(plugin.getDataFolder() + File.separator + filepath);
			
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				content = br.lines().collect(Collectors.joining("\n"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			InputStream input = plugin.getResource(filepath);
			content = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		}
	}

	public String getContent() {
		return content;
	}

	public File getFile() {
		return file;
	}
}
