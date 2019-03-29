# OpenDotaDump Tools

This repository hosts some tools and analysis performed on the [second OpenDota Data Dump][opendota-data-dump].

These tools and analysis were presented as a [poster][poster-download] at the [TRU Undergraduate Research & Innovation Conference 2019][undegrad-conference-session].

## Data Set

The data set used for this analysis is the `matches` file from the aforementioned Open Dota Data Dump. It includes over one billion matches from March 2011 to March 2016. You can download a [sample][matches-sample] (3GB) or the [full file][matches-full] (157GB compressed, 1.2TB uncompressed). The sample file and the full file maintain the same structure as CSV files. We suggest using the sample file for exploring the data before switching to the full file.

While not used currently in this project, there are also two other files in this data dump: the `player_matches` file whose full version is no longer available (lack of seeds) and the `match_skill` file which contains further metadata about matches.

The format of the dataset is described on the [OpenDota project wiki][opendotadump-format].

### Sample of data

```csv
match_id,match_seq_num,radiant_win,start_time,duration,tower_status_radiant,tower_status_dire,barracks_status_radiant,barracks_status_dire,cluster,first_blood_time,lobby_type,human_players,leagueid,positive_votes,negative_votes,game_mode,engine,picks_bans,parse_status,chat,objectives,radiant_gold_adv,radiant_xp_adv,teamfights,version,pgroup
2304340261,2019317886,t,1461013929,1701,1975,4,63,3,155,100,0,10,0,0,0,1,1,,3,,,,,,,"{""0"":{""account_id"":4294967295,""hero_id"":93,""player_slot"":0},""1"":{""account_id"":4294967295,""hero_id"":75,""player_slot"":1},""2"":{""account_id"":4294967295,""hero_id"":19,""player_slot"":2},""3"":{""account_id"":4294967295,""hero_id"":44,""player_slot"":3},""4"":{""account_id"":4294967295,""hero_id"":7,""player_slot"":4},""128"":{""account_id"":4294967295,""hero_id"":46,""player_slot"":128},""129"":{""account_id"":45475622,""hero_id"":38,""player_slot"":129},""130"":{""account_id"":4294967295,""hero_id"":52,""player_slot"":130},""131"":{""account_id"":4294967295,""hero_id"":43,""player_slot"":131},""132"":{""account_id"":4294967295,""hero_id"":60,""player_slot"":132}}"
```

## Tools

### Matches Condenser

Our [Matches Condenser tool](matches-condenser) is a Java tool that condenses the matches file by performing dimensionality and granularity reduction. Depending on the arguments used, it will either produce a JSON of picks per hero per day, or wins and losses per hero per day. We intend to expand this tool to allow for more metadata to be retained, particularly on matches duration. The last stable release available as a [JAR](matches-condenser-release) only produces picks per day and is meant for use with the JSON to CSV tool.

### JSON to CSV

Our [JSON to CSV tool][json-to-csv] performs data cleaning, labelling of heroes, and converts the output of the Matches Condenser into a pretty straight forward CSV file. However, it does not yet support the new `[win, losses]` format unfortunately.

Temporarily we have two Jupyter Notebooks that do so: [OpenDota_Picks_JSON_to_CSV.ipynb][json-to-csv-picks] produces a normal picks only CSV from the winratio JSON, and [OpenDota_Picks_JSON_to_CSV_winratio.ipynb][json-to-csv-winratios] produces a CSV that retains win ratios but does not fully clean up the data. We intend to incorporate these back into the tool shortly.

## Analysis

Our analysis has been done via Jupyter Notebooks which we must note are not yet cleaned up well: both the graph generation for the production of our poster as well as exploratory data analysis are still grouped together in the notebooks, and it takes quite a while to generate these graphs. We fully intend on cleaning up these notebooks in the upcoming weeks.

- 
- [GraphPicks][analysis-graphpicks] [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/github/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_picks/GraphPicks.ipynb]- This notebook was the main exploratory analysis tool, and produced the early graphs

## Other Components

## References

[opendota-data-dump]: https://blog.opendota.com/2017/03/24/datadump2/	"The OpenDota Project. (2017, March 24). Data Dump (March 2011 to March 2016). Retrieved February 25, 2019, from OpenDota website: https://blog.opendota.com/2017/03/24/datadump2/"
[poster-download]:  https://github.com/marcolussetti/opendotadump-tools/releases/download/poster-v1.1/poster_LussettiMarco_FraserDyson_48x36_CORRECTED.pdf
[undergrad-conference-session]: https://digitalcommons.library.tru.ca/urc/2019/postersb/26/	"Lussetti, M., &amp; Fraser, D. (2019, March 29). Big Data Reduction: Lessons Learned from Analyzing One Billion Dota 2 Matches. Poster presented at the 14th annual TRU Undergraduate Research &amp; Innovation Conference, Kamloops, Canada. Retrieved from https://digitalcommons.library.tru.ca/urc/2019/postersb/26"
[matches-sample]: https://storage.googleapis.com/dota-match-dumps/matches_small.csv
[matches-full]: http://academictorrents.com/details/0ddf777978c0669b52fadd1baa9e256a6d8b3996
[opendotadump-format]: https://github.com/odota/core/wiki/JSON-Data-Dump
[matches-condenser]: https://github.com/marcolussetti/opendotadump-tools/tree/master/matches_condenser
[matches-condenser-jar]: https://github.com/marcolussetti/opendotadump-tools/releases/tag/matches_condenser-v0.4
[json-to-csv]: https://github.com/marcolussetti/opendotadump-tools/blob/master/json_to_csv/opendota_jsontocsv.py
[json-to-csv-picks]: https://github.com/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_winratio/OpenDota_Picks_JSON_to_CSV.ipynb
[json-to-csv-winratios]: https://github.com/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_winratio/OpenDota_Picks_JSON_to_CSV_winratio.ipynb
