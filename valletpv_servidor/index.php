<?php
  // remove the following line when in production mode
defined('YII_DEBUG') or define('YII_DEBUG',true);
// include Yii bootstrap file
require_once('/opt/yii/yii.php');
// create application instance and run
$configFile=  dirname(__FILE__).'/protected/config/main.php';
Yii::createWebApplication($configFile)->run();

?>
