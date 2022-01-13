package nrish;

import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.PlanetDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SectorPickerMod extends Mod{
    SectorPickerSettingsDialog settingsDialog;
    PickerSettings settings;
    public SectorPickerMod(){
        settings = new PickerSettings();
        settingsDialog = new SectorPickerSettingsDialog(settings);
        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);

                Field loadoutField = PlanetDialog.class.getField("loadouts");

                loadoutField.setAccessible(true);
                modifiersField.setInt(loadoutField, loadoutField.getModifiers() & ~Modifier.FINAL);
                loadoutField.set(Vars.ui.planet, new LaunchLoadoutDialogOverride());

            }catch(NoSuchFieldException ex){
                Log.err("failed to replace loadout object!");
            } catch (IllegalAccessException ex) {
                Log.err("can't use reflection to access planet ui!");
            }

            Vars.ui.settings.button("Sector Picker Settings", settingsDialog::showDialog);
        });
    }

    @Override
    public void loadContent(){
        //TODO read docs and figure out what's supposed to go here

    }

}
