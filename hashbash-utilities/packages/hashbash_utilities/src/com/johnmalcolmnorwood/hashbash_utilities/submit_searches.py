#!/usr/bin/env python
from . import LOGGER
from jconfigure import configure

import click
import requests
import random
import md5


def submit_search(password, hashbash_server, rainbow_table_id):
    m = md5.new()
    m.update(password)
    hash_value = m.hexdigest()
    LOGGER.info(f"Submitting search for password hash '{hash_value}', which should reverse to '{string}'")

    search_url = "{server}/api/rainbow-table/{id}/search".format(server=hashbash_server, id=rainbow_table_id)
    res = requests.post(search_url, params={"hash": hash_value})


@click.command()
@click.option("-h", "--hashbash", default="https://hashbash.johnmalcolmnorwood.com")
@click.option("-c", "--charset", default="abcdefghijklmnopqrstuvwxyz")
@click.option("-l", "--length", default=8)
@click.argument("rainbow_table_id", type=int)
@click.argument("num_strings", type=int)
def search_for_strings(hashbash, charset, length, rainbow_table_id, num_strings):
    LOGGER.info(f"Submitting {num_strings} random strings of length {length} to hashbash server {hashbash}")

    for i in range(num_strings):
        password = "".join(random.choice(charset) for _ in range(length))
        submit_search(password, hashbash, rainbow_table_id)


def main():
    configure()
    search_for_strings()


if __name__ == "__main__":
    main()
