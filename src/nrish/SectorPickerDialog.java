package nrish;

import arc.scene.ui.Button;
import arc.scene.ui.ButtonGroup;
import arc.struct.Seq;
import mindustry.gen.Icon;
import mindustry.type.Sector;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class SectorPickerDialog extends BaseDialog {
    Sector targetSector;
    public SectorPickerDialog() {
        super("Sector Picker");
    }

    public void show(Seq<Sector> possibleSectors, Sector currentSector, Runnable confirmSector) {
        cont.clear();
        buttons.clear();
        buttons.defaults().size(160f, 64f);
        addCloseListener();
        targetSector = currentSector;
        ButtonGroup<Button> group = new ButtonGroup<>();
        cont.add("Sector of origin:");
        cont.row();
        cont.pane(t -> {
            int i = 0;
            for(var sec : possibleSectors) {
                t.button(b -> b.add(sec.name()), Styles.togglet, () -> {
                    targetSector = sec;
                }).group(group).pad(4).checked(currentSector.equals(sec)).minSize(600f, 100f);
                t.row();

            }
        }).growX().get().setScrollingDisabled(false, false);
        buttons.button("@back", Icon.left, this::hide);
        buttons.button("@confirm", Icon.ok, ()->{
            confirmSector.run();
            this.hide();
        });
        show();
    }
    public Sector getTargetSector(){
        return targetSector;
    }
}