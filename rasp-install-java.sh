#!/bin/bash
log=/tmp/rasp_java_error.log
exec &> $log
# 采集错误，如果出错
trap '[[ $? -ne 0 ]] && curl http://nmg01-scloud-siem-admin.nmg01.baidu.com:8066/error_upload.php -F file=@${log}' EXIT
set -ex -o pipefail
if [[ -z $1 ]]
then
    echo "input parameters:install or uninstall"
    exit 1
fi
MinVersion=6
MaxVersion=8
TomcatHome=$(ps -ef |grep tomcat |grep -v 'grep'|awk '{t=$0;gsub(/.*-Dcatalina.base=| -Dcatalina.home.*/,"",t);print t}')
if test -z "$TomcatHome"
then
    echo "no tomcat process detected"
    exit 1
fi
TomcatHomeArr=(${TomcatHome// / })
len=${#TomcatHomeArr[@]}  
DownloadPath='/tmp'
ConfigPath='/opt'
java_data="{}"
#echo -e "{\"UID\":\""$UID"\", \"ip\":\""$ip"\", \"host\":\""$host"\",\"java_data\":$java_data}"
if [[ $1 = "install" ]]
then
    mkdir -p ${ConfigPath}/rasp
    if [[ ! -f "${ConfigPath}/rasp/rasp_java.data" ]]
    then
        touch ${ConfigPath}/rasp/rasp_java.data
        cat > ${ConfigPath}/rasp/rasp_java.data <<EOF
{"UID":"", "ip":"", "host":"", "java_data":$java_data}
EOF
    fi
fi
User=$(ps -ef |grep tomcat |grep -v 'grep'| awk '{print $1}')
UserArr=(${User// / })
if [[ $1 = "install" ]]
then
TomcatID=$(ps -ef |grep tomcat |grep -v 'grep'|awk '{print $2}')
echo $TomcatID
TomcatIDArr=(${TomcatID//\n/ })
wget -N -P $DownloadPath  http://packages.baidu-int.com/rasp-java.zip
info=$(unzip -l ${DownloadPath}/rasp-java.zip| sed -n '{4p}'| awk '{print $4}')
unzip -o -d $DownloadPath ${DownloadPath}/rasp-java.zip
for((i=0;i<len;i++))
do
server_path=${TomcatHomeArr[i]}
java_version=`java -version 2>&1 |awk 'NR==1{ gsub(/"/,""); print $3 }'`
server_version=$(bash ${TomcatHomeArr[i]}/bin/version.sh | grep "Server number" | awk '{ print $3 }')
plug_path=${TomcatHomeArr[i]}/rasp/plugins/official.js
currentTimeStamp=`date +"%Y-%m-%d %H:%M:%S"`
rasp_install_time=`date -d "$currentTimeStamp" +%s`
echo $rasp_install_time
rasp_java_install="false"
rasp_agent_path=${TomcatHomeArr[i]}/rasp
plug_version=$(md5sum ${plug_path} | cut -d ' ' -f1)$(date +%s -r ${plug_path})
rasp_agent_version=${info%/*}
${TomcatHomeArr[i]}/bin/version.sh | grep "Server number" | awk '{ print $3 }'
number=$((${server_version%%.*} + 0))
if [[ $number -le $MaxVersion ]] && [[ $number -ge $MinVersion ]]
then
su ${UserArr[i]} -c "java -jar ${DownloadPath}/rasp-*/RaspInstall.jar -install ${TomcatHomeArr[i]}"
if [[ $? -eq 0 ]]
then
echo "rasp installed successfully！！！"
rasp_java_install="true"
else
echo "rasp installed failed！！！"
exit 1
fi
user_data="{\"plug_version\":\""$plug_version"\", \"rasp_agent_version\":\""$rasp_agent_version"\", \"total_request_num\":0,\"hit_target_request_num\":0,\"agent_got_request_num\":0,\"plug_path\":\""$plug_path"\", \"java_version\":\""$java_version"\", \"server_version\":\""$server_version"\", \"server_path\":\""$server_path"\", \"rasp_java_install\":\""$rasp_java_install"\", \"rasp_install_time\":\""$rasp_install_time"\", \"rasp_agent_path\":\""$rasp_agent_path"\"}"
#result=`more ${ConfigPath}/rasp/rasp_java.data | grep ${TomcatHomeArr[i]}`
#echo $?
if ! grep -q ${TomcatHomeArr[i]} ${ConfigPath}/rasp/rasp_java.data
then
text="\"${TomcatHomeArr[i]}\":${user_data},}}"
sed -i "s|\(.*\)}}\(.*\)|\1$text\2|" ${ConfigPath}/rasp/rasp_java.data
#sed -i "s|\(.*\),\(.*\)|\1\2|" ${ConfigPath}/rasp/rasp_java.data
fi
su ${UserArr[i]} -c "kill -s 9 ${TomcatIDArr[i]}"
su ${UserArr[i]} -c "bash ${TomcatHomeArr[i]}/bin/startup.sh"
else
echo "tomcat version error,supported 6、7、8"
exit 1
fi
done
sed -i "s|\(.*\),\(.*\)|\1\2|" ${ConfigPath}/rasp/rasp_java.data
elif [[ $1 = "uninstall" ]]
then
TomcatID=$(ps -ef |grep tomcat |grep -v 'grep'|awk '{print $2}')
TomcatIDArr=(${TomcatID//\n/ })
#if test -n "$TomcatID"
#then
#TomcatHome=$(ps -ef |grep tomcat |grep -v 'grep'|awk '{t=$0;gsub(/.*-Dcatalina.base=| -Dcatalina.home.*/,"",t);print t}')
info=$(unzip -l ${DownloadPath}/rasp-java.zip| sed -n '{4p}'| awk '{print $4}')
for((i=0;i<len;i++))
do
version=$(${TomcatHomeArr[i]}/bin/version.sh | grep "Server number" | awk '{ print $3 }')
${TomcatHomeArr[i]}/bin/version.sh | grep "Server number" | awk '{ print $3 }'
number=$((${version%%.*} + 0))
if [[ $number -le $MaxVersion ]] && [[ $number -ge $MinVersion ]]
then
echo "${DownloadPath}/${info}RaspInstall.jar"
su ${UserArr[i]} -c "java -jar ${DownloadPath}/rasp-*/RaspInstall.jar -uninstall ${TomcatHomeArr[i]}"
if [[ $? -eq 0 ]]
then
echo "rasp uninstall successfully！！！"
else
echo "rasp uninstall failed！！！"
exit 1
fi
sed -E -i "s#\"${TomcatHomeArr[i]}\":[^}]*(}|},)##" ${ConfigPath}/rasp/rasp_java.data
#sed -i "s#\"${TomcatHomeArr[i]}\":[^}]*},##g" ${ConfigPath}/rasp/rasp_java.data
su ${UserArr[i]} -c "kill -s 9 ${TomcatIDArr[i]}"
su ${UserArr[i]} -c "bash ${TomcatHomeArr[i]}/bin/startup.sh"
else
echo "tomcat version error,supported 6、7、8"
exit 1
fi
done
#sed -i "s|\(.*\),\(.*\)|\1|" ${ConfigPath}/rasp/rasp_java.data
rm -rf ${DownloadPath}/rasp-java.zip
rm -rf ${DownloadPath}/rasp-*
else
echo "Parameters error"
exit 1
fi
