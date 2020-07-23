# AtherysTowns
A land-management plugin for the A'therys Horizons server, inspired by Towny

## Commands

### Resident
* `/resident`, `/res`
* `/resident info <player>`
* `/resident friend <player>`
* `/resident unfriend <player>`

### Plot
* `/plot`, `/p`
* `/plot info`, `/plot here`
* `/plot select`
* `/plot select A`
* `/plot select B`
* `/plot select clear`
* `/plot border <true/false>`
* `/plot rename <new name>`
* `/plot grant <player>`
* `/plot permit <player/town/nation> <permission>`
* `/plot revoke <player/town/nation> <permission>`

### Town
* `/town`, `/t`
* `/town info <town>`
* `/town here`
* `/town create <name>`
* `/town ruin`
* `/town claim`
* `/town unclaim`, `/town abandon`
* `/town join <town>`
* `/town invite <player>`
* `/town leave`
* `/town raid`
* `/town raid info`
* `/town raid create`
* `/town raid cancel`
* `/town kick <player>`
* `/town paydebt`
* `/town spawn`
* `/town setspawn`
* `/town rename <name>`
* `/town describe <description>`
* `/town motd <motd>`
* `/town color <color>`
* `/town pvp <true/false>`
* `/town deposit <amount> <currency>`
* `/town withdraw <amount> <currency>`
* `/town permit <player/town/nation> <permission>`
* `/town revoke <player/town/nation> <permission>`
* `/town role add <player> <role>`
* `/town role revoke <player> <role>`
### Town Admin Commands
* `/town decrease <amount>`
* `/town increase <amount>`

### Nation
* `/nation`, `/n`
* `/nation info <nation>`
* `/nation list`
* `/nation here`
* `/nation name <name>`
* `/nation description <description>`
* `/nation capital <town>`
* `/nation ally <nation>`
* `/nation neutral <nation>`
* `/nation enemy <nation>`
* `/nation deposit <amount> <currency>`
* `/nation withdraw <amount> <currency>`
* `/nation permit <player/town/nation> <permission>`
* `/nation revoke <player/town/nation> <permission>`
* `/nation tax <tax>`
* `/nation role add <player> <role>`
* `/nation role revoke <player> <role>`
### Nation Admin Commands
* `/nation create <name>`
* `/nation disband <nation>`
* `/nation add <town>`
* `/nation remove <town>`

# Installation
## Requirements
* AtherysCore. See: https://github.com/Atherys-Horizons/AtherysCore
* PostgreSQL database
