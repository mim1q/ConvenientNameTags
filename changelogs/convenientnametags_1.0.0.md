# 0.3.0

## Config

- Added a config file to customize the mod!  
  Following options are available:

| Gamerule                   | Default | Description                                                                 |
|----------------------------|---------|-----------------------------------------------------------------------------|
| `renameCost`               | 0       | Experience levels required to rename a Name Tag                             |
| `renameCostPerWholeStack`  | true    | Should the cost be constant for any stack size (or multiplied by the count) |
| `dropNameTagsOnDeath`      | true    | Should a Name Tag drop when a named mod is killed                           |
| `dropNameTagsOnNameChange` | true    | Should a Name Tag drop from a named mob when another Name Tag is applied    |
| `enableNameTagShearing`    | true    | Should players be able to use shears to remove Name Tags from mobs          | 
| `enableRenameScreen`       | true    | Should players be able to use the Rename Name Tag screen                    |
| `enableCraftingRecipe`     | true    | Should the Name Tag crafting recipe be enabled                              |
| `denyList`                 | empty   | A list of words disallowed when renaming a Name Tag                         | 

## Other features

Two other features were implemented alongside the config. Both are optional, disabled by default.  
These are:

- **Deny List** - you can specify a list of words in the config file that are forbidden. Players will get a chat message 
  when they try to give a Name Tag a name that contains any of these words
- **Rename Cost** - optional experience level cost for renaming Name Tags. When configured to be more than 0, a text
  indicating how much the renaming will cost will appear. The player will not be able to rename the Name Tag unless they 
  have at least the specified experience level, which will be deducted upon renaming.
- The Rename Cost can be configured to be calculated for the entire stack (by default) or for just one Name Tag. The
  latter would mean that renaming e.g. 24 Name Tags would cost 24 times as much as renaming a single Name Tag.