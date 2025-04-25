package at.fontain.bahnhof;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import at.fontain.bahnhof.data.Bahnhof;
import at.fontain.bahnhof.data.Happy;
import at.fontain.bahnhof.data.Lokomotive;
import at.fontain.bahnhof.data.Wagoon;
import at.fontain.bahnhof.data.Zug;
import at.fontain.bahnhof.exception.BahnhofNotFoundException;
import at.fontain.bahnhof.exception.ZugException;

@Controller
public class MetaService {
	private Logger log = LoggerFactory.getLogger(MetaService.class);
	
	private static final String VERSION = "1.0.1";
	
	private Map<String, String> hostMap;
	private Bahnhof bahnhof;
	private int zugSequence = 0;
	private int lokomotiveSequence = 0;
	private int wagoonSequence = 0;

	@Autowired
	private Environment environment;
	
	public MetaService() {
	}
	
	private void initBahnhof() {
		log.info("initBahnhof()");
		
		Bahnhof bahnhof = new Bahnhof();
		setBahnhof(bahnhof);
		String name = environment.getProperty("bahnhof");
		if (name != null) {
			this.getBahnhof().setName(name);
		}
		
		log.info("Bahnhof: {}", bahnhof);
	}
	
	public void init(Map<String, String> hostMap) {
		log.info("init()");
		setHostMap(hostMap);
	}
	
	public void addBahnhof(String bahnhof, String host) {
		this.getHostMap().put(bahnhof, host);
	}
	
	public List<String> bahnhofList() {
		log.info("bahnhofList()");
		List<String> bahnhofList = new ArrayList<>();
		String bahnhofs = environment.getProperty("bahnhofList");
		String[] bahnhofArray = bahnhofs.split(",");
		for (String bahnhof : bahnhofArray) {
			bahnhofList.add(bahnhof);
		}
		return bahnhofList;
	}
	
	public Lokomotive erzeugeLokomotive() {
		log.info("erzeugeLokomotive()");
		
		// Lokomotive
		int lokomotiveNumber = this.lokomotiveSequenceInc();
		String name = "Lokomotive" + this.getBahnhof().getName() + lokomotiveNumber;
		log.info("erzeugeLokomotive() - {}", name);
		Lokomotive lokomotive = new Lokomotive();
		lokomotive.setName(name);
		this.erzeugeZug(lokomotive);
		
		return lokomotive;
	}
	
	private void erzeugeZug(Lokomotive lokomotive) {
		// Zug
		int zugNumber = this.zugSequenceInc();
		String zugName = "Zug" + this.getBahnhof().getName() + zugNumber;
		log.info("erzeugeZug({}) - {}", lokomotive.getName(), zugName);
		Zug zug = new Zug();
		zug.setName(zugName);
		zug.setLokomotive(lokomotive);
		this.getBahnhof().getZugList().add(zug);
	}

	private int zugSequenceInc() {
		this.setZugSequence(getZugSequence() +1);
		return this.getZugSequence();
	}
	
	private int lokomotiveSequenceInc() {
		this.setLokomotiveSequence(getLokomotiveSequence() +1);
		return this.getLokomotiveSequence();
	}
	
	public Wagoon erzeugeWagoon(String zugName) throws ZugException {
		log.info("erzeugeWagoon({})", zugName);
		Zug zug = this.zug(zugName);
		if (zug == null) {
			return null;
		}
		
		// Wagoon
		int wagoonNumber = this.wagoonSequenceInc();
		String name = "Wagoon" + this.getBahnhof().getName() + wagoonNumber;
		log.info("erzeugeWagoon({}) - {}", zug.getName(), name);
		Wagoon wagoon = new Wagoon();
		wagoon.setName(name);
		
		zug.getWagoonList().add(wagoon);
		
		return wagoon;
	}
	
	private int wagoonSequenceInc() {
		this.setWagoonSequence(getWagoonSequence() +1);
		return this.getWagoonSequence();
	}
	
	public Zug zug(String name) throws ZugException {
		if (name == null) {
			throw new ZugException("Der Name des Zuges darf nicht null sein!");
		}
		for (Zug zug : this.getBahnhof().getZugList()) {
			if (name.equals(zug.getName())) {
				return zug;
			}
		}
		throw new ZugException("Es wurde kein Zug mit dem Namen " + name + " gefunden!");
	}

	public String verschiebe(String zugNameVon, String zugNameNach) throws ZugException {
		Zug zugVon = this.zug(zugNameVon);
		Zug zugNach = this.zug(zugNameNach);
		
		List<Wagoon> wagoonList = zugVon.getWagoonList();
		if (wagoonList.isEmpty()) {
			throw new ZugException("Zug hat keine Wagoons zum verschieben!");
		}
		
		Wagoon wagoon = wagoonList.remove(zugVon.getWagoonList().size() - 1);
		zugNach.getWagoonList().add(wagoon);
		return wagoon.getName();
	}

	public void fahreZug(String bahnhof, String zugName) throws BahnhofNotFoundException, ZugException {
		log.info("fahreZug({}, {})", bahnhof, zugName);

		Zug zug = this.zug(zugName);
		log.info("Zug: {}", zug);

		if (!getHostMap().containsKey(bahnhof)) {
			throw new BahnhofNotFoundException("Der Bahnhof mit dem Namen " + bahnhof + " konnte nicht gefunden werden!");
		}
		String host = getHostMap().get(bahnhof);
		String uri = "http://" + host + "/empfangeZug";
		log.info("uri: {}", uri);
		
		RestClient restClient = RestClient.create();
		ResponseEntity<Void> response = restClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).body(zug).retrieve().toEntity(Void.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			this.getBahnhof().getZugList().remove(zug);
		}
		
		log.info("response: {}", response);
	}
	
	public void testHostPort(String host, int port) throws IOException {
		try {
            Socket s = new Socket(host, port);
            s.close();
            log.info("Successfully connected to {}:{}", host, port);
        }
        catch (IOException exc) {
            log.info("Unable to connect to {}:{}", host, port);
            throw exc;
        }    	
	}

	public void empfangeZug(Zug zug) {
		log.info("empfangeZug({})", zug.getName());
		
		this.getBahnhof().getZugList().add(zug);
	}
	
	public Happy happy() {
		log.info("happy()");
		Happy happy = new Happy();
		try {
			String version = VERSION;
			happy.setVersion(version);
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			String hostName = InetAddress.getLocalHost().getHostName();
			happy.setHostAddress(hostAddress);
			happy.setHostName(hostName);
		} catch (UnknownHostException exc) {
			log.error("Error accessing localhost", exc);
		}
		String bahnhofName = this.getBahnhof().getName();
		happy.setBahnhofName(bahnhofName);
		String bahnhofList = "";
		String hostList = "";
		for (String bahnhof : this.getHostMap().keySet()) {
			String host = this.getHostMap().get(bahnhof);
			if (!bahnhofList.isEmpty()) {
				bahnhofList += ",";
				hostList += ",";
			}
			bahnhofList += bahnhof;
			hostList += host;
		}
		happy.setBahnhofList(bahnhofList);
		happy.setHostList(hostList);
		return happy;
	}
	
	public List<Zug> zugList() {
		log.info("zugList()");
	    List<Zug> zugList = this.getBahnhof().getZugList();
	    return zugList;
	}
	
	// getter & setter
	public Map<String, String> getHostMap() {
		if (hostMap == null) {
			this.setHostMap(new TreeMap<>());
		}
		return hostMap;
	}

	public void setHostMap(Map<String, String> hostMap) {
		this.hostMap = hostMap;
	}
	
	public Bahnhof getBahnhof() {
		if (bahnhof == null) {
			this.initBahnhof();
		}
		return bahnhof;
	}

	private void setBahnhof(Bahnhof bahnhof) {
		this.bahnhof = bahnhof;
	}

	public int getZugSequence() {
		return zugSequence;
	}

	public void setZugSequence(int zugSequence) {
		this.zugSequence = zugSequence;
	}

	private int getLokomotiveSequence() {
		return lokomotiveSequence;
	}

	private void setLokomotiveSequence(int lokomotiveSequence) {
		this.lokomotiveSequence = lokomotiveSequence;
	}

	private int getWagoonSequence() {
		return wagoonSequence;
	}

	private void setWagoonSequence(int wagoonSequence) {
		this.wagoonSequence = wagoonSequence;
	}

	public String bahnhofName() {
		return getBahnhof().getName();
	}
}
