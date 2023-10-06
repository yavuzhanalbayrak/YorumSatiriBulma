/**
*
* @author Yavuzhan Albayrak | yavuzhan.albayrak@ogr.sakarya.edu.tr
* @since 16.04.2023
* <p>
* Fonksiyon bilgilerini tutan sınıf.
* </p>
*/

package YorumSatiriBulma;

public class Fonksiyon {
	private String fonksiyonAdi;
	private int tekSayisi;
	private int cokSayisi;
	private int javadocSayisi;
	
	public Fonksiyon() {
		
	}
	
	public Fonksiyon(String fonksiyonAdi) {
		this.fonksiyonAdi=fonksiyonAdi;
		this.tekSayisi=0;
		this.cokSayisi=0;
		this.javadocSayisi=0;
	}
	
	public void tekArttir() {
		
		this.tekSayisi++;
	}
	public void cokArttir() {
		this.cokSayisi++;
	}
	public void javadocArttir() {
		this.javadocSayisi++;
	}
	
	public String getFonksiyonAdi() {
		return this.fonksiyonAdi;
	}
	public int getTekSayisi() {
		return this.tekSayisi;
	}
	public int getCokSayisi() {
		return this.cokSayisi;
	}
	public int getJavadocSayisi() {
		return this.javadocSayisi;
	}
}
