<?php
/**
 * Servicio web para la gestion de zonas del tpv
 * podemos dar altas, bajas, estado, etc...
 * @author vallesoft.es
 * @name /zonas/
 */
class ZonasController extends CController{
    
    /**
     * Funcion solo para saber si hay respuesta del servidor
     * @name /zonas/
     **/
    public function actionIndex(){
        echo 'success';
    }
    
    /**
     * Develve  la lista de zonas que hay en el tpv
     * @name /zonas/listado/
     * @return ARRAY JSON{ID, Nombre, tarifa}
     **/
    public function actionListado(){
        $con = Yii::app()->db;
        $comando = $con->createCommand("SELECT * FROM zonas");
        $reader = $comando->query();
        echo json_encode($reader->readAll());
    }
    
     /**
     * Develve la lista de mesas por zona que hay en el tpv
     * @name /zonas/mesasporzona/
     * @param POST id :id de la zona a mostrar.
     * @return ARRAY JSON{ID, Nombre}.
     **/
    public function actionMesasPorZona(){
        $con = Yii::app()->db;
        $id = $_POST["id"];
        $comando = $con->createCommand("SELECT mesas.ID, mesas.Nombre FROM mesas INNER JOIN mesaszona ON mesas.ID=mesaszona.IDMesa WHERE mesaszona.IDZona=$id");
        $reader = $comando->query();
        echo json_encode($reader->readAll());
    }
   
    /**
     * Agrea una zona nueva para el tpv
     * @name /zonas/addzona
     * @param POST nombre, tarifa, todo en minuscula.
     * @return TEXT success si es correcto o descripcion del error.
     */
    public function actionAddZona(){
      try{ 
        $con = Yii::app()->db;
        $nombre = $_POST["nombre"];
        $tarifa = $_POST["tarifa"];
        $sql = "INSERT INTO zonas (Nombre,tarifa) VALUES ('$nombre',".
                "'$tarifa')";
        $comando = $con->createCommand($sql);
        $comando->execute();
        Sincronizar::push($sql);
        echo 'success';
       }  catch(Exception $e){
           echo $e;
       }
    }
    
    
    /**
     * Borra una zona del tpv ya no se mostrara el rando en el tpv.
     * @name /zonas/remove
     * @param POST id :ID de la zona a borrar
     * @return TEXT success o la descripcion del error.
     */
    public function actionRemove(){
       try{ 
        $con = Yii::app()->db;
        $id = $_POST["id"];
        $sql = "DELETE zonas  WHERE ID=$id";
        $comando = $con->createCommand($sql);
        $comando->execute();
        Sincronizar::push($sql);
         echo 'success';
       }  catch(Exception $e){
           echo $e;
       }
    }
    
    /**
     * Agrega un array de ids a la tabla de zonaszona para
     * unir zonas a un rango determidando.
     * @name /zonas/addmesaszona
     * @param POST id :ID de la zona a modificar.
     * @param POST ids[id] un array que contenga uno o mas ids de zonas para agregar al rango.
     * @return TEXT success o la descripcion del error.
     */
    public function actionAddMesasZona(){
       try{ 
        $con = Yii::app()->db;
        $id = $_POST["id"];
        $ids = $_POST["ids"];
        foreach ($ids as $idmesa) {
          $sql = "INSERT mesaszona (IDZona, IDMesa) VALUES ($id, $idmesa)";
          $comando = $con->createCommand($sql);
          $comando->execute();    
          Sincronizar::push($sql);
        }
        
        echo 'success';
       }  catch(Exception $e){
           echo $e;
       }
    }
    
    /**
     * Elimina zonas de un rango o zona.
     * @name /zonas/removemesazona
     * @param POST id :ID de la zona a modificar.
     * @param POST ids[id] un array que contenga uno o mas ids de zonas para eliminar del rango elegido.
     * @return TEXT success o la descripcion del error.
     */
    public function actionRemoveMesaZona(){
       try{ 
        $con = Yii::app()->db;
        $id = $_POST["id"];
        $ids = $_POST["ids"];
        foreach ($ids as $idmesa) {
          $sql = "DELETE mesaszona WHERE IDZona=$id AND IDMesa=$idmesa";
          $comando = $con->createCommand($sql);
          $comando->execute();    
          Sincronizar::push($sql);
        }
        echo 'success';
       }  catch(Exception $e){
           echo $e;
       }
    }
    
     /**
     * Modifica una zona enviar todos los datos de la zona
     * los modificados y los no modificados.
     * @name /zonas/update
     * @param POST id, nombre, tarifa.
     * @return TEXT success o la descripcion del error.
     */
    
    public function actionUpdate(){
        try{
            $con = Yii::app()->db;
            $nombre=$_POST["nombre"];
            $tarifa=$_POST["tarifa"];
            $id= $_POST["id"];
            $sql = "UPDATE zonas SET Nombre='$nombre', tarifa=$tarifa WHERE ID=$id";
            $comando = $con->createCommand($sql);
            $comando->execute();
            Sincronizar::push($sql);
            echo 'success';
       }  catch(Exception $e){
           echo $e;
       }
    } 
    
}

?>
