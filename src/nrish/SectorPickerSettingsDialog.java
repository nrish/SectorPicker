package nrish;

import arc.scene.ui.CheckBox;
import arc.scene.ui.Slider;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class SectorPickerSettingsDialog extends BaseDialog {
    PickerSettings settings;
    Slider distSlider;
    CheckBox enableLimit;
    public SectorPickerSettingsDialog(PickerSettings settings) {
        super("@nrish.SectorPickerSettingsTitle");
        this.settings = settings;
        enableLimit = new CheckBox("@nrish.disableDistCheck");
        distSlider = new Slider(0, 10, 1, false);
    }

    public void showDialog(){
        cont.clear();
        buttons.clear();

        buttons.defaults().size(160f, 64f);
        addCloseListener();

        cont.add("@nrish.maxSectorDist").row();
        //TODO figure out what distance units to use
        cont.add(new Slider(0, 10, 0.05f, false)).row();
        cont.add(new CheckBox("@nrish.disableDistCheck"));

        buttons.button("@back", Icon.left, this::hide);

        buttons.button("@confirm", () ->{
            settings.sectorLimit = distSlider.getValue();
            settings.enableLimit = enableLimit.isChecked();
            settings.updateSettings();
        });
    }
}
