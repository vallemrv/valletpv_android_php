<?php
  /**
   * Clase creada solo para testear el servicio web
   *  aun no hace nada pero se le agregara algunos servicios.
   * @author vallesoft.es
   */
    class SiteController extends CController{
      
        public function actionIndex(){
           header("Location: /gestion");
        }
      
    }
?>
