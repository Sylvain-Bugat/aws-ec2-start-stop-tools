#!/usr/bin/env bash

typeset -r targetJarPrefix="aws-ec2-start-stop-tools-"
typeset -r targetJarExtension=".jar"

typeset -r execDir=$( dirname "$0" )
typeset -r currentExecutedJar=$( basename "$0" )
typeset targetExecutedJar="${currentExecutedJar}"

#change current directory
cd "${execDir}"

function checkLatestJar
{
	typeset jarList=$( ls -1v "${targetJarPrefix}"*"${targetJarExtension}" 2>/dev/null )
	if [[ $? -ne 0 ]]
	then
		echo "Cannot list jar file in directory ${PWD}" >&2
		return
	fi

	typeset lastestJar=$( tail -1 <<<"${jarList}"  )

	if [[ "${lastestJar}" != "${targetExecutedJar}" ]]
	then
		targetExecutedJar="${lastestJar}"
		echo "Change target jar to execute to ${targetExecutedJar}"
	fi

	for jarFound in ${jarList}
	do
		if [[ "${jarFound}" != "${currentExecutedJar}" && "${jarFound}" != "${targetExecutedJar}" ]]
		then
			rm "${jarFound}" 2>/dev/null
			if [[ $? -ne 0 ]]
			then
				echo "Cannot delete old jar file ${jarFound}" >&2
			else
				echo "Old jar file ${jarFound} deleted"
			fi
		fi
	done
}

checkLatestJar

echo "Executing: ${targetExecutedJar} ${@}"
exec java -jar "${targetExecutedJar}" "${@}"
