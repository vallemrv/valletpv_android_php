<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of TestController
 *
 * @author valle
 */
class TestController extends CController{
    //put your code here
    
    public function actionPrint(){
        $rutFicheros = Yii::app()->params["dirPrint"].  uniqid();
          $nombreImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;
          ImprimirTicket::AbrirCajon($rutFicheros, $nombreImpresora);
          echo 'success';
    }
    
    
    public function actionTestMail(){
         $message = "hola manolo que pasa";
        // send mail
            mail("Manuel Rodriguez <manuelrodriguez@elbrasilia.com>","test de envio",$message,"From:sleep loco pitres <info@elbrasilia.com>");
        echo "pagf eco  de resouesta cañeraooo.";
    }
    
    
    public function actionTestMail2(){
       $id=$_GET["id"];
       $condicion = new CDbCriteria();
       $condicion->order = "t.ID DESC";
       $arqueo = Arqueos::model("Arqueos")->find($condicion);
       $usr = Usuarios::model()->find("ID=$id");
         if($usr){
              Mails::sendCierreCaja($usr, $arqueo->getDesgloseCierre());
         }
    }

      
    public function actionFindErr(){
        $infmesa = Yii::app()->db->createCommand()
                 ->select("UID")
                 ->from("pedidos")
                 ->limit(240, 0)
                 ->order("ID DESC")
                 ->queryAll();
           foreach ($infmesa as $inf){
                  $es = $inf["UID"];
                  $pedidos = new InfMesas();
                  $uid = $pedidos->find("UID = '$es'");
                  if (!$uid){
                      echo "UID infmesa $es <br/>";
                     // echo $uid->UID. "<br/>";
                  }
            }
    }
}
