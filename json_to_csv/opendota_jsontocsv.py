#!/usr/bin/env python3
"""OPENDOTA_JSONTOCSV

Usage:
    opendota_jsontocsv.py JSON_INPUT_FILE CSV_OUTPUT_FILE [--heroes=(names|numbers)] [(-n | --normalize)] [--remove-low-counts]
    opendota_jsontocsv.py JSON_INPUT_FILE CSV_OUTPUT_FILE --picks-by-hero [--remove-low-counts] [--heroes=(names|numbers)]
    opendota_jsontocsv.py JSON_INPUT_FILE CSV_OUTPUT_FILE --picks-by-date [--remove-low-counts]
    opendota_jsontocsv.py (-h | --help)
    opendota_jsontocsv.py --version

Options:
    -h --help                   Show this screen.
    --version                   Show version.
    --heroes=(names|numbers)    Record heroes by name or number [default: names].
    -n --normalize              Normalize picks as proportion of picks per day.
    --remove-low-counts         Removes early records (pre 2011-11-22) as they have lower volumes of recorded matches.
    --picks-by-date             Export the number of picks for each day to CSV.
    --picks-by-hero             Export the number of picks for each hero to CSV.
"""
import datetime

import requests
import pandas as pd
from docopt import docopt


if __name__ == '__main__':
    arguments = docopt(__doc__, version='OpenDotaDumpTools JSONtoCSV 0.2')

    print("Starting OpenDotaDumpTools...")

    df = pd.read_json(arguments["JSON_INPUT_FILE"])
    print("JSON input loaded")

    # Clean up data
    df = df.transpose()  # Rotate so rows = time
    df = df.fillna(0)  # Replace missing values with 0
    df = df.drop(0, 0)  # Remove entries with missing date (1970)
    df = df.drop(0, 1)  # Remove entries with a missing hero (0)
    df.index = [datetime.datetime(1970, 1, 1, 0, 0) + datetime.timedelta(index - 1)
                for index in df.index]  # Convert index (epoch days) to time
    df = df.reindex(sorted(df.columns), axis=1)  # Order columns in ascending order
    for column in df.columns:  # Convert all values from float to integer
        df[column] = df[column].astype('int64')
    print("Input cleaned")

    if arguments["--remove-low-counts"]:
        df = df.loc[df.index >= '2011-11-22 00:00:00']
        print("Data for days previous to 2011-11-22 removed")

    # EXPORT
    if arguments["--picks-by-date"]:
        picks_by_day = df.sum(axis=1)
        picks_by_day.to_csv(arguments["CSV_OUTPUT_FILE"])
        print("Exported picks by date data to {}".format(arguments["CSV_OUTPUT_FILE"]))
        exit(0)  # DONE!

    if arguments["--heroes"] == "names":
        # Fetch heroes from OpenDota API
        heroes_json = requests.get("http://api.opendota.com/api/heroes/").json()
        heroes = {hero["id"]: hero for hero in heroes_json}
        df.columns = [heroes[column]["localized_name"] for column in df.columns]
        print("Heroes ids replaced with heroes names")

    # EXPORT
    if arguments["--picks-by-hero"]:
        picks_by_day = df.sum(axis=0)
        picks_by_day.to_csv(arguments["CSV_OUTPUT_FILE"])
        print("Exported picks by hero data to {}".format(arguments["CSV_OUTPUT_FILE"]))
        exit(0)  # DONE!

    if arguments["--normalize"]:
        df = df.div(df.sum(axis=1), axis=0)
        print("Values normalized")

    # EXPORT
    df.to_csv(arguments["CSV_OUTPUT_FILE"])
    print("Exported data to {}".format(arguments["CSV_OUTPUT_FILE"]))
