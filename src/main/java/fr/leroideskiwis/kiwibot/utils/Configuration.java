package fr.leroideskiwis.kiwibot.utils;

import fr.leroideskiwis.utils.json.JSONReader;
import fr.leroideskiwis.utils.json.JSONWriter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private final File file;
    private JSONObject jsonObject;

    public Configuration(String path) throws IOException {

        this.file = new File(path);
        if(!file.exists()) {
            this.jsonObject = new JSONObject();
            save();
        }
        else
            this.jsonObject = new JSONReader(file).toJSONObject();

    }

    public void reload() throws IOException {
        this.jsonObject = new JSONReader(file).toJSONObject();
    }

    public File getFile(){
        return file;
    }

    public JSONObject getJsonObject(String key, JSONObject defaultValue){
        if(!jsonObject.has(key))
            jsonObject.put(key, defaultValue);
        return jsonObject.getJSONObject(key);
    }

    public void save(){

        try (JSONWriter writer = new JSONWriter(file)){

            writer.write(jsonObject);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getString(String key, String defaultValue){
        if(!jsonObject.has(key))
            jsonObject.put(key, defaultValue);
        return jsonObject.getString(key);
    }

}
