/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.mateml.preprocessing;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author rafael
 */
public class FileList {
	
    public void ListaArquivos(File dirIn, ArrayList<File> filesIn){
        File[] files = dirIn.listFiles();
        
        if (files == null) {
        	System.out.println("Erro: diretorio nao contem arquivos. Metodo: ListaArquivos. - Classe: ListarArquivos.");
        	return;
        }
        for(int i=0;i<files.length;i++){
        	String ext = Treatment.getExtension(files[i]);
			if (!files[i].isDirectory() && ext.toLowerCase().equals("txt"))
                filesIn.add(files[i]);
            else if (files[i].isDirectory())
                ListaArquivos(files[i], filesIn);
        }
    }

    public boolean ListaArquivos(File dirIn, File dirOut, ArrayList<File> filesIn, ArrayList<File> filesOut, File dirBase){
        File[] files = dirIn.listFiles();
        
        for (int i=0;i<files.length;i++) {

        	if (files[i].isDirectory()) {
                File dirNameOut = new File(dirOut.toString() + files[i].toString().substring(dirBase.toString().length(), files[i].toString().length()));
                
                if(!dirNameOut.exists()){
                    boolean criou = dirNameOut.mkdir();
                    if(criou == false){
                        return false;
                    }
                }
                
                ListaArquivos(files[i], dirOut, filesIn, filesOut, dirBase);
            }
            if(!files[i].getName().endsWith("txt")){
                continue;
            }
            String fileName = files[i].toString();
            String fileOut = dirOut.toString() + fileName.substring(dirBase.toString().length(), fileName.length());
            filesIn.add(new File(fileName));
            filesOut.add(new File(fileOut));
        }
        return true;
    }
}
