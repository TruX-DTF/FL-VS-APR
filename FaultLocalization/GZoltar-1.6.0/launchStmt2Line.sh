#!/bin/bash

source activate python27

python -u $1 --stmt-susps $2 --source-code-lines $3 --output $4