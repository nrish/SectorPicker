package nrish;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.UI;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SectorPickerMod extends Mod{

    public SectorPickerMod(){
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
        });
    }

    @Override
    public void loadContent(){
        Log.info("Loading some example content.");
    }

}
