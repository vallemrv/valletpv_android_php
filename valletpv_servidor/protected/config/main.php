<?php
   return array(
       'name'=>'ValleTpv tpv de vallesoft.es',
       'charset'=>'utf-8',
       // user language (for Locale)
       'language'=>'es',
 
       //language for messages and views
       'sourceLanguage'=>'es',
       
       'import'=>array(
                'application.models.*',
                'application.vendor.valle.*',
             ),
       'components'=>array(
           'mailer' => array(
               'class' => 'application.extensions.mailer.EMailer',
               'pathViews' => 'application.views.email',
               'pathLayouts' => 'application.views.email.layouts'
             ), 
           'db'=>array(
                'class'=>'CDbConnection',
                'connectionString' => 'mysql:host=localhost;dbname=brasiliasl',
                'emulatePrepare' => true,
                'enableParamLogging' => true, 
                'username' => 'brasiliasl',
                'password' => 'nitrogeno',
                'charset' => 'utf8',
            ),
          'urlManager'=>array(
                 // the URL format. It must be either 'path' or 'get'.
                 // path: index.php/controller/action/attribute/value
                 // get: index.php?r=controller/action&attribute=value
                 'urlFormat'=>'path',
                 // show www.example.com/index.php/controller/action 
                 // or just www.example.com/controller/action
                 'showScriptName' => false,
                 'caseSensitive'=>false,
                 // rules to redirect a specific url to the controller you want
                 // see: http://www.yiiframework.com/doc/guide/topics.url
                 'rules'=>array(
                     // www.example.com/home instead of www.example.com/site/index
                     'inicio'=>'site/index'
                    
                  ),
            ),
           
       ),
       'params' => array(
                'email'      => 'info@elbrasilia.com',
                'dirPrint' => '/home/brasiliasl/valletpv/aux/',
                'url' => 'http://192.168.1.249/',
                'empresa'=>'Brasilia san lazaro',
                'dbname'=>'elbrasilia'
          )
       
   );
?>
