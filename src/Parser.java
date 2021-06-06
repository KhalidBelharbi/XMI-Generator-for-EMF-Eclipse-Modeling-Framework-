

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import flanagan.math.PsRandom;

/**
* Copyright - This code contains a watermark for the author
* @author Khalid Belharbi
*/
//*********************************************************************************************
public class Parser {
	double probability = Main.probabilty;
	StringBuilder txt;	
	private Vector<Vector> allRows; 
	private File fichier;
	String MM; // meta modele id
	private String MetaData;//head
	private Vector<Element> elems_list;
	private Vector<ComplexType> complextype_list;
	Random rd;			
	String root;	
	public Parser(String path) {		
		
		txt = new StringBuilder();
		rd = new Random();
		elems_list = new Vector<Element>();
		complextype_list = new Vector<ComplexType>();		
		allRows = new Vector<Vector>();
		fichier = new File(path);
		Scanner lire;
		try {
			lire = new Scanner(fichier);		
		while (lire.hasNextLine()) {
	        String data = lire.nextLine();
	        String[] parseLine = data.split(" ");
	        Vector tmp = new Vector();
	        for(String elem : parseLine) 
	        	if(!elem.isEmpty()) tmp.add(elem);	        
	        allRows.add((Vector) tmp.clone());		
		}		
		} catch (FileNotFoundException e) {	e.printStackTrace();}
		elems_list = getElements(allRows); 
		complextype_list = getComplexTypes(allRows); 
		doMetaData(allRows);	
		
		for(Element e : elems_list) 
              e.setType(e.getType().replace("/>",""));		
		for(ComplexType e : complextype_list) {				
			for(Element ee : e.getSequence()) 
				ee.setType(ee.getType().replace("/>",""));	
			for(Attribute x : e.getAttributs())
				x.setType(x.getType().replace("/>",""));
		}
			
		System.out.println("simple elements  \n");	
		for(Element e : elems_list) {
			   System.out.println(e.getName());
               System.out.println(e.getType());
		}		
		
		for(ComplexType e : complextype_list) {	
			System.out.println("\n attributes  : \n");
			for(Attribute at : e.getAttributs()) {
				System.out.println(at.getName()+" "+at.getType());
			}
			System.out.println("\n elements : \n");
			for(Element ee : e.getSequence()) {
				System.out.println(ee.getName()+" "+ee.getType()+" "+ee.getMinOccurs()+" "+ee.getMaxOccurs());
			}
		}
		txt.append(this.MetaData);		
		if(this.probability == -1) this.probability = this.rand.nextDouble(); 		
		generateInstance();
	}

//***************************************************************************************************	
	public void generateInstance() {
		int index=0;
		for(int i=0;i<elems_list.size();i++) {
			if (elems_list.get(i).getName()=="Root") {
				index = i;
				break;
			}
		}		
		
		for(Attribute elem : complextype_list.get(index).getAttributs()) 		
			txt.append(" "+generateRandomValues(elem)+" ");
		txt.append(">");	
		propagation(complextype_list.get(index),true,null);		
		txt.append("\n</"+this.MM+":Root>");
	}
//***************************************************************************************************	
	public int getIndex(Element SubElemOFComplexType) {		
		int index=0;
		for(int i=0;i<elems_list.size();i++) {
			if (elems_list.get(i).getType().equalsIgnoreCase(SubElemOFComplexType.getType())) {
				index = i;
				break;
			}
		}
		return index;
	}
//***************************************************************************************************	
	//			if(this.rand.nextDouble() < this.rand.nextDouble() ) {		
	public void propagation(ComplexType complex, boolean isRoot, Element e) {		
		Vector<Attribute> attrbs = complex.getAttributs();
		Vector<Element> sequence =  complex.getSequence();		
		if(isRoot) {			
				for(Element elem: sequence) {					
					if(this.rand.nextDouble() < this.probability) {
						int min = Integer.parseInt(elem.getMinOccurs().replace("\"",""));
						int max = Main.SCOPE;
						if (!"unbounded".equalsIgnoreCase(elem.getMaxOccurs().replace("\"","")))
								max = Integer.parseInt(elem.getMaxOccurs().replace("\"",""));						
						int o = this.rand.nextInt((max - min) + 1) + min ; 
						System.out.println("Min = "+min+" max="+Main.SCOPE+" cardinal of generated elements="+o);

						for(int k=0;k<o;k++) {
							propagation(complextype_list.get(this.getIndex(elem)),false,elem);
							txt.append("\n</"+elem.getName().replace("\"","")+">");		
						}								
					}			
				}
		}else {			
			txt.append("\n"+"<"+e.getName().replace("\"","")+" ");			
			if(complex.isChild) txt.append("xsi:type="+"\""+complex.nameOfParent+"\" ");			
			for(Attribute a : attrbs) 		
				txt.append(" "+generateRandomValues(a)+" ");
			txt.append(">");
			for(Element elem: sequence) {
				if(this.rand.nextDouble() < this.probability) {
					int min = Integer.parseInt(elem.getMinOccurs().replace("\"",""));
					int max = Main.SCOPE;
					if (!"unbounded".equalsIgnoreCase(elem.getMaxOccurs().replace("\"","")))
							max = Integer.parseInt(elem.getMaxOccurs().replace("\"",""));
					int o = this.rand.nextInt((max - min) + 1) + min ; 
					for(int k=0;k<o;k++) {
					propagation(complextype_list.get(this.getIndex(elem)),false,elem);
					txt.append("\n</"+elem.getName().replace("\"","")+">");
					}
				}			
			}			
		}				
	}

//***************************************************************************************************	
	public void GenerateInstances() {
		Element root;
		int index=0;
		for(int i=0;i<elems_list.size();i++) {
			if (elems_list.get(i).getName()=="Root") {
				root = elems_list.get(i) ;
				index = i;
				break;
			}
		}		
		Vector<Attribute> x = complextype_list.get(index).getAttributs();
		Vector<Element> y =  complextype_list.get(index).getSequence();			
		for(Attribute elem : x) {			
			generateRandomValues(elem);
		}		
		for(Element elem: y) {
			if(this.rd.nextDouble() < this.rd.nextDouble() ) {				
				int min = Integer.parseInt(elem.getMinOccurs());
				int max = Main.SCOPE;
				if (!"\"unbounded\"".equalsIgnoreCase("\""+elem.getMaxOccurs()+"\""))
						max = Integer.parseInt(elem.getMaxOccurs());
				for(int i=min;i<max;i++) {
					if(this.rd.nextDouble() < this.rd.nextDouble() ) {
						//generateComplexType(elem);
					}
					
				}
			}
		}
	}	
//***************************************************************************************************	
	
	// you can add here other data types, this is just an exemple to help you  
public String generateRandomValues(Attribute a) {	
	if(a.getType().contains("EString")) return a.getName().replace("\"","")+"= \""+"RandomValue"+this.rand.nextInt(10000)+"\"";
	if(a.getType().contains("EInt")) return a.getName().replace("\"","")+"= \""+this.rand.nextInt(10000)+"\"";
	if(a.getType().contains("EDouble")) return a.getName().replace("\"","")+"= \""+this.rand.nextDouble()+"\"";
	else {
		return a.getName().replace("\"","")+"="+a.getType();
	}
}
//***********************************************************************************************
Random rand = new Random();
public String findValue(String x,Vector<String> l) {
	for(String e:l) {		
		if(e.contains(x+"=")) {
			int indx = e.indexOf("=");
			return e.substring(indx+1);
		}
	}
	return "";
}
//***********************************************************************************************
public String findValueMM(String x,Vector<String> l) {
	for(String e:l) {		
		if(e.contains(x+":")) {
			int indx = e.indexOf(":");
			return e.substring(indx+1);
		}
	}
	return "";
}
//***********************************************************************************************
public Vector<Element> getElements(Vector<Vector> all){
	 Vector<Element> t = new Vector<Element>();
	for(int i=0;i<all.size();i++) {
		if("<xsd:element".equalsIgnoreCase((String) all.get(i).get(0))) {
			Element e = new Element(findValue("name",all.get(i)),findValue("type",all.get(i)));
			t.add(e);
		}
		if("<xsd:complexType".equalsIgnoreCase((String) all.get(i).get(0))) break;
	}
	return t;
}	
//***********************************************************************************************
public Vector<ComplexType> getComplexTypes(Vector<Vector> all){
	 Vector<ComplexType> t = new Vector<ComplexType>();
	for(int i=0;i<all.size();i++) {
		if("<xsd:element".equalsIgnoreCase((String) all.get(i).get(0))) 
			continue;
		ComplexType tt = new ComplexType();
		if("<xsd:complexType".equalsIgnoreCase((String) all.get(i).get(0))) {			
			if("<xsd:complexContent>".equalsIgnoreCase((String) all.get(i+1).get(0))) {
				tt.isChild = true;
				tt.nameOfParent = findValue("base",all.get(i+1));			
			int j = i;
			while(!("</xsd:complexContent>".equalsIgnoreCase((String) all.get(j).get(0)))) {
				if("<xsd:element".equalsIgnoreCase((String) all.get(j).get(0))) {
					Element e = new Element(findValue("name",all.get(j)),
							findValue("type",all.get(j)),
							findValue("maxOccurs",all.get(j)),
							findValue("minOccurs",all.get(j)));
					tt.getSequence().add(e);
				}
				if("<xsd:attribute".equalsIgnoreCase((String) all.get(j).get(0))) {
					Attribute e = new Attribute(findValue("name",all.get(j)),
							findValue("type",all.get(j)));
					tt.getAttributs().add(e);
				}				
				j++;
			}
			}else {
				int j = i;
				while(!("</xsd:complexType>".equalsIgnoreCase((String) all.get(j).get(0)))) {
					if("<xsd:element".equalsIgnoreCase((String) all.get(j).get(0))) {
						Element e = new Element(findValue("name",all.get(j)),
								findValue("type",all.get(j)),
								findValue("maxOccurs",all.get(j)),
								findValue("minOccurs",all.get(j)));
						tt.getSequence().add(e);
					}
					if("<xsd:attribute".equalsIgnoreCase((String) all.get(j).get(0))) {
						Attribute e = new Attribute(findValue("name",all.get(j)),
								findValue("type",all.get(j)));
						tt.getAttributs().add(e);
					}					
					j++;
				}
			}
			t.add(tt);		
		}		
	}
	return t;	
}

public String getRandomString() {	
	 byte[] array = new byte[rd.nextInt(10)];new Random().nextBytes(array);
	 return new String(array, Charset.forName("UTF-8"));
}
	
public void doMetaData(Vector<Vector> all) {
	StringBuilder s = new StringBuilder();
	StringBuilder d = new StringBuilder(findValueMM("xmlns",all.get(1)));	
	String MM = d.substring(0,d.indexOf("="));		
	s.append(getString(all.get(0),0,all.get(0).size()-1)+" ?>");  
	s.append("\n"+"<"+MM+":Root xmi:version=\"2.0\""+"\n xmlns:xmi=\"http://www.omg.org/XMI\" \n "
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+"\n xmlns:"+d);
	this.MetaData = new String(s);
	this.MM = new String(MM);	
}

public String getString(Vector<String> vect,int i,int j) {	
	StringBuilder s = new StringBuilder();
	for(int k=i;k<j;k++)
		if(k<j) s.append(vect.get(k)+" ");	
	return new String(s);	
}

public String searchValue(String id, Vector<Vector> vect) {	
	for(int i=0;i<vect.size();i++) {
		String s = findValue(id,vect.get(i));
		if (!s.isEmpty()) return s; 
	}
	return "";
}

public String getMetaData() {
	return MetaData;
}

public void setMetaData(String metaData) {
	MetaData = metaData;
}

	public Vector<Vector> getAllRows() {
		return allRows;
	}


	public void setAllRows(Vector<Vector> allRows) {
		this.allRows = allRows;
	}


	public File getFichier() {
		return fichier;
	}


	public void setFichier(File fichier) {
		this.fichier = fichier;
	}


	public Vector<Element> getElems_list() {
		return elems_list;
	}


	public void setElems_list(Vector<Element> elems_list) {
		this.elems_list = elems_list;
	}


	public Vector<ComplexType> getComplextype_list() {
		return complextype_list;
	}


	public void setComplextype_list(Vector<ComplexType> complextype_list) {
		this.complextype_list = complextype_list;
	}

}
//*********************************************************************************************
class ComplexType {
	
	private Vector<Element> sequence;
	
	private Vector<Attribute> attributs;

	
	boolean isChild = false;
	
	
	String nameOfParent;
	
	
	public ComplexType() {
		sequence = new Vector<Element>();
		attributs = new Vector<Attribute>();
	}
	

	public Vector<Element> getSequence() {
		return sequence;
	}

	public void setSequence(Vector<Element> sequence) {
		this.sequence = sequence;
	}

	public Vector<Attribute> getAttributs() {
		return attributs;
	}

	public void setAttributs(Vector<Attribute> attributs) {
		this.attributs = attributs;
	} 
	
}

//*********************************************************************************************
class Element {
	private String EcoreName;
	private String name;
	private String type;
	private String maxOccurs="unbounded", minOccurs="0";

	public Element(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	public Element( String name, String type, String maxOccurs, String minOccurs) {
		
		this.name = name;
		this.type = type;
		this.maxOccurs = maxOccurs;
		this.minOccurs = minOccurs;
	}
	public String getEcoreName() {
		return EcoreName;
	}
	public void setEcoreName(String ecoreName) {
		EcoreName = ecoreName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMaxOccurs() {
		return maxOccurs;
	}
	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	public String getMinOccurs() {
		return minOccurs;
	}
	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}
		
}
//*********************************************************************************************

class Attribute {
	
	public Attribute(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	private String name, type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}




