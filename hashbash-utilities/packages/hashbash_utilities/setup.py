#!/usr/local/bin/python
from setuptools import setup, find_packages

setup(
    name="com.johnmalcolmnorwood.hashbash_utilities",
    version="17.0913.0-dev",
    package_dir={"": "src"},
    packages=find_packages("src"),
    zip_safe=False,
    namespace_packages=["com", "com.johnmalcolmnorwood"],
    entry_points={
        "console_scripts": [
            "submit_searches=com.johnmalcolmnorwood.hashbash_utilities.submit_searches:main",
        ],
    },
    install_requires=[
        "click",
        "jconfigure",
        "requests",
    ],
)
