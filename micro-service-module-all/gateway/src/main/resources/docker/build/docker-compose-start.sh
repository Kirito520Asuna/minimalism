function pointOutUpdateJar() {
  dev="$1"  devname="开发" devname1="测试"
  if [[ "${dev}" =~ ^(test)$ ]]; then
      devname="本地" devname1="本地"
  elif [[ "${dev}" =~ ^(prod)$ ]]; then
      devname="生产" devname1="正式"
  else
      devname="开发" devname1="测试"
  fi
  #echo "devname: $devname"
  #echo "devname1: $devname1"
  read -r -p "确认需要部署的Jar已上传到${devname}/${devname1}服务器的？ [y/N] " input
  if [[ "${input,,}" =~ ^(yes|y)$ ]]
  then
      echo "================"
  else
      echo "请上传好文件后，再次运行该脚本！"
  	exit 0
  fi
}

function startDockerService() {
   dev="$1" service="$2"
   read -r -p "docker打包目录： " dockerpath
   cd ${dockerpath,,}
   cd build/$service/
   echo 当前目录
   $PWD
   echo 下线$service服务
   docker-compose down
   echo 移除$service镜像
   docker rmi ${service}_service-insurance
   echo 启动$service服务
   docker-compose up -d -e JAR_ENV=${dev}

   read -r -p "是否查看${service}服务日志？ [y/N] " input
   if [[ "${input,,}" =~ ^(yes|y)$ ]]
   then
       echo 查看$service服务日志
       docker logs -f -t $service
   else
       echo "未查看${service}服务日志"
   fi
}

read -r -p "启动服务名： " servicename
read -r -p "启动服务环境： " devname
service=${servicename,,} dev=${devname,,}
pointOutUpdateJar $dev
startDockerService $dev $service
