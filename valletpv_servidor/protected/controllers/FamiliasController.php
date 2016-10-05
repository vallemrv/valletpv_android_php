<?php
/**
 * Servicio web para la gestion de familias del tpv
 * podemos dar altas, bajas, estado, etc...
 * @author vallesoft.es
 * @name /familias/
 */
class FamiliasController extends CController{
    
    /**
     * Funcion solo para saber si hay respuesta del servidor
     **/
    ///la llamada es /familias/
    public function actionIndex(){
        echo 'success';
    }
    
    /**
     * Listado de familias.
     * <a href="test/familias/listado.html">Ejemplo de funcionamiento</a> 
     * @return JSON {ID, Nombre, EsTapa, TapasQueGenera, Receptor}
     **/
     /// la llamada /familias/listado
      public function actionListado(){
          $familias = new Familias;
          echo json_encode($familias->queryAll());
     }
    
     
   
    
    
    
    
}

?>
