package com.butterycode.cubefruit.utils;

public class localeManager {
	
	// TODO list:
	/* - default to en_us
	 * - store in json
	 * - always keep locale file up to date
	 *   - remove non existent keys
	 *   - add new entries found in the plugin
	 * - use "&a&l» &7" instead of "&7" for successful commands
	 */
	
	
	
//	public localeManager(String locale) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
//		File file = resolveLocale(locale);
//		
//		var gson = new Gson();
//		var json = gson.fromJson(new FileReader(file), JsonObject.class);
//		var someString = json.get("my-string").getAsString();
//	}

//	private File resolveLocale(String locale) {
//		File file = new File(Main.plugin.getDataFolder() + File.separator + "lang" + File.separator + locale + ".json");
//		
//		Main.plugin.getResource("lang/" + locale + ".json");
//		
//		if (file.exists()) {
//			return file;
//		} else {
//			return resolveLocale("en_us");
//		}
//	}
}
