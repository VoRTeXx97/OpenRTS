package model;

import geometry.Point2D;
import java.io.File;
import java.util.ArrayList;
import model.map.Map;
import model.army.ArmyManager;
import model.lighting.SunLight;
import ressources.definitions.BuilderLibrary;
import model.map.editor.MapToolManager;
import model.map.MapFactory;
import model.map.parcel.ParcelManager;
import ressources.definitions.DefParser;
import tools.LogUtil;

public class Model {
    static final String CONFIG_PATH = "assets/data";
    static final double UPDATE_DELAY = 1000;
    
    public Map map;
    public SunLight sunLight;
    public ArmyManager armyManager;
    
    public Commander commander;
    public Reporter reporter;
    public MapToolManager editor;
    public ParcelManager parcelManager;
    

    public BuilderLibrary lib;
    DefParser parser;
    File confFile;
    long lastModified = 0;
    double nextUpdate = 0;
    
    public Model() {
        lib = new BuilderLibrary();
        parser = new DefParser(lib);

        ArrayList<File> files = getFiles(CONFIG_PATH);
        while(!files.isEmpty()){
            ArrayList<File> toAdd = new ArrayList<>();
            for(File f : files)
                if(f.isFile())
                    parser.addFile(f);
                else if(f.isDirectory())
                    toAdd.addAll(getFiles(f.getAbsolutePath()));
            files.clear();
            files.addAll(toAdd);
        }
        parser.readFile();
        
        this.map = MapFactory.getNew(128, 128, lib);
        sunLight = new SunLight();
        parcelManager = new ParcelManager(map);
        lib.map = map;

        armyManager = new ArmyManager();
        lib.am = armyManager;
        
        commander = new Commander(armyManager, map);
        editor = new MapToolManager(map, parcelManager, lib);
//        armyManager.createTestArmy(lib);
    }
    
    private ArrayList<File> getFiles(String folderPath){
        ArrayList<File> res = new ArrayList<>();
        File folder = new File(folderPath);
        for(File f : folder.listFiles())
            res.add(f);
        return res;
    }
    
    public void updateConfigs() {
        if(System.currentTimeMillis()>nextUpdate){
            nextUpdate = System.currentTimeMillis()+UPDATE_DELAY;
            parser.readFile();
        }
    }
}
