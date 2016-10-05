<?php
/**
 * Servicio web para la gestion de receptores del tpv
 * podemos dar altas, bajas, estado, etc...
 * @author vallesoft.es
 * @name /receptores/
 **/
class ReceptoresController extends CController{
    
    /**
     * Funcion solo para saber si hay respuesta del servidor
     **/
    ///la llamada es /receptores/
    public function actionIndex(){
        echo 'success';
    }
    
    /**
     * Develve  la lista de los receptores que hay en el tpv
     * <a href="test/receptores/test.html">Ejemplo de funcionamiento</a> 
     * @return ARRAY JSON{ID, Nombre} o descripción del error.
     **/
    ///la llamada es /receptores/listado
    public function actionListado(){
          $receptor = new Receptores;
          echo json_encode($receptor->queryAll());
    }
    
    /**
     * Agrega un receptor a la base de datos.
     * <a href="test/receptores/test.html">Ejemplo de funcionamiento</a> 
     * @param POST nombre.
     * @return TEXT success si es correcto o descripcion del error.
     **/
     /// la llamada /receptores/add
    public function actionAdd(){
            $receptor = new Receptores;
            $receptor->Nombre = $_POST["nombre"];
            $receptor->save();
            echo 'success';
    }
    
    /**
     * Elimina un receptor de la base de datos. No se puede borrar si ya tiene familias asignada.
     * <a href="test/receptores/test.html">Ejemplo de funcionamiento</a> 
     * @param POST id .
     * @return TEXT success si es correcto o descripcion del error.
     **/
     /// la llamada /receptores/remove
     public function actionRemove(){
            $receptor = new Receptores;
            $receptor->deleteByPk($_POST["id"]);
            echo 'success';
     }
    
    /**
     * Cambia el nombre de  un receptor de la base de datos.
     * <a href="test/receptores/test.html">Ejemplo de funcionamiento</a> 
     * @param POST {id, nombre} 
     * @return TEXT success si es correcto o descripcion del error.
     **/
     /// la llamada /receptores/update
    public function actionUpdate(){
            $receptor = new Receptores;
            $receptor->findByPk($_POST["id"]);
            $receptor->Nombre = $_POST["nombre"];
            $receptor->update();
            echo 'success';
     }
}

?>
