#!/usr/bin/env python
import click
import requests
from collections import defaultdict
from pprint import pprint

def mean(values):
    num = 0
    value_sum = 0.0

    for value in values:
        value_sum += value
        num += 1
        return value_sum / num


def generate_chain(password, charset, password_length, chain_length, hashbash_server, letter_count):
    search_url = '{server}/api/rainbow-chain/'.format(server=hashbash_server)
    params = {
        'plaintext': password,
        'chainLength': chain_length,
        'charset': charset,
        'passwordLength': password_length,
    }

    chain = requests.get(search_url, params=params).json()
    for index, link in enumerate(chain):
        for letter in link['plaintext']:
            letter_count[index][letter] += 1


@click.command()
@click.argument('password_file')
@click.option('-c', '--charset', default='abcdefghijklmnopqrstuvwxyz')
@click.option('-p', '--pass_len', default=8)
@click.option('-l', '--chain_len', default=10000)
@click.option('-h', '--hashbash', default='http://localhost:8080')
def generate_metrics(password_file, charset, pass_len, chain_len, hashbash):
    with open(password_file, 'r') as password_stream:
        letter_count = defaultdict(lambda: defaultdict(int))
        for password in password_stream:
            generate_chain(password.rstrip('\n'), charset, pass_len, chain_len, hashbash, letter_count)

        for index, letter_counts in letter_count.iteritems():
            occ_mean = mean(letter_counts.itervalues())
            print 'For index {} letters that are 10% off'.format(index)
            for letter, count in letter_counts.iteritems():
                if count > occ_mean * 1.1 or count < occ_mean * .9:
                    print '{}: {}'.format(letter, count / occ_mean)


def main():
    generate_metrics()


if __name__ == '__main__':
    main()
