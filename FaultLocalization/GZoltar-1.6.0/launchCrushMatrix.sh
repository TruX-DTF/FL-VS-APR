#!/bin/bash

source activate python27

python -u $1 --formula $2 --matrix $3 --element-type 'Statement' --element-names $4 --total-defn tests  --output $5


