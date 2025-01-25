#!/bin/bash

maquina2='10.0.2.4'
maquina3='10.0.2.5'

path3="/home/oscaruzhoo/3"
path4="/home/oscaruzhoo/4"
path5="/home/oscaruzhoo/5"
path6="/home/oscaruzhoo/6"

#LIMPIAMOS MAQUINA 1
rm "/home/oscaruzhoo/1/fichero1.log"
rm "/home/oscaruzhoo/2/fichero2.log"
rm "/home/oscaruzhoo/3/fichero3.log"
rm "/home/oscaruzhoo/4/fichero4.log"
rm "/home/oscaruzhoo/5/fichero5.log"
rm "/home/oscaruzhoo/6/fichero6.log"

#LIMPIAMOS MAQUINAS 2 Y 3
ssh $maquina2 "rm $path3/*; rmdir $path3;"
ssh $maquina2 "rm $path4/*; rmdir $path4;"
ssh $maquina3 "rm $path5/*; rmdir $path5;"
ssh $maquina3 "rm $path6/*; rmdir $path6;"
