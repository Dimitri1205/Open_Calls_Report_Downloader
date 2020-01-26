package org.opencallfx;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilteringLists {

    private  Ini ini;
    private static Ini.Section section;
    private static String value = "value";
    private Config config = new Config();

    private String urlString;
    private List<String> lob, ageBucket, usCustomers, canBranches, woTypes,
            austriaBranches, belgiumBranches, franceBranches, germanyBranches,
            spainBranches, spainTerritory, spainCSRcode, switzerlandBranches, ukiBranches,
            apacCountries, apacBranches, apacCSRcode, euCustomers;

    public FilteringLists() {
// ini file initialization, configuration and loading;
        this.ini = new Ini();
        config.setMultiOption(true);
        ini.setConfig(config);
        try { ini.load(new File("data.ini")); } catch (IOException e) { }

//URL String loading
        if (ini.get("url", value) == null) { urlString = ""; }
        else { this.urlString = ini.get("url", value); }

//filtering Lists loading
        this.lob = loadList("lob");
        this.ageBucket = loadList("age");
        this.woTypes = loadList("wot");
        this.usCustomers = loadList("usa");
        this.canBranches = loadList("can");
        this.austriaBranches = loadList("aus");
        this.belgiumBranches = loadList("bel");
        this.franceBranches = loadList("fra");
        this.germanyBranches = loadList("ger");
        this.switzerlandBranches = loadList("swi");
        this.spainBranches = loadList("spbr");
        this.spainTerritory = loadList("sptr");
        this.spainCSRcode = loadList("spcs");
        this.ukiBranches = loadList("uki");
        this.apacCountries = loadList("apco");
        this.apacBranches = loadList("apbr");
        this.apacCSRcode = loadList("apcs");
        this.euCustomers = loadList("euc");


    }

    private List<String> loadList (String sec) {
        section = (Profile.Section) ini.get(sec);
        if(!section.isEmpty())
            return section.getAll(value);
        else return new ArrayList<>();
    }



    public String getUrlString() { return urlString; }
    public List<String> getLob() { return lob; }
    public List<String> getAgeBucket() { return ageBucket; }
    public List<String> getUsCustomers() { return usCustomers; }
    public List<String> getCanBranches() { return canBranches; }
    public List<String> getWoTypes() { return woTypes; }
    public List<String> getAustriaBranches() { return austriaBranches; }
    public List<String> getBelgiumBranches() { return belgiumBranches; }
    public List<String> getFranceBranches() { return franceBranches; }
    public List<String> getGermanyBranches() { return germanyBranches; }
    public List<String> getSpainBranches() { return spainBranches; }
    public List<String> getSpainTerritory() { return spainTerritory; }
    public List<String> getSpainCSRcode() { return spainCSRcode; }
    public List<String> getSwitzerlandBranches() { return switzerlandBranches; }
    public List<String> getUkiBranches() { return ukiBranches; }
    public List<String> getApacCountries() { return apacCountries; }
    public List<String> getApacBranches() { return apacBranches; }
    public List<String> getApacCSRcode() { return apacCSRcode; }
    public List<String> getEuCustomers() { return euCustomers; }
}
