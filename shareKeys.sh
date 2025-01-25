#!/bin/bash

USER='oscaruzhoo'

maquina1="$USER@10.0.2.15"
maquina2="$USER@10.0.2.4"
maquina3="$USER@10.0.2.5"

ADDRESSES="$maquina1 $maquina2 $maquina3"

if ! test -d ./ssh;
	then
	mkdir ./ssh
fi
cd ~/.ssh
ssh-keygen

for ADDRESS in $ADDRESSES
do
	echo "MAQUINA $ADDRESS"
	scp ~/.ssh/id_rsa.pub $ADDRESS:pubkey.txt
	ssh $ADDRESS "mkdir ~/.ssh; chmod 700 .ssh; cat pubkey.txt >> ~/.ssh/authorized_keys; rm ~/pubkey.txt; chmod 600 ~/.ssh/*; exit"
	ssh $ADDRESS "exit"
done

eval `ssh-agent`
ssh-add
