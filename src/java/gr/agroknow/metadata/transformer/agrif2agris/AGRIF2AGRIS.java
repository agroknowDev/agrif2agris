package gr.agroknow.metadata.transformer.agrif2agris;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AGRIF2AGRIS
{
	private JSONObject agrif ;
	private StringBuilder agris ;
	private StringBuilder head ;
	private StringBuilder dctitle ;
	private StringBuilder dccreator ;
	private StringBuilder dcpublisher ;
	private StringBuilder dcdate ;
	private StringBuilder dcsubject ;
	private StringBuilder dcdescription ;
	private StringBuilder dcidentifier ;
	private StringBuilder dctype ;
	private StringBuilder dcformat ;
	private StringBuilder dclanguage ;
	private StringBuilder dcrelation ;
	private StringBuilder agsavailability ;
	private StringBuilder dcsource ;
	private StringBuilder dccoverage ;
	private StringBuilder dcrights ;
	private StringBuilder agscitation ;
	private StringBuilder tail ;
	
	private JSONArray expressions ;
	private JSONObject controlled ;
	private JSONObject languageBlocks ;
	
	public AGRIF2AGRIS( String akifString )
	{		
		agrif = (JSONObject) JSONValue.parse( akifString ) ;
		expressions = (JSONArray)agrif.get( "expressions" ) ;
		controlled = (JSONObject)agrif.get( "controlled" ) ;
		init() ;
		header() ;
		languageBlocks() ;
		creator() ;
		controlled() ;
		expressions() ;
		relation() ;
		rights() ;
		footer() ;
	}
	
	private void languageBlocks()
	{
		if ( agrif.containsKey( "languageBlocks" ) )
		{
			JSONObject lblocks = (JSONObject) agrif.get( "languageBlocks" ) ;
			for (Object lkey: lblocks.keySet() )
			{
				String lang = (String)lkey ;
				JSONObject lblock = (JSONObject) lblocks.get( lkey ) ;
				if ( lblock.containsKey( "title" ) )
				{
					String title = (String) lblock.get( "title" ) ;
					dctitle.append( "    <dc:title xml:lang=\"" + lang + "\">" ) ;
					dctitle.append( title + "</dc:title>\n" ) ;
				}
				if ( lblock.containsKey( "abstract" ) )
				{
					String abstrct = (String) lblock.get( "abstract" ) ;
					dcdescription.append( "    <dc:description>\n" ) ;
					dcdescription.append( "      <dcterms:abstract xml:lang=\"" + lang + "\">" + abstrct + "</dcterms:abstract>\n" ) ;
					dcdescription.append( "    </dc:description>\n" ) ;
				}
				if ( lblock.containsKey( "note" ) )
				{
					String note = (String) lblock.get( "note" ) ;
					dcdescription.append( "    <dc:description>\n" ) ;
					dcdescription.append( "      <ags:descriptionNotes xml:lang=\"" + lang + "\">" + note + "</ags:descriptionNotes>\n" ) ;
					dcdescription.append( "    </dc:description>\n" ) ;
				}
				if ( lblock.containsKey( "alternativeTitle" ) )
				{
					JSONArray altTitles = (JSONArray) lblock.get( "alternativeTitle" ) ;
					for ( Object altTitle: altTitles )
					{
						dctitle.append( "    <dc:title>\n" ) ;
						dctitle.append( "      <dcterms:alternative xml:lang=\"" + lang + "\">" + (String)altTitle + "<dcterms:alternative>\n" ) ;
						dctitle.append( "    </dc:title>\n" ) ;
					}
				}
				if ( lblock.containsKey( "titleSupplemental" ) )
				{
					JSONArray titleSups = (JSONArray) lblock.get( "titleSupplemental" ) ;
					for ( Object titleSup: titleSups )
					{
						// agris.append( "" + (String)titleSup ) ;
						dctitle.append( "    <dc:title>\n" ) ;
						dctitle.append( "      <ags:titleSupplement xml:lang=\"" + lang + "\">" + (String)titleSup + "<ags:titleSupplement>\n" ) ;
						dctitle.append( "    </dc:title>\n" ) ;
					}
				}
				if ( lblock.containsKey( "keyword" ) )
				{
					JSONArray keywords = (JSONArray) lblock.get( "keyword" ) ;
					for ( Object keyword: keywords )
					{
						dcsubject.append( "    <dc:subject xml:lang=\"" + lang + "\">" ) ;
						dcsubject.append( (String)keyword ) ;
						dcsubject.append( "</dc:subject>\n" ) ;
					}
				}
			}
		}
	}
	
	private void controlled() 
	{
		for ( Object key: controlled.keySet() )
		{
			if ( "type".equals( key ) )
			{
				JSONArray types = (JSONArray) controlled.get( key ) ;
				for ( Object otype: types )
				{
					JSONObject type = (JSONObject)otype ;
					if ( type.containsKey( "value" ) )
					{
						dctype.append( "      <dc:type" ) ;
						if ( type.containsKey( "source" ) )
						{
							// dctype.append( " scheme=\"" ) ;
							// dctype.append( (String)type.get( "source" ) + "\"" ) ;
							// <dc:type xml:lang="en" scheme="dcterms:DCMIType">Reports</dc:type>
						}
						dctype.append( ">" + (String)type.get( "value" ) + "</dc:type>\n" ) ;
						
					}
				}
			}
			else if ( "spatialCoverage".equals( key ) )
			{
				JSONArray scovs = (JSONArray) controlled.get( key ) ;
				for ( Object oscov: scovs )
				{
					JSONObject scov = (JSONObject)oscov ;
					if ( scov.containsKey( "value" ) )
					{
						dccoverage.append( "    <dc:coverage>\n" ) ;
						dccoverage.append( "      <dcterms:spatial" ) ;
						if ( scov.containsKey( "source" ) )
						{
							// dccoverage.append( " scheme=\"" ) ;
							// dccoverage.append( (String)scov.get( "source" ) + "\"" ) ;
						}
						dccoverage.append( ">" + (String)scov.get( "value" ) + "</dcterms:spatial>\n" ) ;
						dccoverage.append( "    </dc:coverage>\n" ) ;
					}
				}
			}
			else if ( "temporalCoverage".equals( key ) )
			{
				JSONArray tcovs = (JSONArray) controlled.get( key ) ;
				for ( Object otcov: tcovs )
				{
					JSONObject tcov = (JSONObject)otcov ;
					if ( tcov.containsKey( "value" ) )
					{
						dccoverage.append( "    <dc:coverage>\n" ) ;
						dccoverage.append( "      <dcterms:temporal" ) ;
						if ( tcov.containsKey( "source" ) )
						{
							// dccoverage.append( " scheme=\"" ) ;
							// dccoverage.append( (String)tcov.get( "source" ) + "\"" ) ;
						}
						dccoverage.append( ">" + (String)tcov.get( "value" ) + "</dcterms:spatial>\n" ) ;
						dccoverage.append( "    </dc:coverage>\n" ) ;
					}
				}
			}
			else if ( "reviewStatus".equals( key ) )
			{
				JSONObject rstat = (JSONObject)controlled.get( key ) ;
				if ( rstat.containsKey( "value" ) )
				{
					dcdescription.append( "    <dc:description>\n" ) ;
					dcdescription.append( "      <ags:descriptionNotes>Status: " + (String)rstat.get( "value" ) + "</ags:descriptionNotes>\n" ) ;
					dcdescription.append( "    </dc:description>\n" ) ;
				}
			}
			else
			{
				String elem = null ;
				String thesaurus = (String)key ;
				if ( "ASC".equals( thesaurus ) || "ags:ASC".equals( thesaurus ) || "CABC".equals( thesaurus ) || "ags:CABC".equals( thesaurus ) || "DDC".equals( thesaurus ) || "dcterms:DDC".equals( thesaurus ) || "LCC".equals( thesaurus ) || "dcterms:LCC".equals( thesaurus ) || "UDC".equals( thesaurus ) || "dcterms:UDC".equals( thesaurus ) )
				{
					elem = "subjectClassification" ;
				}
				else
				{
					elem = "subjectThesaurus" ;
				}
				JSONArray descriptors = (JSONArray) controlled.get( key ) ;
				dcsubject.append( "    <dc:subject>\n" ) ;
//			      <ags:subjectClassification scheme="ags:ASC">H20</ags:subjectClassification>
//			      <ags:subjectThesaurus xml:lang="eng" scheme="ags:AGROVOC">Norway</ags:subjectThesaurus>
				for ( Object descriptor: descriptors )
				{
					dcsubject.append( "      <ags:" + elem + " scheme=\"" + thesaurus + "\">" + (String)descriptor + "</ags:" + elem + ">\n" ) ;
				}
				dcsubject.append( "    </dc:subject>\n" ) ;
			}
		}
	}
	
	private void expressions()
	{
		for ( Object obj1: expressions )
		{
			JSONObject expression = (JSONObject)obj1 ;
			if ( expression.containsKey( "language" ) )
			{
				JSONArray languages = (JSONArray)expression.get( "language" ) ;
				for ( Object obj2: languages)
				{
					dclanguage.append( "    <dc:language>" ) ;
					dclanguage.append( (String)obj2 ) ;
					dclanguage.append( "</dc:language>\n" ) ;
				}
			}
			if( expression.containsKey( "fullCitation" ) )
			{
				JSONArray fullCitations = (JSONArray) expression.get( "fullCitation" ) ;
				for ( Object obj10: fullCitations )
				{
					dcsource.append( "    <dc:source>" + (String)obj10 + "</dc:source>\n" ) ;
				}
			}
			
			if ( expression.containsKey( "citation" ) )
			{
				JSONArray citations = (JSONArray) expression.get( "citation" ) ;
				for ( Object obj2: citations)
				{
					JSONObject citation = (JSONObject)obj2 ;
					agscitation.append( "    <ags:citation>\n" ) ;
					if ( citation.containsKey( "title" ) )
					{
						JSONArray titles = (JSONArray)citation.get( "title" ) ;
						for ( Object obj3: titles )
						{
							agscitation.append( "      <ags:citationTitle>" ) ;
							agscitation.append( (String)obj3 ) ;
							agscitation.append( "</ags:citationTitle>\n" ) ;
						}
					}
					for ( Object o: citation.keySet() )
					{
						String key = (String)o ;
						if ( ( !"title".equals( key ) ) && ( !"citationNumber".equals( key ) ) && ( !"citationChronology".equals( key ) ) )
						{
							JSONArray values = (JSONArray)citation.get( key ) ;
							for (Object obj4: values)
							{
								agscitation.append( "      <ags:citationIdentifier scheme=\"ags:" ) ;
								agscitation.append( key ) ;
								agscitation.append( "\">" ) ;
								agscitation.append( (String)obj4 ) ; 
								agscitation.append( "</ags:citationIdentifier>\n" ) ;
							}
						}
					}
					if ( citation.containsKey( "citationNumber" ) )
					{
						JSONArray citationNumbers = (JSONArray)citation.get( "citationNumber" ) ;
						for ( Object obj5: citationNumbers)
						{
							agscitation.append( "      <ags:citationNumber>" ) ;
							agscitation.append( (String)obj5 ) ;
							agscitation.append( "</ags:citationNumber>\n" ) ;
						}
					}
					if ( citation.containsKey( "citationChronology" ) )
					{
						JSONArray citationChronologies = (JSONArray)citation.get( "citationChronology" ) ;
						for ( Object obj6: citationChronologies )
						{
							agscitation.append( "      <ags:citationChronology>" ) ;
							agscitation.append( (String)obj6 ) ;
							agscitation.append( "</ags:citationChronology>\n" ) ;
						}
					}
					agscitation.append( "    </ags:citation>\n" ) ;
				}
			}
			if ( expression.containsKey( "publisher" ) )
			{
				JSONArray publishers = (JSONArray) expression.get( "publisher" ) ;
				for ( Object obj71: publishers )
				{
					JSONObject publisher = (JSONObject)obj71 ;
					if ( publisher.containsKey( "date" ) )
					{
						dcdate.append( "    <dc:date>\n" ) ;
						dcdate.append( "      <dcterms:dateIssued>" ) ;
						dcdate.append( (String)publisher.get( "date" ) ) ;
						dcdate.append( "</dcterms:dateIssued>\n" ) ;
						dcdate.append( "    </dc:date>\n" ) ;
					}
				}
				for ( Object obj72: publishers )
				{
					boolean badend = true ;
					boolean name = false ;
					boolean location = false ;
					JSONObject publisher = (JSONObject)obj72 ;
					if ( publisher.containsKey( "name" ) )
					{
						name = true ;
						if ( !location )
						{
							dcpublisher.append( "    <dc:publisher>\n" ) ;
						}
						dcpublisher.append( "      <ags:publisherName>" + (String)publisher.get( "name" ) + "</ags:publisherName>\n" ) ;
						if ( location )
						{
							dcpublisher.append( "    </dc:publisher>\n" ) ;
							badend = false ;
						}
					}
					if ( publisher.containsKey( "location" ) )
					{
						location = true ;
						if ( !name )
						{
							dcpublisher.append( "    <dc:publisher>\n" ) ;
						}

						dcpublisher.append( "      <ags:publisherPlace>" + (String)publisher.get( "name" ) + "</ags:publisherPlace>\n" ) ;
						if ( name )
						{
							dcpublisher.append( "    </dc:publisher>\n" ) ;
							badend = false ;
						}
					}
					if ( ( name || location ) && badend )
					{
						dcpublisher.append( "    </dc:publisher>\n" ) ;
					}
				}
			}
			if ( expression.containsKey( "descriptionEdition" ) )
			{
				JSONArray deditions = (JSONArray) expression.get( "descriptionEdition" ) ;
				for ( Object obj8: deditions )
				{
					dcdescription.append( "    <dc:description>\n" ) ;
					dcdescription.append( "      <ags:descriptionEdition>" + (String)obj8 + "<ags:descriptionEdition>\n" ) ;
					dcdescription.append( "    </dc:description>\n" ) ;
				}
			}
			// publicationStatus can be ignored
			if ( expression.containsKey( "publicationStatus" ) )
			{
				JSONObject pstat = (JSONObject)controlled.get( "publicationStatus" ) ;
				if ( pstat.containsKey( "value" ) )
				{
					dcdescription.append( "    <dc:description>\n" ) ;
					dcdescription.append( "      <ags:descriptionNotes>Status: " + (String)pstat.get( "value" ) + "</ags:descriptionNotes>\n" ) ;
					dcdescription.append( "    </dc:description>\n" ) ;
				}
			}
			if ( expression.containsKey( "manifestations" ) )
			{
				JSONArray manifestations = (JSONArray) expression.get( "manifestations" ) ;
				for (Object obj9: manifestations)
				{
					manifestation( (JSONObject)obj9 ) ;
				}
			}
		}
	}
	
	private void manifestation( JSONObject manifestation )
	{
		for ( Object key: manifestation.keySet() )
		{
			if ( "items".equals( key ) )
			{
				JSONArray items = (JSONArray) manifestation.get( "items" ) ;
				for (Object obj1: items)
				{
					item( (JSONObject)obj1 ) ;
				}
			}
			else if ( "format".equals( key ) )
			{
				JSONArray formats = (JSONArray)manifestation.get( "format" ) ;
				for (Object obj2: formats)
				{
					dcformat.append( "    <dc:format>\n" ) ;
					dcformat.append( "      <dcterms:medium>" + (String)obj2 + "</dcterms:medium>\n" ) ;
					dcformat.append( "    </dc:format>\n" ) ;
				}
			}
			else if ( "manifestationType".equals( key ) )
			{
				// IGNORE!
				// JSONArray mtypes = (JSONArray)manifestation.get( "manifestationType" ) ;
				// for (Object obj3: mtypes)
				// {
					// agris.append( (String)obj3 ) ;
				// }
			}
			else if ( "duration".equals( key ) )
			{
				// temporary ignored
				// String duration = (String)manifestation.get( "duration" ) ;
				// agris.append( duration ) ;
			}
			else if ( "size".equals( key ) )
			{
				String size = (String)manifestation.get( "size" ) ;
				dcformat.append( "    <dc:format>\n" ) ;
				dcformat.append( "      <dcterms:extent>" + size + "</dcterms:extent>\n" ) ;
				dcformat.append( "    </dc:format>\n" ) ;
			}
			else if ( "description".equals( key ) )
			{
				JSONObject description = (JSONObject)manifestation.get( "description" ) ;
				for ( Object lang: description.keySet() )
				{
					String descr = (String)description.get( lang ) ;
					// agris.append( (String) lang + descr ) ;
					dcformat.append( "    <dc:format xml:lang=\"" + (String)lang + "\">\n" ) ;
					dcformat.append( "      <dcterms:medium>" + descr + "</dcterms:medium>\n" ) ;
					dcformat.append( "    </dc:format>\n" ) ;
				}
			}
			else
			{
				String idtype = (String)key ;
				if ("isbn".equals( idtype.toLowerCase() ))
				{
					idtype="ags:ISBN" ;
				}
				if ("doi".equals( idtype.toLowerCase() ))
				{
					idtype="ags:DOI" ;
				}
				if ("rn".equals( idtype.toLowerCase() ))
				{
					idtype="ags:RN" ;
				}
				if ("jn".equals( idtype.toLowerCase() ))
				{
					idtype="ags:JN" ;
				}
				if ("pn".equals( idtype.toLowerCase() ))
				{
					idtype="ags:PN" ;
				}
				if ("ipc".equals( idtype.toLowerCase() ))
				{
					idtype="ags:IPC" ;
				}
				if ("uri".equals( idtype.toLowerCase() ))
				{
					idtype="dcterms:URI" ;
				}
				JSONArray identifiers = (JSONArray)manifestation.get( key ) ;
				for (Object id: identifiers)
				{
					dcidentifier.append( "    <dc:identifier scheme=\"" + idtype +"\">" + (String) id + "</dc:identifier>\n" ) ;
					// agris.append( idtype + (String) id ) ;
				}
			}
		}
	}
	
	private void item( JSONObject item )
	{
		boolean location = false ;
		boolean number = false ;
		if (item.containsKey( "location" ))
		{
			if ( !number )
			{
				agsavailability.append( "    <agls:availability>\n" ) ;
			}
			location = true ;
			agsavailability.append( "      <ags:availabilityLocation>" + (String)item.get( "location" ) + "</ags:availabilityLocation>\n" ) ;
		}
		if (item.containsKey( "number" ))
		{
			if ( !location )
			{
				agsavailability.append( "    <agls:availability>\n" ) ;
			}
			number = true ;
			agsavailability.append( "      <ags:availabilityNumber>" + (String)item.get( "number" ) + "</ags:availabilityNumber>\n" ) ;
		}
		if ( location || number )
		{
			agsavailability.append( "    </agls:availability>\n" ) ;
		}
		if (item.containsKey( "url" ))
		{
			dcidentifier.append( "    <dc:identifier scheme=\"dcterms:URI\">" + (String)item.get( "url" ) + "</dc:identifier>\n" ) ;
		}
	}
	
	private void creator()
	{
		if ( agrif.containsKey( "creator" ) )
		{
			JSONArray creators = (JSONArray) agrif.get( "creator" ) ;
			for ( Object obj1: creators )
			{
				String opening = null ;
				String closing = null ;
				JSONObject creator = (JSONObject)obj1 ;
				if ( creator.containsKey( "identifier" ) )
				{
					// ignored
				}
				if ( creator.containsKey( "type" ) )
				{
					String type = (String)creator.get( "type" ) ;
					if ( "person".equals( type ) )
					{
						opening = "      <ags:creatorPersonal>" ;
						closing = "</ags:creatorPersonal>\n" ;
					}
					else if ( "organization".equals( type ) )
					{
						opening = "      <ags:creatorCorporate>" ;
						closing = "</ags:creatorCorporate>\n" ;
					}
					else
					{
						opening = "      <ags:creatorConference>" ;
						closing = "</ags:creatorConference>\n" ;
					}
				}
				if ( creator.containsKey( "name" ) )
				{
					if ( opening != null )
					{
						dccreator.append( "    <dc:creator>\n" ) ;
						dccreator.append( opening + (String)creator.get( "name" )  + closing ) ;
						dccreator.append( "    </dc:creator>\n" ) ;
					}
					else
					{
						dccreator.append( "    <dc:creator>" ) ;
						dccreator.append( (String)creator.get( "name" ) ) ;
						dccreator.append( "</dc:creator>\n" ) ;
					}
				}
			}
			
		}
	}
	
	private void relation()
	{
		if ( agrif.containsKey( "relation" ) )
		{
			JSONArray relations = (JSONArray) agrif.get( "relation" ) ;
			for (Object obj1: relations )
			{
				String relType = "" ;
				JSONObject relation = (JSONObject)obj1 ;
				dcrelation.append( "    <dc:relation>\n" ) ;
				if ( relation.containsKey( "typeOfRelation" ) )
				{
					relType = (String)relation.get( "typeOfRelation" ) ;
				}
				dcrelation.append( "      <dcterms:" + relType ) ;
				if ( relation.containsKey( "typeOfReference" ) )
				{
					dcrelation.append( " scheme=\"dcterms:URI\"" ) ;
				}
				if ( relation.containsKey( "reference" ) )
				{
					dcrelation.append( ">" + (String) relation.get( "reference" ) + "</dcterms:"+ relType + ">" ) ;
				}
				dcrelation.append( "    </dc:relation>\n" ) ;
			}
		}
	}
	
	private void rights()
	{
		if ( agrif.containsKey( "rights" ) )
		{
			JSONArray rights = (JSONArray) agrif.get( "rights" ) ;
			for (Object obj1: rights )
			{
				JSONObject right = (JSONObject)obj1 ;
				if ( right.containsKey( "identifier" ) )
				{
					dcrights.append( "    <dc:rights>\n" ) ;
					dcrights.append( "      <ags:rightsStatement>" + (String)right.get( "identifier" ) + "</ags:rightsStatement>\n" ) ;
					dcrights.append( "    </dc:rights>\n" ) ;
				}
				for ( Object obj2: right.keySet() )
				{
					String key = (String)obj2 ;
					if ( !"identifier".equals( key ) )
					{
						dcrights.append( "    <dc:rights>\n" ) ;
						JSONObject rstatement = (JSONObject) right.get( key ) ;
						if ( rstatement.containsKey( "rightsStatement" ) )
						{
							dcrights.append( "      <ags:rightsStatement>" + (String)rstatement.get( "rightsStatement" ) + "</ags:rightsStatement>\n" ) ;
						}
						if ( rstatement.containsKey( "termsOfUse" ) )
						{
							dcrights.append( "      <ags:rightsTermsofUse>" + (String)rstatement.get( "termsOfUse" ) + "</ags:rightsTermsofUse>\n" ) ;
						}
						dcrights.append( "    </dc:rights>\n" ) ;
					}
				}
				
			}
		}
	}	
	
	public String toString()
	{
		consolidate() ;
		return agris.toString() ;
	}
	
	private void init()
	{
		head = new StringBuilder() ;
		dctitle = new StringBuilder() ;
		dccreator  = new StringBuilder() ;
		dcpublisher = new StringBuilder() ;
		dcdate = new StringBuilder() ;
		dcsubject = new StringBuilder() ;
		dcdescription = new StringBuilder() ;
		dcidentifier = new StringBuilder() ;
		dctype = new StringBuilder() ;
		dcformat = new StringBuilder() ;
		dclanguage = new StringBuilder() ;
		dcrelation = new StringBuilder() ;
		agsavailability = new StringBuilder() ;
		dcsource = new StringBuilder() ;
		dccoverage = new StringBuilder() ;
		dcrights = new StringBuilder() ;
		agscitation = new StringBuilder() ;
		tail = new StringBuilder() ;	
	}
	
	private void consolidate()
	{
		agris = new StringBuilder() ;
		agris.append( head ) ;
		agris.append( dctitle ) ;
		agris.append( dccreator ) ;
		agris.append( dcpublisher ) ;
		agris.append( dcdate ) ;
		agris.append( dcsubject ) ;
		agris.append( dcdescription ) ;
		agris.append( dcidentifier ) ;
		agris.append( dctype ) ;
		agris.append( dcformat ) ;
		agris.append( dclanguage ) ;
		agris.append( dcrelation ) ;
		agris.append( agsavailability ) ;
		agris.append( dcsource ) ;
		agris.append( dccoverage ) ;
		agris.append( dcrights ) ;
		agris.append( agscitation ) ;
		agris.append( tail ) ;
	}
	
	private void header()
	{
		head.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" ) ;
		head.append( "<ags:resources xmlns:ags=\"http://purl.org/agmes/1.1/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:agls=\"http://www.naa.gov.au/recordkeeping/gov_online/agls/1.2\">\n" ) ;
		head.append( "  <ags:resource ags:ARN=\"" ) ;
		if ( agrif.containsKey( "origin" ) )
		{
			JSONObject origin = (JSONObject) agrif.get( "origin" ) ;
			if ( !origin.values().isEmpty() )
			{
				head.append( (String)origin.values().toArray()[0] ) ;
			}
		}
		head.append( "\">\n" ) ;
	}
	
	private void footer()
	{
		tail.append( "  </ags:resource>\n</ags:resources>\n" ) ;
	}
}
