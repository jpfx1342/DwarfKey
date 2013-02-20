/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpfx.dwarfkey.macro;

import java.util.ArrayList;

/**
 *
 * @author john
 */
public class Command {
	public static interface CommandMatcher {
		public boolean matches(String command);
	}
	public static class PrefixCommandMatcher implements CommandMatcher {
		public final String[] targ;

		public PrefixCommandMatcher(String... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (command.startsWith(targ[i]))
					return true;
			return false;
		}
	}
	public static class ExactCommandMatcher implements CommandMatcher {
		public final String[] targ;

		public ExactCommandMatcher(String... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (command.equals(targ[i]))
					return true;
			return false;
		}
	}
	public static class ANDCommandMatcher implements CommandMatcher {
		public final CommandMatcher[] targ;

		public ANDCommandMatcher(CommandMatcher... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (!targ[i].matches(command))
					return false;
			return true;
		}
	}
	public static class ORCommandMatcher implements CommandMatcher {
		public final CommandMatcher[] targ;

		public ORCommandMatcher(CommandMatcher... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (targ[i].matches(command))
					return true;
			return false;
		}
	}
	public static class NOTCommandMatcher implements CommandMatcher {
		public final CommandMatcher targ;

		public NOTCommandMatcher(CommandMatcher targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			return !targ.matches(command);
		}
	}
	
	public static interface CommandGroup {
		public boolean contains(String command);
	}
	public static enum CommandSuperGroup implements CommandGroup {
		GENERAL,
		WORLD,
		ADVENTURE,
		DWARF_MODE,
		EMBARK,
		BUILDING,
		WORKSHOP,
		PILEZONE,
		STOCKORDER,
		MILITIA,
		TEXT_ENTRY,
		;

		@Override
		public boolean contains(String command) {
			CommandSubgroup group = getSubgroup(command);
			return this == group.group;
		}
		
	}
	public static enum CommandSubgroup implements CommandGroup {
		NAV_SELECT     (CommandSuperGroup.GENERAL, new ExactCommandMatcher("SELECT", "SEC_SELECT", "DESELECT", "SELECT_ALL", "DESELECT_ALL")),
		NAV_MENU       (CommandSuperGroup.GENERAL, new ExactCommandMatcher("LEAVESCREEN", "LEAVESCREEN_ALL", "CLOSE_MEGA_ANNOUNCEMENT", "MENU_CONFIRM")),
		NAV_TAB        (CommandSuperGroup.GENERAL, new ExactCommandMatcher("CHANGETAB", "SEC_CHANGETAB")),
		NAV_SCROLL     (CommandSuperGroup.GENERAL, new PrefixCommandMatcher("STANDARDSCROLL_", "SECONDSCROLL_")),
		NAV_CURSOR     (CommandSuperGroup.GENERAL, new PrefixCommandMatcher("CURSOR_")),
		
		MENU_META      (CommandSuperGroup.GENERAL, new ExactCommandMatcher("OPTIONS", "HELP", "MOVIES", "OPTION_EXPORT")),
		CONFIG_CONTROL (CommandSuperGroup.GENERAL, new ExactCommandMatcher("TOGGLE_FULLSCREEN", "TOGGLE_TTF", "FPS_UP", "FPS_DOWN")),
		ZOOM_CONTROL   (CommandSuperGroup.GENERAL, new PrefixCommandMatcher("ZOOM_")),
		MENU_MOVIES    (CommandSuperGroup.GENERAL, new PrefixCommandMatcher("MOVIE_")),
		MENU_BINDINGS  (CommandSuperGroup.GENERAL, new ExactCommandMatcher("SAVE_BINDINGS", "LOAD_BINDINGS")),
		MACRO_CONTROL  (CommandSuperGroup.GENERAL, new ExactCommandMatcher("RECORD_MACRO", "PLAY_MACRO", "SAVE_MACRO", "LOAD_MACRO", "PREFIX")),
		GEN_OPTION     (CommandSuperGroup.GENERAL, new PrefixCommandMatcher("OPTION", "SEC_OPTION")),
		GEN_CUSTOM     (CommandSuperGroup.GENERAL, new PrefixCommandMatcher("CUSTOM_")),
		
		WORLD_PARAM    (CommandSuperGroup.WORLD, new PrefixCommandMatcher("WORLD_PARAM_")),
		WORLD_GEN      (CommandSuperGroup.WORLD, new PrefixCommandMatcher("WORLD_GEN_", "WORLDGEN")),
		WORLD_LEGENDS  (CommandSuperGroup.WORLD, new PrefixCommandMatcher("LEGENDS_")),
		
		ADV_ARENA      (CommandSuperGroup.ADVENTURE, new ExactCommandMatcher("A_RETURN_TO_ARENA")),
		ADV_MOVE       (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_MOVE_", "A_CARE_MOVE_")),
		ADV_COMBAT     (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_COMBAT", "A_ATTACK")),
		ADV_STATUS     (CommandSuperGroup.ADVENTURE, new ORCommandMatcher(new PrefixCommandMatcher("A_STATUS"), new ExactCommandMatcher("A_DATE", "A_WEATHER", "A_TEMPERATURE", "A_STANCE"))),
		ADV_SLEEP      (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_SLEEP")),
		ADV_ACTION     (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_ACTION")),
		ADV_INV        (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_INV_")),
		ADV_TRAVEL     (CommandSuperGroup.ADVENTURE, new ORCommandMatcher(new PrefixCommandMatcher("A_TRAVEL"), new ExactCommandMatcher("A_END_TRAVEL"))),
		ADV_CREATION   (CommandSuperGroup.ADVENTURE, new ExactCommandMatcher("A_ENTER_NAME", "A_CUST_NAME", "A_RANDOM_NAME", "A_CHANGE_GENDER")),
		ADV_LOG        (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_LOG")),
		ADV_BARTER     (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_BARTER_")),
		ADV_MAIN       (CommandSuperGroup.ADVENTURE, new PrefixCommandMatcher("A_")),
		
		EMBARK_LOCAL_MOVE (CommandSuperGroup.EMBARK, new PrefixCommandMatcher("SETUP_LOCAL")),
		EMBARK_NOTES      (CommandSuperGroup.EMBARK, new PrefixCommandMatcher("SETUP_NOTES")),
		EMBARK_BIOME      (CommandSuperGroup.EMBARK, new PrefixCommandMatcher("SETUP_BIOME")),
		EMBARK_NAME       (CommandSuperGroup.EMBARK, new PrefixCommandMatcher("SETUP_NAME", "CHOOSE_NAME")),
		EMBARK_SETUP      (CommandSuperGroup.EMBARK, new PrefixCommandMatcher("SETUP_")),
		EMBARK_PROFILE    (CommandSuperGroup.EMBARK, new PrefixCommandMatcher("SETUPGAME_")),
		
		BUILDJOB_WELL       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_WELL_")),
		BUILDJOB_TARGET     (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_TARGET_")),
		BUILDJOB_STATUE     (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_STATUE_")),
		BUILDJOB_CAGE       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_CAGE_")),
		BUILDJOB_CHAIN      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_CHAIN_")),
		BUILDJOB_SIEGE      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_SIEGE_")),
		BUILDJOB_DOOR       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_DOOR_")),
		BUILDJOB_COFFIN     (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_COFFIN_")),
		BUILDJOB_CHAIR      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_CHAIR_")),
		BUILDJOB_TABLE      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_TABLE_")),
		BUILDJOB_BED        (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_BED_")),
		BUILDJOB_DEPOT      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_DEPOT_")),
		BUILDJOB_ANIMALTRAP (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_ANIMALTRAP_")),
		BUILDJOB_FARM       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_FARM_")),
		BUILDJOB_RACK       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_RACK_")),
		BUILDJOB_STAND      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_STAND_")),
		BUILDJOB_RACKSTAND  (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_RACKSTAND_")),
		BUILDJOB_MAIN       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDJOB_")),
		
		BUILD_MACHINE      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_MACHINE_")),
		BUILD_SIEGEENGINE  (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_SIEGEENGINE_")),
		BUILD_TRAP         (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_TRAP_")),
		BUILD_CONSTRUCTION (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_CONSTRUCTION_")),
		BUILD_WORKSHOP     (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_WORKSHOP_")),
		BUILD_FURNACE      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_FURNACE_")),
		BUILD_MAIN         (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HOTKEY_BUILDING_")),
		
		BUILDING_HIVE       (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("HIVE_")),
		BUILDING_TRIGGER    (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDING_TRIGGER_")),
		BUILDING_ROLLERS    (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDING_ROLLERS_")),
		BUILDING_TRACK_STOP (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDING_TRACK_STOP_")),
		BUILDING_BUILD      (CommandSuperGroup.BUILDING, new PrefixCommandMatcher("BUILDING_DIM_", "BUILDING_ORIENT_")),
		BUILDING_CONTROL    (CommandSuperGroup.BUILDING, new ORCommandMatcher(new PrefixCommandMatcher("BUILDING_", "BUILDING_ORIENT_", "BUILDINGLIST"), new ExactCommandMatcher("DESTROYBUILDING", "SUSPENDBUILDING"))),
		
		WORKSHOP_FURNACE_WOOD    (CommandSuperGroup.WORKSHOP, new ExactCommandMatcher("HOTKEY_MAKE_ASH", "HOTKEY_MAKE_CHARCOAL")),
		WORKSHOP_FURNACE_SMELTER (CommandSuperGroup.WORKSHOP, new ExactCommandMatcher("HOTKEY_MELT_OBJECT")),
		WORKSHOP_FURNACE_GLASS   (CommandSuperGroup.WORKSHOP, new ORCommandMatcher(new PrefixCommandMatcher("HOTKEY_GLASS_"), new ExactCommandMatcher("HOTKEY_COLLECT_SAND", "HOTKEY_COLLECT_CLAY"))),
		WORKSHOP_FURNACE_ASHERY  (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_ASHERY_")),
		WORKSHOP_CARPENTER       (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_CARPENTER_")),
		WORKSHOP_SIEGE           (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_SIEGE_")),
		WORKSHOP_LEATHER         (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_LEATHER_")),
		WORKSHOP_CLOTHES         (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_CLOTHES_")),
		WORKSHOP_CRAFTS          (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_CRAFTS_")),
		WORKSHOP_SMITH           (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_SMITH_")),
		WORKSHOP_STILL           (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_STILL_")),
		WORKSHOP_LOOM            (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_LOOM_")),
		WORKSHOP_KITCHEN         (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_KITCHEN_")),
		WORKSHOP_FARMER          (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_FARMER_")),
		WORKSHOP_MILL            (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_MILL_")),
		WORKSHOP_KENNEL          (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_KENNEL_")),
		WORKSHOP_FISHERY         (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_FISHERY_")),
		WORKSHOP_BUTCHER         (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_BUTCHER_")),
		WORKSHOP_TANNER          (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_TANNER_")),
		WORKSHOP_DYER            (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_DYER_")),
		WORKSHOP_JEWELER         (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_JEWELER_")),
		WORKSHOP_MECHANIC        (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_MECHANIC_")),
		WORKSHOP_MASON           (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_MASON_")),
		WORKSHOP_LEVER           (CommandSuperGroup.WORKSHOP, new PrefixCommandMatcher("HOTKEY_TRAP_")),
		
		PILE_SETTINGS (CommandSuperGroup.PILEZONE, new PrefixCommandMatcher("STOCKPILE_SETTINGS_")),
		PILE_MAIN     (CommandSuperGroup.PILEZONE, new PrefixCommandMatcher("STOCKPILE_")),
		
		ZONE_HOSPITAL (CommandSuperGroup.PILEZONE, new PrefixCommandMatcher("CIVZONE_HOSPITAL_")),
		ZONE_PITPOND  (CommandSuperGroup.PILEZONE, new PrefixCommandMatcher("CIVZONE_PEN_", "CIVZONE_POND_")),
		ZONE_MAIN     (CommandSuperGroup.PILEZONE, new PrefixCommandMatcher("CIVZONE_")),
		
		STORES_MAIN  (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("STORES_")),
		ANIMALS_MAIN (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ANIMAL_", "PET_")),
		KITCHEN_MAIN (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("KITCHEN_")),
		
		ORDERS_FORBID (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ORDERS_FORBID_")),
		ORDERS_REFUSE (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ORDERS_REFUSE_")),
		ORDERS_GATHER (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ORDERS_GATHER_")),
		ORDERS_JOBS   (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ORDERS_AUTO_")),
		ORDERS_ZONE   (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ORDERS_ZONE_")),
		ORDERS_MAIN   (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("ORDERS_")),
		
		MIL_ALERTS   (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_MILITARY_ALERTS_")),
		MIL_EQUIP    (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_MILITARY_EQUIP_")),
		MIL_SUPPLIES (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_MILITARY_SUPPLIES_", "D_MILITARY_ADD_", "D_MILITARY_REPLACE_CLOTHING", "D_MILITARY_EXACT_MATCH")),
		MIL_AMMO     (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_MILITARY_AMMUNITION_")),
		MIL_SCHEDULE (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_SQUAD_SCH_")),
		MIL_MAIN     (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_MILITARY_")),
		MIL_SQUADS   (CommandSuperGroup.STOCKORDER, new PrefixCommandMatcher("D_SQUADS_")),
		
		FORT_TIME                  (CommandSuperGroup.DWARF_MODE, new ExactCommandMatcher("D_ONESTEP", "D_PAUSE")),
		FORT_MENU_HOTKEY            (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_HOTKEY_")),
		FORT_HOTKEYS                (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_HOTKEY")),
		FORT_MENU_HAULING           (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_HAULING_")),
		FORT_MENU_BURROWS           (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_BURROWS_")),
		FORT_MENU_NOTE              (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_NOTE_")),
		FORT_MENU_BITEMS            (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_BITEM_")),
		FORT_MENU_LOOK              (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_LOOK_")),
		FORT_MENU_TRADEGOODS        (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("ASSIGNTRADE_")),
		FORT_MENU_TRADE             (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("TRADE_")),
		FORT_MENU_NOBLES            (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("NOBLELIST_")),
		FORT_MENU_MILITARY          (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("MILITARY_")),
		FORT_MENU_ANNOUNCE          (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("ANNOUNCE_")),
		FORT_MENU_UNITJOB           (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("UNITJOB_")),
		FORT_MENU_MANAGER           (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("MANAGER_")),
		FORT_MENU_DESIGNATE_TRAFFIC (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("DESIGNATE_TRAFFIC_")),
		FORT_MENU_DESIGNATE_ITEMS   (CommandSuperGroup.DWARF_MODE, new ExactCommandMatcher("DESIGNATE_CLAIM", "DESIGNATE_UNCLAIM", "DESIGNATE_MELT", "DESIGNATE_NO_MELT", "DESIGNATE_DUMP", "DESIGNATE_NO_DUMP", "DESIGNATE_HIDE", "DESIGNATE_NO_HIDE")),
		FORT_MENU_DESIGNATE         (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("DESIGNATE_")),
		FORT_MENU_UNITVIEW_PRF      (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("UNITVIEW_PRF_")),
		FORT_MENU_UNITVIEW_GEN      (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("UNITVIEW_GEN_")),
		FORT_MENU_UNITVIEW_RELATE   (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("UNITVIEW_RELATIONSHIPS_")),
		FORT_MENU_UNITVIEW          (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("UNITVIEW_")),
		
		FORT_ITEM         (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("ITEM_")),
		FORT_MAIN         (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("D_")),
		
		CUSTOMIZE_UNIT              (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("CUSTOMIZE_UNIT_")),
		
		ARENA_CREATURE    (CommandSuperGroup.DWARF_MODE, new PrefixCommandMatcher("ARENA_CREATURE_")),
		
		TEXT_STRING    (CommandSuperGroup.TEXT_ENTRY, new PrefixCommandMatcher("STRING_")),
		;
		public final CommandSuperGroup group;
		public final CommandMatcher matcher;

		private CommandSubgroup(CommandSuperGroup group, CommandMatcher matcher) {
			this.group = group;
			this.matcher = matcher;
		}
		
		@Override
		public boolean contains(String command) {
			return matcher.matches(command);
		}
	}
	public static CommandSubgroup getSubgroup(String command) {
		CommandSubgroup[] groups = CommandSubgroup.values();
		
		for (int i = 0; i < groups.length; i++)
			if (groups[i].matcher.matches(command))
				return groups[i];
		
		return null;
	}
	
	public static interface CommandFilter {
		public boolean matches(String command);
	}
	public static class GroupCommandFilter implements CommandFilter {
		public final CommandGroup[] targ;

		public GroupCommandFilter(CommandGroup... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (targ[i].contains(command))
					return true;
			return false;
		}
	}
	public static class ANDCommandFilter implements CommandFilter {
		public final CommandFilter[] targ;

		public ANDCommandFilter(CommandFilter... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (!targ[i].matches(command))
					return false;
			return true;
		}
	}
	public static class ORCommandFilter implements CommandFilter {
		public final CommandFilter[] targ;

		public ORCommandFilter(CommandFilter... targ) { this.targ = targ; }
		
		@Override
		public boolean matches(String command) {
			for (int i = 0; i < targ.length; i++)
				if (targ[i].matches(command))
					return true;
			return false;
		}
	}
	public static void applyCommandFilter(Group group, CommandFilter filter) {
		for (int i = 0; i < group.commands.size(); i++) {
			if (!filter.matches(group.commands.get(i))) {
				group.commands.remove(i--);
			}
		}
	}
	public static void applyCommandFilter(Macro macro, CommandFilter filter) {
		for (int i = 0; i < macro.groups.size(); i++)
			applyCommandFilter(macro.groups.get(i), filter);
	}
}
