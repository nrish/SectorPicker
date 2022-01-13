package nrish;

import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonWriter;
import mindustry.Vars;

import java.io.IOException;

public class PickerSettings {
    Float sectorLimit;
    Boolean enableLimit;
    Fi settingsFile;
    public PickerSettings() {
        sectorLimit = 0f;
        enableLimit = false;
        settingsFile = new Fi(Vars.dataDirectory.absolutePath() + "/SectorPickerSettings.json");
        if(settingsFile.exists() && settingsFile.readString().length() != 0){
            JsonReader reader = new JsonReader();
            var jsonData = reader.parse(settingsFile);
            Json json = new Json();
            json.readField(this, "sectorLimit", jsonData);
            json.readField(this, "enableLimit", jsonData);
        }else{
            JsonWriter writer = new JsonWriter(settingsFile.writer(false));
            try {
                writer.object();
                writer.name("sectorLimit");
                writer.value(sectorLimit);
                writer.name("enableLimit");
                writer.value(enableLimit);
                writer.flush();
            }catch (Exception e){
                Log.err("failed to write initial settings");
            }
        }
    }
    void updateSettings() throws IOException {
        JsonWriter writer = new JsonWriter(settingsFile.writer(false));
        writer.object();
        writer.name("sectorLimit");
        writer.value(sectorLimit);
        writer.name("enableLimit");
        writer.value(enableLimit);
        writer.flush();
    }
}
