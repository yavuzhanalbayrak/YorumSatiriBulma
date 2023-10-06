/**
*
* @author Yavuzhan Albayrak | yavuzhan.albayrak@ogr.sakarya.edu.tr
* @since 16.04.2023
* <p>
* Main Sınıfı
* </p>
*/
package YorumSatiriBulma;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	public static void main(String[] args) throws IOException {
		String line;
		String fonksiyonAdi;
		int fonksiyonIndex=-1;//Diziye eklenen son fonksiyona ulaşmaya yarar.
		String classAdi = null;
		ArrayList<Fonksiyon> fonksiyonDizisi=new ArrayList<Fonksiyon>();
		String javadocYorumu = "";
		Boolean javadocTutucu=false;
		int scopeTutucu=0;//açılan scope için artıp kapanan için azalır. Değeri 0 olursa fonksiyonun dışına çıkılmış demektir.
		
		//Dosya oluşturma
		File fileTek = new File("teksatir.txt");
		if(!fileTek.exists()) {
			fileTek.createNewFile();
		}
		FileWriter f1writer = new FileWriter(fileTek,false);
		BufferedWriter tekWriter = new BufferedWriter(f1writer);
		
		File fileCok = new File("coksatir.txt");
		if(!fileCok.exists()) {
			fileCok.createNewFile();
		}
		FileWriter f2writer = new FileWriter(fileCok,false);
		BufferedWriter cokWriter = new BufferedWriter(f2writer);
		
		File fileJava = new File("javadoc.txt");
		if(!fileJava.exists()) {
			fileJava.createNewFile();
		}
		FileWriter f3writer = new FileWriter(fileJava,false);
		BufferedWriter javaWriter = new BufferedWriter(f3writer);
		
		
		
		
		// dosyadan okuma islemleri.
		File file = new File(args[0]);
		FileReader freader;
		BufferedReader bReader;
		try {
			freader = new FileReader(file);
			bReader = new BufferedReader(freader);
		} catch (Exception e) {
			System.out.print("Dosya Bulunamadı.");
			return;
		}
		
		
		
		
		//*.java dosyasını satır satır gezer.
		while((line=bReader.readLine())!=null) {
			
			//Kurucu fonksiyonu bulmada kullanabilmek için class adı bulundu.
			Pattern oruntuClass = Pattern.compile("class(\\s+[a-zA-Z_]\\w*)");
			Matcher eslesmeClass = oruntuClass.matcher(line);
			if(eslesmeClass.find()) {
				String tempClass = line.substring(eslesmeClass.start(),eslesmeClass.end()).trim();
				Pattern oruntu = Pattern.compile("\\s+\\w+");
				Matcher eslesme = oruntu.matcher(tempClass);
			
				if(eslesme.find()) {
					classAdi = tempClass.substring(eslesme.start(),eslesme.end()).trim();
				}
				
			}
			
			//Fonksiyon üstündeki javadoc'u bulma
			Pattern oruntuKapa = Pattern.compile("[*][/]");
			Matcher eslesmeKapa = oruntuKapa.matcher(line);
			
			Pattern oruntuJava = Pattern.compile("([/][*][*])");
			Matcher eslesmeJava = oruntuJava.matcher(line);
	
			if(eslesmeJava.find()) {
				//javadoc'un olduğ ve içindeki yorum tutulur.
				javadocYorumu+= line.substring(eslesmeJava.start()+3).trim()+"\n";
				javadocTutucu = !javadocTutucu;
				while((line=bReader.readLine())!=null) {
					//Scope kapanırsa yorum satırı dışına çıkılır
					oruntuKapa = Pattern.compile("[*][/]");
					eslesmeKapa = oruntuKapa.matcher(line);
					if(eslesmeKapa.find()) {
						break;
					}
					Pattern oruntuYorum = Pattern.compile("[^*]");
					Matcher eslesmeYorum = oruntuYorum.matcher(line);
					//Yorum satırı boyunca yorumlar stringe eklenir.
					if(eslesmeYorum.find())
						javadocYorumu+=line.substring(eslesmeYorum.start()).trim()+"\n";
				}
			}
			
			//Yeni Fonksiyon Bulma
			//Fonksiyon içeren satırı arar. Fakat kurucu fonksiyonun oluşturulması ve çağırılması 
			//durumu birbirine benzeyebildiği için oluşabilcek karmaşıklığı ortadan kaldırabilmek için
			//işlemler yapılmıştır.
			Pattern oruntuFonksiyon = Pattern.compile("((void|byte|short|int|long|float|double|"
					+ "char|boolean|String)(\\s+[a-zA-Z_]\\w*)\\s*[(])|"+classAdi
					+"\\s*[(](\\s*([a-zA-Z_]\\w*)*\\s*(,)*)*([)]$|([)]\\s*[^;][{]+)|[)]\\s*\\s$|"
					+ "[)]\\s*(/))");
			Matcher eslesmeFonksiyon = oruntuFonksiyon.matcher(line);
			//Eşleşme oldu mu kontrol eder.
			if(eslesmeFonksiyon.find()) {
				//Fonksiyonun adını satırda arar.(Fonksiyon)
				Pattern oruntu = Pattern.compile("\\w+\\s*[(]");
				Matcher eslesme = oruntu.matcher(line);
				//Fonksiyonun adı bulunur ve bu isimle birlikte yeni bir fonksiyon nesnesi türetilip 
				//fonksiyonlar dizisine koyulur.
				if (eslesme.find()) {
					fonksiyonAdi = line.substring(eslesme.start(),eslesme.end()-1).trim();
					Fonksiyon fonksiyon = new Fonksiyon(fonksiyonAdi);
					//Dosyalara fonksiyon eklenir.
					tekWriter.write("Fonksiyon Adı: "+fonksiyonAdi+"\n\n");
					cokWriter.write("Fonksiyon Adı: "+fonksiyonAdi+"\n\n");
					javaWriter.write("Fonksiyon Adı: "+fonksiyonAdi+"\n\n");
					fonksiyonDizisi.add(fonksiyon);
					fonksiyonIndex++;
					//Eğer ki fonksiyonun üstünde bir javadoc bulunmuşsa değerleri bu fonksiyona eklenir ve değişkenler resetlenir.
					if(javadocTutucu) {
						fonksiyonDizisi.get(fonksiyonIndex).javadocArttir();
						javaWriter.write(javadocYorumu);
						javadocTutucu=!javadocTutucu;
						javadocYorumu="";
					}
					
				}
				
				//Fonksiyonun Scope'una Girme.
				do {
					Pattern oruntuScopeAc = Pattern.compile("[{]");
					Matcher eslesmeScopeAc = oruntuScopeAc.matcher(line);
					if(eslesmeScopeAc.find()) {
						//Tek satırlı yorumla aynı satırda olması durumunda hangisi önce geliyorsa ona göre bir işlem uygulandı.
						Pattern oruntuTek = Pattern.compile("[/][/]");
						Matcher eslesmeTek = oruntuTek.matcher(line);
						Boolean dene= eslesmeTek.find();
						if(dene&&eslesmeTek.start()>eslesmeScopeAc.start()) {
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
							scopeTutucu++;
							break;
						}else if(dene&&eslesmeTek.start()<eslesmeScopeAc.start()) {
							
						}
						else {
							scopeTutucu++;
							break;
						}
						
					}
				}while((line=bReader.readLine())!=null);
				
				//Fonksiyon içindeki yorum Satırlarını bulma
				while((line=bReader.readLine())!=null){
					Pattern oruntuScopeAc = Pattern.compile("[{]");
					Matcher eslesmeScopeAc = oruntuScopeAc.matcher(line);
						
					Pattern oruntuTek = Pattern.compile("[/][/]");
					Matcher eslesmeTek = oruntuTek.matcher(line);
					
					Pattern oruntuCokAc = Pattern.compile("([/][*]$)|([/][*][^*](.)*)");
					Matcher eslesmeCokAc = oruntuCokAc.matcher(line);
					Boolean _eslesmeCokAc = eslesmeCokAc.find();
					
					oruntuKapa = Pattern.compile("[*][/]");
					eslesmeKapa = oruntuKapa.matcher(line);
				
					Pattern oruntuScopeKapa = Pattern.compile("[}]");
					Matcher eslesmeScopeKapa = oruntuScopeKapa.matcher(line);
					
					//javadoc bulma
					oruntuJava = Pattern.compile("([/][*][*])");
					eslesmeJava = oruntuJava.matcher(line);
			
					if(eslesmeJava.find()) {
						javaWriter.write(line.substring(eslesmeJava.start()+3).trim()+"\n");
						fonksiyonDizisi.get(fonksiyonIndex).javadocArttir();
						while((line=bReader.readLine())!=null) {
							//Scope kapanırsa yorum satırı dışına çıkılır
							oruntuKapa = Pattern.compile("[*][/]");
							eslesmeKapa = oruntuKapa.matcher(line);
							if(eslesmeKapa.find()) {
								break;
							}
							Pattern oruntuYorum = Pattern.compile("[^*]");
							Matcher eslesmeYorum = oruntuYorum.matcher(line);
							if(eslesmeYorum.find())
							javaWriter.write(line.substring(eslesmeYorum.start()).trim()+"\n");
						}
					}
					
					
					oruntuKapa = Pattern.compile("[*][/]");
					eslesmeKapa = oruntuKapa.matcher(line);
					//Çok Satırlı Bulma
					if(_eslesmeCokAc&&eslesmeKapa.find()) {
						cokWriter.write(line.substring(eslesmeCokAc.start()+2,eslesmeKapa.start()).trim()+"\n");
						fonksiyonDizisi.get(fonksiyonIndex).cokArttir();
						continue;
					}
					else if(_eslesmeCokAc) {
						cokWriter.write(line.substring(eslesmeCokAc.start()+2).trim()+"\n");
						fonksiyonDizisi.get(fonksiyonIndex).cokArttir();
						while((line=bReader.readLine())!=null) {
							//Scope kapanırsa yorum satırı dışına çıkılır
							oruntuKapa = Pattern.compile("[*][/]");
							eslesmeKapa = oruntuKapa.matcher(line);
							if(eslesmeKapa.find()) {
								break;
							}
							Pattern oruntuYorum = Pattern.compile("[^*]");
							Matcher eslesmeYorum = oruntuYorum.matcher(line);
							if(eslesmeYorum.find())
							cokWriter.write(line.substring(eslesmeYorum.start()).trim()+"\n");
						}
					}
					
					
					//Tek Satırları bulma
					//Scopların ve tek yorum satırının aynı satırda bulunma durumu.
					if(eslesmeScopeAc.find()&&eslesmeTek.find()&&eslesmeScopeKapa.find()) {
						if(eslesmeTek.start()<eslesmeScopeAc.start()) {
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
							continue;
						}
						else if(eslesmeTek.start()>eslesmeScopeAc.start()&&eslesmeTek.start()<eslesmeScopeKapa.start()) {
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
							scopeTutucu++;
							continue;
						}
						else {
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
							continue;
						}
						
						
					}
					oruntuScopeAc = Pattern.compile("[{]");
					eslesmeScopeAc = oruntuScopeAc.matcher(line);
					
					oruntuTek = Pattern.compile("[/][/]");
					eslesmeTek = oruntuTek.matcher(line);
					//Scope açma ile tek yorum satırının aynı satırda olma durumu
					if(eslesmeScopeAc.find()&&eslesmeTek.find()){
					
						if(eslesmeScopeAc.start()<eslesmeTek.start())
						{
							scopeTutucu++;
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
							continue;
						}
						else {
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
							continue;
						}
					}
					else {
						oruntuScopeAc = Pattern.compile("[{]");
						eslesmeScopeAc = oruntuScopeAc.matcher(line);
							
						if(eslesmeScopeAc.find()) {
							scopeTutucu++;
							}
					}
						
					oruntuScopeKapa = Pattern.compile("[}]");
					eslesmeScopeKapa = oruntuScopeKapa.matcher(line);
						
					oruntuTek = Pattern.compile("[/][/]");
					eslesmeTek = oruntuTek.matcher(line);
				
					if(eslesmeScopeKapa.find()&&eslesmeTek.find()) {
						//scope kapama ile tek satır yorumu aynı satırdaysa.
						if(eslesmeScopeKapa.start()<eslesmeTek.start()) {
							scopeTutucu--;
							if(scopeTutucu==0) {
								break;
							}
							else fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
								
							}
						else if(eslesmeScopeKapa.start()>eslesmeTek.start()) {
							fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
							tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
									
								
						}
							
					}
					else {
						//Satırda tek yorum satırı ve scope kapamadan sadece biri varsa.
						oruntuScopeKapa = Pattern.compile("[}]");
						eslesmeScopeKapa = oruntuScopeKapa.matcher(line);
							
						oruntuTek = Pattern.compile("[/][/]");
						eslesmeTek = oruntuTek.matcher(line);
						
						if(eslesmeScopeKapa.find()) {
							scopeTutucu--;
								
							}
						if(scopeTutucu!=0) {
							if(eslesmeTek.find()) {
								fonksiyonDizisi.get(fonksiyonIndex).tekArttir();
								tekWriter.write(line.substring(eslesmeTek.start()+2).trim()+"\n");
								
							}
						}else {
							break;
						}
					}
				}
				//Dosyalarda Fonksiyonları ayırma.
				tekWriter.write("\n---------------------------------------------------\n");
				cokWriter.write("\n---------------------------------------------------\n");
				javaWriter.write("\n---------------------------------------------------\n");
				
			}
		
			
		}
		//Consola Yazma işlemi
		System.out.println("class: "+classAdi);
		for(int i=0;i<fonksiyonDizisi.size();i++) {
			System.out.println("\tFonksiyon: "+fonksiyonDizisi.get(i).getFonksiyonAdi());
			System.out.println("\t\ttek: "+fonksiyonDizisi.get(i).getTekSayisi());
			System.out.println("\t\tçok: "+fonksiyonDizisi.get(i).getCokSayisi());
			System.out.println("\t\tjava: "+fonksiyonDizisi.get(i).getJavadocSayisi());
			System.out.println("---------------------------------------------");
		}
		tekWriter.close();
		cokWriter.close();
		javaWriter.close();
		bReader.close();
	}

}
