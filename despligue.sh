#!/bin/bash

#USUARIO
USER="oscaruzhoo"

#MAQUINAS
MAQUINA1="10.0.2.15"
MAQUINA2="10.0.2.4"
MAQUINA3="10.0.2.5"
ADDRESSES="$MAQUINA1 $MAQUINA2 $MAQUINA3"

#PATH
TOMCAT="apache-tomcat-8.5.65"
DREMOTO="/home/$USER/Escritorio/Defensa"
DTOMCAT="/$DREMOTO/$TOMCAT.zip"
DCATALINA="/$DREMOTO/$TOMCAT/bin/catalina.sh"
DSHUTDOWN="/$DREMOTO/$TOMCAT/bin/shutdown.sh"


trap ctrl_c INT
function ctrl_c() {
	echo -e "\n\n[*]Exiting...\n"
	tput cnorm; exit 0
}
tput civis;

#ELECCION
echo "0 (START) 1 (SHUTDOWN)"
read A

#COMPARTIR CLAVES
if [ $A -eq 0 ];
then
	bash -c "sh shareKeys.sh"
fi

#COPIAR FICHEROS
for ADDRESS in $ADDRESSES
do
	#INICIAR
	if [ $A -eq 0 ];
	then
		ssh $USER@$ADDRESS "rm -rf $DREMOTO; mkdir $DREMOTO; exit"
		scp /home/$USER/Escritorio/$TOMCAT.zip $USER@$ADDRESS:$DREMOTO
		ssh $USER@$ADDRESS "unzip $DTOMCAT -d $DREMOTO/ > unzip.log; chmod 777 $DCATALINA";
		ssh $USER@$ADDRESS "sh $DCATALINA start"
		echo "$ADDRESS CONFIGURADA"
	else
	#APAGAR
		ssh $USER@$ADDRESS "sh $DSHUTDOWN start"
	fi
done;


if [ $A -eq 0 ];
then	#INICIAR
	bash -c "sh start.sh"
else	#BORRAR
	bash -c "sh borra.sh"
fi

tput cnorm;
