import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

//Codice per creare un unico file HTML con all'interno tutte le pagine che stanno nelle tre cartelle "Clocks", "Games" e "Tools".
//Codice creato da Gemini e Claude lunedì nove febbraio duemilaventisei dopo un altro tentativo fatto con Gemini due giorni prima.
//Ho fatto varie altre modifiche. Le ultime martedì trentuno marzo duemilaventisei.
//Il numero di pagine cresce sempre più e stanno per arrivarne diverse altre.
//Da mettere nella cartella in cui ci sono: "Clocks", "Games" e "Tools".
//Da compilare e lanciare lì da terminale.

public class HTML_Pages_Unifier {

	private static final String[] FOLDERS = {"Clocks", "Games", "Tools"};
	private static final Map<String, String> EMOJIS;
	static {
		Map<String, String> tempMap = new HashMap<>();
		tempMap.put("Clocks","🕰️");
		tempMap.put("Games","🎮");
		tempMap.put("Tools","🛠️");
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
		for(String folderName : FOLDERS){
			File folder = new File(folderName);
			String sectionId = "grid-" + folderName.toLowerCase();
			htmlMenuBuilder.append("<div class='section'>").append("<h2 class='section-header' tabindex='0' onclick=\"toggleSection('").append(sectionId).append("')\">").append("<span>").append(EMOJIS.get(folderName) + " " + folderName).append("</span>").append("<span class='arrow'>&#9654;</span>").append("</h2>").append("<div class='grid' id='").append(sectionId).append("'>");
			if(folder.exists() && folder.isDirectory()){
				File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
				if(files != null){
					Arrays.sort(files);
					for(File file : files){
						try{
							String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
							content = replaceReloadCalls(content);
							content = injectEscHandler(content);
							content = content.replace("\\", "\\\\").replace("`", "\\`").replace("${", "\\${").replace("</script>", "<\\/script>");
							String fileName = file.getName();
							String displayName = fileName.replace(".html", "").replace("_", " ");
							String key = folderName + "_" + fileName.replaceAll("[^a-zA-Z0-8]", "_");
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
				}
			}
			htmlMenuBuilder.append("</div></div>");
		}
		generateFinalHtml(htmlMenuBuilder.toString(), jsDataBuilder.toString());
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
		max-width: 480px;
		width: 90%;
		position: relative;
		box-shadow: 0 20px 60px rgba(0,0,0,0.6);
		animation: slideUp 0.3s cubic-bezier(0.16,1,0.3,1) forwards;
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

	function toggleSection(gridId){
		const grid = document.getElementById(gridId);
		const header = grid.previousElementSibling;
		const isOpen = grid.style.display === 'grid';
		grid.style.display = isOpen ? 'none' : 'grid';
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
		{ lang: 'EN', flag: '<svg viewBox="0 0 60 30"><clipPath id="s"><path d="M0,0 v30 h60 v-30 z"/></clipPath><path d="M0,0 v30 h60 v-30 z" fill="#012169"/><path d="M0,0 L60,30 M60,0 L0,30" stroke="#fff" stroke-width="6"/><path d="M0,0 L60,30 M60,0 L0,30" clip-path="url(#s)" stroke="#C8102E" stroke-width="4"/><path d="M30,0 v30 M0,15 h60" stroke="#fff" stroke-width="10"/><path d="M30,0 v30 M0,15 h60" stroke="#C8102E" stroke-width="6"/></svg>', msg: 'Press <kbd>?</kbd> inside an app to open a pop-up with all available keyboard shortcuts for that page.' },
		//{ lang: 'EN', flag: '<svg class="flag-svg" viewBox="0 0 7410 3900"><rect width="7410" height="3900" fill="#b22234"/><path d="M0 300h7410M0 900h7410M0 1500h7410M0 2100h7410M0 2700h7410M0 3300h7410" stroke="#fff" stroke-width="300"/><rect width="2964" height="2100" fill="#3c3b6e"/><path d="M247 175l247 759-646-469h798l-646 469z" fill="#fff" transform="scale(.2)"/></svg>',
		{ lang: 'IT', flag: '<svg viewBox="0 0 3 2"><rect width="1" height="2" fill="#008d46"/><rect width="1" height="2" x="1" fill="#f4f5f0"/><rect width="1" height="2" x="2" fill="#d2232c"/></svg>', msg: 'Premi <kbd>?</kbd> in un\u2019app per aprire un pop-up con tutte le scorciatoie da tastiera disponibili per quella pagina.' },
		{ lang: 'FR', flag: '<svg viewBox="0 0 3 2"><rect width="1" height="2" fill="#002395"/><rect width="1" height="2" x="1" fill="#fff"/><rect width="1" height="2" x="2" fill="#ed2939"/></svg>', msg: 'Appuyez sur <kbd>?</kbd> dans une app pour ouvrir un pop-up avec tous les raccourcis clavier disponibles pour cette page.' },
		{ lang: 'ES', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#c60b1e"/><rect width="3" height="1" y="0.5" fill="#ffc400"/></svg>', msg: 'Pulsa <kbd>?</kbd> dentro de una app para abrir un pop-up con todos los atajos de teclado disponibles en esa página.' },
		{ lang: 'DE', flag: '<svg viewBox="0 0 5 3"><rect width="5" height="3"/><rect width="5" height="2" y="1" fill="#d00"/><rect width="5" height="1" y="2" fill="#ffce00"/></svg>', msg: 'Drücke <kbd>?</kbd> in einer App, um ein Pop-up mit allen verfügbaren Tastaturkürzeln für diese Seite zu öffnen.' },
		{ lang: 'PT', flag: '<svg viewBox="0 0 600 400"><rect width="240" height="400" fill="#006600"/><rect x="240" width="360" height="400" fill="#ff0000"/></svg>', msg: 'Pressione <kbd>?</kbd> dentro de um app para abrir um pop-up com todos os atalhos de teclado disponíveis nessa página.' },
		{ lang: 'RO', flag: '<svg viewBox="0 0 3 2"><rect width="1" height="2" fill="#002b7f"/><rect width="1" height="2" x="1" fill="#fcd116"/><rect width="1" height="2" x="2" fill="#ce1126"/></svg>', msg: 'Apasă <kbd>?</kbd> în interiorul unei aplicații pentru a deschide un pop-up cu toate comenzile rapide de tastatură disponibile.' },
		{ lang: 'UA', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="1" fill="#0057b7"/><rect width="3" height="1" y="1" fill="#ffd700"/></svg>', msg: 'Натисніть <kbd>?</kbd> всередині програми, щоб відкрити спливаюче вікно з усіма доступними комбінаціями клавіш.' },
		{ lang: 'GR', flag: '<svg viewBox="0 0 27 18"><rect width="27" height="18" fill="#005bae"/><path d="M0 2h27M0 6h27M0 10h27M0 14h27" stroke="#fff" stroke-width="2"/><rect width="10" height="10" fill="#005bae"/><path d="M5 0v10M0 5h10" stroke="#fff" stroke-width="2"/></svg>', msg: 'Πατήστε <kbd>?</kbd> μέσα σε μια εφαρμογή για να ανοίξετε ένα pop-up με όλες τις διαθέσιμες συντομεύσεις πληκτρολογίου.' },
		//{ lang: 'LA', flag '<svg class="flag-svg" viewBox="0 0 1 1"><rect width="0.5" height="1" fill="#ffe000"/><rect x="0.5" width="0.5" height="1" fill="#fff"/></svg>',
		{ lang: 'LA', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#800000"/><path d="M0.5,0.5 L2.5,0.5 L2.5,1.5 L0.5,1.5 Z" fill="none" stroke="#FFD700" stroke-width="0.1"/><text x="1.5" y="1.15" font-family="serif" font-weight="bold" font-size="0.4" fill="#FFD700" text-anchor="middle">SPQR</text></svg>', msg: 'Preme <kbd>?</kbd> intra applicationem ut fenestram cum omnibus compendiis claviaturarum aperias.' },
		{ lang: 'CN', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#de2110"/><path d="M.5 1.1l.3-.9.3.9-1-.6h1z" fill="#ffde00" transform="matrix(.4 0 0 .4 .1 .1)"/></svg>', msg: '在应用内按 <kbd>?</kbd> 可打开一个弹窗，显示该页面所有可用的键盘快捷键。' },
		{ lang: 'JP', flag: '<svg viewBox="0 0 3 2"><rect width="3" height="2" fill="#fff"/><circle cx="1.5" cy="1" r="0.6" fill="#bc002d"/></svg>', msg: 'アプリ内で <kbd>?</kbd> を押すと、そのページで使用可能なすべてのキーボードショートカットを表示するポップアップが開きます。' }
	];

	let activeKbLang = 0;

	function buildKbHint(){
		const flagsEl = document.getElementById('kb-hint-flags');
		flagsEl.innerHTML = kbMessages.map((item, i) =>
			`<span class="kb-flag-btn ${i===0?'active':''}" onclick="selectKbLang(${i})" title="${item.lang}">${item.flag}</span>`
		).join('');
		document.getElementById('kb-hint-text').innerHTML = kbMessages[0].msg;
	}

	function selectKbLang(i){
		activeKbLang = i;
		document.querySelectorAll('.kb-flag-btn').forEach((el, idx) => el.classList.toggle('active', idx === i));
		document.getElementById('kb-hint-text').innerHTML = kbMessages[i].msg;
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
			if(e.key.toLowerCase() === 'l'){
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