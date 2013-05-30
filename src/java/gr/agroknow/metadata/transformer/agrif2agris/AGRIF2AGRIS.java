package gr.agroknow.metadata.transformer.agrif2agris;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AGRIF2AGRIS
{
	private JSONObject agrif ;
	private StringBuilder agris ;
	
	private JSONArray expressions ;
	private JSONObject controlled ;
	private JSONObject languageBlocks ;
	
	public AGRIF2AGRIS( String akifString )
	{
		agrif = (JSONObject) JSONValue.parse( akifString ) ;
		expressions = (JSONArray)agrif.get( "expressions" ) ;
		controlled = (JSONObject)agrif.get( "controlled" ) ;
		agris = new StringBuilder() ;
		header() ;
		languageBlocks() ;
		controlled() ;
		expressions() ;
		creator() ;
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
					agris.append( "    <dc:title xml:lang=\"" + lang + "\">" ) ;
					agris.append( title + "</dc:title>\n" ) ;
				}
				if ( lblock.containsKey( "abstract" ) )
				{
					String abstrct = (String) lblock.get( "abstract" ) ;
					agris.append( "    <dc:description xml:lang=\"" + lang + "\">\n" ) ;
					agris.append( "      <dcterms:abstract>" + abstrct + "</dcterms:abstract>\n" ) ;
					agris.append( "    </dc:description>\n" ) ;
				}
				if ( lblock.containsKey( "note" ) )
				{
					String note = (String) lblock.get( "note" ) ;
					agris.append( "    <dc:description xml:lang=\"" + lang + "\">\n" ) ;
					agris.append( "      <ags:descriptionNotes>" + note + "</ags:descriptionNotes>\n" ) ;
					agris.append( "    </dc:description>\n" ) ;
				}
				if ( lblock.containsKey( "alternativeTitle" ) )
				{
					JSONArray altTitles = (JSONArray) lblock.get( "alternativeTitle" ) ;
					for ( Object altTitle: altTitles )
					{
						agris.append( "    <dc:title xml:lang=\"" + lang + "\">\n" ) ;
						agris.append( "      <dcterms:alternative>" + (String)altTitle + "<dcterms:alternative>\n" ) ;
						agris.append( "    </dc:title>\n" ) ;
					}
				}
				if ( lblock.containsKey( "titleSupplemental" ) )
				{
					JSONArray titleSups = (JSONArray) lblock.get( "titleSupplemental" ) ;
					for ( Object titleSup: titleSups )
					{
						// agris.append( "" + (String)titleSup ) ;
						agris.append( "    <dc:title xml:lang=\"" + lang + "\">\n" ) ;
						agris.append( "      <ags:titleSupplement>" + (String)titleSup + "<ags:titleSupplement>\n" ) ;
						agris.append( "    </dc:title>\n" ) ;
					}
				}
				if ( lblock.containsKey( "keyword" ) )
				{
					JSONArray keywords = (JSONArray) lblock.get( "keyword" ) ;
					for ( Object keyword: keywords )
					{
						agris.append( "    <dc:subject xml:lang=\"" + lang + "\">" ) ;
						agris.append( (String)keyword ) ;
						agris.append( "</dc:subject>\n" ) ;
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
						agris.append( "      <dc:type" ) ;
						if ( type.containsKey( "source" ) )
						{
							agris.append( " scheme=\"" ) ;
							agris.append( (String)type.get( "source" ) + "\"" ) ;
							// <dc:type xml:lang="en" scheme="dcterms:DCMIType">Reports</dc:type>
						}
						agris.append( ">" + (String)type.get( "value" ) + "</dc:type>\n" ) ;
						
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
						agris.append( "    <dc:coverage>\n" ) ;
						agris.append( "      <dcterms:spatial" ) ;
						if ( scov.containsKey( "source" ) )
						{
							agris.append( " scheme=\"" ) ;
							agris.append( (String)scov.get( "source" ) + "\"" ) ;
						}
						agris.append( ">" + (String)scov.get( "value" ) + "</dcterms:spatial>\n" ) ;
						agris.append( "    </dc:coverage>\n" ) ;
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
						agris.append( "    <dc:coverage>\n" ) ;
						agris.append( "      <dcterms:temporal" ) ;
						if ( tcov.containsKey( "source" ) )
						{
							agris.append( " scheme=\"" ) ;
							agris.append( (String)tcov.get( "source" ) + "\"" ) ;
						}
						agris.append( ">" + (String)tcov.get( "value" ) + "</dcterms:spatial>\n" ) ;
						agris.append( "    </dc:coverage>\n" ) ;
					}
				}
			}
			else if ( "reviewStatus".equals( key ) )
			{
				JSONObject rstat = (JSONObject)controlled.get( key ) ;
				if ( rstat.containsKey( "value" ) )
				{
					agris.append( "    <dc:description>\n" ) ;
					agris.append( "      <ags:descriptionNotes>Status: " + (String)rstat.get( "value" ) + "</ags:descriptionNotes>\n" ) ;
					agris.append( "    </dc:description>\n" ) ;
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
				agris.append( "    <dc:subject>\n" ) ;
//			      <ags:subjectClassification scheme="ags:ASC">H20</ags:subjectClassification>
//			      <ags:subjectThesaurus xml:lang="eng" scheme="ags:AGROVOC">Norway</ags:subjectThesaurus>
				for ( Object descriptor: descriptors )
				{
					agris.append( "      <ags:" + elem + " scheme=\"" + thesaurus + "\">" + (String)descriptor + "</ags:" + elem + ">\n" ) ;
				}
				agris.append( "    </dc:subject>\n" ) ;
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
					agris.append( "    <dc:language scheme=\"dcterms:ISO639-1\">" ) ;
					agris.append( (String)obj2 ) ;
					agris.append( "</dc:language>\n" ) ;
				}
			}
			if ( expression.containsKey( "citation" ) )
			{
				JSONArray citations = (JSONArray) expression.get( "citation" ) ;
				for ( Object obj2: citations)
				{
					JSONObject citation = (JSONObject)obj2 ;
					agris.append( "    <ags:citation>\n" ) ;
					if ( citation.containsKey( "title" ) )
					{
						JSONArray titles = (JSONArray)citation.get( "title" ) ;
						for ( Object obj3: titles )
						{
							agris.append( "      <ags:citationTitle>" ) ;
							agris.append( (String)obj3 ) ;
							agris.append( "</ags:citationTitle>\n" ) ;
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
								agris.append( "      <ags:citationIdentifier scheme=\"ags:" ) ;
								agris.append( key ) ;
								agris.append( "\">" ) ;
								agris.append( (String)obj4 ) ; 
								agris.append( "</ags:citationIdentifier>\n" ) ;
							}
						}
					}
					if ( citation.containsKey( "citationNumber" ) )
					{
						JSONArray citationNumbers = (JSONArray)citation.get( "citationNumber" ) ;
						for ( Object obj5: citationNumbers)
						{
							agris.append( "      <ags:citationNumber>" ) ;
							agris.append( (String)obj5 ) ;
							agris.append( "</ags:citationNumber>\n" ) ;
						}
					}
					if ( citation.containsKey( "citationChronology" ) )
					{
						JSONArray citationChronologies = (JSONArray)citation.get( "citationChronology" ) ;
						for ( Object obj6: citationChronologies )
						{
							agris.append( "      <ags:citationChronology>" ) ;
							agris.append( (String)obj6 ) ;
							agris.append( "</ags:citationChronology>\n" ) ;
						}
					}
					agris.append( "    </ags:citation>\n" ) ;
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
						agris.append( "    <dc:date>\n" ) ;
						agris.append( "      <dcterms:dateIssued>" ) ;
						agris.append( (String)publisher.get( "date" ) ) ;
						agris.append( "</dcterms:dateIssued>\n" ) ;
						agris.append( "    </dc:date>\n" ) ;
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
							agris.append( "    <dc:publisher>\n" ) ;
						}
						agris.append( "      <ags:publisherName>" + (String)publisher.get( "name" ) + "</ags:publisherName>\n" ) ;
						if ( location )
						{
							agris.append( "    </dc:publisher>\n" ) ;
							badend = false ;
						}
					}
					if ( publisher.containsKey( "location" ) )
					{
						location = true ;
						if ( !name )
						{
							agris.append( "    <dc:publisher>\n" ) ;
						}

						agris.append( "      <ags:publisherPlace>" + (String)publisher.get( "name" ) + "</ags:publisherPlace>\n" ) ;
						if ( name )
						{
							agris.append( "    </dc:publisher>\n" ) ;
							badend = false ;
						}
					}
					if ( ( name || location ) && badend )
					{
						agris.append( "    </dc:publisher>\n" ) ;
					}
				}
			}
			if ( expression.containsKey( "descriptionEdition" ) )
			{
				JSONArray deditions = (JSONArray) expression.get( "descriptionEdition" ) ;
				for ( Object obj8: deditions )
				{
					agris.append( "    <dc:description>\n" ) ;
					agris.append( "      <ags:descriptionEdition>" + (String)obj8 + "<ags:descriptionEdition>\n" ) ;
					agris.append( "    </dc:description>\n" ) ;
				}
			}
			// publicationStatus can be ignored
			if ( expression.containsKey( "publicationStatus" ) )
			{
				JSONObject pstat = (JSONObject)controlled.get( "publicationStatus" ) ;
				if ( pstat.containsKey( "value" ) )
				{
					agris.append( "    <dc:description>\n" ) ;
					agris.append( "      <ags:descriptionNotes>Status: " + (String)pstat.get( "value" ) + "</ags:descriptionNotes>\n" ) ;
					agris.append( "    </dc:description>\n" ) ;
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
					agris.append( "    <dc:format>\n" ) ;
					agris.append( "      <dcterms:medium>" + (String)obj2 + "</dcterms:medium>\n" ) ;
					agris.append( "    </dc:format>\n" ) ;
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
				agris.append( "    <dc:format>\n" ) ;
				agris.append( "      <dcterms:extent>" + size + "</dcterms:extent>\n" ) ;
				agris.append( "    </dc:format>\n" ) ;
			}
			else if ( "description".equals( key ) )
			{
				JSONObject description = (JSONObject)manifestation.get( "description" ) ;
				for ( Object lang: description.keySet() )
				{
					String descr = (String)description.get( lang ) ;
					// agris.append( (String) lang + descr ) ;
					agris.append( "    <dc:format xml:lang=\"" + (String)lang + "\">\n" ) ;
					agris.append( "      <dcterms:medium>" + descr + "</dcterms:medium>\n" ) ;
					agris.append( "    </dc:format>" ) ;
					
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
					agris.append( "    <dc:identifier scheme=\"" + idtype +"\">" + (String) id + "</dc:identifier>\n" ) ;
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
				agris.append( "    <agls:availability>\n" ) ;
			}
			location = true ;
			agris.append( "      <ags:availabilityLocation>" + (String)item.get( "location" ) + "</ags:availabilityLocation>\n" ) ;
		}
		if (item.containsKey( "number" ))
		{
			if ( !location )
			{
				agris.append( "    <agls:availability>\n" ) ;
			}
			number = true ;
			agris.append( "      <ags:availabilityNumber>" + (String)item.get( "number" ) + "</ags:availabilityNumber>\n" ) ;
		}
		if ( location || number )
		{
			agris.append( "    </agls:availability>\n" ) ;
		}
		if (item.containsKey( "url" ))
		{
			agris.append( "    <dc:identifier scheme=\"dcterms:URI\">" + (String)item.get( "url" ) + "</dc:identifier>\n" ) ;
		}
	}
	
	private void creator()
	{
		if ( agrif.containsKey( "creator" ) )
		{
			JSONArray creators = (JSONArray) agrif.get( "creator" ) ;
			agris.append( "    <dc:creator>\n" ) ;
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
					agris.append( opening + (String)creator.get( "name" )  + closing ) ;
				}
			}
			agris.append( "    </dc:creator>\n" ) ;
		}
	}
	
	private void relation()
	{
		if ( agrif.containsKey( "relation" ) )
		{
			JSONArray relations = (JSONArray) agrif.get( "relation" ) ;
			for (Object obj1: relations )
			{
				JSONObject creator = (JSONObject)obj1 ;
				//agris.append( "    <dc:relation>\n" ) ;
				if ( creator.containsKey( "typeOfRelation" ) )
				{
					// agris.append( "" ) ;
				}
				if ( creator.containsKey( "typeOfReference" ) )
				{
					// agris.append( "" ) ;
				}
				if ( creator.containsKey( "reference" ) )
				{
					// agris.append( "" ) ;
				}
				//agris.append( "    </dc:relation>\n" ) ;
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
					agris.append( "    <dc:rights>\n" ) ;
					agris.append( "      <ags:rightsStatement>" + (String)right.get( "identifier" ) + "</ags:rightsStatement>\n" ) ;
					agris.append( "    </dc:rights>\n" ) ;
				}
				for ( Object obj2: right.keySet() )
				{
					String key = (String)obj2 ;
					if ( !"identifier".equals( key ) )
					{
						agris.append( "    <dc:rights>\n" ) ;
						JSONObject rstatement = (JSONObject) right.get( key ) ;
						if ( rstatement.containsKey( "rightsStatement" ) )
						{
							agris.append( "      <ags:rightsStatement>" + (String)right.get( "rightsStatement" ) + "</ags:rightsStatement>\n" ) ;
						}
						if ( rstatement.containsKey( "termsOfUse" ) )
						{
							agris.append( "      <ags:rightsTermsofUse>" + (String)right.get( "termsOfUse" ) + "</ags:rightsTermsofUse>\n" ) ;
						}
						agris.append( "    </dc:rights>\n" ) ;
					}
				}
				
			}
		}
	}	
	
	public String toString()
	{
		return agris.toString() ;
	}
	
	private void header()
	{
		agris.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" ) ;
		agris.append( "<ags:resources xmlns:ags=\"http://purl.org/agmes/1.1/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:agls=\"http://www.naa.gov.au/recordkeeping/gov_online/agls/1.2\">\n" ) ;
		agris.append( "  <ags:resource ags:ARN=\"" ) ;
		if ( agrif.containsKey( "origin" ) )
		{
			JSONObject origin = (JSONObject) agrif.get( "origin" ) ;
			if ( !origin.values().isEmpty() )
			{
				agris.append( (String)origin.values().toArray()[0] ) ;
			}
		}
		agris.append( "\">\n" ) ;
	}
	

	
	private void footer()
	{
		agris.append( "  </ags:resource>\n</ags:resources>\n" ) ;
	}
}
