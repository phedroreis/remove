package remove;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Um filtro para o objeto do tipo JFileChooser permitir selecionar apenas 
 * arquivos htm ou html e que não tenham sido gerados por este próprio programa.
 * 
 * @author "Pedro Reis"
 */
public class ZapFileFilter extends FileFilter {
    
    private final String editedFileExtension;
    
    
    public ZapFileFilter(final String editedFileExtension){
        
        super();  
        
        this.editedFileExtension = editedFileExtension;
     
    }

    @Override
    public boolean accept(File file){

        if (file.isDirectory()) return true;

        String filename = file.getName().toLowerCase();

        if (filename.endsWith(editedFileExtension)) return false;
 
        return (filename.endsWith(".html") || filename.endsWith(".htm"));
    }

    @Override
    public String getDescription(){

        return "Arquivos do ZAP";
    }
    
}
