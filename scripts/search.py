#!/usr/bin/env python
import click
import requests
import md5


def search_for_password(password, hashbash_server, rainbow_table_id):
    m = md5.new()
    m.update(password)
    hash_value = m.hexdigest()
    print 'Searching for {} -> {}'.format(hash_value, password)

    search_url = '{server}/api/rainbow-table/{id}/search'.format(
        server=hashbash_server,
        id=rainbow_table_id,
        hash=hash_value,
    )

    res = requests.get(search_url, params={'hash': hash_value})
    return res.status_code == 200


@click.command()
@click.argument('rainbow_table_id', type=int)
@click.argument('password_file')
@click.option('-h', '--hashbash', default='http://localhost:8080')
def search_for_strings(rainbow_table_id, password_file, hashbash):
    with open(password_file, 'r') as password_stream:
        line_count = 0
        cracked_count = 0
        for line in password_stream:
            line_count += 1
            if search_for_password(line.rstrip('\n'), hashbash, rainbow_table_id):
                cracked_count += 1

        print 'Successfully cracked {} / {} passwords: {}%'.format(
            cracked_count,
            line_count,
            float(cracked_count) / float(line_count) * 100
        )


def main():
    search_for_strings()


if __name__ == '__main__':
    main()
