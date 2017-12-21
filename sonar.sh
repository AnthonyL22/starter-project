#!/usr/bin/env bash

echo "Analyzing Sonar Log(s) for Issues $(date)"

declare file="/local/mnt/jenkins/workspace/${JOB_NAME}/.sonar/issues-report/issues-report.html"
declare regex="^.*?big\ worst.*?$"

declare file_content=$( cat "${file}" )
if [[ " $file_content " =~ $regex ]]
    then
        echo "---------------------------------------------------------------------------------"
        echo "***** New Sonar Issues Found.  Please check Sonar report for details. *****"
        echo "More Info: $JENKINS_URL/job/${JOB_NAME}/ws/.sonar/issues-report/issues-report.html"
        echo "---------------------------------------------------------------------------------"
        exit 1
    else
    	echo "--------------------------------------------------"
        echo "***** No Sonar Issues Found in Current Report *****"
        echo "More Info: $JENKINS_URL/job/${JOB_NAME}/ws/.sonar/issues-report/issues-report.html"
        echo "--------------------------------------------------"
fi

exit
