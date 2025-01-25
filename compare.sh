#!/bin/bash

maquina2='10.0.2.4'
maquina3='10.0.2.5'

trap ctrl_c INT

function ctrl_c() {
	echo -e "\n\n[*]Exiting...\n"
	tput cnorm; exit 0
}
tput civis;

scp oscaruzhoo@$maquina2:/home/oscaruzhoo/3/fichero3.log /home/oscaruzhoo/3
scp oscaruzhoo@$maquina2:/home/oscaruzhoo/4/fichero4.log /home/oscaruzhoo/4
scp oscaruzhoo@$maquina3:/home/oscaruzhoo/5/fichero5.log /home/oscaruzhoo/5
scp oscaruzhoo@$maquina3:/home/oscaruzhoo/6/fichero6.log /home/oscaruzhoo/6

echo "diff f2 f1"
diff "/home/oscaruzhoo/2/fichero2.log" "/home/oscaruzhoo/1/fichero1.log"
echo "diff f2 f3"
diff "/home/oscaruzhoo/2/fichero2.log" "/home/oscaruzhoo/3/fichero3.log"
echo "diff f2 f4"
diff "/home/oscaruzhoo/2/fichero2.log" "/home/oscaruzhoo/4/fichero4.log"
echo "diff f2 f5"
diff "/home/oscaruzhoo/2/fichero2.log" "/home/oscaruzhoo/5/fichero5.log"
echo "diff f2 f6"
diff "/home/oscaruzhoo/2/fichero2.log" "/home/oscaruzhoo/6/fichero6.log"

tput cnorm;
