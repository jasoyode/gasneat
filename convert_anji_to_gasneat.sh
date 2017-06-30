#!/bin/bash

anji_chromosome=$1
gasneat_chromosome=$2

#touch $gasneat_chromosome


cat $anji_chromosome | grep "neuron" | sed "s/neuron/gasneat-neuron/" | sed "s/\// firing-threshold=\"0.0\" gas-type=\"0\" syn-gas-type=\"0\" x-pos=\"0\" y-pos=\"0\" emission-rate=\"0\" gas-emitter=\"false\" emission-radius=\"0\" receptor-type=\"0\"\//"

cat $anji_chromosome | grep "connection" | sed "s/connection/gasneat-connection/"
echo "</chromosome>"