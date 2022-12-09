import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;

public class Relation implements Serializable{
  String nom;
  Vector<String> colonne;
  Vector<Vector<String>> donnee;

  public Relation(String n,Vector<String> c,Vector<Vector<String>> d){
    this.setnom(n);
    this.setColonne(c);
    this.setdonnee(d);
  }
  public Relation(String n,Vector<String> c){
    this.setnom(n);
    this.setColonne(c);
  }
  public Relation(){
    
  }

	public void createRelation(String nom,Vector<String> att)throws Exception{
		File relation = new File(nom+".txt");
		File attribut = new File(nom+"att.txt");
		File listRelation = new File("ListeTable.txt");

		FileOutputStream outRelation = new FileOutputStream(relation,true);
		PrintWriter writeRelation = new PrintWriter(outRelation);
		FileOutputStream outAttribut = new FileOutputStream(attribut,true);
		PrintWriter writeAttribut = new PrintWriter(outAttribut);
		FileOutputStream outList = new FileOutputStream(listRelation,true);
		PrintWriter writeList = new PrintWriter(outList);

		writeList.print(nom+";;");
		writeRelation.print("");
		for(int i=0;i<att.size();i++){
			writeAttribut.print(att.get(i)+";;");
		}
		writeRelation.flush();
		writeRelation.close();
		writeAttribut.flush();
		writeAttribut.close();
		writeList.flush();
		writeList.close();
	}

	public void insertDataRelation(String nom,Vector<String> data,String s)throws Exception{
		File relationData = new File(nom+".txt");
		FileOutputStream relation = new FileOutputStream(relationData,true);
		PrintWriter writeData = new PrintWriter(relation);
		for(int i=0;i<data.size();i++){
			writeData.print(data.get(i)+";;");
		}
		writeData.print("//");
		writeData.flush();
		writeData.close();
	}

	public void insertDataRelation(String nom,Vector<String> donnee)throws Exception {
		String tables=getAllTable();
		if(tables.contains(";"+nom+";")) {
			Vector<String> att=getAttribut(nom);
			if(att.size()==donnee.size()) {
				File table=new File(nom+".txt");
				FileOutputStream outTab =new FileOutputStream(table,true);        
			    PrintWriter pTab=new PrintWriter(outTab);
			    for(int i=0;i<donnee.size();i++) {
			    	 pTab.print(donnee.get(i)+";;");
			    }
			    pTab.print("//");
			    pTab.flush(); pTab.close();
			}
			else if(att.size()<donnee.size()) {
				throw new Exception("    there are many data"); 
			}
			else { 
				throw new Exception("    the data is not enough");
			}
		}else { 
			throw new Exception("    the table does not exist");
		}
	}

	public void cmd() {
		for(int i=0;i<this.getcolonne().size();i++) {
			System.out.print(this.getcolonne().get(i)+"|");
		}
		System.out.println();
		for(int j=0;j<this.getdonnee().size();j++) {
			for(int i=0;i<this.getdonnee().get(j).size();i++) {
				System.out.print(this.getdonnee().get(j).get(i)+"|");
			}
			System.out.println();
		}
	}

	public Vector<String> getAttribut(String name)throws Exception{
		File fichier = new File(name+"att.txt");
		FileReader file = new FileReader(fichier);
		BufferedReader buf = new BufferedReader(file);
		Vector<String> resp = new Vector<String>();
		String[] attribut = buf.readLine().split(";;");
		for(int i=0;i<attribut.length;i++) {
			resp.addElement(attribut[i]);
		}
		return resp;
	}

  public Relation getAllData(String name)throws Exception {
		File fichier = new File(name+".txt");
		FileReader file = new FileReader(fichier);
		BufferedReader buf = new BufferedReader(file);
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		String[] ivale = buf.readLine().split("//");
		for(int i=0;i<ivale.length;i++) {
			String[] dataCo=ivale[i].split(";;");
			Vector<String> irep = new Vector<String>();
			for(int j=0;j<dataCo.length;j++) {
				irep.addElement(dataCo[j]);
			}
			data.addElement(irep);
		}
		Vector<String> attribut = getAttribut(name);
		Relation resp = new Relation(name,attribut,data);
		return resp;
	}

  public Relation projection(String name,String colonne)throws Exception {
		Relation all=getAllData(name);
		Vector<String> att=getAttribut(name);
		String[] cols=colonne.split(",");
		Vector<Integer> indice=new Vector<Integer>();
		for(int i=0;i<cols.length;i++) {
			int marque=0;
			for(int j=0;j<att.size();j++) {
				if(cols[i].hashCode()==att.get(j).hashCode()) {
					marque=1;
					indice.add(j);
					break;
				}
			}
			if(marque==0) {
				throw new Exception("    There is no "+cols[i]+" column"+" in the table "+name);
			}
		}
		Vector<String> colrep=new Vector<String>();
		Vector<Vector<String>> datarep=new Vector<Vector<String>>();
		for(int i=0;i<cols.length;i++) {
			colrep.addElement(all.getcolonne().get(indice.get(i)));
		}
		for(int i=0;i<all.getdonnee().size();i++) {
			Vector<String> temp=new Vector<String>();
			for(int j=0;j<cols.length;j++) {
				temp.addElement(all.getdonnee().get(i).get(indice.get(j)));
			}
			datarep.addElement(temp);
		}
		Relation projection=new Relation("",colrep,datarep);
		return projection;
		
	}

	public String getAllTable()throws Exception{
		File fichier = new File("ListeTable.txt");
		FileReader file = new FileReader(fichier);
		BufferedReader buf = new BufferedReader(file);
		String alltab = buf.readLine();
		return alltab;
	}

	public String AllTrait(String sql)throws Exception{
		String[] req = new String[3];
		req[0]="select ";
		req[1]="insert ";
		req[2]="create table ";
		for(int j=0;j<req.length;j++) {
			int count =0;
			for(int i=0;i<req[j].length();i++){
						if(req[j].charAt(i)==sql.charAt(i)){
							 count++;
						}
				}
			if(count==req[j].length()) {
				return req[j];
			}
		}
		return "tsisy";		
	}

	public String TraitSelect(String[] req)throws Exception{
		String tab = getAllTable();
		if (tab.contains(req[3]+";")) {
			if (req[2].toLowerCase().hashCode() == "from".hashCode()) {
				if(req[1].toLowerCase().hashCode()=="*".hashCode()) {
					return "*";
				}else {
					return req[1];
				}
			}else { 
				throw new Exception("    please it's from"); 
			}
		}else { 
			throw new Exception("    this table is not admitted"); 
		}

	}

	public Relation select(String req)throws Exception{
		if(AllTrait(req).toLowerCase().hashCode()=="select ".hashCode()) {
			String[] reqs=req.split(" ");
			if(reqs.length==4) {
				if(TraitSelect(reqs).hashCode()=="*".hashCode()) {
					Relation rep=getAllData(reqs[3]);
					return rep;
				}else { 
					Relation rep=projection(reqs[3],reqs[1].toLowerCase());
					return rep;
				}
			}else { throw new Exception("    Check your request"); }
		}else { throw new Exception("    please it's select"); }
	}

	public boolean traitInsert(String req)throws Exception{
		String tables=getAllTable();
		String[] reqs=req.split(" ");
		if(reqs.length==5){
			if(tables.contains(";"+reqs[2]+";")){
				if(reqs[1].toLowerCase().hashCode()=="into".hashCode()){
					if(reqs[3].toLowerCase().hashCode()=="values".hashCode()){
							return true;
					}else{ 
						throw new Exception("    please it is values");
					}
				}else{ 
					throw new Exception("    please it is into");
				}
			}else{ 
				throw new Exception("    table does not exist");
			}
		}else{ 
			throw new Exception("    check your request");
		}
	}

	public void insert(String nom,String donnee)throws Exception{
		String[] d=donnee.split(",");
		Vector<String> v=new Vector<String>();
		for(int i=0;i<d.length;i++){
			v.add(d[i]);
		}
		insertDataRelation(nom,v);
	}

	public boolean traitCreate(String req)throws Exception{
		File listrelation=new File("ListeTable.txt");
		if(listrelation.exists()==false){
			FileOutputStream outList=new FileOutputStream(listrelation,true);        
      PrintWriter writeList=new PrintWriter(outList);
			writeList.print(";;");
			writeList.flush();
			writeList.close();
		}
		String tables=getAllTable();
		String[] reqs=req.split(" ");
		if(reqs.length==5){
			if(tables.contains(";"+reqs[2]+";")==false){
				if(reqs[3].toLowerCase().hashCode()=="column".hashCode()){
					return true;
				}else{ 
					throw new Exception("    please it is column");
				}
			}else{ 
				throw new Exception("    the table already exists");
			}
		}else{ 
			throw new Exception("    Check your request");
		}
	}

	public void create(String name,String donnee)throws Exception{
		String[] d=donnee.split(",");
		Vector<String> v=new Vector<String>();
		for(int i=0;i<d.length;i++){
			v.add(d[i]);
		}
		createRelation(name,v);
	}

	public Relation request(String req)throws Exception{
		String r=AllTrait(req);
		if(r.hashCode()=="select ".hashCode()){
			Relation rep=select(req);
			return rep;
		}
		else if(r.hashCode()=="insert ".hashCode()){
			if(traitInsert(req)){
					String[] reqs=req.split(" ");
					insert(reqs[2],reqs[4]);
					Vector<String> ok=new Vector<String>();
					Relation rep=getAllData(reqs[2]);
					return rep; 
			}
		}
		else if(r.hashCode()=="create table ".hashCode()){
			if(traitCreate(req)){
					String[] reqs=req.split(" ");
					create(reqs[2],reqs[4]);
					Vector<String> ok=new Vector<String>();
					ok.add("succes");
					Vector<Vector<String>> okk=new Vector<Vector<String>>();
					Vector<String> okkk=new Vector<String>();
					okkk.add("table "+reqs[2]+" cree");
					okk.addElement(okkk);
					Relation rep=new Relation("creatio",ok,okk);
					return rep; 
			}
		}else{
			throw new Exception("    Check your request");
		}
		return null;
	}
 
  public String getnom(){
    return nom;
  }
  public void setnom(String n){
    this.nom = n;
  }
  public Vector<String> getcolonne(){
    return colonne;
  }
  public void setColonne(Vector<String> c){
    this.colonne = c;
  }
  public Vector<Vector<String>> getdonnee(){
    return donnee;
  }
  public void setdonnee(Vector<Vector<String>> d){
    this.donnee = d;
  }
  public Relation requette(String req) {
    return null;
  }
  
}