# OpenDotaDump Tools

This repository hosts some tools and analysis performed on the [second OpenDota Data Dump][opendota-data-dump].

These tools and analysis were presented as a [poster][poster-download] at the [TRU Undergraduate Research & Innovation Conference 2019][undergrad-conference-session].

We also presented these results as two presentations [in class][comp4610-presentation] and at the [TRU Computing Science Showcase - Winter 2019][showcase-presentation].

We submitted [a report][comp4610-report] as part of our class that is an expanded version of the previously submitted work.

## Data Set

The data set used for this analysis is the `matches` file from the aforementioned Open Dota Data Dump. It includes over one billion matches from March 2011 to March 2016. You can download a [sample][matches-sample] (3GB) or the [full file][matches-full] (157GB compressed, 1.2TB uncompressed). The sample file and the full file maintain the same structure as CSV files. We suggest using the sample file for exploring the data before switching to the full file.

While not used currently in this project, there are also two other files in this data dump: the `player_matches` file whose full version is no longer available (lack of seeds) and the `match_skill` file which contains further metadata about matches.

The format of the dataset is described on the [OpenDota project wiki][opendotadump-format].

### Sample of data

```csv
match_id,match_seq_num,radiant_win,start_time,duration,tower_status_radiant,tower_status_dire,barracks_status_radiant,barracks_status_dire,cluster,first_blood_time,lobby_type,human_players,leagueid,positive_votes,negative_votes,game_mode,engine,picks_bans,parse_status,chat,objectives,radiant_gold_adv,radiant_xp_adv,teamfights,version,pgroup
2304340261,2019317886,t,1461013929,1701,1975,4,63,3,155,100,0,10,0,0,0,1,1,,3,,,,,,,"{""0"":{""account_id"":4294967295,""hero_id"":93,""player_slot"":0},""1"":{""account_id"":4294967295,""hero_id"":75,""player_slot"":1},""2"":{""account_id"":4294967295,""hero_id"":19,""player_slot"":2},""3"":{""account_id"":4294967295,""hero_id"":44,""player_slot"":3},""4"":{""account_id"":4294967295,""hero_id"":7,""player_slot"":4},""128"":{""account_id"":4294967295,""hero_id"":46,""player_slot"":128},""129"":{""account_id"":45475622,""hero_id"":38,""player_slot"":129},""130"":{""account_id"":4294967295,""hero_id"":52,""player_slot"":130},""131"":{""account_id"":4294967295,""hero_id"":43,""player_slot"":131},""132"":{""account_id"":4294967295,""hero_id"":60,""player_slot"":132}}"
```

## Generated (condensed) Data Sets

The condensed versions produced with our tools (see below) are available on this repository:

- [Pick Rates Per Hero Per Day][data-heroespicks]
- [Wins and Losses per Hero Per Day][data-heroeswinratios]
- [Unprocessed JSONs & SER files][data-condensedmatches] (direct output of the MatchesCondenser tool)

## Tools

### Matches Condenser

Our [Matches Condenser tool][matches-condenser] is a Java tool that condenses the matches file by performing dimensionality and granularity reduction. Depending on the arguments used, it will either produce a JSON of picks per hero per day, or wins and losses per hero per day. We intend to expand this tool to allow for more metadata to be retained, particularly on matches duration. The last stable release available as a [JAR][matches-condenser-release] only produces picks per day and is meant for use with the JSON to CSV tool.

### JSON to CSV

Our [JSON to CSV tool][json-to-csv] performs data cleaning, labelling of heroes, and converts the output of the Matches Condenser into a pretty straight forward CSV file. However, it does not yet support the new `[win, losses]` format unfortunately.

Temporarily we have two Jupyter Notebooks that do so: [OpenDota_Picks_JSON_to_CSV.ipynb][json-to-csv-picks] produces a normal picks only CSV from the winratio JSON, and [OpenDota_Picks_JSON_to_CSV_winratio.ipynb][json-to-csv-winratios] produces a CSV that retains win ratios but does not fully clean up the data. We intend to incorporate these back into the tool shortly.

## Analysis

Our analysis has been done via Jupyter Notebooks which we must note are not yet cleaned up well: both the graph generation for the production of our poster as well as exploratory data analysis are still grouped together in the notebooks, and it takes quite a while to generate these graphs. We fully intend on cleaning up these notebooks in the future.

Please also note that due to the versions of various libraries needed by Plotnine and the versions provided in Google Colab, you will need to run the pip commands at the top of the notebook and then reload the runtime once before running the notebook through. If running through the entire notebook, please be aware that the large graph size and the complexity of the graphs mean that it may take quite a while to run through it.

- [LookupSpikes][analysis-lookupspikes] [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)][analysis-lookupspikes-colab]: This notebook is the closest thing to a usable analysis tool. It's a WIP cleanup of the GraphPicks (see below) original notebook, but it needs a lot of cleaning still.
- [GraphPicks][analysis-graphpicks] [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)][analysis-graphpicks-colab]: This notebook was the main exploratory analysis tool, and produced the early graphs
- [GraphWinRatios][analysis-graphwinratios] [![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)][analysis-graphwinratios-colab]: This notebook was the main exploratory tool for looking at Win Ratios

## Other Components

### Poster

As mentioned above, we had the privilege of presenting a poster at the [TRU Undergraduate Research & Innovation Conference 2019][undegrad-conference-session]. The tools and source files used in generating the poster are included in this repository.

- [Poster directory][poster-dir]
- [Poster as a PDF][poster-pdf]

### Prints

We also produced a number of smaller prints for explanatory purposes that we brought along to the conference.

- [Prints directory][prints-dir]
- [Explain peaks in poster by listing contributory hero pick rate shifts][prints-explain]
- [MatchesCondenser source code as PDF][prints-sourcecode]
- [Top 10 Most Popular Heroes - Pick Rate][prints-mostpopular]
- [Heroes with the highest variation in picks - Pick Rate][prints-variability]
- [Win Ratios for all heroes][prints-winratio]
- [Win Ratio Differences][prints-winratio-difference]

## Citation

If for any reason you wish to cite this work, we suggest using our poster's presentation as base:

Lussetti, M., & Fraser, D. (2019, March 29). *Big Data Reduction: Lessons Learned from Analyzing One Billion Dota 2 Matches*. Presented at the 14th annual TRU Undergraduate Research & Innovation Conference, Kamloops, Canada. Retrieved from https://digitalcommons.library.tru.ca/urc/2019/postersb/26

`
Lussetti, M., & Fraser, D. (2019, March 29). Big Data Reduction: Lessons Learned from Analyzing One Billion Dota 2 Matches. Presented at the 14th annual TRU Undergraduate Research & Innovation Conference, Kamloops, Canada. Retrieved from https://digitalcommons.library.tru.ca/urc/2019/postersb/26
`



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
[analysis-lookupspikes]: https://github.com/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_picks/LookupSpikes.ipynb
[analysis-lookupspikes-colab]: https://colab.research.google.com/github/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_picks/LookupSpikes.ipynb
[analysis-graphpicks]: https://github.com/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_picks/GraphPicks.ipynb
[analysis-graphpicks-colab]: https://colab.research.google.com/github/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_picks/GraphPicks.ipynb
[analysis-graphwinratios]: https://github.com/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_winratio/GraphWinRatios.ipynb
[analysis-graphwinratios-colab]: https://colab.research.google.com/github/marcolussetti/opendotadump-tools/blob/master/analysis/heroes_winratio/GraphWinRatios.ipynb
[data-heroespicks]: https://github.com/marcolussetti/opendotadump-tools/tree/master/data/heroes_picks_csvs
[data-heroeswinratios]: https://github.com/marcolussetti/opendotadump-tools/tree/master/data/heroes_winratio_csvs
[data-condensedmatches]: https://github.com/marcolussetti/opendotadump-tools/tree/master/data/condensed_matches_data
[poster-pdf]: https://github.com/marcolussetti/opendotadump-tools/blob/master/poster/poster_LussettiMarco_FraserDyson_48x36_CORRECTED.pdf
[poster-dir]: https://github.com/marcolussetti/opendotadump-tools/tree/master/poster
[prints-dir]: https://github.com/marcolussetti/opendotadump-tools/tree/master/prints
[prints-explain]: https://github.com/marcolussetti/opendotadump-tools/blob/master/prints/explain_peaks/explain_horizontal.pdf
[prints-sourcecode]: https://github.com/marcolussetti/opendotadump-tools/blob/master/prints/matches_condenser_source_code/Java_11x17.pdf
[prints-mostpopular]: https://github.com/marcolussetti/opendotadump-tools/blob/master/prints/picks_most_popular/most_popular_champions.pdf
[prints-variability]: https://github.com/marcolussetti/opendotadump-tools/blob/master/prints/picks_highest_variability_heroes/highest_variability_champions.pdf
[prints-winratio]: https://github.com/marcolussetti/opendotadump-tools/blob/master/prints/winratios_all/WinRatios.pdf
[prints-winratio-difference]: https://github.com/marcolussetti/opendotadump-tools/blob/master/prints/winratios_differences/win_ratio_differences.pdf
[comp4610-presentation]: http://sno.cc/comp4610
[showcase-presentation]: http://sno.cc/showcase2019
[comp4610-report]:https://github.com/marcolussetti/opendotadump-tools/blob/master/report/main.pdf
