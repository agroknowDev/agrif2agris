package gr.agroknow.metadata.transformer.agrif2agris;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class Main
{
	
	public static void main(String[] args)
	{
		if ( args.length != 2 )
		{
			System.err.println( "Usage : java -jar agrif2agris.jar <INPUT_FOLDER> <OUTPUT_FOLDER>" ) ;
			System.exit( -1 ) ;
		}
		String inputFolder = args[0] ;
		String outputFolder = args[1] ;
		
		AGRIF2AGRIS transformer = null ;
		String fileName = "";
		File inputDirectory = new File( inputFolder ) ;
		String akifString = null ;
		int wrong = 0 ;
		for (File agrifFile: inputDirectory.listFiles() )
		{
			try
			{
				fileName = agrifFile.getName().substring(0, agrifFile.getName().length()-5 ) ;
				akifString = FileUtils.readFileToString( agrifFile ) ;
				transformer = new AGRIF2AGRIS( akifString ) ;
				FileUtils.writeStringToFile( new File( outputFolder + File.separator + fileName + ".xml" ) , transformer.toString() ) ;
			}
			catch( Exception e )
			{
				wrong++ ;
				System.err.println( "Wrong file : " + fileName ) ;
				e.printStackTrace() ;
				System.exit( -1 ) ;
			}
		}
		System.out.println( "#wrong : " + wrong ) ;
	}

}
