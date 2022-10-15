package remove;

/******************************************************************************
 * A finalidade desta aplicacao eh remover de arquivos de entrada (no formato 
 * HTML) o conteudo (escopo) de DIVs que tenham o atributo 
 * aria-label="Lista de conversas"
 * 
 * Um conjunto de arquivos HTML pode ser selecionado atraves da interface e o 
 * programa deverah produzir copias destes arquivos excluindo destes as DIVs do
 * tipo acima mencionado.
 * 
 * 
 * 
 * IMPORTANTE: Esta aplicacao pode nao funcionar corretamente se os arquivos de 
 * entrada nao forem pre-processados pelo aplicativo "beautifie".
 * 
 * @author "Pedro Reis"
 *****************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class Remove extends JFrame{
       
    private static final Pattern PARENT_DIV_REGEXP =
        Pattern.compile("<div aria-label=\"Lista de conversas\".+?>");
   
    private static final Pattern OPEN_NESTED_DIV_REGEXP =
        Pattern.compile("<div.+?>");
   
    private static final Pattern CLOSE_DIV_REGEXP =
        Pattern.compile("<\\/div>");
    
    private static final String EDITED_FILE_EXTENSION = ".acervo.html";
    
    private final JFileChooser jFileChooser;
    
    private final JProgressBar jProgressBar;
    
    /*[00]----------------------------------------------------------------------
   
    --------------------------------------------------------------------------*/ 
    /**
     * Construtor da classe
     */
    private Remove() {
        
        super("Remover Listas de Conversas");//Chama construtor da super classe 
         
        /*
        Configura um objeto JFileChooser para selecionar os arquivos de
        entrada.
        */
        jFileChooser = new JFileChooser(".");
        
        jFileChooser.setDialogTitle("Selecione os arquivos do ZAP");
        
        //Cria filtro para aceitar apenas selecao de certos tipos de arquivos
	jFileChooser.setAcceptAllFileFilterUsed(false);
  	jFileChooser.
                addChoosableFileFilter(new ZapFileFilter(EDITED_FILE_EXTENSION));
        
        //Permitirah selecionar multiplos arquivos para processamento
        jFileChooser.setMultiSelectionEnabled(true);
        
        /*
        Configura um objeto JProgressBar para ser a barra de progresso na
        interface da aplicacao
        */
        jProgressBar = new JProgressBar();
        
        jProgressBar.setStringPainted(true);//Permite escrever na barra de prog.  
        
        jProgressBar.setMinimum(0);//Irah de 0 ate num. de arqs. selecionados
        
        /*
        Configura a janela principal do programa.
        */
        setSize(450, 150);//450 largura x 150 altura
        
        //Encerra aplicacao se fechar janela principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        add(jProgressBar);//Adiciona barra de progresso na janela principal
        
        setVisible(true);//Abre na tela a janela principal
         
    }//construtor
    
    /*[01]----------------------------------------------------------------------
   
    --------------------------------------------------------------------------*/ 
    /**
     * Cria uma copia do arquivo excluindo todo o conteudo das DIVs com atributo 
     * aria-label="Lista de conversas".
     * 
     * O arquivo gerado tera extensao ".acervo.html"
     * 
     * @param pathName O caminho absoluto (completo) do arquivo a ser processado
     * 
     * @throws Lanca IOException em caso de erro de IO
     */ 
    private void createEditedZapFile(String pathName) throws IOException {
      
        File out = new File(pathName + EDITED_FILE_EXTENSION);//Arquivo de saida

        int count = 0; //Contador de DIVs abertas

        //Objeto para ler linha por linha do arq. de entrada
        BufferedReader htmlReader =
            new BufferedReader(
                new FileReader(pathName, StandardCharsets.UTF_8),
                65536
            );

        Matcher regexpFinder;//Obj. para localizar regexps em uma String

        String line;//Recebe cada linha lida do arq. de entrada
        
        /*
        Um buffer para armazenar temporariamente o que serah gravado no 
        arquivo de saida
        */
        StringBuilder buffer = new StringBuilder(65536);//Buffer de saida

        /*
        O loop le o arquivo de entrada linha a linha, mas copia para o buffer de
        saida apenas as linhas que nao pertencam ao escopo de uma DIV com 
        atributo aria-label="Lista de conversas"
        */
        while ((line = htmlReader.readLine()) != null) {

            if (count == 0) {

                regexpFinder = PARENT_DIV_REGEXP.matcher(line);

                if (regexpFinder.find())

                    count = 1;//Abriu o DIV pai

                else

                    buffer.append(line).append("\n");//Grava no buffer
            }
            else {

                regexpFinder = OPEN_NESTED_DIV_REGEXP.matcher(line);              
                while (regexpFinder.find()) count++;

                regexpFinder = CLOSE_DIV_REGEXP.matcher(line);
                while (regexpFinder.find()) count--;

            }//if-else

        }//while
        
        /*
        Grava o conteudo do buffer no arquivo de saida
        */
        try (FileWriter fw = new FileWriter(out, StandardCharsets.UTF_8)) {
            
            fw.write(buffer.toString());
            
        }
        
    }//createEditedZapFile()
    
 
    /*[02]----------------------------------------------------------------------
   
    --------------------------------------------------------------------------*/
    /**
     * @param args Parametros de linha de comando (sem funcao no programa)
     */
    public static void main(String[] args) {
        
        Remove remove = new Remove();//Cria um objeto desta classe
        
        JFileChooser jfc = remove.jFileChooser;//Objeto seletor de arquivos
        
        JProgressBar jpb = remove.jProgressBar;//Objeto de barra de progresso
        
        /*
        Processa os arquivos selecionados se algum arquivo for selecionado
        */
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            
            //Array com os arqs. selecionados
            File[] selectedFiles = jfc.getSelectedFiles();
                   
            jpb.setMaximum(selectedFiles.length);//Valor max. da barra de progr.
            
            int indexFile = 0;//Contador de arquivos processados
            
            jpb.setValue(indexFile);//Inicializa barra de progresso em 0%
            
            //Loop for processa cada aquivo selecionado
            for (File selectedFile : selectedFiles) {
 
                jpb.setString("Processando " + selectedFile.getName());
                
                try {
                    
                    //Cria copia do arquivo sem as DIVs de listas de conversas
                    remove.createEditedZapFile(selectedFile.getAbsolutePath());
                
                }
                //Captura erro de IO se ocorrer
                catch (IOException e) {
                    
                    //Exibe janela com mensagem de erro
                    JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(), 
                        "Erro de IO",
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    System.exit(1);//Aborta programa em caso de erro de IO
                    
                }
                
                /*
                Incrementa contador de arqs. processados e atualiza barra de 
                progresso
                */
                jpb.setValue(++indexFile);
           
            }//Fim do loop for
            
            jpb.setString("Feito!");//Mensagem final na barra de progresso
            
        }//Fim do if
       
    }//main()
    
  
}//classe Remove