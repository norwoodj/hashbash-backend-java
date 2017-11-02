#!/usr/bin/env python
import click
import requests
import random

from jconfigure import configure
from time import sleep

from . import LOGGER
from .utils import get_hash_function_for_name, SearchStatus


def retrieve_rainbow_table(hashbash_server, rainbow_table_id):
    rainbow_table_url = f"{hashbash_server}/api/rainbow-table/{rainbow_table_id}"
    res = requests.get(rainbow_table_url)
    res.raise_for_status()

    return res.json()


def get_random_strings(rainbow_table, num_strings):
    charset = rainbow_table["characterSet"]
    length = rainbow_table["passwordLength"]

    return (
        "".join(random.choice(charset) for _ in range(length)) for _ in range(num_strings)
    )


def submit_search(password, hashbash_server, rainbow_table, hash_fn):
    hash_value = hash_fn(password.encode("utf-8")).hexdigest()
    LOGGER.info(f"Submitting search for password hash '{hash_value}', which should reverse to '{password}'")

    search_url = f"{hashbash_server}/api/rainbow-table/{rainbow_table['id']}/search"
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

            if search["status"] == SearchStatus.FOUND:
                done.append(s)
                found[search["hash"]] = search["password"]
            elif search["status"] == SearchStatus.NOT_FOUND:
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
@click.option("-h", "--hashbash", help="Specify the Hashbash Server to call (Default: https://hashbash.johnmalcolmnorwood.com)", default="https://hashbash.johnmalcolmnorwood.com")
@click.option("--poll/--no-poll", help="Specify whether to poll until all submitted searches have completed (Default: false)", default=False)
@click.argument("rainbow_table_id", type=int)
@click.argument("num_strings", type=int)
def search_for_strings(hashbash, poll, rainbow_table_id, num_strings):
    LOGGER.info(f"Submitting {num_strings} search strings for Rainbow Table {rainbow_table_id} to hashbash server {hashbash}")

    rainbow_table = retrieve_rainbow_table(hashbash, rainbow_table_id)
    LOGGER.info(f"Retrieved Rainbow Table {rainbow_table}")

    random_strings = get_random_strings(rainbow_table, num_strings)
    hash_fn = get_hash_function_for_name(rainbow_table["hashFunction"])
    search_ids = {submit_search(s, hashbash, rainbow_table, hash_fn) for s in random_strings}

    if not poll:
        LOGGER.info(f"--no-poll passed, not polling until searches complete")
        return

    poll_until_all_done(hashbash, search_ids)


def main():
    configure()
    search_for_strings()


if __name__ == "__main__":
    main()
