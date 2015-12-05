package br.edu.ifpb.pod.syncronizer.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Classe responsável por gerenciar os arquivos de propriedades utilizados para
 * comparação de versões dos bancos, com o intuito de detectar mudanças.
 * 
 * @author douglasgabriel
 * @version 0.1
 * 
 */
public class PropertiesFileManager {

    private Properties prop;
    private Map<Integer, String> toUpdate = new HashMap<>();
    private String propFilePath;

    /**
     * @param propFilePath caminho do arquivo de propriedades que será utilizado
     * @throws IOException caso o arquivo não exista e não seja possível criá-lo
     * no caminho especificado.
     */    
    public PropertiesFileManager(String propFilePath) throws IOException {
        if (!Files.exists(Paths.get(propFilePath))){
            Files.createFile(Paths.get(propFilePath));
        }
        this.propFilePath = propFilePath;
        prop = new Properties();
        prop.load(Files.newInputStream(Paths.get(propFilePath)));
    }

    /**
     * Recarrega as informações do arquivo de propriedades, de forma que as
     * informações que não foram comitadas serão perdidas.
     */
    public void begin() {
        try{
            prop.load(Files.newInputStream(Paths.get(propFilePath)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Armazena a informação passada em um cache que só terá as informações
     * salvas no commit.
     * 
     * @param key chave da propriedade
     * @param md5 valor da propriedade
     * 
     */
    public void persist(int key, String md5) throws RemoteException {
        try {
            toUpdate.put(key, md5);
        } catch (Exception e) {
            throw new RemoteException("No current transaction");
        }
    }

    /**
     * Persiste as informações presentes no cache em definitivo no arquivo de
     * propriedades.
     */
    public void commit() throws RemoteException {
        try {
            for (int i : toUpdate.keySet()) {
                if (prop.get(""+i) != null)
                    prop.replace(i, toUpdate.get(i));
                prop.setProperty("" + i, toUpdate.get(i));
                prop.store(Files.newOutputStream(Paths.get(propFilePath)), null);
            }
        } catch (Exception e) {
            throw new RemoteException("No current transaction");
        }
    }

    public String getProperty (String key){
        return prop.getProperty(key);
    }
    
    /**
     * Apaga o cache de informações e recarrega o arquivo de propriedades.
     */
    public void rollback() {
        toUpdate = null;
        try{
            prop.load(Files.newInputStream(Paths.get(propFilePath)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
