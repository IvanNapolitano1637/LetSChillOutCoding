import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

//Codice per creare un unico file HTML con all'interno tutte le pagine che stanno nelle cartelle "Games" e "Tools".
//La cartella "Games" contiene quattro sottocartelle: "Board", "Card", "Math" e "Puzzle".
//La cartella "Tools" contiene tre sottocartelle: "Clocks", "Lab" e "Utilities".
//Codice creato da Gemini e Claude lunedì nove febbraio duemilaventisei dopo un altro tentativo fatto con Gemini due giorni prima.
//Ho fatto varie altre modifiche. L'ultima mercoledì sei maggio duemilaventisei.
//Il numero di pagine cresce sempre più e stanno per arrivarne diverse altre.
//Da mettere nella cartella in cui ci sono: "Games" e "Tools".
//Da compilare e lanciare lì da terminale.

public class HTML_Pages_Unifier{

	private static final String[] FOLDERS = {"Games", "Tools"};
	private static final Map<String, String> EMOJIS;
	static {
		Map<String, String> tempMap = new HashMap<>();
		tempMap.put("Games","🎮");
		tempMap.put("Board","♟️");
		tempMap.put("Card","🃏");
		tempMap.put("Math","🔢");
		tempMap.put("Puzzle","🧩");
		tempMap.put("Tools","🛠️");
		tempMap.put("Clocks","🕰️");
		tempMap.put("Lab","🧪");
		tempMap.put("Utilities","⚙️");
		EMOJIS = Collections.unmodifiableMap(tempMap);
	}

	private static String injectEscHandler(String content) {
		String escScript = """
			<script>
			document.addEventListener('keydown', function(e) {
				if(e.key === 'Escape'){
					window.parent.postMessage('CLOSE_REQUEST', '*');
				}
			});
			</script>
			""";
		if(content.contains("</body>")){
			return content.replace("</body>", escScript + "</body>");
		}else{
			return content + escScript;
		}
	}

	public static void main(String[] args) {
		StringBuilder jsDataBuilder = new StringBuilder();
		StringBuilder htmlMenuBuilder = new StringBuilder();

		// Mappa delle sottocartelle per ciascuna cartella principale
		Map<String, String[]> subFolderMap = new LinkedHashMap<>();
		subFolderMap.put("Games", new String[]{"Board", "Card", "Math", "Puzzle"});
		subFolderMap.put("Tools", new String[]{"Clocks", "Lab", "Utilities"});

		for(String folderName : FOLDERS){
			File folder = new File(folderName);
			String sectionId = "grid-" + folderName.toLowerCase().replace(" ", "-");
			htmlMenuBuilder.append("<div class='section'>").append("<h2 class='section-header' tabindex='0' onclick=\"toggleSection('").append(sectionId).append("')\">").append("<span>").append(EMOJIS.get(folderName) + " " + folderName).append("</span>").append("<span class='arrow'>&#9654;</span>").append("</h2>").append("<div class='section-body' id='").append(sectionId).append("'>");
			if(folder.exists() && folder.isDirectory()){
				String[] subFolderNames = subFolderMap.get(folderName);
				if(subFolderNames != null){
					for(String subFolderName : subFolderNames){
						File subFolder = new File(folder, subFolderName);
						if(subFolder.exists() && subFolder.isDirectory()){
							String subSectionId = "grid-" + subFolderName.toLowerCase().replace(" ", "-");
							htmlMenuBuilder.append("<div class='section subsection'>").append("<h2 class='section-header subsection-header' tabindex='0' onclick=\"toggleSection('").append(subSectionId).append("')\">").append("<span>").append(EMOJIS.get(subFolderName) + " " + subFolderName).append("</span>").append("<span class='arrow'>&#9654;</span>").append("</h2>").append("<div class='grid' id='").append(subSectionId).append("'>");
							File[] subFiles = subFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
							if(subFiles != null){
								Arrays.sort(subFiles);
								for(File file : subFiles){
									processFile(file, subFolderName, jsDataBuilder, htmlMenuBuilder);
								}
							}
							htmlMenuBuilder.append("</div></div>");
						}
					}
				}
			}
			htmlMenuBuilder.append("</div></div>");
		}
		generateFinalHtml(htmlMenuBuilder.toString(), jsDataBuilder.toString());
	}

	private static void processFile(File file, String folderName, StringBuilder jsDataBuilder, StringBuilder htmlMenuBuilder){
		try{
			String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
			content = replaceReloadCalls(content);
			content = injectEscHandler(content);
			content = content.replace("\\", "\\\\").replace("`", "\\`").replace("${", "\\${").replace("</script>", "<\\/script>");
			String fileName = file.getName();
			String displayName = fileName.replace(".html", "").replace("_", " ");
			String key = folderName.replace(" ", "_") + "_" + fileName.replaceAll("[^a-zA-Z0-8]", "_");
			if(fileName.contains(" - ")){
				String[] parts = fileName.split(" - ");
				if(parts.length > 1){
					displayName = parts[1].replace(".html", "").replace("_", " ");
				}
			}
			jsDataBuilder.append("\n	// --- ").append(displayName).append(" ---\n");
			jsDataBuilder.append("	'").append(key).append("': { name: `").append(displayName).append("`, content: `").append(content).append("` },\n");
			htmlMenuBuilder.append("<button class='card' onclick=\"openPage('").append(key).append("')\">").append(displayName).append("</button>");
		}catch(IOException e){
			System.err.println("Errore lettura: " + file.getName());
		}
	}

	private static String replaceReloadCalls(String content){
		String reloadHelper = "window.parent.postMessage('RELOAD_REQUEST','*')";
		content = content.replaceAll("onclick\\s*=\\s*\"\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*\"", "onclick=\"" + reloadHelper + "\"");
		content = content.replaceAll("onclick\\s*=\\s*'\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*'", "onclick='" + reloadHelper + "'");
		content = content.replaceAll("window\\.location\\.reload\\s*\\(\\s*[^)]*\\s*\\)", reloadHelper);
		content = content.replaceAll("(?<!window\\.)(?<!\\.)\\blocation\\.reload\\s*\\(\\s*[^)]*\\s*\\)", reloadHelper);
		return content;
	}

	private static void generateFinalHtml(String menuHtml, String jsObjectContent){
		String template = """
<!DOCTYPE html>
<!--https://github.com/IvanNapolitano1637/LetSChillOutCoding/blob/main/HTML_CSS_JavaScript/index.html-->
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Toolbox & Playground</title>
		<style>
			:root{
				--bg-gradient: linear-gradient(-45deg, #1e2a38, #11151c, #1a222c, #0f1218);
				--card-bg: rgba(255, 255, 255, 0.05);
				--card-border: rgba(255, 255, 255, 0.1);
				--text-main: #ffffff;
				--accent: #4a90e2;
				--accent-hover: #357abd;
				--viewer-bg: rgba(20, 20, 25, 0.98);
			}
		
			*{
				box-sizing: border-box;
				-webkit-tap-highlight-color: transparent;
			}
		
			*:focus{
				outline: none !important;
				box-shadow: none !important;
			}
		
			body{
				font-family: 'Segoe UI', Roboto, sans-serif;
				margin: 0;
				background: var(--bg-gradient);
				background-size: 400% 400%;
				animation: gradientBG 15s ease infinite;
				color: var(--text-main);
				min-height: 100vh;
				overflow-x: hidden;
				-webkit-user-select: none;
				-ms-user-select: none;
				user-select: none;
			}
			
			@keyframes gradientBG {
				0% {
					background-position: 0% 50%;
					}
				50% {
					background-position: 100% 50%;
					}
				100% {
					background-position: 0% 50%;
					}
			}
		
			#dashboard{
				padding: 40px 20px;
				max-width: 1000px;
				margin: 0 auto;
				animation: fadeIn 1s ease-out;
			}
			
			h1{
				text-align: center;
				font-weight: 200;
				font-size: 2.5rem;
				letter-spacing: 3px;
				margin-bottom: 50px;
				background: linear-gradient(to right, #fff, #888);
				-webkit-background-clip: text;
				background-clip: text;
				-webkit-text-fill-color: transparent;
				-webkit-tap-highlight-color: transparent;
			}
		
			h2{
				border-bottom: 1px solid var(--card-border);
				padding-bottom: 10px;
				margin-top: 50px;
				color: var(--accent);
				font-size: 0.9rem;
				text-transform: uppercase;
				letter-spacing: 1.5px;
				transition: color 0.3s;
			}
		
			.section-body{
				display: none;
			}
		
			.subsection{
				margin-top: 10px;
			}
		
			.subsection-header{
				font-size: 0.8rem;
				margin-top: 20px;
				padding-left: 10px;
				border-left: 3px solid var(--accent);
			}
		
			.section-header{
				cursor: pointer;
				display: flex;
				align-items: center;
				justify-content: space-between;
				-webkit-tap-highlight-color: transparent;
				transition: 0.3s;
			}
		
			.section-header:hover{
				color: var(--text-main);
			}
		
			.section-header .arrow{
				font-size: 0.7rem;
				transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
			}
		
			.section-header.open .arrow{
				transform: rotate(90deg);
				color: var(--accent);
			}
		
			.grid{
				display: none;
				grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
				gap: 20px;
				padding-top: 15px;
			}
		
			.card{
				background: var(--card-bg);
				border: 1px solid var(--card-border);
				border-radius: 12px;
				padding: 20px;
				color: var(--text-main);
				cursor: pointer;
				min-height: 100px;
				display: flex;
				align-items: center;
				justify-content: center;
				text-align: center;
				transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
				-webkit-backdrop-filter: blur(5px);
				backdrop-filter: blur(5px);
				-webkit-user-select: none;
				user-select: none;
			}
		
			.card:hover{
				transform: translateY(-8px) scale(1.03);
				background: rgba(255, 255, 255, 0.08);
				border-color: var(--accent);
				box-shadow: 0 10px 25px rgba(74, 144, 226, 0.25);
			}
		
			.card:active{
				transform: translateY(-2px) scale(0.98);
			}
		
			#viewer{
				display: none;
				position: fixed;
				top: 0;
				left: 0;
				width: 100%;
				height: 100%;
				background: var(--viewer-bg);
				z-index: 2000;
				flex-direction: column;
				-webkit-backdrop-filter: blur(5px);
				backdrop-filter: blur(20px);
				transition: opacity 0.3s ease;
			}
		
			.viewer-show{
				animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
			}
		
			@keyframes slideUp{
				from{
					transform: translateY(30px);
					opacity: 0;
				}
				to{
					transform: translateY(0);
					opacity: 1;
				}
			}
		
			#app-header{
				height: 60px;
				background: rgba(0,0,0,0.4);
				border-bottom: 1px solid var(--card-border);
				display: flex;
				align-items: center;
				justify-content: space-between;
				padding: 0 25px;
				flex-shrink: 0;
			}
			
			#app-title{
				font-weight: 300;
				letter-spacing: 1px;
				font-size: 1.1rem;
				color: var(--accent);
			}
		
			#close-btn{
				background: rgba(255,255,255,0.05);
				color: #fff;
				border: 1px solid rgba(255,255,255,0.2);
				border-radius: 30px;
				padding: 8px 20px;
				cursor: pointer;
				transition: 0.3s;
				text-transform: uppercase;
				font-size: 0.75rem;
				font-weight: bold;
				letter-spacing: 1px;
			}
		
			#close-btn:hover{
				background: #e74c3c;
				border-color: #e74c3c;
				box-shadow: 0 0 15px rgba(231, 76, 60, 0.4);
			}
		
			#iframe-container{
				flex-grow: 1;
				background: #fff;
				overflow: hidden;
			}
		
			iframe{
				width: 100%;
				height: 100%;
				border: none;
				display: block;
			}
		
			@keyframes fadeIn{
				from{
					opacity: 0;
				}to{
					opacity: 1;
				}
			}
		
			#kb-hint-btn{
				position: fixed;
				top: 20px;
				right: 20px;
				z-index: 1000;
				background: var(--card-bg);
				border: 1px solid var(--card-border);
				border-radius: 50%;
				width: 48px;
				height: 48px;
				font-size: 1.4rem;
				cursor: pointer;
				display: flex;
				align-items: center;
				justify-content: center;
				-webkit-backdrop-filter: blur(5px);
				backdrop-filter: blur(5px);
				transition: all 0.3s ease;
				color: var(--text-main);
			}
		
			@media (hover: none) and (pointer: coarse){
				#kb-hint-btn{
					display: none;
				}
			}
		
			#kb-hint-btn:hover{
				background: rgba(255,255,255,0.12);
				border-color: var(--accent);
				box-shadow: 0 0 18px rgba(74, 144, 226, 0.35);
				transform: scale(1.1);
			}
		
			#kb-hint-overlay{
				display: none;
				position: fixed;
				inset: 0;
				background: rgba(0,0,0,0.6);
				z-index: 3000;
				align-items: center;
				justify-content: center;
				-webkit-backdrop-filter: blur(4px);
				backdrop-filter: blur(4px);
				animation: fadeIn 0.2s ease;
			}
		
			#kb-hint-modal{
				background: #1a2030;
				border: 1px solid var(--card-border);
				border-radius: 16px;
				padding: 30px 35px 25px;
				max-width: 560px;
				width: 90%;
				position: relative;
				box-shadow: 0 20px 60px rgba(0,0,0,0.6);
				animation: slideUp 0.3s cubic-bezier(0.16,1,0.3,1) forwards;
				max-height: 85vh;
				overflow-y: auto;
			}
		
			.kb-shortcuts-table{
				width: 100%;
				border-collapse: collapse;
				margin-top: 18px;
				font-size: 0.88rem;
			}
		
			.kb-shortcuts-table tbody tr{
				border-bottom: 1px solid rgba(255,255,255,0.06);
			}
		
			.kb-shortcuts-table tbody tr:last-child{
				border-bottom: none;
			}
		
			.kb-shortcuts-table td{
				padding: 7px 10px;
				color: rgba(255,255,255,0.8);
				vertical-align: middle;
			}
		
			.kb-shortcuts-table td:first-child{
				width: 60px;
				text-align: center;
			}
		
			.kb-shortcuts-table kbd{
				background: rgba(255,255,255,0.1);
				border: 1px solid rgba(255,255,255,0.25);
				border-radius: 5px;
				padding: 3px 10px;
				font-family: monospace;
				font-size: 0.88rem;
				color: var(--accent);
				white-space: nowrap;
			}
		
			#kb-hint-close{
				position: absolute;
				top: 14px;
				right: 16px;
				background: none;
				border: none;
				color: rgba(255,255,255,0.4);
				font-size: 1.1rem;
				cursor: pointer;
				transition: color 0.2s;
				padding: 4px 8px;
			}
		
			#kb-hint-close:hover{
				color: #fff;
			}
		
			#kb-hint-flags{
				display: flex;
				flex-wrap: wrap;
				gap: 10px;
				margin-bottom: 20px;
				justify-content: center;
			}
		
			.kb-flag-btn{
				cursor: pointer;
				border-radius: 6px;
				overflow: hidden;
				width: 36px;
				height: 24px;
				border: 2px solid transparent;
				transition: border-color 0.2s, transform 0.2s;
				flex-shrink: 0;
			}
		
			.kb-flag-btn:hover{
				transform: scale(1.15);
			}
		
			.kb-flag-btn.active{
				border-color: var(--accent);
			}
		
			.kb-flag-btn svg{
				width: 100%;
				height: 100%;
				display: block;
			}
		
			#kb-hint-text{
				color: rgba(255,255,255,0.85);
				font-size: 0.95rem;
				line-height: 1.7;
				text-align: center;
				min-height: 60px;
			}
		
			#kb-hint-text kbd{
				background: rgba(255,255,255,0.1);
				border: 1px solid rgba(255,255,255,0.25);
				border-radius: 5px;
				padding: 2px 8px;
				font-family: monospace;
				font-size: 0.9rem;
				color: var(--accent);
			}
		</style>
	</head>
	<body>
		<button id="kb-hint-btn" onclick="openKbHint()" title="Keyboard shortcuts info">⌨️</button>
		
		<div id="kb-hint-overlay" onclick="closeKbHint()">
			<div id="kb-hint-modal" onclick="event.stopPropagation()">
				<button id="kb-hint-close" onclick="closeKbHint()">✕</button>
				<div id="kb-hint-flags"></div>
				<div id="kb-hint-text"></div>
				<div id="kb-shortcuts-body"></div>
			</div>
		</div>
		
		<div id="dashboard">
			<h1>Toolbox & Playground</h1>
			{{MENU_CONTENT}}
		</div>
		<div id="viewer">
			<div id="app-header">
				<span id="app-title">App Title</span>
				<button id="close-btn" onclick="closePage()">Close</button>
			</div>
			<div id="iframe-container">
				<iframe id="app-frame" name="app-frame" tabindex="0"></iframe>
			</div>
		</div>
		
		<script>
			const DESKTOP = !(/Mobi|Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i).test(navigator.userAgent);
			const pages = { {{JS_CONTENT}} };
			let currentKey = null;
		
			function manageKeyboardButtonVisibility(){
				document.getElementById('kb-hint-btn').style.display = DESKTOP ? 'block' : 'none';
			}
		
			function toggleSection(sectionId){
				const el = document.getElementById(sectionId);
				const header = el.previousElementSibling;
				const isOpen = el.style.display !== 'none' && el.style.display !== '';
				// section-body usa display:block, grid usa display:grid
				const openDisplay = el.classList.contains('grid') ? 'grid' : 'block';
				el.style.display = isOpen ? 'none' : openDisplay;
				header.classList.toggle('open', !isOpen);
			}
		
			function openPage(key){
				const data = pages[key];
				if(!data){
					return;
				}
				currentKey = key;
				document.getElementById('app-title').innerText = data.name;
				const viewer = document.getElementById('viewer');
				const frame = document.getElementById('app-frame');
				viewer.style.display = 'flex';
				viewer.classList.add('viewer-show');
				document.body.style.overflow = 'hidden';
				frame.srcdoc = data.content;
				frame.onload = function() {
					frame.focus();
				};
			}
		
			window.addEventListener('message', function(event){
				if(event.data === 'RELOAD_REQUEST' && currentKey){
					openPage(currentKey);
				}
				if(event.data === 'CLOSE_REQUEST'){
					closePage();
				}
			});
		
			function closePage(){
				const viewer = document.getElementById('viewer');
				viewer.style.opacity = '0';
				setTimeout(() => {
					viewer.style.display = 'none';
					viewer.style.opacity = '1';
					viewer.classList.remove('viewer-show');
					document.body.style.overflow = '';
					document.getElementById('app-frame').srcdoc = '';
				}, 300);
				currentKey = null;
			}
		
			const kbMessages = [
				{ lang: 'EN', flag: '<svg viewBox="0 0 60 30"><clipPath id="s"><path d="M0,0 v30 h60 v-30 z"/></clipPath><path d="M0,0 v30 h60 v-30 z" fill="#012169"/><path d="M0,0 L60,30 M60,0 L0,30" stroke="#fff" stroke-width="6"/><path d="M0,0 L60,30 M60,0 L0,30" clip-path="url(#s)" stroke="#C8102E" stroke-width="4"/><path d="M30,0 v30 M0,15 h60" stroke="#fff" stroke-width="10"/><path d="M30,0 v30 M0,15 h60" stroke="#C8102E" stroke-width="6"/></svg>',
				  msg: 'Press <kbd>?</kbd> inside an app to open a pop-up with all available keyboard shortcuts for that page.',
				  shortcuts: [
				    { key: 'G', desc: 'Toggle Games section' },
				    { key: 'T', desc: 'Toggle Tools section' },
				    { key: 'B', desc: 'Toggle Board subsection' },
				    { key: 'A', desc: 'Toggle Card subsection' },
				    { key: 'M', desc: 'Toggle Math subsection' },
				    { key: 'P', desc: 'Toggle Puzzle subsection' },
				    { key: 'U', desc: 'Toggle Utilities subsection' },
				    { key: 'L', desc: 'Toggle Lab subsection' },
				    { key: 'C', desc: 'Toggle Clocks subsection' },
				    { key: 'N', desc: 'Next language in this popup' },
				    { key: '?', desc: 'Open / close this popup' },
				    { key: 'Esc', desc: 'Close open app or this popup' }
				  ]
				},
				//{ lang: 'EN', flag: '<svg class="flag-svg" viewBox="0 0 7410 3900"><rect width="7410" height="3900" fill="#b22234"/><path d="M0 300h7410M0 900h7410M0 1500h7410M0 2100h7410M0 2700h7410M0 3300h7410" stroke="#fff" stroke-width="300"/><rect width="2964" height="2100" fill="#3c3b6e"/><path d="M247 175l247 759-646-469h798l-646 469z" fill="#fff" transform="scale(.2)"/></svg>',
				{ lang: 'IT', flag: '<svg viewBox="0 0 3 2"><rect width="1" height="2" fill="#008d46"/><rect width="1" height="2" x="1" fill="#f4f5f0"/><rect width="1" height="2" x="2" fill="#d2232c"/></svg>',
				  msg: 'Premi <kbd>?</kbd> in un\u2019app per aprire un pop-up con tutte le scorciatoie da tastiera disponibili per quella pagina.',
				  shortcuts: [
				    { key: 'G', desc: 'Apri/chiudi sezione Games' },
				    { key: 'T', desc: 'Apri/chiudi sezione Tools' },
				    { key: 'B', desc: 'Apri/chiudi sottosezione Board' },
				    { key: 'A', desc: 'Apri/chiudi sottosezione Card' },
				    { key: 'M', desc: 'Apri/chiudi sottosezione Math' },
				    { key: 'P', desc: 'Apri/chiudi sottosezione Puzzle' },
				    { key: 'U', desc: 'Apri/chiudi sottosezione Utilities' },
				    { key: 'L', desc: 'Apri/chiudi sottosezione Lab' },
				    { key: 'C', desc: 'Apri/chiudi sottosezione Clocks' },
				    { key: 'N', desc: 'Lingua successiva in questo popup' },
				    { key: '?', desc: 'Apri / chiudi questo popup' },
				    { key: 'Esc', desc: 'Chiudi l\u2019app aperta o questo popup' }
				  ]
				},
				{ lang: 'FR', flag: '<svg viewBox="0 0 3 2"><rect width="1" height="2" fill="#002395"/><rect width="1" height="2" x="1" fill="#fff"/><rect width="1" height="2" x="2" fill="#ed2939"/></svg>',
				  msg: 'Appuyez sur <kbd>?</kbd> dans une app pour ouvrir un pop-up avec tous les raccourcis clavier disponibles pour cette page.',
				  shortcuts: [
				    { key: 'G', desc: 'Afficher/masquer la section Games' },
				    { key: 'T', desc: 'Afficher/masquer la section Tools' },
				    { key: 'B', desc: 'Afficher/masquer la sous-section Board' },
				    { key: 'A', desc: 'Afficher/masquer la sous-section Card' },
				    { key: 'M', desc: 'Afficher/masquer la sous-section Math' },
				    { key: 'P', desc: 'Afficher/masquer la sous-section Puzzle' },
				    { key: 'U', desc: 'Afficher/masquer la sous-section Utilities' },
				    { key: 'L', desc: 'Afficher/masquer la sous-section Lab' },
				    { key: 'C', desc: 'Afficher/masquer la sous-section Clocks' },
				    { key: 'N', desc: 'Langue suivante dans ce pop-up' },
				    { key: '?', desc: 'Ouvrir / fermer ce pop-up' },
				    { key: 'Esc', desc: 'Fermer l\u2019app ouverte ou ce pop-up' }
				  ]
				},
				{ lang: 'ES', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#c60b1e"/><rect width="3" height="1" y="0.5" fill="#ffc400"/></svg>',
				  msg: 'Pulsa <kbd>?</kbd> dentro de una app para abrir un pop-up con todos los atajos de teclado disponibles en esa p\u00e1gina.',
				  shortcuts: [
				    { key: 'G', desc: 'Mostrar/ocultar secci\u00f3n Games' },
				    { key: 'T', desc: 'Mostrar/ocultar secci\u00f3n Tools' },
				    { key: 'B', desc: 'Mostrar/ocultar subsecci\u00f3n Board' },
				    { key: 'A', desc: 'Mostrar/ocultar subsecci\u00f3n Card' },
				    { key: 'M', desc: 'Mostrar/ocultar subsecci\u00f3n Math' },
				    { key: 'P', desc: 'Mostrar/ocultar subsecci\u00f3n Puzzle' },
				    { key: 'U', desc: 'Mostrar/ocultar subsecci\u00f3n Utilities' },
				    { key: 'L', desc: 'Mostrar/ocultar subsecci\u00f3n Lab' },
				    { key: 'C', desc: 'Mostrar/ocultar subsecci\u00f3n Clocks' },
				    { key: 'N', desc: 'Siguiente idioma en este pop-up' },
				    { key: '?', desc: 'Abrir / cerrar este pop-up' },
				    { key: 'Esc', desc: 'Cerrar la app abierta o este pop-up' }
				  ]
				},
				{ lang: 'DE', flag: '<svg viewBox="0 0 5 3"><rect width="5" height="3"/><rect width="5" height="2" y="1" fill="#d00"/><rect width="5" height="1" y="2" fill="#ffce00"/></svg>',
				  msg: 'Dr\u00fccke <kbd>?</kbd> in einer App, um ein Pop-up mit allen verf\u00fcgbaren Tastaturk\u00fcrzeln f\u00fcr diese Seite zu \u00f6ffnen.',
				  shortcuts: [
				    { key: 'G', desc: 'Abschnitt Games ein-/ausblenden' },
				    { key: 'T', desc: 'Abschnitt Tools ein-/ausblenden' },
				    { key: 'B', desc: 'Unterabschnitt Board ein-/ausblenden' },
				    { key: 'A', desc: 'Unterabschnitt Card ein-/ausblenden' },
				    { key: 'M', desc: 'Unterabschnitt Math ein-/ausblenden' },
				    { key: 'P', desc: 'Unterabschnitt Puzzle ein-/ausblenden' },
				    { key: 'U', desc: 'Unterabschnitt Utilities ein-/ausblenden' },
				    { key: 'L', desc: 'Unterabschnitt Lab ein-/ausblenden' },
				    { key: 'C', desc: 'Unterabschnitt Clocks ein-/ausblenden' },
				    { key: 'N', desc: 'N\u00e4chste Sprache in diesem Pop-up' },
				    { key: '?', desc: 'Dieses Pop-up \u00f6ffnen / schlie\u00dfen' },
				    { key: 'Esc', desc: 'Ge\u00f6ffnete App oder dieses Pop-up schlie\u00dfen' }
				  ]
				},
				{ lang: 'PT', flag: '<svg viewBox="0 0 600 400"><rect width="240" height="400" fill="#006600"/><rect x="240" width="360" height="400" fill="#ff0000"/></svg>',
				  msg: 'Pressione <kbd>?</kbd> dentro de um app para abrir um pop-up com todos os atalhos de teclado dispon\u00edveis nessa p\u00e1gina.',
				  shortcuts: [
				    { key: 'G', desc: 'Mostrar/ocultar se\u00e7\u00e3o Games' },
				    { key: 'T', desc: 'Mostrar/ocultar se\u00e7\u00e3o Tools' },
				    { key: 'B', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Board' },
				    { key: 'A', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Card' },
				    { key: 'M', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Math' },
				    { key: 'P', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Puzzle' },
				    { key: 'U', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Utilities' },
				    { key: 'L', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Lab' },
				    { key: 'C', desc: 'Mostrar/ocultar subse\u00e7\u00e3o Clocks' },
				    { key: 'N', desc: 'Pr\u00f3ximo idioma neste pop-up' },
				    { key: '?', desc: 'Abrir / fechar este pop-up' },
				    { key: 'Esc', desc: 'Fechar o app aberto ou este pop-up' }
				  ]
				},
				{ lang: 'RO', flag: '<svg viewBox="0 0 3 2"><rect width="1" height="2" fill="#002b7f"/><rect width="1" height="2" x="1" fill="#fcd116"/><rect width="1" height="2" x="2" fill="#ce1126"/></svg>',
				  msg: 'Apas\u0103 <kbd>?</kbd> \u00een interiorul unei aplica\u021bii pentru a deschide un pop-up cu toate comenzile rapide de tastatur\u0103 disponibile.',
				  shortcuts: [
				    { key: 'G', desc: 'Arat\u0103/ascunde sec\u021biunea Games' },
				    { key: 'T', desc: 'Arat\u0103/ascunde sec\u021biunea Tools' },
				    { key: 'B', desc: 'Arat\u0103/ascunde subsec\u021biunea Board' },
				    { key: 'A', desc: 'Arat\u0103/ascunde subsec\u021biunea Card' },
				    { key: 'M', desc: 'Arat\u0103/ascunde subsec\u021biunea Math' },
				    { key: 'P', desc: 'Arat\u0103/ascunde subsec\u021biunea Puzzle' },
				    { key: 'U', desc: 'Arat\u0103/ascunde subsec\u021biunea Utilities' },
				    { key: 'L', desc: 'Arat\u0103/ascunde subsec\u021biunea Lab' },
				    { key: 'C', desc: 'Arat\u0103/ascunde subsec\u021biunea Clocks' },
				    { key: 'N', desc: 'Urm\u0103toarea limb\u0103 \u00een acest pop-up' },
				    { key: '?', desc: 'Deschide / \u00eenchide acest pop-up' },
				    { key: 'Esc', desc: '\u00cenchide aplica\u021bia deschis\u0103 sau acest pop-up' }
				  ]
				},
				{ lang: 'UA', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="1" fill="#0057b7"/><rect width="3" height="1" y="1" fill="#ffd700"/></svg>',
				  msg: '\u041d\u0430\u0442\u0438\u0441\u043d\u0456\u0442\u044c <kbd>?</kbd> \u0432\u0441\u0435\u0440\u0435\u0434\u0438\u043d\u0456 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u0438, \u0449\u043e\u0431 \u0432\u0456\u0434\u043a\u0440\u0438\u0442\u0438 \u0441\u043f\u043b\u0438\u0432\u0430\u044e\u0447\u0435 \u0432\u0456\u043a\u043d\u043e \u0437 \u0443\u0441\u0456\u043c\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0438\u043c\u0438 \u043a\u043e\u043c\u0431\u0456\u043d\u0430\u0446\u0456\u044f\u043c\u0438 \u043a\u043b\u0430\u0432\u0456\u0448.',
				  shortcuts: [
				    { key: 'G', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u0440\u043e\u0437\u0434\u0456\u043b Games' },
				    { key: 'T', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u0440\u043e\u0437\u0434\u0456\u043b Tools' },
				    { key: 'B', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Board' },
				    { key: 'A', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Card' },
				    { key: 'M', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Math' },
				    { key: 'P', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Puzzle' },
				    { key: 'U', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Utilities' },
				    { key: 'L', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Lab' },
				    { key: 'C', desc: '\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u0438/\u0441\u0445\u043e\u0432\u0430\u0442\u0438 \u043f\u0456\u0434\u0440\u043e\u0437\u0434\u0456\u043b Clocks' },
				    { key: 'N', desc: '\u041d\u0430\u0441\u0442\u0443\u043f\u043d\u0430 \u043c\u043e\u0432\u0430 \u0432 \u0446\u044c\u043e\u043c\u0443 \u0432\u0456\u043a\u043d\u0456' },
				    { key: '?', desc: '\u0412\u0456\u0434\u043a\u0440\u0438\u0442\u0438 / \u0437\u0430\u043a\u0440\u0438\u0442\u0438 \u0446\u0435 \u0432\u0456\u043a\u043d\u043e' },
				    { key: 'Esc', desc: '\u0417\u0430\u043a\u0440\u0438\u0442\u0438 \u0432\u0456\u0434\u043a\u0440\u0438\u0442\u0443 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u0443 \u0430\u0431\u043e \u0446\u0435 \u0432\u0456\u043a\u043d\u043e' }
				  ]
				},
				{ lang: 'GR', flag: '<svg viewBox="0 0 27 18"><rect width="27" height="18" fill="#005bae"/><path d="M0 2h27M0 6h27M0 10h27M0 14h27" stroke="#fff" stroke-width="2"/><rect width="10" height="10" fill="#005bae"/><path d="M5 0v10M0 5h10" stroke="#fff" stroke-width="2"/></svg>',
				  msg: '\u03a0\u03b1\u03c4\u03ae\u03c3\u03c4\u03b5 <kbd>?</kbd> \u03bc\u03ad\u03c3\u03b1 \u03c3\u03b5 \u03bc\u03b9\u03b1 \u03b5\u03c6\u03b1\u03c1\u03bc\u03bf\u03b3\u03ae \u03b3\u03b9\u03b1 \u03bd\u03b1 \u03b1\u03bd\u03bf\u03af\u03be\u03b5\u03c4\u03b5 \u03ad\u03bd\u03b1 pop-up \u03bc\u03b5 \u03cc\u03bb\u03b5\u03c2 \u03c4\u03b9\u03c2 \u03b4\u03b9\u03b1\u03b8\u03ad\u03c3\u03b9\u03bc\u03b5\u03c2 \u03c3\u03c5\u03bd\u03c4\u03bf\u03bc\u03b5\u03cd\u03c3\u03b5\u03b9\u03c2 \u03c0\u03bb\u03b7\u03ba\u03c4\u03c1\u03bf\u03bb\u03bf\u03b3\u03af\u03bf\u03c5.',
				  shortcuts: [
				    { key: 'G', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Games' },
				    { key: 'T', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Tools' },
				    { key: 'B', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Board' },
				    { key: 'A', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Card' },
				    { key: 'M', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Math' },
				    { key: 'P', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Puzzle' },
				    { key: 'U', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Utilities' },
				    { key: 'L', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Lab' },
				    { key: 'C', desc: '\u0395\u03bc\u03c6\u03ac\u03bd\u03b9\u03c3\u03b7/\u03b1\u03c0\u03cc\u03ba\u03c1\u03c5\u03c8\u03b7 \u03c5\u03c0\u03bf\u03b5\u03bd\u03cc\u03c4\u03b7\u03c4\u03b1\u03c2 Clocks' },
				    { key: 'N', desc: '\u0395\u03c0\u03cc\u03bc\u03b5\u03bd\u03b7 \u03b3\u03bb\u03ce\u03c3\u03c3\u03b1 \u03c3\u03b5 \u03b1\u03c5\u03c4\u03cc \u03c4\u03bf pop-up' },
				    { key: '?', desc: '\u0386\u03bd\u03bf\u03b9\u03b3\u03bc\u03b1 / \u03ba\u03bb\u03b5\u03af\u03c3\u03b9\u03bc\u03bf \u03b1\u03c5\u03c4\u03bf\u03cd \u03c4\u03bf\u03c5 pop-up' },
				    { key: 'Esc', desc: '\u039a\u03bb\u03b5\u03af\u03c3\u03b9\u03bc\u03bf \u03b1\u03bd\u03bf\u03b9\u03c7\u03c4\u03ae\u03c2 \u03b5\u03c6\u03b1\u03c1\u03bc\u03bf\u03b3\u03ae\u03c2 \u03ae \u03c4\u03bf\u03c5 pop-up' }
				  ]
				},
				//{ lang: 'LA', flag '<svg class="flag-svg" viewBox="0 0 1 1"><rect width="0.5" height="1" fill="#ffe000"/><rect x="0.5" width="0.5" height="1" fill="#fff"/></svg>',
				{ lang: 'LA', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#800000"/><path d="M0.5,0.5 L2.5,0.5 L2.5,1.5 L0.5,1.5 Z" fill="none" stroke="#FFD700" stroke-width="0.1"/><text x="1.5" y="1.15" font-family="serif" font-weight="bold" font-size="0.4" fill="#FFD700" text-anchor="middle">SPQR</text></svg>',
				  msg: 'Preme <kbd>?</kbd> intra applicationem ut fenestram cum omnibus compendiis claviaturarum aperias.',
				  shortcuts: [
				    { key: 'G', desc: 'Ostende/occlude sectionem Games' },
				    { key: 'T', desc: 'Ostende/occlude sectionem Tools' },
				    { key: 'B', desc: 'Ostende/occlude subsectionem Board' },
				    { key: 'A', desc: 'Ostende/occlude subsectionem Card' },
				    { key: 'M', desc: 'Ostende/occlude subsectionem Math' },
				    { key: 'P', desc: 'Ostende/occlude subsectionem Puzzle' },
				    { key: 'U', desc: 'Ostende/occlude subsectionem Utilities' },
				    { key: 'L', desc: 'Ostende/occlude subsectionem Lab' },
				    { key: 'C', desc: 'Ostende/occlude subsectionem Clocks' },
				    { key: 'N', desc: 'Lingua proxima in hac fenestra' },
				    { key: '?', desc: 'Aperi / claude hanc fenestram' },
				    { key: 'Esc', desc: 'Claude applicationem apertam aut hanc fenestram' }
				  ]
				},
				{ lang: 'CN', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#de2110"/><path d="M.5 1.1l.3-.9.3.9-1-.6h1z" fill="#ffde00" transform="matrix(.4 0 0 .4 .1 .1)"/></svg>',
				  msg: '\u5728\u5e94\u7528\u5185\u6309 <kbd>?</kbd> \u53ef\u6253\u5f00\u4e00\u4e2a\u5f39\u7a97\uff0c\u663e\u793a\u8be5\u9875\u9762\u6240\u6709\u53ef\u7528\u7684\u952e\u76d8\u5feb\u6377\u952e\u3002',
				  shortcuts: [
				    { key: 'G', desc: '\u663e\u793a/\u9690\u85cf Games \u533a\u57df' },
				    { key: 'T', desc: '\u663e\u793a/\u9690\u85cf Tools \u533a\u57df' },
				    { key: 'B', desc: '\u663e\u793a/\u9690\u85cf Board \u5b50\u533a\u57df' },
				    { key: 'A', desc: '\u663e\u793a/\u9690\u85cf Card \u5b50\u533a\u57df' },
				    { key: 'M', desc: '\u663e\u793a/\u9690\u85cf Math \u5b50\u533a\u57df' },
				    { key: 'P', desc: '\u663e\u793a/\u9690\u85cf Puzzle \u5b50\u533a\u57df' },
				    { key: 'U', desc: '\u663e\u793a/\u9690\u85cf Utilities \u5b50\u533a\u57df' },
				    { key: 'L', desc: '\u663e\u793a/\u9690\u85cf Lab \u5b50\u533a\u57df' },
				    { key: 'C', desc: '\u663e\u793a/\u9690\u85cf Clocks \u5b50\u533a\u57df' },
				    { key: 'N', desc: '\u5f39\u7a97\u4e2d\u5207\u6362\u4e0b\u4e00\u79cd\u8bed\u8a00' },
				    { key: '?', desc: '\u6253\u5f00 / \u5173\u95ed\u5f39\u7a97' },
				    { key: 'Esc', desc: '\u5173\u95ed\u5df2\u6253\u5f00\u7684\u5e94\u7528\u6216\u5f39\u7a97' }
				  ]
				},
				{ lang: 'JP', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#fff"/><circle cx="1.5" cy="1" r="0.6" fill="#bc002d"/></svg>',
				  msg: '\u30a2\u30d7\u30ea\u5185\u3067 <kbd>?</kbd> \u3092\u62bc\u3059\u3068\u3001\u305d\u306e\u30da\u30fc\u30b8\u3067\u4f7f\u7528\u53ef\u80fd\u306a\u3059\u3079\u3066\u306e\u30ad\u30fc\u30dc\u30fc\u30c9\u30b7\u30e7\u30fc\u30c8\u30ab\u30c3\u30c8\u3092\u8868\u793a\u3059\u308b\u30dd\u30c3\u30d7\u30a2\u30c3\u30d7\u304c\u958b\u304d\u307e\u3059\u3002',
				  shortcuts: [
				    { key: 'G', desc: 'Games \u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'T', desc: 'Tools \u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'B', desc: 'Board \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'A', desc: 'Card \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'M', desc: 'Math \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'P', desc: 'Puzzle \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'U', desc: 'Utilities \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'L', desc: 'Lab \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'C', desc: 'Clocks \u30b5\u30d6\u30bb\u30af\u30b7\u30e7\u30f3\u3092\u958b\u9589' },
				    { key: 'N', desc: '\u3053\u306e\u30dd\u30c3\u30d7\u30a2\u30c3\u30d7\u3067\u6b21\u306e\u8a00\u8a9e\u3078' },
				    { key: '?', desc: '\u3053\u306e\u30dd\u30c3\u30d7\u30a2\u30c3\u30d7\u3092\u958b\u304f / \u9589\u3058\u308b' },
				    { key: 'Esc', desc: '\u958b\u3044\u3066\u3044\u308b\u30a2\u30d7\u30ea\u307e\u305f\u306f\u30dd\u30c3\u30d7\u30a2\u30c3\u30d7\u3092\u9589\u3058\u308b' }
				  ]
				}
			];
		
			let activeKbLang = 0;
		
			function buildShortcutsTable(shortcuts){
				const rows = shortcuts.map(s =>
					`<tr><td><kbd>${s.key}</kbd></td><td>${s.desc}</td></tr>`
				).join('');
				return `<table class="kb-shortcuts-table"><tbody>${rows}</tbody></table>`;
			}
		
			function buildKbHint(){
				const flagsEl = document.getElementById('kb-hint-flags');
				flagsEl.innerHTML = kbMessages.map((item, i) =>
					`<span class="kb-flag-btn ${i===0?'active':''}" onclick="selectKbLang(${i})" title="${item.lang}">${item.flag}</span>`
				).join('');
				const first = kbMessages[0];
				document.getElementById('kb-hint-text').innerHTML = first.msg;
				document.getElementById('kb-shortcuts-body').innerHTML = buildShortcutsTable(first.shortcuts);
			}
		
			function selectKbLang(i){
				activeKbLang = i;
				document.querySelectorAll('.kb-flag-btn').forEach((el, idx) => el.classList.toggle('active', idx === i));
				document.getElementById('kb-hint-text').innerHTML = kbMessages[i].msg;
				document.getElementById('kb-shortcuts-body').innerHTML = buildShortcutsTable(kbMessages[i].shortcuts);
			}
		
			function openKbHint(){
				const overlay = document.getElementById('kb-hint-overlay');
				overlay.style.display = 'flex';
			}
		
			function closeKbHint(){
				document.getElementById('kb-hint-overlay').style.display = 'none';
			}
		
			buildKbHint();
		
			document.addEventListener('keydown', function(e){
				if(e.key === 'Escape'){
					if(document.getElementById('kb-hint-overlay').style.display === 'flex'){
						closeKbHint();
					}else{
						closePage();
					}
				}
				if(document.activeElement.tagName !== 'INPUT' && document.activeElement.tagName !== 'TEXTAREA'){
					if(e.key.toLowerCase() === 'c'){
						toggleSection('grid-clocks');
					}
					if(e.key.toLowerCase() === 'g'){
						toggleSection('grid-games');
					}
					if(e.key.toLowerCase() === 't'){
						toggleSection('grid-tools');
					}
					if(e.key.toLowerCase() === 'b'){
						toggleSection('grid-board');
					}
					if(e.key.toLowerCase() === 'a'){
						toggleSection('grid-card');
					}
					if(e.key.toLowerCase() === 'm'){
						toggleSection('grid-math');
					}
					if(e.key.toLowerCase() === 'p'){
						toggleSection('grid-puzzle');
					}
					if(e.key.toLowerCase() === 'u'){
						toggleSection('grid-utilities');
					}
					if(e.key.toLowerCase() === 'l'){
						toggleSection('grid-lab');
					}
					if(e.key.toLowerCase() === 'n'){
						activeKbLang = (activeKbLang + 1) % kbMessages.length;
						selectKbLang(activeKbLang);
					}
					if(e.key === '?'){
						const overlay = document.getElementById('kb-hint-overlay');
						if(overlay.style.display === 'flex'){
							closeKbHint();
						}else{
							openKbHint();
						}
					}
				}
			});
		
			window.onload = manageKeyboardButtonVisibility();
		</script>
	</body>
</html>
""";
		String finalHtml = template.replace("{{MENU_CONTENT}}", menuHtml).replace("{{JS_CONTENT}}", jsObjectContent).stripTrailing();
		try{
			Files.writeString(Paths.get("index.html"), finalHtml);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}