#!/usr/bin/env python
import click
import random


@click.command()
@click.argument('num_strings', type=int)
@click.option('-c', '--charset', default='abcdefghijklmnopqrstuvwxyz')
@click.option('-p', '--pass_len', default=8)
def generate_strings(num_strings, charset, pass_len):
    for i in xrange(num_strings):
        print ''.join(random.choice(charset) for _ in xrange(pass_len))


def main():
    generate_strings()


if __name__ == '__main__':
    main()
