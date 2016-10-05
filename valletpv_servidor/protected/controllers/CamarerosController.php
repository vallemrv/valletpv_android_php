<?php


/**
 * Clase encargade de la gestion de camareros en la base de datos.
 * @author vallesoft.es
 * @name /camareros/
 */

class CamarerosController extends CController {
    
    /**
     * Funcion solo para saber si hay respuesta del servidor
     **/
    ///la llamada es /receptores/
    public function actionIndex(){
        echo 'success';
    }
    
    /**
     * Develve  la lista de todos los camareros activos del tpv.
     * Esto se utiliza para mostrar camareros activos en el punto de venta o telecomandas.
     * <a href="test/camareros/listado.html">Ejemplo de funcionamiento</a> 
     * @param POST {numIni, numMax} o nada para el listado completo.
     * @return ARRAY JSON{ID, Nombre, Apellidos, pass, NomFoto} o descripción del error.
     **/
    ///la llamada es /camareros/listado
    public function actionListado(){
        $condicion = new CDbCriteria();
        $condicion->addCondition("Activo=1");
        $lstObj = array();
        foreach (Camareros::model("Camareros")->findAll($condicion) as $obj){
            $lstObj[] = $obj->attributes;   
         }
         
        echo json_encode($lstObj);
    }
    
    public function actionTodos(){
        $condicion = new CDbCriteria();
         
        $lstObj = array();
        foreach (Camareros::model("Camareros")->findAll($condicion) as $obj){
            $lstObj[] = $obj->attributes;   
         }
         
        echo json_encode($lstObj);
    }
    
    
    /**
     * Agrega un camarero a la base de datos.
     * <a href="test/camareros/addreceptor.html">Ejemplo de funcionamiento</a> 
     * @param POST nombre, apellidos.
     * @return TEXT success si es correcto o descripcion del error.
     **/
     /// la llamada /camareros/addcam
    public function actionAdd(){
        try{
           $reg = Camareros::model("Camareros")->findByPk($_POST["ID"]);
           if(!$reg)  $reg = new Camareros();
            
            $reg->Nombre = $_POST["Nombre"];
            $reg->Apellidos = $_POST["Apellidos"];
            $reg->Pass = isset($_POST["Pass"]) ? $_POST["Pass"] : "";
            $reg->Email = isset($_POST["Email"]) ? $_POST["Email"] : "";
            $reg->Activo = 1;
            $reg->save();
            echo $reg->ID;
           }catch (Exception $e){
              echo $e;
           }
      }
      
      public function actionActivar(){
        try{
           $reg = Camareros::model("Camareros")->findByPk($_POST["ID"]);
           if($reg){
            $reg->Activo = $_POST["Activo"]=="true" ? 1 : 0;
            $reg->save();
            echo "success";
           }
           }catch (Exception $e){
              echo $e;
           }
      }
    
    /**
     * Elimina camareros de la base de datos. No se puede elemiminar un camareros si tiene ventas.
     * solo se puede desactivar.
     * <a href="test/camareros/remove.html">Ejemplo de funcionamiento</a> 
     * @param POST id.
     * @return TEXT success si es correcto o descripcion del error.
     **/
     /// la llamada /camareros/remove
    public function actionRm(){
        try{
            $reg = Camareros::model("Camareros")->findByPk($_POST["ID"]);
            $reg->delete();
            echo 'success';
         }  catch (Exception $e){
             echo $e;
         }
    }
    
   
   
    /**
     * Cambia la contraseña del camarero.
     * <a href="test/camareros/uppass.html">Ejemplo de funcionamiento</a> 
     * @param POST {id, pass} 
     * @return TEXT success si es correcto o descripcion del error.
     **/
     /// la llamada /camareros/uppass
    public function actionChPass(){
        try{
            $reg = Camareros::model("Camareros")->findByPk($_POST["ID"]);
            $reg->Pass = $_POST["Pass"];
            $reg->update();
            echo 'success';
         }  catch (Exception $e){
             echo $e;
         }
    }
    
   
    
  
}

?>
