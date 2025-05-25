package one.oth3r.directionhud.common.hud.module;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.files.playerdata.PDHud;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.ActionResult;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Lang;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ModuleManager {
    public static final Lang LANG = new Lang("directionhud.hud.module.");
    private static final ActionResult INVALID_MODULE = new ActionResult(false, LANG.error("invalid"));

    public static class Reset {
        /**
         * resets all modules to their default state
         */
        public static ActionResult resetEverything(Player player) {
            player.getPData().getHud().setModules(PlayerData.getDefaults().getHud().getModules());
            return new ActionResult(true, LANG.msg("reset_all", CUtl.LANG.btn("all").color('c')));
        }

        /**
         * resets a module's custom settings to the default state. (excluding the state and order of the module)
         *
         * @param player the target player
         * @param module the module to reset
         */
        public static ActionResult resetModule(Player player, Module module) {
            if (module.equals(Module.UNKNOWN)) {
                return INVALID_MODULE;
            }
            // the original module
            BaseModule mod = player.getPData().getHud().getModule(module);

            if (!mod.hasSettings()) {
                return new ActionResult(false, Setting.LANG.error("no_settings", new CTxT(module.getName()).color(CUtl.s())));
            }
            if (!canResetSettings(mod)) {
                return new ActionResult(false, LANG.error("reset.already_reset", new CTxT(module.getName()).color(CUtl.s())));
            }

            // the module to reset to
            BaseModule resetModule = PlayerData.getDefaults().getHud().getModule(module).clone();

            // set the reset module's order and state to the current module
            resetModule.setOrder(mod.getOrder());
            resetModule.setState(mod.isEnabled());

            // set the module to the reset one (saves)
            player.getPData().getHud().setModule(resetModule);

            return new ActionResult(true, LANG.msg("reset", CTxT.of(module.toString()).color(CUtl.s())));
        }

        /**
         * checks if a module has been edited
         *
         * @return true if the module has been edited, as it can reset
         */
        public static boolean canResetSettings(BaseModule module) {
            BaseModule defaultModule = PlayerData.getDefaults().getHud().getModule(module.getModuleType());
            return !defaultModule.getSettings().equals(module.getSettings());
        }

        /**
         * checks if a module can be reset to its default state
         *
         * @return true if the module can be reset
         */
        public static boolean canReset(BaseModule module) {
            BaseModule defaultModule = PlayerData.getDefaults().getHud().getModule(module.getModuleType());
            return !defaultModule.equals(module);
        }
    }
    public static class State {
        public static ActionResult disable(Player player, Module module) {
            if (module.equals(Module.UNKNOWN)) return INVALID_MODULE;

            // get the module
            BaseModule mod = player.getPData().getHud().getModule(module);

            // if already disabled
            if (!mod.isEnabled()) {
                return new ActionResult(false, LANG.error("state",module.getCTxT(),LANG.error("state.disabled")));
            }

            int order = mod.getOrder() == null ? 1 : mod.getOrder(); // get the order of the module
            // remove the module's order & disable
            mod.setOrder(null);
            mod.setState(false);

            // fix the order
            Order.fixOrder(player.getPCache().getHud().getModules());

            // save the changes
            player.getPData().queueSave();

            // build the message
            CTxT msg = LANG.msg("state",
                            Hud.modules.LANG.msg("state.disabled").color('c'),
                            new CTxT(module.toString()).color(CUtl.s())).append(" ")
                    .append(new CTxT(Assets.symbols.arrows.leftEnd).color(Assets.mainColors.back).btn(true)
                            .click(1, "/hud modules enable "+module.getName()+" "+order) // enable the module at the order it was at
                            .hover(Hud.modules.Disabled.LANG.hover("undo").color(Assets.mainColors.back).append("\n")
                                    .append(Hud.modules.Disabled.LANG.hover("undo.click",new CTxT(module.getName()).color(CUtl.s())))));

            return new ActionResult(true, msg, "module", Order.getModuleAt(player,order-1).toString());
        }

        public static ActionResult enable(Player player, Module module, Integer order) {
            if (module.equals(Module.UNKNOWN)) return INVALID_MODULE;

            // get the module
            BaseModule mod = player.getPData().getHud().getModule(module);

            // if already enabled
            if (mod.isEnabled()) {
                return new ActionResult(false, LANG.error("state",module.getCTxT(),LANG.error("state.enabled")));
            }

            ArrayList<BaseModule> enabled = getEnabled(player);
            // get the disabled page of the module
            int page = Hud.modules.Disabled.getList(player).getPageOf(mod);

            mod.setState(true);
            if (order != null) {
                order = Math.max(Math.min(order, enabled.size()+1), 1); // remove order bad data if not null
                mod.setOrder(order);
            } else {
                mod.setOrder(enabled.size()+1); // set the order to the end of the list
            }

            // save the changes
            player.getPData().queueSave();

            // build the message
            CTxT msg = LANG.msg("state",
                    Hud.modules.LANG.msg("state.enabled").color('a'),
                    new CTxT(module.toString()).color(CUtl.s())).append(" ")
                    // edit pencil
                    .append(new CTxT(Assets.symbols.pencil).btn(true).color(Assets.mainColors.edit)
                            .click(1,"/hud modules edit "+module.getName())
                            .hover(Hud.modules.Edit.LANG.hover("edit").color(Assets.mainColors.edit)
                                    .append("\n").append(Hud.modules.Edit.LANG.hover("edit.click",new CTxT(module.getName()).color(CUtl.s())))));

            return new ActionResult(true, msg, "page", String.valueOf(page)); // return the page of the module
        }

        /**
         * Gets a list of modules that are enabled, sorted by their order.
         * @return a list of enabled modules in the HUD order
         */
        public static ArrayList<BaseModule> getEnabled(Player player) {
            return player.getPCache().getHud().getModules().stream().filter(BaseModule::isEnabled)
                    .sorted(Comparator.comparingInt(BaseModule::getOrder))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * Gets a list of modules that are disabled.
         */
        public static ArrayList<BaseModule> getDisabled(Player player) {
            return player.getPCache().getHud().getModules().stream().filter(mod -> !mod.isEnabled())
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }
    public static class Order {

        /**
         * move a module's position in the HUD
         * @param module module to move
         * @param newOrder position in the list, starts at 1
         */
        public static ActionResult move(Player player, Module module, int newOrder) {
            if (module.equals(Module.UNKNOWN)) return INVALID_MODULE;
            // get the module in question
            BaseModule mod = player.getPData().getHud().getModule(module);

            // error - you can't move a disabled module
            if (!mod.isEnabled()) {
                return new ActionResult(false, LANG.error("order"));
            }

            // get order list and enabled list
            ArrayList<BaseModule> list = player.getPData().getHud().getModules(), enabled = State.getEnabled(player);
            // clamp the new order
            newOrder = Math.max(1,Math.min(newOrder,enabled.size()));
            int oldOrder = mod.getOrder();


//            if (oldOrder < newOrder) {
//                // if old is less than new, start from the new order and move everything down one order
//                for (int i = newOrder; i > oldOrder; i--) {
//                    // set the order of the affected modules
//                    player.getPData().getHud().getModule(enabled.get(i-1).getModuleType()).setOrder(i-1);
//                }
//            } else {
//                // if new is less than old, start from the new order and move everything up
//                for (int i = newOrder; i < oldOrder; i++) {
//                    player.getPData().getHud().getModule(enabled.get(i-1).getModuleType()).setOrder(i + 1);
//                }
//            }
            // adjust the order numbers of the other modules
            if (oldOrder != newOrder) {
                int direction = oldOrder < newOrder ? -1 : 1;

                for (int i = newOrder; i != oldOrder; i += direction) {
                    player.getPData().getHud().getModule(enabled.get(i-1).getModuleType()).setOrder(i + direction);
                }
            }

            mod.setOrder(newOrder);

            // make sure everything is saved
            player.getPData().queueSave();
            // get the new order number (formatted for user)
            int order = BaseModule.findInArrayList(list,module).orElse(
                    player.getPData().getHud().getModule(module)).getOrder();

            // set the order of module to <>
            CTxT msg = LANG.msg("order",
                    CTxT.of(module.toString()).color(CUtl.s()),
                    CTxT.of(String.valueOf(order)).color(CUtl.s()));

            return new ActionResult(true, msg);
        }

        /**
         * overload of {@link #fixOrder(ArrayList, boolean)} that doesn't get the factory default list
         * @param list the list of modules to fix
         */
        public static void fixOrder(ArrayList<BaseModule> list) {
            fixOrder(list, false);
        }

        /**
         * module order fixer, removes unknown modules, and fills in the gaps if modules are missing
         *
         * @param list              the list that needs to be fixed
         * @param getFactoryDefault if the list to check against should be the factory default or not
         */
        public static void fixOrder(ArrayList<BaseModule> list, boolean getFactoryDefault) {
            /*
            1. removes duplicates and invalid entries
            2. sorts based on module.getOrder()
            3. adds any missing modules
             */

            // the default list of modules
            ArrayList<BaseModule> defaultList;
            if (getFactoryDefault) defaultList = new PDHud().getModules();
            else defaultList = PlayerData.getDefaults().getHud().getModules();

            // if the module isn't valid or there's a duplicate module, remove
            Helper.removeDuplicateSubclasses(list);

            // if the size of the list is still bigger than the default list, return the default
            if (list.size() > defaultList.size()) {
                list.clear();
                list.addAll(defaultList);
                return;
            }

            // add missing modules to the list
            defaultList.stream()
                    .filter(mod -> list.stream().noneMatch(module -> module.getClass().equals(mod.getClass())))
                    .forEach(list::add);

            // sort the list (order first nulls (disabled) last)
            list.sort(Comparator.comparing(BaseModule::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

            // set each order in the list from 1 - max (if the module already has an order
            setOrder(list);
        }

        /**
         * sets the order to how the modules are in the array list
         */
        public static void setOrder(ArrayList<BaseModule> list) {
            // set the order of the module to the order in the arraylist
            int orderIndex = 1;
            for (BaseModule baseModule : list) { // should already be in the right order
                if (baseModule.isEnabled()) {
                    baseModule.setOrder(orderIndex++); // post fix the order after setting
                } else {
                    // remove order if not enabled
                    baseModule.setOrder(null);
                }
            }
        }

        public static Module getModuleAt(Player player, int order) {
            ArrayList<BaseModule> modules = State.getEnabled(player);
            if (modules.isEmpty()) return Module.UNKNOWN;
            // use min and max to make sure no out of bounds errors happen
            return modules.get(Math.min(Math.max(order-1, 0), modules.size()-1)).getModuleType();
        }
    }

    public static class Setting {
        public static final Lang LANG = new Lang("directionhud.hud.module.setting.");

        public static ActionResult setSetting(Player player, Module module, String settingID, String value) {
            if (module.equals(Module.UNKNOWN)) return INVALID_MODULE;

            BaseModule mod = player.getPData().getHud().getModule(module);
            ActionResult result = mod.setSetting(settingID, value);

            // set the module to save if a success
            if (result.success()) player.getPData().getHud().setModule(mod);

            return result;
        }
    }
}
