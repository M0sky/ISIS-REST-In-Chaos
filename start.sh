#!/bin/bash

PORT='8080'
MAQUINA1="10.0.2.15:$PORT"
MAQUINA2="10.0.2.4:$PORT"
MAQUINA3="10.0.2.5:$PORT"
ADDRESSES="$MAQUINA1 $MAQUINA2 $MAQUINA3"
ID=1

tput civis;

#MULTIDIFUSION SI O NO
echo "Multidifusion? Y->1 N->0"
read M

#CONFIGURAR MAQUINAS
curl -X GET -i "http://$MAQUINA1/trabajo2/rest/hola/configura?maquina1=$MAQUINA1&maquina2=$MAQUINA2&maquina3=$MAQUINA3"
curl -X GET -i "http://$MAQUINA2/trabajo2/rest/hola/configura?maquina1=$MAQUINA1&maquina2=$MAQUINA2&maquina3=$MAQUINA3"
curl -X GET -i "http://$MAQUINA3/trabajo2/rest/hola/configura?maquina1=$MAQUINA1&maquina2=$MAQUINA2&maquina3=$MAQUINA3"


#INICIAR
for ADDRESS in $ADDRESSES
do
	curl -X GET -i "http://$ADDRESS/trabajo2/rest/hola/start?ip_puerto=$ADDRESS&server_ID=$ID&multidifusion=$M" &
	ID=$(($ID+1))

	echo ""
	echo "Started-> $ADDRESS"
done; wait

tput cnorm;
