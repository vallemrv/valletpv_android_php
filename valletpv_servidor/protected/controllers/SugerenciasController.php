 <?php
/**
 * Description of SugerenciasController
 *
 * @author valle
 */
class SugerenciasController extends CController {
    //put your code here
    
    public function actionLsArticulos(){
         $pedidos = Yii::app()->db->createCommand()
                ->select('teclas.ID, teclas.Nombre')
                ->from('sugerencias')
                ->join("teclas","teclas.ID=sugerencias.IDTecla")
                ->group("teclas.ID")
                ->queryAll();
           echo json_encode($pedidos);
      
      }
    
    public function actionLs(){
        $id = $_POST["id"];
        $condicion = new CDbCriteria();
        $condicion->addCondition("IDTecla=$id");
        if(isset($_POST["str"])) $condicion->addSearchCondition("Sugerencia", $_POST["str"]);
        $sug = Sugerencias::model("Sugerencias")->findAll($condicion);
        $a = array();
        foreach ($sug as $s){
            $a[]=$s->attributes;
        }
        
        echo json_encode($a);
    }


    
    function actionAdd(){
        $idArt = $_POST["idArt"];
        $sug = $_POST["sug"];
        if($sug!=""){
            $sugerencia = new Sugerencias();
            $sugerencia->IDTecla = $idArt;
            $sugerencia->Sugerencia = $sug;
            $sugerencia->save();
        }
        echo "success";
    }
    
    function actionEdit(){
         $id = $_POST["id"]; $sug = $_POST["Sug"];
         $idArt = $_POST["IDArt"];$_POST["id"] = $idArt;
        
         $sugerencia = Sugerencias::model("Sugerencias")->findByPk($id);
         if(!$sugerencia) $sugerencia = new Sugerencias();
         $sugerencia->IDTecla = $idArt;
         $sugerencia->Sugerencia = $sug;
         $sugerencia->save();
         $this->actionLs();
    }
    function actionRm(){
         $id = $_POST["id"];$idArt = $_POST["IDArt"];$_POST["id"] = $idArt;
         $sugerencia = Sugerencias::model("Sugerencias")->deleteByPk($id);
         $this->actionLs();
        
    }
   
}
