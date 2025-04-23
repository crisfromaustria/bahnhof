package at.fontain.bahnhof;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.fontain.bahnhof.data.Happy;
import at.fontain.bahnhof.data.Lokomotive;
import at.fontain.bahnhof.data.Wagoon;
import at.fontain.bahnhof.data.Zug;
import at.fontain.bahnhof.exception.BahnhofNotFoundException;
import at.fontain.bahnhof.exception.ZugException;

@RestController
public class MetaRest {
	private Logger log = LoggerFactory.getLogger(MetaRest.class);
	
	@Autowired
	private MetaService metaService;
	
	@GetMapping("/")
	public ResponseEntity<Happy> happy() {
		Happy happy = this.metaService.happy();
		return new ResponseEntity<>(happy, HttpStatus.OK);
	}
	
	@GetMapping("/bahnhof")
	public ResponseEntity<String> bahnhof() {
		String bahnhof = this.metaService.bahnhofName();
		return new ResponseEntity<>(bahnhof, HttpStatus.OK);
	}
	
	@GetMapping("/bahnhofList")
	public ResponseEntity<List<String>> bahnhofList() {
		List<String> bahnhofList = metaService.bahnhofList();
		return new ResponseEntity<>(bahnhofList, HttpStatus.OK);
	}
	
	@PostMapping("/initBahnhof")
	public ResponseEntity<Void> hostMap(@RequestBody Map<String, String> hostMap) {
		this.metaService.init(hostMap);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/addBahnhof/{bahnhof}/{host}")
	public ResponseEntity<Void> addBahnhof(@PathVariable String bahnhof, @PathVariable String host) {
		this.metaService.addBahnhof(bahnhof, host);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/erzeugeLokomotive")
	public ResponseEntity<Lokomotive> erzeugeLokomotive() {
		Lokomotive lokomotive = this.metaService.erzeugeLokomotive();
		return new ResponseEntity<>(lokomotive, HttpStatus.OK);
	}
	
	@GetMapping("/erzeugeWagoon/{zugName}")
	public ResponseEntity<Wagoon> erzeugeWagoon(@PathVariable String zugName) {
		HttpStatus httpStatus = HttpStatus.OK;
		
		Wagoon wagoon = null;
		try {
			wagoon = this.metaService.erzeugeWagoon(zugName);
		} catch (ZugException exc) {
			log.error("Fehler in RESTful Service von /erzeugeWagoon/" + zugName + "!", exc);
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity<>(wagoon, httpStatus);
	}
	
	@GetMapping("/zugList")
	public ResponseEntity<List<Zug>> zugList() {
	    List<Zug> zugList = this.metaService.getBahnhof().getZugList();
		return new ResponseEntity<>(zugList, HttpStatus.OK);
	}
	
	@GetMapping("/fahreZug/{bahnhof}/{zug}")
	public ResponseEntity<Void> fahreZug(@PathVariable String bahnhof, @PathVariable String zug) {
		HttpStatus httpStatus = HttpStatus.OK;
		
		try {
			this.metaService.fahreZug(bahnhof, zug);
		} catch (BahnhofNotFoundException | ZugException exc) {
			log.error("Fehler im RESTful Service /fahreZug/"+ bahnhof + "/" + zug + "!", exc);
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(httpStatus);
	}
	
	@PostMapping("/empfangeZug")
	public ResponseEntity<Void> empfangeZug(@RequestBody Zug zug) {
		this.metaService.empfangeZug(zug);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/testHostPort/{host}/{port}")
	public ResponseEntity<Void> testHostPort(@PathVariable String host, @PathVariable int port) {
		HttpStatus httpStatus = HttpStatus.OK;
		try {
			this.metaService.testHostPort(host, port);
        }
        catch (IOException exc) {
        	log.error("Fehler in RESTful Service /testHostPort/" + host + "/" + port + "!", exc);
        	httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }    	

		return new ResponseEntity<>(httpStatus);
	}
	
	@GetMapping("/githook/{info}")
	public ResponseEntity<Void> githook(@PathVariable String info) {
		log.info("REST /githook/{}", info);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
