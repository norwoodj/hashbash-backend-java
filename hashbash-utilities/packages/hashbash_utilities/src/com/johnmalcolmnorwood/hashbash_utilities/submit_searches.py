#!/usr/bin/env python
from . import LOGGER
from jconfigure import configure

import click
import requests
import random
from hashlib import md5
from time import sleep


def submit_search(password, hashbash_server, rainbow_table_id):
    hash_value = md5(password.encode("utf-8")).hexdigest()
    LOGGER.info(f"Submitting search for password hash '{hash_value}', which should reverse to '{password}'")

    search_url = f"{hashbash_server}/api/rainbow-table/{rainbow_table_id}/search"
    res = requests.post(search_url, params={"hash": hash_value})
    res.raise_for_status()
    return res.json()["searchId"]

def poll_until_all_done(hashbash_server, search_ids):
    LOGGER.info(f"Polling until all searches for ids ({', '.join(str(s) for s in search_ids)}) are complete")
    found = {}

    while search_ids:
        done = []

        for s in search_ids:
            search_url = f"{hashbash_server}/api/rainbow-table/search/{s}"
            res = requests.get(search_url)
            res.raise_for_status()
            search = res.json()

            if search["status"] == "FOUND":
                done.append(s)
                found[search["hash"]] = search["password"]
            elif search["status"] == "NOT_FOUND":
                done.append(s)
            else:
                LOGGER.info(f"Result for hash {search['hash']} still {search['status']}")

        for s in done:
            search_ids.remove(s)

        if search_ids:
            sleep(10)

    if len(found) == 0:
        LOGGER.info("Found none :(((")

    for hash, password in found.items():
        LOGGER.info(f"Found hash mapping {hash} => {password}")


@click.command()
@click.option("-h", "--hashbash", default="https://hashbash.johnmalcolmnorwood.com")
@click.option("-c", "--charset", default="abcdefghijklmnopqrstuvwxyz")
@click.option("-l", "--length", default=8)
@click.argument("rainbow_table_id", type=int)
@click.argument("num_strings", type=int)
def search_for_strings(hashbash, charset, length, rainbow_table_id, num_strings):
    LOGGER.info(f"Submitting {num_strings} random strings of length {length} to hashbash server {hashbash}")

    random_strings = ("".join(random.choice(charset) for _ in range(length)) for i in range(num_strings))
    search_ids = {submit_search(s, hashbash, rainbow_table_id) for s in random_strings}

    poll_until_all_done(hashbash, search_ids)


def main():
    configure()
    search_for_strings()


if __name__ == "__main__":
    main()
