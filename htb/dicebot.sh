#!/bin/bash

mkfifo in out
nc 154.57.164.66 32010 <in >out &
exec 3> in 4< out

echo 1 >&3
declare -i max; max=0
declare -i player; player=0
declare -i cur; cur=0
declare -i p; p=1
while read -r line; do
	echo $line
	[[ $line =~ ^Player ]] && {
		cur=$(awk '{
			sum = 0
			for (i = 3; i <= NF; i++) {
				sum += $i
			}
			print sum
		}' <<< "$line")
		[[ $cur -ge $max ]] && {
			max=$cur
			player=$p
		}
		p+=1
	}
	[[ $line =~ ^Who ]] && [[ $player -gt 0 ]] && {
		echo $player >&3
		max=0
		p=1
	}
done <&4


rm in out
