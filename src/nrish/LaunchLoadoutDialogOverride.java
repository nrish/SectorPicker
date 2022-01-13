package nrish;

import arc.Core;
import arc.func.Cons;
import arc.math.geom.Vec3;
import arc.scene.ui.Button;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Schematic;
import mindustry.gen.Icon;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.type.Sector;
import mindustry.ui.Cicon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.LaunchLoadoutDialog;
import mindustry.ui.dialogs.LoadoutDialog;
import mindustry.ui.dialogs.SchematicsDialog.SchematicImage;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

/** Dialog for selecting loadout at sector launch. */
public class LaunchLoadoutDialogOverride extends LaunchLoadoutDialog {
    LoadoutDialog loadout = new LoadoutDialog();
    //total required items
    ItemSeq total = new ItemSeq();
    //currently selected schematic
    Schematic selected;
    //validity of loadout items
    boolean valid;
    ItemSeq sitems;
    /**
     * Sector picked by SectorPickerDialog, or the default provided by PlanetDialog
     */
    Sector fromSector;

    public void show(CoreBlock core, Sector defaultSector, Runnable confirm){
        fromSector = defaultSector;
        cont.clear();
        buttons.clear();

        buttons.defaults().size(160f, 64f);
        buttons.button("@back", Icon.left, this::hide);
        addCloseListener();

        sitems = fromSector.items();

        //updates sum requirements
        Runnable update = () -> {
            int cap = selected.findCore().itemCapacity;

            //cap resources based on core type
            ItemSeq resources = universe.getLaunchResources();
            resources.min(cap);
            universe.updateLaunchResources(resources);

            total.clear();
            selected.requirements().each(total::add);
            universe.getLaunchResources().each(total::add);
            valid = sitems.has(total);
        };
        Cons<Label> launchLabelBuilder = label -> label.setText(Core.bundle.format("launch.from", fromSector.name()));

        Cons<Table> rebuild = table -> {
            table.clearChildren();
            int i = 0;

            ItemSeq schems = selected.requirements();
            ItemSeq launches = universe.getLaunchResources();

            for(ItemStack s : total){
                table.image(s.item.icon(Cicon.small)).left().size(Cicon.small.size);
                int as = schems.get(s.item), al = launches.get(s.item);

                String amountStr = (al + as) + "[gray] (" + (al + " + " + as + ")");
                table.add(
                        sitems.has(s.item, s.amount) ? amountStr :
                                "[scarlet]" + (Math.min(sitems.get(s.item), s.amount) + "[lightgray]/" + amountStr)).padLeft(2).left().padRight(4);

                if(++i % 4 == 0){
                    table.row();
                }
            }
        };

        Table items = new Table();
        Label launchName = new Label("");
        Runnable rebuildLabel = () -> launchLabelBuilder.get(launchName);
        Runnable rebuildItems = () -> rebuild.get(items);

        buttons.button("@resources", Icon.terrain, () -> {
            ItemSeq stacks = universe.getLaunchResources();
            Seq<ItemStack> out = stacks.toSeq();

            ItemSeq realItems = sitems.copy();
            selected.requirements().each(realItems::remove);

            loadout.show(selected.findCore().itemCapacity, realItems, out, UnlockableContent::unlocked, out::clear, () -> {}, () -> {
                universe.updateLaunchResources(new ItemSeq(out));
                update.run();
                rebuildItems.run();
            });
        }).width(204);

        buttons.button("@confirm", Icon.ok, () -> {
            universe.updateLoadout(core, selected);
            //we don't run the confirm to avoid modifying the planet dialog
            //confirm.run();
            hide();
            fromSector.removeItems(universe.getLastLoadout().requirements());
            fromSector.removeItems(universe.getLaunchResources());

            ui.planet.launching = true;
            ui.planet.zoom = 0.5f;

            ui.hudfrag.showLaunchDirect();
            Time.runTask(launchDuration, () -> control.playSector(fromSector, ui.planet.selected));
        }).disabled(b -> !valid);
        buttons.button("@nrish.launchFrom", Icon.terrain, () -> {
            Seq<Sector> capturedSectors = new Seq<>();
            for(Sector s : fromSector.planet.sectors) {
                //get dist to planet center? See PlanetGrid, as far as I can tell planet grid is centered around 0,0,0
                var dist = s.tile.v.dst(Vec3.Zero);
                //allowing player to essentially travel from sectors as long as they are within at least 1 planetary radius away
                //prevents cross-globe travel
                if (s.isCaptured() && s.hasBase() && dist < s.tile.v.dst(fromSector.tile.v)) {
                    capturedSectors.add(s);
                }
            }
            SectorPickerDialog dialog = new SectorPickerDialog();
            dialog.show(capturedSectors, fromSector, () ->{

                fromSector = dialog.getTargetSector();
                sitems = fromSector.items();

                update.run();
                rebuildItems.run();
                rebuildLabel.run();
            });
        });

        int cols = Math.max((int)(Core.graphics.getWidth() / Scl.scl(230)), 1);
        ButtonGroup<Button> group = new ButtonGroup<>();
        selected = universe.getLoadout(core);
        if(selected == null) selected = schematics.getLoadouts().get((CoreBlock)Blocks.coreShard).first();

        cont.add(launchName);
        cont.row();

        cont.pane(t -> {
            int i = 0;

            for(var entry : schematics.getLoadouts()){
                if(entry.key.size <= core.size){
                    for(Schematic s : entry.value){

                        t.button(b -> b.add(new SchematicImage(s)), Styles.togglet, () -> {
                            selected = s;
                            update.run();
                            rebuildItems.run();
                        }).group(group).pad(4).checked(s == selected).size(200f);

                        if(++i % cols == 0){
                            t.row();
                        }
                    }
                }
            }


        }).growX().get().setScrollingDisabled(true, false);
        cont.row();

        cont.pane(items);
        cont.row();

        cont.add("@sector.missingresources").visible(() -> !valid);

        update.run();
        rebuildItems.run();
        rebuildLabel.run();

        show();
    }
}