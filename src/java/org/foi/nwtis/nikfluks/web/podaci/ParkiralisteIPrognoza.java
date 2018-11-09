package org.foi.nwtis.nikfluks.web.podaci;

/**
 * Klasa služi za prikazivanje podataka iz tablica Meteo i Parkiralista. Postoji poseban atribut datum koji je zapravo vrijednost
 * dobivena iz web servisa i predstavlja vrijeme za koje se odnosi prognoza i formatiran je u HR stilu.
 *
 * @author Nikola
 * @version 1
 */
public class ParkiralisteIPrognoza {

    private int idParkiralista;
    private String nazivParkiralista;
    private String adresaParkiralista;
    private MeteoPodaci meteoPodaci;
    private String datum;

    /**
     * Prazan konstruktor klase.
     */
    public ParkiralisteIPrognoza() {
    }

    /**
     * Konstuktor klase sa parametrima.
     *
     * @param idParkiralista id parkiralista
     * @param nazivParkiralista naziv parkiralista
     * @param adresaParkiralista adresa parkiralista
     * @param meteoPodaci meteo podaci dobiveni sa web servisa
     * @param datum datum formatiran u HR stilu
     */
    public ParkiralisteIPrognoza(int idParkiralista, String nazivParkiralista, String adresaParkiralista,
            MeteoPodaci meteoPodaci, String datum) {
        this.idParkiralista = idParkiralista;
        this.nazivParkiralista = nazivParkiralista;
        this.adresaParkiralista = adresaParkiralista;
        this.meteoPodaci = meteoPodaci;
        this.datum = datum;
    }

    /**
     * Getter za id parkiralista.
     *
     * @return id parkiralista
     */
    public int getIdParkiralista() {
        return idParkiralista;
    }

    /**
     * Setter za id parkiralista.
     *
     * @param idParkiralista id parkiralista koji se postavlja
     */
    public void setIdParkiralista(int idParkiralista) {
        this.idParkiralista = idParkiralista;
    }

    /**
     * Getter za naziv parkiralista.
     *
     * @return naziv parkiralista
     */
    public String getNazivParkiralista() {
        return nazivParkiralista;
    }

    /**
     * Setter za naziv parkiralista.
     *
     * @param nazivParkiralista naziv parkiralista koji se postavlja
     */
    public void setNazivParkiralista(String nazivParkiralista) {
        this.nazivParkiralista = nazivParkiralista;
    }

    /**
     * Getter za adresu parkiralista.
     *
     * @return adresa parkiralista
     */
    public String getAdresaParkiralista() {
        return adresaParkiralista;
    }

    /**
     * Setter za adresu parkiralista.
     *
     * @param adresaParkiralista adresa parkiralista koja se postavlja
     */
    public void setAdresaParkiralista(String adresaParkiralista) {
        this.adresaParkiralista = adresaParkiralista;
    }

    /**
     * Getter za meteo podatke.
     *
     * @return meteo podaci
     */
    public MeteoPodaci getMeteoPodaci() {
        return meteoPodaci;
    }

    /**
     * Setter za meteo podatke.
     *
     * @param meteoPodaci meteo podaci koji se postavljaju
     */
    public void setMeteoPodaci(MeteoPodaci meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }

    /**
     * Getter za formatirani datum.
     *
     * @return formatirani datum
     */
    public String getDatum() {
        return datum;
    }

    /**
     * Setter za formatirani datum.
     *
     * @param datum formatirani datum koji se postavlja
     */
    public void setDatum(String datum) {
        this.datum = datum;
    }

}
