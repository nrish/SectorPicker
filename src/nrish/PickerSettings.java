package nrish;

import arc.files.Fi;
import arc.util.serialization.Json;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonWriter;
import mindustry.Vars;

public class PickerSettings {
    float sectorLimit;
    boolean enableLimit;
    Fi settingsFile;
    public PickerSettings(){
        settingsFile = new Fi(Vars.saveDirectory.absolutePath() + "/SectorPickerSettings.json");
        if(settingsFile.exists()){
            JsonReader reader = new JsonReader();
            var jsonData = reader.parse(settingsFile);
            Json json = new Json();
            json.readField(sectorLimit, "SectorLimit", jsonData);
            json.readField(enableLimit, "enableLimit", jsonData);
        }else{
            Json json = new Json();
            JsonWriter writer = new JsonWriter(settingsFile.writer(false));
            json.setWriter(writer);
            json.writeValue("sectorLimit", sectorLimit);
            json.writeValue("enableLimit", enableLimit);
        }
    }
    void updateSettings(){
        Json json = new Json();
        JsonWriter writer = new JsonWriter(settingsFile.writer(false));
        json.setWriter(writer);
        json.writeValue("sectorLimit", sectorLimit);
        json.writeValue("enableLimit", enableLimit);
    }
}
